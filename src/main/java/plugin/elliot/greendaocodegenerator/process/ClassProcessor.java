package plugin.elliot.greendaocodegenerator.process;


import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import plugin.elliot.greendaocodegenerator.entity.ClassEntity;
import plugin.elliot.greendaocodegenerator.entity.MoudelLibrary;


/**
 * Created by Elliot on 16/11/7.
 */
public class ClassProcessor {

    private PsiElementFactory factory;
    private PsiClass cls;
    private Processor processor;

    public ClassProcessor(PsiElementFactory factory, PsiClass cls) {
        this.factory = factory;
        this.cls = cls;
        processor = Processor.getProcessor(MoudelLibrary.from());
    }

    public void generate(ClassEntity classEntity, IProcessor visitor) {
        if (processor != null) {
            processor.process(classEntity, factory, cls, visitor);
        }
    }
}
