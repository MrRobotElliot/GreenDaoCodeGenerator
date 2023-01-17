package plugin.elliot.greendaocodegenerator.ui.dialog;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class InitDialog extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(InitDialog.class);

    private JPanel contentPane;
    private JTextField dbDirPathTF;
    private JButton chooseDirBtn;
    private JButton okBtn;
    private JButton cancleBtn;
    private JButton settingBtn;


    private Project project;

    private PsiDirectory dataBataDir = null;
    private PsiDirectory daoDir = null;
    private PsiDirectory entityDir = null;
    private PsiDirectory manageerDir = null;
    enum EmDir {

    }

    public InitDialog(Project project) {
        this.project = project;
        setContentPane(contentPane);
        getRootPane().setDefaultButton(okBtn);
        initView();
        initLinstener();
    }

    private void initView() {

    }

    private void initLinstener() {
        chooseDirBtn.addActionListener(al -> {
            chooseDirOfDataBaseDir();
        });
    }

    private void chooseDirOfDataBaseDir() {
        PackageChooserDialog selector = new PackageChooserDialog("Select a Package", project);
        selector.show();
        PsiPackage selectedPackage = selector.getSelectedPackage();
        if (selectedPackage != null) {
            //创建目录
            String rootPath = project.getBasePath();
            String selectPkg = selectedPackage.getQualifiedName();
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
                if (!hasSpecifiedSuDir(preSubDir, "dataBase")) {
                    dataBataDir = preSubDir.createSubdirectory("dataBase");
                }
                if (dataBataDir != null) {
                    if (hasSpecifiedSuDir(dataBataDir, "dao")) {
                        daoDir = dataBataDir.createSubdirectory("dao");
                    }
                    if (hasSpecifiedSuDir(dataBataDir, "entity")) {
                        entityDir = dataBataDir.createSubdirectory("entity");
                    }
                    if (hasSpecifiedSuDir(dataBataDir, "manageer")) {
                        manageerDir = dataBataDir.createSubdirectory("manageer");
                    }

                }
            }


        }

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
                        case "dataBase":
                            dataBataDir = rootDir.getSubdirectories()[i];
                            break;
                        case "dao":
                            daoDir = rootDir.getSubdirectories()[i];
                            break;
                        case "entity":
                            entityDir = rootDir.getSubdirectories()[i];
                            break;
                        case "manageer":
                            manageerDir = rootDir.getSubdirectories()[i];
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
        InitDialog dialog = new InitDialog(project);
        dialog.setSize(1080, 512);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }


}
