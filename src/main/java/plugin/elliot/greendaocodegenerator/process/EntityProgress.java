package plugin.elliot.greendaocodegenerator.process;

import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.IncorrectOperationException;
import com.thoughtworks.qdox.model.JavaClass;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugin.elliot.greendaocodegenerator.common.*;
import plugin.elliot.greendaocodegenerator.config.Config;
import plugin.elliot.greendaocodegenerator.config.Constant;
import plugin.elliot.greendaocodegenerator.entity.ClassEntity;
import plugin.elliot.greendaocodegenerator.entity.FieldEntity;
import plugin.elliot.greendaocodegenerator.entity.MoudelLibrary;

public class EntityProgress extends Processor {
    private static final Logger logger = LoggerFactory.getLogger(EntityProgress.class);

    @Override
    public void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {
        guidePacket(factory, cls);
        injectEntityAnotaion(factory, cls);
//        clearHadData(factory, cls);
    }


    @Override
    void onEndProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {
        if (visitor != null) {
            visitor.onEndProcess(classEntity, factory, cls);
        }
        formatJavCode(cls);
        if (isRightDir(classEntity)) {
        }
    }

    @Override
    protected void onStartGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, IProcessor visitor) {
        super.onStartGenerateClass(factory, classEntity, parentClass, visitor);
        if (visitor != null) {
            visitor.onStartGenerateClass(factory, classEntity, parentClass);
        }

    }


    @Override
    public void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass, IProcessor visitor) {

        if (visitor != null) {
            visitor.onEndGenerateClass(factory, classEntity, parentClass, generateClass);
            //使用Lombok生成Lombok注解
            if (Config.getInstant().isUseLombok()) {
                PsiDocComment docComment = generateClass.getDocComment();
                if (docComment == null && Config.getInstant().isUseComment()) {
                    JavaDocUtils.addClassComment(generateClass, classEntity, factory);
                }

            }
        }
    }


    private void clearHadData(PsiElementFactory factory, PsiClass generateClass) {
        if (factory == null || generateClass == null) {
            return;
        }
        generateClass.delete();
    }

    /**
     * 导包
     */
    private void guidePacket(PsiElementFactory factory, PsiClass generateClass) {
        if (factory == null || generateClass == null) {
            return;
        }
        PsiClassUtil.addImport(factory, generateClass, "org.greenrobot.greendao.annotation.Property");
    }


    /**
     * 注入实体注解
     *
     * @param factory
     * @param generateClass
     */
    private void injectEntityAnotaion(PsiElementFactory factory, PsiClass generateClass) {
        if (factory == null || generateClass == null) {
            return;
        }
        PsiModifierList modifierList = generateClass.getModifierList();
        if (modifierList != null) {
            //添加类注解
            PsiElement firstChild = modifierList.getFirstChild();
            PsiAnnotation[] annotations = modifierList.getAnnotations();
            Boolean isHasDataFlag = Boolean.FALSE;
            Boolean isHasNoArgsConstructoryFlag = Boolean.FALSE;
            if (annotations != null && annotations.length > 0) {
                for (PsiAnnotation annotation : annotations) {
                    if (!isHasNoArgsConstructoryFlag && annotation.getText().contains(Constant.noArgsConstructorAnnotation)) {
                        isHasNoArgsConstructoryFlag = Boolean.TRUE;
                    }

                    if (!isHasDataFlag && annotation.getText().contains(Constant.dataAnnotation)) {
                        isHasDataFlag = Boolean.TRUE;
                    }
                }
            }

            if (!isHasNoArgsConstructoryFlag) {
                String tabName = "";
                if (generateClass.getName().contains("Entity")) {
                    tabName = generateClass.getName().substring(0, generateClass.getName().lastIndexOf("Entity"));
                } else {
                    tabName = generateClass.getName();
                }
                PsiAnnotation annotationFromText = factory.createAnnotationFromText("@Entity(nameInDb = \"" + tabName + "Tab\")", generateClass);
                modifierList.addBefore(annotationFromText, firstChild);
            }

        }


    }


    protected void injectLombokAnnotation(PsiElementFactory factory, PsiClass generateClass) {
        if (factory == null || generateClass == null) {
            return;
        }

        PsiModifierList modifierList = generateClass.getModifierList();
        if (modifierList != null) {
            //添加类注解
            PsiElement firstChild = modifierList.getFirstChild();
            PsiAnnotation[] annotations = modifierList.getAnnotations();
            Boolean isHasDataFlag = Boolean.FALSE;
            Boolean isHasNoArgsConstructoryFlag = Boolean.FALSE;
            if (annotations != null && annotations.length > 0) {
                for (PsiAnnotation annotation : annotations) {
                    if (!isHasNoArgsConstructoryFlag && annotation.getText().contains(Constant.noArgsConstructorAnnotation)) {
                        isHasNoArgsConstructoryFlag = Boolean.TRUE;
                    }

                    if (!isHasDataFlag && annotation.getText().contains(Constant.dataAnnotation)) {
                        isHasDataFlag = Boolean.TRUE;
                    }
                }
            }

            if (!isHasNoArgsConstructoryFlag) {
                PsiAnnotation annotationFromText = factory.createAnnotationFromText("@lombok.NoArgsConstructor", generateClass);
                modifierList.addBefore(annotationFromText, firstChild);
            }
            if (!isHasDataFlag) {
                PsiAnnotation annotationFromText = factory.createAnnotationFromText("@lombok.Data", generateClass);
                modifierList.addBefore(annotationFromText, firstChild);
            }
        }
    }


    @Override
    public void process(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {
        mainPackage = PsiClassUtil.getPackage(cls);
        onStarProcess(classEntity, factory, cls, visitor);
        for (FieldEntity fieldEntity : classEntity.getFields()) {
            generateField(factory, fieldEntity, cls, classEntity);
        }
        for (ClassEntity innerClass : classEntity.getInnerClasses()) {
            generateClass(factory, innerClass, cls, visitor);
        }
        generateConstructor(classEntity, factory, cls);
        generateGetterAndSetter(classEntity, factory, cls); // 生成get、set方法
//        generateConvertMethod(factory, cls, classEntity); // 生成转换方法
        onEndProcess(classEntity, factory, cls, visitor);
    }


    /**
     * 生成字段
     *
     * @param factory
     * @param fieldEntity
     * @param cls
     * @param classEntity
     */
    protected void generateField(PsiElementFactory factory, FieldEntity fieldEntity, PsiClass cls, ClassEntity classEntity) {

        if (fieldEntity.isGenerate()) {
            Try.run(new Try.TryListener() {
                @Override
                public void run() {
//                    factory.createAnnotationFromText(generateAnnotationText(classEntity, fieldEntity, null), cls);
                    PsiField field = factory.createFieldFromText(generateFieldText(classEntity, fieldEntity, null), cls);
                    if (Config.getInstant().isUseComment()) {
                        JavaDocUtils.addFieldComment(field, fieldEntity, factory);
                    }
                    cls.add(field);
                }

                @Override
                public void runAgain() {
                    //生成自定义名字
                    fieldEntity.setFieldName(FieldHelper.generateLuckyFieldName(fieldEntity.getFieldName()));
                    cls.add(factory.createFieldFromText(generateFieldText(classEntity, fieldEntity, Constant.FIXME), cls));
                }

                @Override
                public void error() {
                    cls.addBefore(factory.createCommentFromText("// FIXME generate failure  field " + fieldEntity.getFieldName(), cls), cls.getChildren()[0]);
                }
            });
        }
    }

    /**
     * 生成类
     *
     * @param factory
     * @param classEntity
     * @param parentClass
     * @param visitor
     */
    protected void generateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, IProcessor visitor) {
        onStartGenerateClass(factory, classEntity, parentClass, visitor);
        PsiClass generateClass = null;
        if (classEntity.isGenerate()) {
            //// TODO: 16/11/9  待重构
            if (Config.getInstant().isSplitGenerate()) {
                //单独生成子类
                try {
                    generateClass = PsiClassUtil.getPsiClass(parentClass.getContainingFile(), parentClass.getProject(), classEntity.getQualifiedName());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                //生成内部静态类
                String classContent = "public static class " + classEntity.getClassName() + "{}";
                generateClass = factory.createClassFromText(classContent, null).getInnerClasses()[0];
            }

            if (generateClass != null) {

                for (ClassEntity innerClass : classEntity.getInnerClasses()) {
                    generateClass(factory, innerClass, generateClass, visitor);
                }
                if (!Config.getInstant().isSplitGenerate()) {
                    generateClass = (PsiClass) parentClass.add(generateClass);
                }
                for (FieldEntity fieldEntity : classEntity.getFields()) {
                    generateField(factory, fieldEntity, generateClass, classEntity);
                }
                generateGetterAndSetter(classEntity, factory, generateClass);
                generateConvertMethod(factory, generateClass, classEntity);
            }
        }
        onEndGenerateClass(factory, classEntity, parentClass, generateClass, visitor);
        if (Config.getInstant().isSplitGenerate()) {
            formatJavCode(generateClass);
        }
    }

    /**
     * 生成构造函数
     *
     * @param factory
     * @param cls
     * @param classEntity
     */
    protected void generateConstructor(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
        cls.add(factory.createConstructor(cls.getName(), cls));
        String annGenerated = "    @Generated\n";
        StringBuilder constructorSb = new StringBuilder();
        if (cls.getName().contains("Entity")) {
            constructorSb.append(cls.getName() + "(");
        } else {
            constructorSb.append(cls.getName() + "Entity(");
        }
        // 构造函数的参数
        for (FieldEntity field : classEntity.getFields()) {
            String fieldName = field.getGenerateFieldName();
            String typeStr = field.getRealType();
            String variable = typeStr + " " + fieldName + ",";
            constructorSb.append(variable);
        }
        String subLastDouhao = constructorSb.substring(0, constructorSb.lastIndexOf(","));
        constructorSb.delete(0, constructorSb.length());
        constructorSb.append(subLastDouhao);
        constructorSb.append("){\n");
        constructorSb.append("\tsuper();\n");
        //构造函数的成员变量
        for (FieldEntity field : classEntity.getFields()) {
            String fieldName = field.getGenerateFieldName();
            String thisStr = ("\tthis." + fieldName + " = " + fieldName + ";\n");
            constructorSb.append(thisStr);
        }
        constructorSb.append("}");
        cls.add(factory.createMethodFromText(constructorSb.toString(), cls));
    }

    /**
     * 生成 Get 和 Set
     *
     * @param factory
     * @param cls
     * @param classEntity
     */
    protected void generateGetterAndSetter(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
        //使用Lombok无需生成Getter与Setter
//        if (Config.getInstant().isUseLombok()) {
//            return;
//        }
        if (Config.getInstant().isFieldPrivateMode()) {
            for (FieldEntity field : classEntity.getFields()) {
                createGetAndSetMethod(factory, cls, field);
            }
        }
    }


    protected void createGetAndSetMethod(PsiElementFactory factory, PsiClass cls, FieldEntity field) {
        if (field.isGenerate()) {
            String fieldName = field.getGenerateFieldName();
            String typeStr = field.getRealType();
            if (Config.getInstant().isUseFieldNamePrefix()) {
                String temp = fieldName.replaceAll("^" + Config.getInstant().getFiledNamePreFixStr(), "");
                if (!TextUtils.isEmpty(temp)) {
                    fieldName = temp;
                }
            }
            if (typeStr.equals("boolean")) {
                String method = "public ".concat(typeStr).concat("   is").concat(StringUtils.captureName(fieldName)).concat("() {   return ").concat(field.getGenerateFieldName()).concat(" ;} ");
                cls.add(factory.createMethodFromText(method, cls));
            } else {
                String method = "public ".concat(typeStr).concat("   get").concat(StringUtils.captureName(fieldName)).concat("() {   return ").concat(field.getGenerateFieldName()).concat(" ;} ");
                cls.add(factory.createMethodFromText(method, cls));
            }

            String arg = fieldName;
            if (Config.getInstant().isUseFieldNamePrefix()) {
                String temp = fieldName.replaceAll("^" + Config.getInstant().getFiledNamePreFixStr(), "");
                if (!TextUtils.isEmpty(temp)) {
                    fieldName = temp;
                    arg = fieldName;
                    if (arg.length() > 0) {

                        if (arg.length() > 1) {
                            arg = (arg.charAt(0) + "").toLowerCase() + arg.substring(1);
                        } else {
                            arg = arg.toLowerCase();
                        }
                    }
                }
            }

            String method = "public void  set".concat(StringUtils.captureName(fieldName)).concat("( ").concat(typeStr).concat(" ").concat(arg).concat(") {   ");
            if (field.getGenerateFieldName().equals(arg)) {
                method = method.concat("this.").concat(field.getGenerateFieldName()).concat(" = ").concat(arg).concat(";} ");
            } else {
                method = method.concat(field.getGenerateFieldName()).concat(" = ").concat(arg).concat(";} ");
            }

            String finalMethod = method;
            String finalFieldName = fieldName;
            Try.run(new Try.TryListener() {
                @Override
                public void run() {
                    cls.add(factory.createMethodFromText(finalMethod, cls));
                }

                @Override
                public void runAgain() {
                    cls.addBefore(factory.createCommentFromText("// FIXME generate failure  method  set and get " + StringUtils.captureName(finalFieldName), cls), cls.getChildren()[0]);

                }

                @Override
                public void error() {

                }
            });
        }
    }


    private String generateAnnotationText(ClassEntity classEntity, FieldEntity fieldEntity, String fixme) {
        return "";
    }

    /**
     * 生成字段文本
     * FIXME: 2020/2/23 如果未知字段名如_$349634Bean则设置为Map<String,Entity>
     *
     * @param classEntity
     * @param fieldEntity
     * @param fixme
     * @return
     */
    private String generateFieldText(ClassEntity classEntity, FieldEntity fieldEntity, String fixme) {
        fixme = fixme == null ? "" : fixme;
        StringBuilder fieldSb = new StringBuilder();
        String filedName = fieldEntity.getGenerateFieldName();
        if (!TextUtils.isEmpty(classEntity.getExtra())) {
            fieldSb.append(classEntity.getExtra()).append("\n");
            classEntity.setExtra(null);
        }
        if (fieldEntity.getTargetClass() != null) {
            fieldEntity.getTargetClass().setGenerate(true);
        }
        //添加字段序列化注解
        if (!isNumberKeyFieldAsMap(fieldEntity) && (filedName.equals(fieldEntity.getKey())) || Config.getInstant().isUseAnnotation()) {
            String nameInDb = PsiClassUtil.generatorDataNameInDb(fieldEntity.getKey());
            fieldSb.append(Config.getInstant().geFullNameAnnotation().replaceAll("\\{filed\\}", nameInDb));
        }
        //添加字段类型与名称
        if (Config.getInstant().isFieldPrivateMode()) {
            fieldSb.append("private ").append(fieldEntity.getFullNameType()).append(" ").append(filedName).append(";");
        } else {
            fieldSb.append("public ").append(fieldEntity.getFullNameType()).append(" ").append(filedName).append(";");
        }
        String fieldText = fieldSb.toString().concat(fixme);


        if (isNumberKeyFieldAsMap(fieldEntity)) {
            fieldText = fieldText.replace(fieldEntity.getFullNameType(), "java.util.Map<String," + fieldEntity.getFullNameType() + ">");
        }
        return fieldText;
    }

    /**
     * 未知字段名如_$349634Bean则修改为Map<String,_$349634Bean>
     *
     * @param fieldEntity
     * @return
     */
    private boolean isNumberKeyFieldAsMap(FieldEntity fieldEntity) {
        return Config.getInstant().isUseNumberKeyAsMap() && fieldEntity.getFullNameType().startsWith("_$");
    }


    protected void generateConvertMethod(PsiElementFactory factory, PsiClass cls, ClassEntity classEntity) {
        if (cls == null || cls.getName() == null) {
            return;
        }

    }

    public boolean isRightDir(ClassEntity classEntity) {
        logger.debug(classEntity.getPackName());
        return false;
    }


}

