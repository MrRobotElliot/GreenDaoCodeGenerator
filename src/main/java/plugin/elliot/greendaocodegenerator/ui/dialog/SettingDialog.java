package plugin.elliot.greendaocodegenerator.ui.dialog;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugin.elliot.greendaocodegenerator.common.PsiClassUtil;
import plugin.elliot.greendaocodegenerator.common.PsiFileFactoryUtil;
import plugin.elliot.greendaocodegenerator.enums.DirEnum;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SettingDialog extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(SettingDialog.class);


    private JPanel contentPane;
    private JTextField dbDirPathTF;
    private JButton chooseDirBtn;
    private JButton okBtn;
    private JButton cancleBtn;


    private String className;
    private PsiClass cls;
    private PsiFile file;
    private Project project;

    private PsiDirectory dataBasePDir = null;
    private PsiDirectory daoPDir = null;
    private PsiDirectory entityPDir = null;
    private PsiDirectory manageerPDir = null;
    private PsiPackage curPkg = null;
    private StringBuilder sbDbDri = null;
    private StringBuilder sbDaoDir = null;

    private StringBuilder sbEntityDir = null;
    private StringBuilder sbManageerDir = null;

    private String sTableKey = null;


    public SettingDialog(Project project) {
        this.project = project;
        setContentPane(contentPane);
        getRootPane().setDefaultButton(okBtn);
        initView();
        initLinstener();
        initData();
    }

    public SettingDialog(String className, PsiClass cls, PsiFile file, Project project) {
        this.className = className;
        this.cls = cls;
        this.file = file;
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
        cancleBtn.addActionListener(al -> {
            onCancel();
        });
        okBtn.addActionListener(al -> {
            completeSet();
        });
    }


    private void initData() {
        sbDbDri = new StringBuilder();
        sbDaoDir = new StringBuilder();
        sbEntityDir = new StringBuilder();
        sbManageerDir = new StringBuilder();
    }


    private void chooseDirOfDataBaseDir() {
        PackageChooserDialog selector = new PackageChooserDialog("选择包路径", project);
        selector.show();
        if (selector.isOK()) {
            curPkg = selector.getSelectedPackage();
            getDataBasePDir(curPkg);
            setDbDirPath();
            this.showOrHide(true);
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
            PsiDirectory srcDir = PsiClassUtil.getSpecifiedSuDir(baseDir, "src");

            if (srcDir != null) {
                PsiDirectory preSubDir = srcDir;
                for (int i = 0; i < subDirsOfSelctPkg.size(); i++) {
                    preSubDir = PsiClassUtil.getSpecifiedSuDir(preSubDir, subDirsOfSelctPkg.get(i));
                }
                if (!hasSubDir(preSubDir, DirEnum.DATABASE.getType()) && !preSubDir.getName().equals(DirEnum.DATABASE.getType())) {
                    dataBasePDir = preSubDir.createSubdirectory(DirEnum.DATABASE.getType());
                } else if (preSubDir.getName().equals(DirEnum.DATABASE.getType())) {
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
            if (!hasSubDir(dataBasePDir, DirEnum.DAO.getType())) {
                daoPDir = dataBasePDir.createSubdirectory(DirEnum.DAO.getType());
            }
            sbDaoDir.append(sbDbDri).append(".").append(daoPDir.getName());
            if (!hasSubDir(dataBasePDir, DirEnum.ENTITY.getType())) {
                entityPDir = dataBasePDir.createSubdirectory(DirEnum.ENTITY.getType());
            }
            sbEntityDir.append(sbDbDri).append(".").append(entityPDir.getName());
            if (!hasSubDir(dataBasePDir, DirEnum.MANAGER.getType())) {
                manageerPDir = dataBasePDir.createSubdirectory(DirEnum.MANAGER.getType());
            }
            sbManageerDir.append(sbDbDri).append(".").append(manageerPDir.getName());
        }
    }

    private void onCancel() {
        dispose();
    }

    private void completeSet() {
        createDataBaseDirAndSubDir();
        showOrHide(false);
        createDaoManager();
        dispose();
    }


    private boolean hasSubDir(PsiDirectory rootDir, String subDirName) {
        boolean isCreated = false;
        for (int i = 0; i < rootDir.getSubdirectories().length; i++) {
            if (rootDir.getSubdirectories()[i].getName().equals(subDirName)) {
                isCreated = true;
                switch (subDirName) {
                    case "dataBase":
                        dataBasePDir = rootDir.getSubdirectories()[i];
                        break;
                    case "dao":
                        daoPDir = rootDir.getSubdirectories()[i];
                        break;
                    case "entity":
                        entityPDir = rootDir.getSubdirectories()[i];
                        break;
                    case "manager":
                        manageerPDir = rootDir.getSubdirectories()[i];
                        break;
                }
            }
        }
        return isCreated;
    }


    public static void showDlg(String className, PsiClass cls, PsiFile file, Project project) {
        SettingDialog settingDialog = new SettingDialog(className, cls, file, project);
        settingDialog.setSize(1080, 512);
        settingDialog.setLocationRelativeTo(null);
        settingDialog.showOrHide(true);
    }

    private void showOrHide(boolean isShow) {
        this.setVisible(isShow);
    }


    private void setDbDirPath() {
        dbDirPathTF.setText(sbDbDri.toString());
    }

    private void createDaoManager() {
        String className = "DaoManager";
        StringBuilder classSampleContentSb = new StringBuilder();
        classSampleContentSb.append("package ").append(curPkg.getQualifiedName() + "." + manageerPDir.getName());
        classSampleContentSb.append("\n\n");
        classSampleContentSb.append("public class " + className + " {\n\n}");
        final PsiFileFactory factory = PsiFileFactory.getInstance(project);
        PsiJavaFile file = (PsiJavaFile) factory.createFileFromText(className + ".java", JavaFileType.INSTANCE, classSampleContentSb.toString());
        manageerPDir.add(file);
    }

}
