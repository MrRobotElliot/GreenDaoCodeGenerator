package plugin.elliot.greendaocodegenerator.common;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NonNls;

public class PsiFileFactoryUtil {

    public static PsiFile createFromTemplate(final PsiDirectory directory, String fileName, String extentsion, FileType type, String templateName, @NonNls String... parameters) throws IncorrectOperationException {

        final FileTemplate template = FileTemplateManager.getInstance(directory.getProject()).getInternalTemplate(templateName);
        String text;
        try {
            text = template.getText();
        } catch (Exception e) {
            throw new RuntimeException("Unable to load template for " + FileTemplateManager.getInstance().internalTemplateToSubject(templateName), e);
        }

        final PsiFileFactory factory = PsiFileFactory.getInstance(directory.getProject());
        final PsiFile file = factory.createFileFromText(!TextUtils.isEmpty(extentsion) ? (fileName += "." + extentsion) : fileName, type, text);
        CodeStyleManager.getInstance(directory.getProject()).reformat(file);
        return (PsiFile) directory.add(file);
    }


}
