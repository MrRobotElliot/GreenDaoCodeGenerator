package plugin.elliot.greendaocodegenerator.config;

import com.intellij.ide.util.PropertiesComponent;


/**
 * Created by Elliot on 15/5/31.
 */

public class Config {
    private static Config config;

    private boolean fieldPrivateMode = true;
    private boolean generateComments = false;
    /**
     * 是否使用注解
     */
    private boolean useAnnotation = true;

    private boolean arrayFromData = false;
    private boolean arrayFromData1 = false;
    private boolean reuseEntity = false;
    /**
     * 处女座模式
     */
    private boolean virgoMode = true;
    private boolean splitGenerate = false;


    private String arrayFromDataStr;
    private String arrayFromData1Str;

    /**
     * 注解语句
     */
    private String annotationStr;
    /**
     * 字段前缀
     */
    private String filedNamePreFixStr;
    /**
     * 创建实体类的包名.
     */
    private String entityPackName;
    private String suffixStr;

    /**
     * 错误次数,前两次提醒哪里查看错误日志.
     */
    private int errorCount;

    private boolean useFieldNamePrefix = true;

    /**
     * 是否使用包装类来替代基本类型
     */
    private boolean useWrapperClass = true;

    /**
     * 是否使用字段注释
     */
    private boolean useComment = false;

    /**
     * 是否使用Lombok
     */
    private boolean useLombok = true;

    /**
     * 是否使用数字键作为map
     */
    private boolean useNumberKeyAsMap = true;


    private Config() {

    }

    public void save() {

        PropertiesComponent.getInstance().setValue("fieldPrivateMode", isFieldPrivateMode() + "");

        PropertiesComponent.getInstance().setValue("arrayFromData", arrayFromData + "");
        PropertiesComponent.getInstance().setValue("arrayFromData1", arrayFromData1 + "");
        ;
        PropertiesComponent.getInstance().setValue("arrayFromData1Str", arrayFromData1Str + "");
        PropertiesComponent.getInstance().setValue("suffixStr", suffixStr + "");
        PropertiesComponent.getInstance().setValue("reuseEntity", reuseEntity + "");
        PropertiesComponent.getInstance().setValue("virgoMode", virgoMode + "");
        PropertiesComponent.getInstance().setValue("filedNamePreFixStr", filedNamePreFixStr + "");
        PropertiesComponent.getInstance().setValue("annotationStr", annotationStr + "");
        PropertiesComponent.getInstance().setValue("errorCount", errorCount + "");
        PropertiesComponent.getInstance().setValue("entityPackName", entityPackName + "");
        PropertiesComponent.getInstance().setValue("useFieldNamePrefix", useFieldNamePrefix + "");
        PropertiesComponent.getInstance().setValue("generateComments", generateComments + "");
        PropertiesComponent.getInstance().setValue("splitGenerate", splitGenerate + "");
        PropertiesComponent.getInstance().setValue("useWrapperClass", useWrapperClass + "");
        PropertiesComponent.getInstance().setValue("useComment", useComment + "");
        PropertiesComponent.getInstance().setValue("useLombok", useLombok + "");
        PropertiesComponent.getInstance().setValue("useNumberKeyAsMap", useNumberKeyAsMap + "");
    }

    public static Config getInstant() {

        if (config == null) {
            config = new Config();
            config.setFieldPrivateMode(PropertiesComponent.getInstance().getBoolean("fieldPrivateMode", true));
            config.setFieldPrivateMode(true);
            config.setArrayFromData(PropertiesComponent.getInstance().getBoolean("arrayFromData", false));
            config.setArrayFromData1(PropertiesComponent.getInstance().getBoolean("arrayFromData1", false));
            config.setSuffixStr(PropertiesComponent.getInstance().getValue("suffixStr", "DTO"));
            config.setReuseEntity(PropertiesComponent.getInstance().getBoolean("reuseEntity", false));
//            config.setAnnotationStr(PropertiesComponent.getInstance().getValue("annotationStr", Constant.greenDaoAnnotation));
            config.setAnnotationStr(Constant.greenDaoAnnotation);
            config.setEntityPackName(PropertiesComponent.getInstance().getValue("entityPackName"));
            config.setFiledNamePreFixStr(PropertiesComponent.getInstance().getValue("filedNamePreFixStr"));
            config.setErrorCount(PropertiesComponent.getInstance().getOrInitInt("errorCount", 0));
            config.setVirgoMode(PropertiesComponent.getInstance().getBoolean("virgoMode", true));
            config.setUseFieldNamePrefix(PropertiesComponent.getInstance().getBoolean("useFieldNamePrefix", false));
            config.setGenerateComments(PropertiesComponent.getInstance().getBoolean("generateComments", false));
            config.setSplitGenerate(PropertiesComponent.getInstance().getBoolean("splitGenerate", false));
            config.setUseWrapperClass(PropertiesComponent.getInstance().getBoolean("useWrapperClass", true));
            config.setUseComment(PropertiesComponent.getInstance().getBoolean("useComment", false));
            config.setUseLombok(PropertiesComponent.getInstance().getBoolean("useLombok", true));
            config.setUseNumberKeyAsMap(PropertiesComponent.getInstance().getBoolean("useNumberKeyAsMap", false));
        }
        return config;
    }

    public boolean isUseFieldNamePrefix() {
        return useFieldNamePrefix;
    }

