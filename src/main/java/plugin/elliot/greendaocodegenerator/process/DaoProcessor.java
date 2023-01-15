package plugin.elliot.greendaocodegenerator.process;

import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugin.elliot.greendaocodegenerator.common.PsiClassUtil;
import plugin.elliot.greendaocodegenerator.common.StringUtils;
import plugin.elliot.greendaocodegenerator.config.Constant;
import plugin.elliot.greendaocodegenerator.entity.ClassEntity;
import plugin.elliot.greendaocodegenerator.entity.FieldEntity;

public class DaoProcessor extends Processor {
    private static final Logger logger = LoggerFactory.getLogger(DaoProcessor.class);

    private String entityName;

    @Override
    void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {
        addImport(factory, cls);
        String name = cls.getName();
        entityName = name.replaceAll("Dao", "") + "Entity";
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
        onStarProcess(classEntity, factory, cls, visitor);
        createExtendClass(factory, cls);
        generateConstructor(factory, cls);
        generateField(classEntity, factory, cls);
        generateMemberMethod(classEntity, factory, cls);
    }

    /**
     * 导包
     *
     * @param factory
     * @param cls
     */
    private static void addImport(PsiElementFactory factory, PsiClass cls) {
        PsiClassUtil.addImport(factory, cls, "org.greenrobot.greendao.AbstractDao");
        PsiClassUtil.addImport(factory, cls, "org.greenrobot.greendao.annotation.Property");
        PsiClassUtil.addImport(factory, cls, "org.greenrobot.greendao.internal.DaoConfig");
        PsiClassUtil.addImport(factory, cls, "android.database.Cursor");
        PsiClassUtil.addImport(factory, cls, "org.greenrobot.greendao.database.DatabaseStatement");
        PsiClassUtil.addImport(factory, cls, "android.database.sqlite.SQLiteStatement");
//        PsiClassUtil.addImport(factory, cls, "cn.com.hzb.cmmp.database.manager.DaoSession");
    }


