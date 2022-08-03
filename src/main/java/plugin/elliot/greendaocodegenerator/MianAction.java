package plugin.elliot.greendaocodegenerator;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.projectImport.ProjectSetProcessor;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilBase;
import plugin.elliot.greendaocodegenerator.basic.MessageDialog;
import plugin.elliot.greendaocodegenerator.common.PsiClassUtil;
import plugin.elliot.greendaocodegenerator.config.Constant;
import plugin.elliot.greendaocodegenerator.ui.JsonDialog;
import com.intellij.psi.util.PsiTypesUtil;

import javax.naming.Context;

import static com.intellij.ide.actions.SearchEverywhereClassifier.EP_Manager.isClass;

public class MianAction extends BaseGenerateAction {


    public MianAction() {
        super(null);
    }

    protected MianAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE); // 获取当前操作的文件，是一个PsiFile类型
        Project project = e.getData(PlatformDataKeys.PROJECT);
        Constant.sProject = project;
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
//        PsiFile mFile = PsiUtilBase.getPsiFileInEditor(editor, project);
//        PsiClass psiClass = getTargetClass(editor, psiFile);
        PsiClass psiClass = PsiClassUtil.getCurrentPsiClass(e);
        if (psiFile.getFileType().getName().equals("JAVA")) {
            String fileFullName = psiFile.getName();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
            JsonDialog.showDlg(fileName, psiClass, psiFile, project);
        } else {
            MessageDialog.showDlg("当前文件 " + psiFile.getName() + " 不是Java文件！");
        }
    }

}
