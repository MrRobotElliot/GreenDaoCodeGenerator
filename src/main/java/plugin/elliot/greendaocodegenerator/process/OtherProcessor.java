package plugin.elliot.greendaocodegenerator.process;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import plugin.elliot.greendaocodegenerator.entity.ClassEntity;

/**
 * Created by dim on 16/11/7.
 */
public class OtherProcessor extends Processor {
    @Override
    void process(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {

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
}
