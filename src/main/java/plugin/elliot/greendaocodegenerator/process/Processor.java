package plugin.elliot.greendaocodegenerator.process;


import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import plugin.elliot.greendaocodegenerator.common.*;
import plugin.elliot.greendaocodegenerator.config.Config;
import plugin.elliot.greendaocodegenerator.config.Constant;
import plugin.elliot.greendaocodegenerator.entity.ClassEntity;
import plugin.elliot.greendaocodegenerator.entity.MoudelLibrary;
import plugin.elliot.greendaocodegenerator.entity.FieldEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Elliot on 16/11/7.
 */
public abstract class Processor {

    private static Map<MoudelLibrary, Processor> sProcessorMap = new HashMap<>();

    protected String mainPackage;

    static {
        sProcessorMap.put(MoudelLibrary.ENTITY, new EntityProgress());
        sProcessorMap.put(MoudelLibrary.DAO, new DaoProcessor());
        sProcessorMap.put(MoudelLibrary.OTHER, new OtherProcessor());
    }

    static Processor getProcessor(MoudelLibrary moudelLibrary) {
        return sProcessorMap.get(moudelLibrary);
    }

    abstract void process(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor);

    abstract void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor);


    abstract void onEndProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor);

    protected void formatJavCode(PsiClass cls) {
        if (cls == null) {
            return;
        }
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(cls.getProject());
        styleManager.optimizeImports(cls.getContainingFile());
        styleManager.shortenClassReferences(cls);
    }

    /**
     * 开始生成类时
     *
     * @param factory
     * @param classEntity
     * @param parentClass
     * @param visitor
     */
    protected void onStartGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, IProcessor visitor) {
        if (visitor != null) {
            visitor.onStartGenerateClass(factory, classEntity, parentClass);
        }
    }

    public abstract void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass, IProcessor visitor);





}
