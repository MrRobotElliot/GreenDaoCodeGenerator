package plugin.elliot.greendaocodegenerator.ui.dialog;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import plugin.elliot.greendaocodegenerator.ConvertBridge;
import plugin.elliot.greendaocodegenerator.common.*;
import plugin.elliot.greendaocodegenerator.config.Config;
import plugin.elliot.greendaocodegenerator.config.Constant;
import plugin.elliot.greendaocodegenerator.entity.ClassEntity;
import plugin.elliot.greendaocodegenerator.entity.MoudelLibrary;
import plugin.elliot.greendaocodegenerator.tools.JSONException;
import plugin.elliot.greendaocodegenerator.tools.JSONObject;
import plugin.elliot.greendaocodegenerator.ui.NotificationCenter;
import plugin.elliot.greendaocodegenerator.ui.Toast;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;

import java.util.*;
import java.util.List;

public class JsonDialog extends JDialog implements ConvertBridge.Operator {


    private JPanel contentPane;
    private JTextField generateClassTF;
    private JComboBox instanceTypeCB;
    private JTextArea jsonTA;
    private JTextArea entityTA;
    private JButton formatBtn;
    private JButton settingBtn;
    private JButton okBtn;
    private JButton cancelBtn;
    private JButton previewBtn;
    private JPanel generateClassP;
    private JLabel errorLB;


    private String entityBody = "";
    private String constructor = "";
    private ClassEntity generateClassEntity = new ClassEntity();


    private PsiClass cls;
    private PsiFile file;
    private Project project;
    private String className = "";

    private String currentPkg = null;
    private String errorInfo = null;


    public JsonDialog(String className, PsiClass cls, PsiFile file, Project project) {
        this.className = className;
        this.cls = cls;
        this.file = file;
        this.project = project;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(okBtn);
        initView();
        initListener();
    }

    private void initView() {
        Border borders = BorderFactory.createLineBorder(Color.GRAY);
        jsonTA.setBorder(borders);
        entityTA.setBorder(borders);
        //获取当前Class

        currentPkg = ((PsiJavaFileImpl) file).getPackageName() + "." + file.getName().split("\\.")[0];
        generateClassTF.setText(currentPkg);
    }

