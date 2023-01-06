package plugin.elliot.greendaocodegenerator.process;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilBase;
import plugin.elliot.greendaocodegenerator.config.Constant;
import plugin.elliot.greendaocodegenerator.entity.ClassEntity;

public class DaoProcessor extends Processor {


    @Override
    void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {

    }

    @Override
    void onEndProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {
        if (visitor != null) {
            visitor.onEndProcess(classEntity, factory, cls);
        }
        formatJavCode(cls);
    }

    @Override
    public void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass, IProcessor visitor) {

    }


    @Override
    void process(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {
        createExtendClass(factory, cls);
    }


    private void createExtendClass(PsiElementFactory factory, PsiClass cls) {
        String name = cls.getName();
        String entityName = name + "Entity";
        String strExtend = "AbstractDao<" + entityName + ", Long>";
        String parentClassContent = strExtend;
//        String parentClassContent = "class " + name + "Dao extend " +  strExtend + "{}";
//        PsiClass parentClass =  factory.createClassFromText(parentClassContent, null);
        try {
            PsiClass parentClass = factory.createClass("AbstractDao");
            PsiFile curFile = PsiUtilBase.getPsiFileInEditor(Constant.sEditor, cls.getProject());
            PsiFile entityPsiFile = PsiUtilBase.getPsiFileAtOffset(curFile, 1);
            GlobalSearchScope globalSearchScope = GlobalSearchScope.fileScope(entityPsiFile);
            PsiType entityType = factory.createType(cls, PsiType.getTypeByName(entityName, Constant.sRootProject, globalSearchScope));
            PsiElement entityElement = factory.createParameter(entityName, entityType);
            PsiElement longElement = factory.createParameter("Long", PsiType.LONG);
            parentClass.addAfter(entityElement, null);
            parentClass.addAfter(entityElement, null);
            GlobalSearchScope scope = GlobalSearchScope.fileScope(parentClass.getContainingFile());
            PsiClassType parentType = PsiClassType.getTypeByName(parentClass.getName(), Constant.sRootProject, scope);
            //继承父类的类型
            cls.getExtendsList().add(factory.createReferenceElementByType(parentType));
//        cls.getExtendsList().add(factory.createParameter(entityName, ));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