    public void setUseFieldNamePrefix(boolean useFieldNamePrefix) {
        this.useFieldNamePrefix = useFieldNamePrefix;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public String getEntityPackName() {
        return entityPackName;
    }

    public String geFullNameAnnotation() {
        if (annotationStr.equals(Constant.greenDaoAnnotation)) {
            return Constant.greenDaoNameAnnotation;
        }

        if (annotationStr.equals(Constant.roomAnnotation)) {
            return Constant.roomFullNameAnnotation;
        }
        return annotationStr.replaceAll("\\(", "(").replaceAll("\\)", ")").replaceAll("\\s\\*", "");
    }

    public String geNameAnnotation(){
        if (annotationStr.equals(Constant.greenDaoAnnotation)) {
            return Constant.greenDaoNameAnnotation;
        }

        if (annotationStr.equals(Constant.roomAnnotation)) {
            return Constant.roomFullNameAnnotation;
        }
        return annotationStr.replaceAll("\\(", "(").replaceAll("\\)", ")").replaceAll("\\s\\*", "");

    }

    public boolean isGenerateComments() {
        return generateComments;
    }

    public void setGenerateComments(boolean generateComments) {
        this.generateComments = generateComments;
    }

    public void setEntityPackName(String entityPackName) {
        this.entityPackName = entityPackName;
    }

    public boolean isVirgoMode() {
        return virgoMode;
    }

    public void setVirgoMode(boolean virgoMode) {
        this.virgoMode = virgoMode;
    }

    public String getFiledNamePreFixStr() {
        return filedNamePreFixStr;
    }

    public void setFiledNamePreFixStr(String filedNamePreFixStr) {
        this.filedNamePreFixStr = filedNamePreFixStr;
    }

    public String getAnnotationStr() {
        return annotationStr;
    }

    public void setAnnotationStr(String annotationStr) {
        this.annotationStr = annotationStr;
    }


    public boolean isArrayFromData() {
        return arrayFromData;
    }

    public void setArrayFromData(boolean arrayFromData) {
        this.arrayFromData = arrayFromData;
    }

    public boolean isArrayFromData1() {
        return arrayFromData1;
    }

    public void setArrayFromData1(boolean arrayFromData1) {
        this.arrayFromData1 = arrayFromData1;
    }


    public void setArrayFromDataStr(String arrayFromDataStr) {
        this.arrayFromDataStr = arrayFromDataStr;
    }

    public void setArrayFromData1Str(String arrayFromData1Str) {
        this.arrayFromData1Str = arrayFromData1Str;
    }


    public String getArrayFromDataStr() {
        return arrayFromDataStr;
    }

    public String getArrayFromData1Str() {
        return arrayFromData1Str;
    }

    public String getSuffixStr() {
        return suffixStr;
    }

    public void setSuffixStr(String suffixStr) {
        this.suffixStr = suffixStr;
    }

    public boolean isReuseEntity() {
        return reuseEntity;
    }

    public void setReuseEntity(boolean reuseEntity) {
        this.reuseEntity = reuseEntity;
    }


    public boolean isFieldPrivateMode() {
        return fieldPrivateMode;
    }

    public void setFieldPrivateMode(boolean fieldPrivateMode) {
        this.fieldPrivateMode = fieldPrivateMode;
    }


    public void saveArrayFromDataStr(String arrayFromDataStr) {
        this.arrayFromDataStr = arrayFromDataStr;
        PropertiesComponent.getInstance().setValue("arrayFromDataStr", arrayFromDataStr + "");
    }

    public void saveArrayFromData1Str(String arrayFromData1Str) {
        this.arrayFromData1Str = arrayFromData1Str;
        PropertiesComponent.getInstance().setValue("arrayFromData1Str", arrayFromData1Str + "");
    }

    public boolean isToastError() {
        if (Config.getInstant().getErrorCount() < 3) {
            Config.getInstant().setErrorCount(Config.getInstant().getErrorCount() + 1);
            Config.getInstant().save();
            return true;
        }
        return false;
    }

    public boolean isSplitGenerate() {
        return splitGenerate;
    }

    public void setSplitGenerate(boolean splitGenerate) {
        this.splitGenerate = splitGenerate;
    }

    public void saveCurrentPackPath(String entityPackName) {
        if (entityPackName == null) {
            return;
        }
        setEntityPackName(entityPackName + ".");
        save();
    }

    public boolean isUseWrapperClass() {
        return useWrapperClass;
    }

    public void setUseWrapperClass(boolean useWrapperClass) {
        this.useWrapperClass = useWrapperClass;
    }

    public boolean isUseComment() {
        return useComment;
    }

    public void setUseComment(boolean useComment) {
        this.useComment = useComment;
    }

    public boolean isUseLombok() {
        return useLombok;
    }

    public void setUseLombok(boolean useLombok) {
        this.useLombok = useLombok;
    }

    public boolean isUseNumberKeyAsMap() {
        return useNumberKeyAsMap;
    }

    public void setUseNumberKeyAsMap(boolean useNumberKeyAsMap) {
        this.useNumberKeyAsMap = useNumberKeyAsMap;
    }

    public boolean isUseAnnotation() {
        return useAnnotation;
    }

    public void setUseAnnotation(boolean useAnnotation) {
        this.useAnnotation = useAnnotation;
    }
}
