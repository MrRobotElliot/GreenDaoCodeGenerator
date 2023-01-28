package plugin.elliot.greendaocodegenerator.ui.dialog;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugin.elliot.greendaocodegenerator.config.Constant;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class InitDialog extends JDialog {

    private static final String DIR_TEXT_DATABASE = "dataBase";
    private static final String DIR_TEXT_DAO = "dao";
    private static final String DIR_TEXT_ENTITY = "entity";
    private static final String DIR_TEXT_MANAGEER = "manageer";

    private static final Logger logger = LoggerFactory.getLogger(InitDialog.class);

    private JPanel contentPane;
    private JTextField dbDirPathTF;
    private JButton chooseDirBtn;
    private JButton okBtn;
    private JButton cancleBtn;
    private JButton settingBtn;


    private Project project;

    private PsiDirectory dataBasePDir = null;
    private PsiDirectory daoPDir = null;
    private PsiDirectory entityPDir = null;
    private PsiDirectory manageerPDir = null;

    private StringBuilder sbDbDri = null;

    enum EmDir {

    }

    private static InitDialog INSTANCE = null;

    public static InitDialog getInstance(Project project) {
        if (null == INSTANCE) {
            synchronized (InitDialog.class) {
                if (null == INSTANCE) {
                    INSTANCE = new InitDialog(project);
                }
            }
        }
        return INSTANCE;
    }

    public InitDialog(Project project) {
        this.project = project;
        setContentPane(contentPane);
        getRootPane().setDefaultButton(okBtn);
        initView();
        initLinstener();
        initData();
    }

    private void initView() {

    }

    private void initLinstener() {
        chooseDirBtn.addActionListener(al -> {
            chooseDirOfDataBaseDir();
        });
        okBtn.addActionListener(al -> {
            completeInit();
        });
    }

    private void initData() {
        sbDbDri = new StringBuilder();
    }


    private void chooseDirOfDataBaseDir() {
        PackageChooserDialog selector = new PackageChooserDialog("选择包路径", project);
        selector.show();
        if (selector.isOK()) {
            getDataBasePDir(selector.getSelectedPackage());
            InitDialog.getInstance(project).setDbDirPath();
            InitDialog.showDlg(project);
        }
    }

    private void getDataBasePDir(PsiPackage selectedPackage) {
        if (selectedPackage != null) {
            //创建目录
            String selectPkg = selectedPackage.getQualifiedName();
            sbDbDri.append(selectPkg);
            List<String> subDirsOfSelctPkg = new ArrayList<>();
            int preIndexOfDot = -1;
            for (int i = 0; i < selectPkg.length(); i++) {
                if (selectPkg.charAt(i) == '.') {
                    subDirsOfSelctPkg.add(selectPkg.substring(preIndexOfDot + 1, i));
                    preIndexOfDot = i;
                }
            }
            subDirsOfSelctPkg.add(selectPkg.substring(preIndexOfDot + 1));
            PsiDirectory baseDir = PsiDirectoryFactory.getInstance(project).createDirectory(project.getBaseDir());
            //安装 Android 的目录
//            PsiDirectory appDir = getSpecifiedSuDir(baseDir, "app");
//            PsiDirectory srcDir = getSpecifiedSuDir(appDir, "src");
            //按照 IDEA 的目录
            PsiDirectory srcDir = getSpecifiedSuDir(baseDir, "src");

            if (srcDir != null) {
                PsiDirectory preSubDir = srcDir;
                for (int i = 0; i < subDirsOfSelctPkg.size(); i++) {
                    preSubDir = getSpecifiedSuDir(preSubDir, subDirsOfSelctPkg.get(i));
                }
                if (!hasSpecifiedSuDir(preSubDir, DIR_TEXT_DATABASE) && !preSubDir.getName().equals(DIR_TEXT_DATABASE)) {
                    dataBasePDir = preSubDir.createSubdirectory(DIR_TEXT_DATABASE);
                } else if (preSubDir.getName().equals(DIR_TEXT_DATABASE)) {
                    dataBasePDir = preSubDir;
                }
                if (dataBasePDir != null) {
                    sbDbDri.append(".").append(dataBasePDir.getName());
                }
            }

        }
    }

    private void createDataBaseDirAndSubDir() {
        if (dataBasePDir != null) {
            if (!hasSpecifiedSuDir(dataBasePDir, DIR_TEXT_DAO)) {
                daoPDir = dataBasePDir.createSubdirectory(DIR_TEXT_DAO);
            }
            if (!hasSpecifiedSuDir(dataBasePDir, DIR_TEXT_ENTITY)) {
                entityPDir = dataBasePDir.createSubdirectory(DIR_TEXT_ENTITY);
            }
            if (!hasSpecifiedSuDir(dataBasePDir, DIR_TEXT_MANAGEER)) {
                manageerPDir = dataBasePDir.createSubdirectory(DIR_TEXT_MANAGEER);
            }

        }
    }

    private void completeInit() {
        createDataBaseDirAndSubDir();
        Constant.bInited = true;
        INSTANCE.setVisible(false);
    }


    private boolean hasSubDir(PsiDirectory rootDir) {
        return rootDir.getSubdirectories().length == 0 ? false : true;
    }

    private boolean hasSpecifiedSuDir(PsiDirectory rootDir, String subDirName) {
        boolean isCreated = false;
        if (hasSubDir(rootDir)) {
            for (int i = 0; i < rootDir.getSubdirectories().length; i++) {
                if (rootDir.getSubdirectories()[i].getName().equals(subDirName)) {
                    isCreated = true;
                    switch (subDirName) {
                        case DIR_TEXT_DATABASE:
                            dataBasePDir = rootDir.getSubdirectories()[i];
                            break;
                        case DIR_TEXT_DAO:
                            daoPDir = rootDir.getSubdirectories()[i];
                            break;
                        case DIR_TEXT_ENTITY:
                            entityPDir = rootDir.getSubdirectories()[i];
                            break;
                        case DIR_TEXT_MANAGEER:
                            manageerPDir = rootDir.getSubdirectories()[i];
                            break;
                    }
                }
            }
        }
        return isCreated;
    }

    private PsiDirectory getSpecifiedSuDir(PsiDirectory rootDir, String subDirName) {
        if (hasSubDir(rootDir)) {
            for (int i = 0; i < rootDir.getSubdirectories().length; i++) {
                if (rootDir.getSubdirectories()[i].getName().equals(subDirName)) {
                    PsiDirectory srcDir = rootDir.getSubdirectories()[i];
                    return srcDir;
                }
            }
        }
        return null;
    }


    public static void showDlg(Project project) {
        if (INSTANCE == null) {
            INSTANCE = getInstance(project);
            INSTANCE.setSize(1080, 512);
            INSTANCE.setLocationRelativeTo(null);
        } else {
            INSTANCE.setVisible(true);
        }
    }

    public void setDbDirPath() {
        dbDirPathTF.setText(sbDbDri.toString());
    }


}
