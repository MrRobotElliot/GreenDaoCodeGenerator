package plugin.elliot.greendaocodegenerator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiFile;
import plugin.elliot.greendaocodegenerator.basic.MessageDialog;
import plugin.elliot.greendaocodegenerator.json.JsonDialog;



public class MianAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE); // 获取当前操作的文件，是一个PsiFile类型
        if (psiFile.getFileType().getName().equals("JAVA")) {
            String fileFullName = psiFile.getName();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
            JsonDialog.showDlg(fileName);
        }else{
            MessageDialog.showDlg("当前类 "+psiFile.getName() +" 不是 Java文件！");
        }
    }
}
