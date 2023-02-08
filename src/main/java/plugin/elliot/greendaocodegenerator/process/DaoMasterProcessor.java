package plugin.elliot.greendaocodegenerator.process;

import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import plugin.elliot.greendaocodegenerator.config.Constant;
import plugin.elliot.greendaocodegenerator.entity.ClassEntity;

public class DaoMasterProcessor extends Processor {

    @Override
    void process(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {
        createExtendClass(factory, cls);
    }

    @Override
    void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {

    }

    @Override
    void onEndProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {

    }

    @Override
    public void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass, IProcessor visitor) {

    }

    private void createExtendClass(PsiElementFactory factory, PsiClass cls) {
        final String extendsType = "AbstractDaoMaster";
        final PsiClassType[] extendsListTypes = cls.getExtendsListTypes();
        for (PsiClassType extendsListType : extendsListTypes) {
            PsiClass resolved = extendsListType.resolve();
            // Already implements Parcelable, no need to add it
            if (resolved != null && extendsType.equals(resolved.getQualifiedName())) {
                return;
            }
        }
        PsiJavaCodeReferenceElement extendsReference = factory.createReferenceFromText(extendsType, cls);
        PsiReferenceList extendsList = cls.getExtendsList();
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(Constant.sRootProject);
        if (extendsList != null) {
            styleManager.shortenClassReferences(extendsList.add(extendsReference));
        }
    }
}
