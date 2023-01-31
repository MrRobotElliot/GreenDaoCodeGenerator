package plugin.elliot.greendaocodegenerator;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugin.elliot.greendaocodegenerator.basic.MessageDialog;
import plugin.elliot.greendaocodegenerator.common.PsiClassUtil;
import plugin.elliot.greendaocodegenerator.config.Constant;
import plugin.elliot.greendaocodegenerator.enums.DirEnum;
import plugin.elliot.greendaocodegenerator.ui.dialog.InitDialog;
import plugin.elliot.greendaocodegenerator.ui.dialog.JsonDialog;

import javax.naming.spi.DirectoryManager;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MianAction extends BaseGenerateAction {
    private static final Logger logger = LoggerFactory.getLogger(MianAction.class);

    private PsiDirectory dataBasePDir = null;
    private PsiDirectory daoPDir = null;
    private PsiDirectory entityPDir = null;
    private PsiDirectory manageerPDir = null;

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
        Constant.sRootProject = project;
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        Constant.sEditor = editor;
        if (!isExistDataBaseDir(project)) {
            PsiClass psiClass = PsiClassUtil.getCurrentPsiClass(e);
            String fileFullName = psiFile.getName();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
            if (psiFile.getFileType().getName().equals("JAVA")) {
                InitDialog.showDlg(fileName, psiClass, psiFile, project);  // 显示 InitDialog
            }

        } else {
            // 显示 JsonDialog
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

    private boolean isExistDataBaseDir(Project project) {
        PsiDirectory baseDir = PsiDirectoryFactory.getInstance(project).createDirectory(project.getBaseDir());
        PsiDirectory srcDir = PsiClassUtil.getSpecifiedSuDir(baseDir, "src");
        return hasSpecifiedSuDir(srcDir, Constant.DIR_TEXT_DATABASE);
    }

    private boolean hasSpecifiedSuDir(PsiDirectory rootDir, String subDirName) {
        boolean isCreated = false;
        if (PsiClassUtil.hasSubDir(rootDir)) {
            for (int i = 0; i < rootDir.getSubdirectories().length; i++) {
                if (rootDir.getSubdirectories()[i].getName().equals(subDirName)) {
                    isCreated = true;
                }
            }
        }
        return isCreated;
    }


}
