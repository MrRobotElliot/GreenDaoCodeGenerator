package plugin.elliot.greendaocodegenerator.common;

import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Created by Elliot on 15/8/22.
 */
public class PsiClassUtil {

    public static PsiClass exist(PsiFile psiFile, String generateClass) {
        PsiClass psiClass = null;
        PsiDirectory psiDirectory = getJavaSrc(psiFile);
        if (psiDirectory == null || psiDirectory.getVirtualFile().getCanonicalPath() == null) {
            return null;
        }

        File file = new File(psiDirectory.getVirtualFile().getCanonicalPath().concat("/").concat(generateClass.trim().replace(".", "/")).concat(".java"));

        String[] strArray = generateClass.replace(" ", "").split("\\.");
        if (TextUtils.isEmpty(generateClass)) {
            return null;
        }
        String className = strArray[strArray.length - 1];
        String packName = generateClass.substring(generateClass.length() - className.length(), generateClass.length());
        if (file.exists()) {
            for (int i = 0; i < strArray.length - 1; i++) {
                psiDirectory = psiDirectory.findSubdirectory(strArray[i]);
                if (psiDirectory == null) {
                    return null;
                }
            }
            PsiFile psiFile1 = psiDirectory.findFile(className + ".java");
            if ((psiFile1 instanceof PsiJavaFile) && ((PsiJavaFile) psiFile1).getClasses().length > 0) {
                psiClass = ((PsiJavaFile) psiFile1).getClasses()[0];
            }
        }
        return psiClass;
    }

    public static PsiDirectory getJavaSrc(PsiFile psiFile) {
        PsiDirectory psiDirectory = null;
        if (psiFile instanceof PsiJavaFileImpl) {
            String packageName = ((PsiJavaFileImpl) psiFile).getPackageName();
            String[] arg = packageName.split("\\.");
            psiDirectory = psiFile.getContainingDirectory();

            for (int i = 0; i < arg.length; i++) {
                psiDirectory = psiDirectory.getParent();
                if (psiDirectory == null) {
                    break;
                }
            }
        }
        return psiDirectory;
    }

    public static File getPackageFile(PsiFile psiFile, String packageName) {
        PsiDirectory psiDirectory = getJavaSrc(psiFile);
        if (psiDirectory == null || psiDirectory.getVirtualFile().getCanonicalPath() == null) {
            return null;
        }

        if (packageName == null) {
            return new File(psiDirectory.getVirtualFile().getCanonicalPath());
        }
        File file = new File(psiDirectory.getVirtualFile().getCanonicalPath().concat("/").concat(packageName.trim().replace(".", "/")));
        if (file.exists()) {
            return file;
        }
        return null;
    }


    public static PsiClass getPsiClass(PsiFile psiFile, Project project, String generateClass) throws Throwable {

        PsiClass psiClass = null;
        PsiDirectory psiDirectory = getJavaSrc(psiFile);

        if (psiDirectory == null || psiDirectory.getVirtualFile().getCanonicalPath() == null) {
            return null;
        }

        File file = new File(psiDirectory.getVirtualFile().getCanonicalPath().concat("/").concat(generateClass.trim().replace(".", "/")).concat(".java"));

        String[] strArray = generateClass.replace(" ", "").split("\\.");
        if (TextUtils.isEmpty(generateClass)) {
            return null;
        }
        String className = strArray[strArray.length - 1];
        String packName = generateClass.substring(0, generateClass.length() - className.length());
        if (file.exists()) {
            for (int i = 0; i < strArray.length - 1; i++) {
                psiDirectory = psiDirectory.findSubdirectory(strArray[i]);
                if (psiDirectory == null) {
                    return null;
                }
            }
            PsiFile psiFile1 = psiDirectory.findFile(className + ".java");
            if ((psiFile1 instanceof PsiJavaFile) && ((PsiJavaFile) psiFile1).getClasses().length > 0) {
                psiClass = ((PsiJavaFile) psiFile1).getClasses()[0];
            }
            if (psiClass != null) {
                FileEditorManager manager = FileEditorManager.getInstance(project);
                manager.openFile(psiClass.getContainingFile().getVirtualFile(), true, true);
            }

        } else {
            if (!file.getParentFile().exists() && !TextUtils.isEmpty(packName)) {
                psiDirectory = createPackageInSourceRoot(packName, psiDirectory);

            } else {
                for (int i = 0; i < strArray.length - 1; i++) {
                    psiDirectory = psiDirectory.findSubdirectory(strArray[i]);
                    if (psiDirectory == null) {
                        return null;
                    }
                }
            }

            psiClass = JavaDirectoryService.getInstance().createClass(psiDirectory, className);
            FileEditorManager manager = FileEditorManager.getInstance(project);
            manager.openFile(psiClass.getContainingFile().getVirtualFile(), true, true);
        }

        return psiClass;
    }

