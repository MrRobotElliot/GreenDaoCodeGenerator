package plugin.elliot.greendaocodegenerator.config;

import com.intellij.openapi.project.Project;

/**
 * Created by Elliot on 15/5/31.
 */
public class Constant {

    public static String sInstanceType = "Entity";
    public static Project sProject = null;
    public static final String DEFAULT_PREFIX = "_$";
    public static String FIXME = "// FIXME check this code";

    public static final String privateStr = "   private String name;\n" + "\n" + "    public void setName(String name){\n" + "        this.name=name;\n" + "    }\n" + "\n" + "    public String getName(){\n" + "        return name;\n" + "    }";
    public static final String publicStr = "    public String name;";


    public static final String autoValueMethodTemplate = "public static com.google.gson.TypeAdapter<$className$> typeAdapter(com.google.gson.Gson gson)" + " {\n" + "    return new AutoValue_$AdapterClassName$.GsonTypeAdapter(gson);\n" + "}";

    public static final String greenDaoAnnotation = "@org.greenrobot.greendao.annotation.Property\\s*\\(\\s*\"{filed}\"\\s*\\)";

    public static final String greenDaoFullNameAnnotation = "@org.greenrobot.greendao.annotation.Property(nameInDb =\"{filed}\")";

    public static final String roomAnnotation = "Room";
    public static final String roomFullNameAnnotation = "@Room(\"{filed}\")";

    public static final String jsonIgnoreAnnotation = "@JsonIgnoreProperties";

    public static final String dataAnnotation = "@Data";

    public static final String noArgsConstructorAnnotation = "@NoArgsConstructor";

    public static final String loganSquareFullNameAnnotation = "@com.bluelinelabs.logansquare.annotation.JsonField(name=\"{filed}\")";

}
