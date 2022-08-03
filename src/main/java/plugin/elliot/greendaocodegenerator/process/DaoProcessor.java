package plugin.elliot.greendaocodegenerator.process;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import plugin.elliot.greendaocodegenerator.config.Constant;
import plugin.elliot.greendaocodegenerator.entity.ClassEntity;
import plugin.elliot.greendaocodegenerator.entity.FieldEntity;

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
        PsiClass parentClass = factory.createClass("AbstractDao");
        Project project = Constant.sProject;
        GlobalSearchScope scope = GlobalSearchScope.fileScope(parentClass.getContainingFile());
        PsiClassType parentType = PsiClassType.getTypeByName(parentClass.getName(), project, scope);
        //继承父类的类型
        cls.getExtendsList().add(factory.createReferenceElementByType(parentType));
    }


}