    public static PsiDirectory createPackageInSourceRoot(String packageName, PsiDirectory sourcePackageRoot) {
        return DirectoryUtil.createSubdirectories(packageName, sourcePackageRoot, ".");
    }

    private PsiClass getPsiClassByName(Project project, String cls) {
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        return javaPsiFacade.findClass(cls, searchScope);
    }


    public static String getPackage(PsiClass cls) {
        if (cls.getQualifiedName() == null) {
            return null;
        }
        int i = cls.getQualifiedName().lastIndexOf(".");
        if (i > -1) {
            return cls.getQualifiedName().substring(0, i);
        } else {
            return "";
        }
    }

    public static boolean isClassAvailableForProject(Project project, String className) {
        PsiClass classInModule = JavaPsiFacade.getInstance(project).findClass(className, new EverythingGlobalScope(project));
        return classInModule != null;
    }

    /**
     * 获得当前文件的PisClass
     *
     * @param e
     * @return
     */
    public static PsiClass getCurrentPsiClass(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE); // 获取当前操作的文件，是一个PsiFile类型
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return psiClass;
    }

    /**
     * 获得当前文件的PisClass
     *
     * @param psiFile
     * @param editor
     * @return
     */
    public static PsiClass getCurrentPsiClass(PsiFile psiFile, Editor editor) {
        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return psiClass;
    }


    /**
     * 添加包
     *
     * @param elementFactory
     * @param cls
     * @param fullyQualifiedName
     */
    public static void addImport(PsiElementFactory elementFactory, PsiClass cls, String fullyQualifiedName) {
        final PsiFile file = cls.getContainingFile();
        if (!(file instanceof PsiJavaFile)) {
            return;
        }
        final PsiJavaFile javaFile = (PsiJavaFile) file;

        PsiImportList importList = javaFile.getImportList();
        if (importList == null) {
            return;
        }

        // Check if already imported
        for (PsiImportStatementBase is : importList.getAllImportStatements()) {
            String impQualifiedName = is.getImportReference().getQualifiedName();
            if (fullyQualifiedName.equals(impQualifiedName)) {
                return; // Already imported so nothing neede
            }

        }
        // Not imported yet so add it
        importList.add(elementFactory.createImportStatementOnDemand(fullyQualifiedName));
    }


    /**
     * 生成在数据库中的名字
     *
     * @param fielName
     * @return
     */
    @NotNull
    public static String generatorDataNameInDb(String fielName) {
        Boolean isHasUp = false;
        String nameInDbUp = "";
        int preIndex = 0;
        for (int i = 0; i < fielName.length(); i++) {
            String ch = fielName.substring(i, i + 1);
            if (ch == ch.toUpperCase()) {
                isHasUp = true;
                nameInDbUp += fielName.substring(preIndex, i);
                nameInDbUp = nameInDbUp.toUpperCase() + "_";
                preIndex = i;
            }
        }
        if (isHasUp) {
            nameInDbUp += fielName.substring(preIndex).toUpperCase();
        } else {
            nameInDbUp = fielName.toUpperCase();
        }
        return nameInDbUp;
    }


    @Contract(pure = true)
    public static String exchangTypeOfDataFromJson(String jsonType) {
        String exchangedType = "";
        switch (jsonType) {
            case "String":
                exchangedType = "TEXT";
                break;
            case "Integer":
                exchangedType = "INTEGER";
                break;
            case "Long":
                exchangedType = "LONG";
                break;
        }
        return exchangedType;
    }

}
