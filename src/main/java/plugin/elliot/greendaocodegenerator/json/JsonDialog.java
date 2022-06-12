package plugin.elliot.greendaocodegenerator.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonDialog extends JDialog {
    private JPanel contentPane;
    private JTextField classNameTF;
    private JTextArea jsonTA;
    private JTextArea entityTA;
    private JButton formatBTN;
    private JButton settingBTN;
    private JButton okBTN;
    private JButton cancelBTN;
    private JButton previewBTN;


    private String className = "";
    private String entityBody = "";
    private String constructor = "";


    public JsonDialog(String fileName) {
        this.className = fileName;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(okBTN);
        initView();
        initListener();
    }

    private void initView() {
        Border borders = BorderFactory.createLineBorder(Color.GRAY);
        jsonTA.setBorder(borders);
        entityTA.setBorder(borders);
        classNameTF.setText(className);
    }

    private void initListener() {
        previewBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outPutEntity(jsonTA.getText());
                entityTA.setText(entityBody);
            }
        });
        okBTN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        cancelBTN.addActionListener(new ActionListener() {
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
        // add your code here

//        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void showDlg(String fileName) {
        JsonDialog dialog = new JsonDialog(fileName);
        dialog.setSize(1080, 512);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }


    private void outPutEntity(String json) {
        String annEntity = "@Entity(nameInDb = \"" + className + "Tab\"" + "+)";
        entityBody = "public class " + className + "Entity { \n";
        entityBody += addIndentationAndNewline("@Id(autoincrement = true)") + addIndentationAndNewline("@Property(nameInDb = \"ID\")") + addIndentationAndNewline("private Long id;");
        Map<String, Object> jsonMap = getMapFromJson(json);
        jsonMap.forEach((key, value) -> {
            Boolean isHasUp = false;
            String nameInDbUp = "";
            int preIndex = 0;
            for (int i = 0; i < key.length(); i++) {
                String ch = key.substring(i, i + 1);
                if (ch == ch.toUpperCase()) {
                    isHasUp = true;
                    nameInDbUp += key.substring(preIndex, i);
                    nameInDbUp = nameInDbUp.toUpperCase() + "_";
                    preIndex = i;
                }
            }
            if (isHasUp) {
                nameInDbUp += key.substring(preIndex).toUpperCase();
            } else {
                nameInDbUp = key.toUpperCase();
            }
            String annProperty = addIndentationAndNewline("@Property(nameInDb = \"" + nameInDbUp + "\")");
            String type = getDataType(value, key);
            String priVariable = addIndentationAndNewline("private " + type + " " + key); //  客户号
            entityBody += annProperty + priVariable;
        });
        setConstructor(className, jsonMap);
        setGetSetMethodes(jsonMap);
        entityBody += "}\n";
    }

    String addIndentationAndNewline(String lineCode) {
        return "\t" + lineCode + "\n";
    }


    Map<String, Object> getMapFromJson(String str) {
        JSONObject obj = JSON.parseObject(str);
        Map<String, Object> map = new HashMap<>();
        Iterator it = obj.entrySet().iterator();
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
        } else if (value instanceof Array || value instanceof List || value instanceof JSONArray) {
            type = "List<" + key.substring(0, 1).toUpperCase() + key.substring(1) + ">";
        } else if (value instanceof Object || value instanceof JSONObject) {
            type = key.substring(0, 1).toUpperCase() + key.substring(1);
        }
        return type;
    }


    void setConstructor(String className, Map<String, Object> jsonMap) {
        String annGenerated = "@Generated\n";
        constructor = annGenerated + addIndentationAndNewline("public " + className + "Entity(");
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


}
