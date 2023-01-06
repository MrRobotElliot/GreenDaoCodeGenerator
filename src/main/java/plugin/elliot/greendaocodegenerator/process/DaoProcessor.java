package plugin.elliot.greendaocodegenerator.process;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilBase;
import plugin.elliot.greendaocodegenerator.common.PsiClassUtil;
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
        PsiClassUtil.addImport(factory, cls, "org.greenrobot.greendao.AbstractDao");
        PsiClassUtil.addImport(factory, cls, "org.greenrobot.greendao.annotation.Property");
        createExtendClass(factory, cls);
        generateField(classEntity, factory, cls);
    }


    /**
     * 添加继承类
     *
     * @param factory
     * @param cls
     */
    private void createExtendClass(PsiElementFactory factory, PsiClass cls) {
        String name = cls.getName();
        String entityName = name.replaceAll("Dao", "") + "Entity";
        final String extendsType = "AbstractDao<" + entityName + ", Long>";
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

    private void generateField(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
        addFieldOfTabeleName(factory, cls);
        addFieldOfProperties(classEntity, factory, cls);
    }


    /**
     * 添加成员变量tableName
     *
     * @param factory
     * @param cls
     */
    private static void addFieldOfTabeleName(PsiElementFactory factory, PsiClass cls) {
        StringBuilder tabNameSB = new StringBuilder();
        String name = cls.getName();
        String tableName = name.replaceAll("Dao", "") + "Tab";
        tabNameSB.append("public static final String TABLENAME = \"").append(tableName).append("\";");
        cls.add(factory.createFieldFromText(tabNameSB.toString(), cls));
    }


    private static void addFieldOfProperties(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
        StringBuilder classSB = new StringBuilder("public static class Properties {");
        StringBuilder fieldSB = new StringBuilder();
        for (int i = 0; i < classEntity.getFields().size(); i++) {
            FieldEntity fieldEntity = classEntity.getFields().get(i);
            fieldSB.append("public final static Property ")
                    .append(fieldEntity.getFieldName().substring(0, 1).toUpperCase() + fieldEntity.getFieldName().substring(1) + " = new Property(")
                    .append(i).append(",")
                    .append(fieldEntity.getType()).append(".class,")
                    .append("\"").append(fieldEntity.getFieldName().toLowerCase()).append("\",")
                    .append(fieldEntity.getFieldName().equals("id") ? "true," : "false,")
                    .append("\"").append(fieldEntity.getFieldName().toUpperCase()).append("\");\n");
        }
        classSB.append(fieldSB);
        classSB.append("}");
        cls.add(factory.createClassFromText(classSB.toString(), null).getInnerClasses()[0]);
    }

}
