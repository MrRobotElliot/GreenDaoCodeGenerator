package plugin.elliot.greendaocodegenerator.common;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import plugin.elliot.greendaocodegenerator.config.Constant;
import plugin.elliot.greendaocodegenerator.ui.dialog.SettingDialog;

import javax.swing.*;

public class DialogUtil {


    public static JDialog curFileDialog = null;
    public static String curFileType = "";

    public static void ShowSettingDlg(String className, PsiClass cls, PsiFile file, Project project) {
        SettingDialog.showDlg(className, cls, file, project);
    }
}
