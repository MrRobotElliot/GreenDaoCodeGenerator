package plugin.elliot.greendaocodegenerator.process;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import plugin.elliot.greendaocodegenerator.entity.ClassEntity;

/**
 * Created by Elliot on 16/11/8.
 */
public interface IProcessor {

    /**
     * 开始处理时
     *
     * @param classEntity
     * @param factory
     * @param cls
     */
    void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls);

    /**
     * 结束处理时
     *
     * @param classEntity
     * @param factory
     * @param cls
     */
    void onEndProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls);

    /**
     * 开始生成类时
     *
     * @param factory
     * @param classEntity
     * @param parentClass
     */
    void onStartGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass);

    /**
     * 结束生成类时
     *
     * @param factory
     * @param classEntity
     * @param parentClass
     * @param generateClass
     */
    void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass);

}