    /**
     * 添加继承类
     *
     * @param factory
     * @param cls
     */
    private void createExtendClass(PsiElementFactory factory, PsiClass cls) {
        String name = cls.getName();
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
            fieldSB.append("public final static Property ").append(StringUtils.capitalLetter(fieldEntity.getFieldName()) + " = new Property(").append(i).append(",").append(fieldEntity.getType()).append(".class,").append("\"").append(fieldEntity.getFieldName()).append("\",").append(fieldEntity.getFieldName().equals("id") ? "true," : "false,").append("\"").append(PsiClassUtil.generatorDataNameInDb(fieldEntity.getFieldName())).append("\");\n");
        }
        classSB.append(fieldSB);
        classSB.append("}");
        cls.add(factory.createClassFromText(classSB.toString(), null).getInnerClasses()[0]);
    }

    /**
     * 生成构造函数
     *
     * @param factory
     * @param cls
     */
    protected void generateConstructor(PsiElementFactory factory, PsiClass cls) {
        StringBuilder constructor01Sb = new StringBuilder();
        appendConstruterMethodName(cls, constructor01Sb);
        constructor01Sb.append("DaoConfig config) {").append(" super(config);").append("}");
        cls.add(factory.createMethodFromText(constructor01Sb.toString(), cls));

        StringBuilder constructor02Sb = new StringBuilder();
        appendConstruterMethodName(cls, constructor02Sb);
        constructor02Sb.append("DaoConfig config, DaoSession daoSession) {").append("super(config, daoSession);").append("}");
        cls.add(factory.createMethodFromText(constructor02Sb.toString(), cls));
    }

    private void appendConstruterMethodName(PsiClass cls, StringBuilder constructorSb) {
        if (cls.getName().contains("Dao")) {
            constructorSb.append(cls.getName() + "(");
        } else {
            constructorSb.append(cls.getName() + "Dao(");
        }
    }

    /**
     * 生成成员方法
     *
     * @param factory
     * @param cls
     */
    private void generateMemberMethod(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
        generateMethodOfCreateTable(classEntity, factory, cls);
        generateMethodOfDropTable(factory, cls);
        generateMethodOfReadKey(factory, cls);
        generateMethodOfReadEntity(classEntity, factory, cls);
        generateMethodOfBindValuesDb(classEntity, factory, cls);
        generateMethodOfBindValuesSQL(classEntity, factory, cls);
        generateMethodOfUpdateKeyAfterInsert(factory, cls);
        generateMethodOfGetKey(factory, cls);
        generateMethodOfHasKey(factory, cls);
        generateMethodOfIsEntityUpdateable(factory, cls);

    }

    /**
     * 生成createTable函数
     *
     * @param factory
     * @param cls
     */
    private void generateMethodOfCreateTable(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
        StringBuilder createTableSb = new StringBuilder();
        createTableSb.append("public static void createTable(Database db, boolean ifNotExists) {");
        createTableSb.append("String constraint = ifNotExists ? \"IF NOT EXISTS \" : \"\";");
        createTableSb.append("final String CREATE_TABLE = \"CREATE TABLE \" + constraint + TABLENAME + \" (\"\n");
        for (FieldEntity fieldEntity : classEntity.getFields()) {
            if (fieldEntity.getFieldName().equals("id")) {
                createTableSb.append("+ \"ID INTEGER PRIMARY KEY AUTOINCREMENT,  \"\n");
            } else {
                createTableSb.append("+ \"" + PsiClassUtil.generatorDataNameInDb(fieldEntity.getFieldName()) + " " + PsiClassUtil.exchangTypeOfDataFromJson(fieldEntity.getType()) + ",\"\n");
            }
        }
        createTableSb.delete(createTableSb.length() - 3, createTableSb.length());
        createTableSb.append("\"\n");
        createTableSb.append("+ \")\";");
        createTableSb.append("db.execSQL(CREATE_TABLE);");
        createTableSb.append("}");
        cls.add(factory.createMethodFromText(createTableSb.toString(), cls));
    }

    /**
     * 生成DropTable函数
     *
     * @param factory
     * @param cls
     */
    private void generateMethodOfDropTable(PsiElementFactory factory, PsiClass cls) {
        StringBuilder dropTableSb = new StringBuilder();
        dropTableSb.append("public static void dropTable(Database db, boolean ifExists) {");
        dropTableSb.append("String sql = \"DROP TABLE \" + (ifExists ? \"IF EXISTS \" : \"\") + TABLENAME;");
        dropTableSb.append("db.execSQL(sql);");
        dropTableSb.append("}");
        cls.add(factory.createMethodFromText(dropTableSb.toString(), cls));
    }


    /**
     * 生成ReadKey函数
     *
     * @param factory
     * @param cls
     */
    private void generateMethodOfReadKey(PsiElementFactory factory, PsiClass cls) {
        StringBuilder readKeySb = new StringBuilder();
        readKeySb.append("@Override");
        readKeySb.append(" protected Long readKey(Cursor cursor, int offset) {");
        readKeySb.append("return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);");
        readKeySb.append("}");
        cls.add(factory.createMethodFromText(readKeySb.toString(), cls));
    }


    /**
     * 生成ReadEntity函数
     *
     * @param factory
     * @param cls
     */
    private void generateMethodOfReadEntity(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
        StringBuilder readEntitySb = new StringBuilder();
        readEntitySb.append("@Override\n");
        readEntitySb.append("protected void readEntity(Cursor cursor, " + entityName + " entity, int offset) {");
        for (int i = 0; i < classEntity.getFields().size(); i++) {
            FieldEntity fieldEntity = classEntity.getFields().get(i);
            readEntitySb.append(" entity.set" + StringUtils.capitalLetter(fieldEntity.getFieldName()) + "(cursor.isNull(offset + " + i + ") ? null : cursor.get" + fieldEntity.getType() + "(offset + " + i + "));");
        }
        readEntitySb.append("}");
        cls.add(factory.createMethodFromText(readEntitySb.toString(), cls));
    }

    /**
     * 生成BindValuesDb函数
     *
     * @param classEntity
     * @param factory
     * @param cls
     */
    private void generateMethodOfBindValuesDb(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
        StringBuilder bindValueSb = new StringBuilder();
        bindValueSb.append("@Override\n");
        bindValueSb.append("protected void bindValues(DatabaseStatement stmt, " + entityName + " entity) {");
        for (int i = 0; i < classEntity.getFields().size(); i++) {
            FieldEntity fieldEntity = classEntity.getFields().get(i);
            bindValueSb.append(fieldEntity.getType() + " " + fieldEntity.getFieldName() + " = entity.get" + StringUtils.capitalLetter(fieldEntity.getFieldName()) + "();");
            bindValueSb.append("if (" + fieldEntity.getFieldName() + " != null) {");
            int index = i + 1;
            String bindType = "";
            if (fieldEntity.getType().equals("Integer")) {
                bindType = "Long";
            } else {
                bindType = fieldEntity.getType();
            }
            bindValueSb.append("stmt.bind" + bindType + "(" + index + ", " + fieldEntity.getFieldName() + ");");
            bindValueSb.append("}");
        }
        bindValueSb.append("}");
        cls.add(factory.createMethodFromText(bindValueSb.toString(), cls));
    }

    /**
     * 生成BindValuesSQL函数
     *
     * @param classEntity
     * @param factory
     * @param cls
     */
    private void generateMethodOfBindValuesSQL(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
        StringBuilder bindValueSb = new StringBuilder();
        bindValueSb.append("@Override\n");
        bindValueSb.append("protected void bindValues(SQLiteStatement stmt, " + entityName + " entity) {");
        for (int i = 0; i < classEntity.getFields().size(); i++) {
            FieldEntity fieldEntity = classEntity.getFields().get(i);

            bindValueSb.append(fieldEntity.getType() + " " + fieldEntity.getFieldName() + " = entity.get" + StringUtils.capitalLetter(fieldEntity.getFieldName()) + "();");
            bindValueSb.append("if (" + fieldEntity.getFieldName() + " != null) {");
            int index = i + 1;
            String bindType = "";
            if (fieldEntity.getType().equals("Integer")) {
                bindType = "Long";
            } else {
                bindType = fieldEntity.getType();
            }
            bindValueSb.append("stmt.bind" + bindType + "(" + index + ", " + fieldEntity.getFieldName() + ");");
            bindValueSb.append("}");
        }
        bindValueSb.append("}");
        cls.add(factory.createMethodFromText(bindValueSb.toString(), cls));
    }

    /**
     * 生成UpdateKeyAfterInsert函数
     *
     * @param factory
     * @param cls
     */
    private void generateMethodOfUpdateKeyAfterInsert(PsiElementFactory factory, PsiClass cls) {
        StringBuilder updateKeyAfterInsertSb = new StringBuilder();
        updateKeyAfterInsertSb.append("@Override\n");
        updateKeyAfterInsertSb.append("protected Long updateKeyAfterInsert(" + entityName + " entity, long rowId) {");
        updateKeyAfterInsertSb.append("entity.setId(rowId);");
        updateKeyAfterInsertSb.append("return rowId;");
        updateKeyAfterInsertSb.append("}");
        cls.add(factory.createMethodFromText(updateKeyAfterInsertSb.toString(), cls));
    }

    /**
     * 生成GetKey函数
     *
     * @param factory
     * @param cls
     */
    private void generateMethodOfGetKey(PsiElementFactory factory, PsiClass cls) {
        StringBuilder getKeySb = new StringBuilder();
        getKeySb.append("@Override\n");
        getKeySb.append("protected Long getKey(" + entityName + " entity){");
        getKeySb.append("return entity != null ? entity.getId() : null;");
        getKeySb.append("}");
        cls.add(factory.createMethodFromText(getKeySb.toString(), cls));
    }

    /**
     * 生成HasKey函数
     *
     * @param factory
     * @param cls
     */
    private void generateMethodOfHasKey(PsiElementFactory factory, PsiClass cls) {
        StringBuilder hasKeySb = new StringBuilder();
        hasKeySb.append("@Override\n");
        hasKeySb.append("protected boolean hasKey(" + entityName + " entity) {");
        hasKeySb.append("return entity.getId() != null;");
        hasKeySb.append("}");
        cls.add(factory.createMethodFromText(hasKeySb.toString(), cls));
    }

    private void generateMethodOfIsEntityUpdateable(PsiElementFactory factory, PsiClass cls) {
        StringBuilder isEntityUpdateableSb = new StringBuilder();
        isEntityUpdateableSb.append("@Override\n");
        isEntityUpdateableSb.append("protected boolean isEntityUpdateable() {");
        isEntityUpdateableSb.append("return true;");
        isEntityUpdateableSb.append("}");
        cls.add(factory.createMethodFromText(isEntityUpdateableSb.toString(), cls));
    }

}