    private void initListener() {
        formatBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formatJson();
            }
        });
        previewBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outPutEntity(jsonTA.getText());
                entityTA.setText(entityBody);
            }
        });
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        String jsonSTR = jsonTA.getText().trim();
        String jsonComment = "";
        getCurInstanceType();
        if (TextUtils.isEmpty(jsonSTR)) {
            return;
        }
        //生成类名
        String generateClassName = getGeneratePkgPath(generateClassTF.getText());
        if (TextUtils.isEmpty(generateClassName) || generateClassName.endsWith(".")) {
            Toast.make(project, generateClassP, MessageType.ERROR, "the path is not allowed");
            return;
        }
        PsiClass generateClass = null;
        if (!currentPkg.equals(generateClassName)) {
            generateClass = PsiClassUtil.exist(file, generateClassTF.getText());
        } else {
            generateClass = cls;
        }
        //执行转换
        new ConvertBridge(this, jsonSTR, jsonComment, file, project, generateClass, cls, generateClassName).run();
        setVisible(false);
    }

    private void getCurInstanceType() {
        switch (instanceTypeCB.getSelectedIndex()) {
            case 0:
                Constant.sInstanceType = MoudelLibrary.ENTITY.getType();
                break;
            case 1:
                Constant.sInstanceType = MoudelLibrary.DAO.getType();
                break;
        }
    }

    private String getGeneratePkgPath(String pkgName) {
        String generatePkgPath = pkgName.replaceAll(" ", "").replaceAll(".java$", "");
        return generatePkgPath;
    }


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void showDlg(String className, PsiClass cls, PsiFile file, Project project) {
        JsonDialog dialog = new JsonDialog(className, cls, file, project);
        dialog.setSize(1080, 512);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void formatJson() {
        String json = jsonTA.getText();
        json = json.trim();
        try {
            if (json.startsWith("{")) {
                plugin.elliot.greendaocodegenerator.tools.JSONObject jsonObject = new plugin.elliot.greendaocodegenerator.tools.JSONObject(json);
                String formatJson = jsonObject.toString(4);
                jsonTA.setText(formatJson);
            } else if (json.startsWith("[")) {
                plugin.elliot.greendaocodegenerator.tools.JSONArray jsonArray = new plugin.elliot.greendaocodegenerator.tools.JSONArray(json);
                String formatJson = jsonArray.toString(4);
                jsonTA.setText(formatJson);
            }
        } catch (JSONException jsonException) {
            try {
                String goodJson = JsonUtils.removeComment(json);
                String formatJson = JsonUtils.formatJson(goodJson);
                jsonTA.setText(formatJson);
            } catch (Exception exception) {
                exception.printStackTrace();
                NotificationCenter.sendNotificationForProject("json格式不正确，格式需要标准的json或者json5", NotificationType.ERROR,project);
                return;
            }
        }
    }


    private void outPutEntity(String json) {
        String annEntity = "@Entity(nameInDb = \"" + className + "Tab\"" + ")\n";
        entityBody += annEntity;
        entityBody += "public class " + className + "Entity { \n";
        entityBody += addIndentationAndNewline("@Id(autoincrement = true)") + addIndentationAndNewline("@Property(nameInDb = \"ID\")") + addIndentationAndNewline("private Long id;");
        Map<String, Object> jsonMap = getMapFromJson(json);
        jsonMap.forEach((key, value) -> {
            String nameInDbUp = generatorDataName(key);
            String annProperty = addIndentationAndNewline("@Property(nameInDb = \"" + nameInDbUp + "\")");
            String type = getDataType(value, key);
            String priVariable = addIndentationAndNewline("private " + type + " " + key); //  客户号
            entityBody += annProperty + priVariable;
        });
        setConstructor(currentPkg, jsonMap);
        setGetSetMethodes(jsonMap);
        entityBody += "}\n";
    }

    @NotNull
    private String generatorDataName(String fielName) {
        Boolean isHasUp = false;
        String nameInDbUp = "";
        int preIndex = 0;
        for (int i = 0; i < fielName.length(); i++) {
            String ch = fielName.substring(i, i + 1);
            if (ch == ch.toUpperCase()) {
                isHasUp = true;
                nameInDbUp += fielName.substring(preIndex, i);
                nameInDbUp = nameInDbUp.toUpperCase() + "_";
                preIndex = i;
            }
        }
        if (isHasUp) {
            nameInDbUp += fielName.substring(preIndex).toUpperCase();
        } else {
            nameInDbUp = fielName.toUpperCase();
        }
        return nameInDbUp;
    }

    String addIndentationAndNewline(String lineCode) {
        return "\t" + lineCode + "\n";
    }


    Map<String, Object> getMapFromJson(String str) {
        JSONObject obj = new JSONObject(str);
        Map<String, Object> map = new HashMap<>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    String getDataType(Object value, String key) {
        String type = "";
        if (value instanceof String) {
            type = "String";
        } else if (value instanceof Integer) {
            type = "Integer";
        } else if (value instanceof Long) {
            type = "BooleLongan";
        } else if (value instanceof Boolean) {
            type = "Boolean";
        } else if (value instanceof Array || value instanceof List || value instanceof JSONObject) {
            type = "List<" + key.substring(0, 1).toUpperCase() + key.substring(1) + ">";
        } else if (value instanceof Object || value instanceof JSONObject) {
            type = key.substring(0, 1).toUpperCase() + key.substring(1);
        }
        return type;
    }


    void setConstructor(String currentClass, Map<String, Object> jsonMap) {
        String annGenerated = "@Generated\n";
        constructor = annGenerated + addIndentationAndNewline("public " + currentClass + "Entity(");
        jsonMap.forEach((key, value) -> {
            String type = getDataType(value, key);
            String variable = addIndentationAndNewline("\t" + type + " " + key + ",");
            constructor += variable;
        });
        constructor = addIndentationAndNewline(constructor.substring(0, constructor.length() - 2) + "){");
        constructor += addIndentationAndNewline("\tsuper();");

        jsonMap.forEach((key, value) -> {
            String thisStr = addIndentationAndNewline("\tthis." + key + " = " + key + ";");
            constructor += thisStr;
        });
        constructor += addIndentationAndNewline("}");
        entityBody += constructor;
    }

    void setGetSetMethodes(Map<String, Object> jsonMap) {
        jsonMap.forEach((key, value) -> {
            String type = getDataType(value, key);
            String variable = key;
            String getMethod = addIndentationAndNewline("public " + type + " get" + variable.substring(0, 1).toUpperCase() + variable.substring(1) + "() { ") + addIndentationAndNewline("\treturn " + variable + ";") + addIndentationAndNewline("}");
            String setMethod = addIndentationAndNewline("public void set" + variable.substring(0, 1).toUpperCase() + variable.substring(1) + "(" + type + " " + variable + ")  {") + addIndentationAndNewline("\treturn this." + variable + " = " + variable + ";") + addIndentationAndNewline("}");
            entityBody += getMethod + setMethod;
        });


    }


    @Override
    public void showError(ConvertBridge.Error err) {
        switch (err) {
            case DATA_ERROR:
                errorLB.setText("data err !!");
                if (Config.getInstant().isToastError()) {
                    Toast.make(project, errorLB, MessageType.ERROR, "click to see details");
                }
                break;
            case PARSE_ERROR:
                errorLB.setText("parse err !!");
                if (Config.getInstant().isToastError()) {
                    Toast.make(project, errorLB, MessageType.ERROR, "click to see details");
                }
                break;
            case PATH_ERROR:
                Toast.make(project, generateClassP, MessageType.ERROR, "the path is not allowed");
                break;
            default:
                break;
        }
    }

    @Override
    public void setErrorInfo(String error) {
        errorInfo = error;
    }

    @Override
    public void cleanErrorInfo() {
        errorInfo = null;
    }


}
