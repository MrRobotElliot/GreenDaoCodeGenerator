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
    private JButton formatBtn;
    private JButton settingBtn;
    private JButton okBtn;
    private JButton cancelBtn;
    private JPanel generateClassP;
    private JLabel errorLB;


    private String entityBody = "";
    private String constructor = "";
    private ClassEntity generateClassEntity = new ClassEntity();


    private PsiClass cls;
    private PsiFile file;
    private String className = "";

    private String currentPkg = null;
    private String errorInfo = null;


    public JsonDialog(String className, PsiClass cls, PsiFile file) {
        this.className = className;
        this.cls = cls;
        this.file = file;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(okBtn);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        Border borders = BorderFactory.createLineBorder(Color.GRAY);
        jsonTA.setBorder(borders);
        //获取当前Class
        currentPkg = ((PsiJavaFileImpl) file).getPackageName() + "." + file.getName().split("\\.")[0];
        generateClassTF.setText(currentPkg);
    }

    private void initListener() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        formatBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formatJson();
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
        settingBtn.addActionListener(al -> {
            this.hide();
            DialogUtil.ShowSettingDlg(className, cls, file);
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void initData() {
        MoudelLibrary[] values = MoudelLibrary.values();
        for (MoudelLibrary it : values) {
            if (file.getName().contains(it.getType())) {
                DialogUtil.curFileType = it.getType();
                instanceTypeCB.setSelectedIndex(it.getIndex());
            }
        }
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
            Toast.make(Constant.sRootProject, generateClassP, MessageType.ERROR, "the path is not allowed");
            return;
        }
        PsiClass generateClass = null;
        if (!currentPkg.equals(generateClassName)) {
            generateClass = PsiClassUtil.exist(file, generateClassTF.getText());
        } else {
            generateClass = cls;
        }
        //执行转换
        new ConvertBridge(this, jsonSTR, jsonComment, file, Constant.sRootProject, generateClass, cls, generateClassName).run();
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
            case 2:
                Constant.sInstanceType = MoudelLibrary.DAO_MASTER.getType();
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

    public static void showDlg(String className, PsiClass cls, PsiFile file) {
        JsonDialog dialog = new JsonDialog(className, cls, file);
        dialog.setSize(1080, 512);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        DialogUtil.curFileDialog = dialog;
    }

    private void formatJson() {
        String json = jsonTA.getText();
        json = json.trim();
        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
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
                NotificationCenter.sendNotificationForProject("json格式不正确，格式需要标准的json或者json5", NotificationType.ERROR, Constant.sRootProject);
                return;
            }
        }
    }


    @Override
    public void showError(ConvertBridge.Error err) {
        switch (err) {
            case DATA_ERROR:
                errorLB.setText("data err !!");
                if (Config.getInstant().isToastError()) {
                    Toast.make(Constant.sRootProject, errorLB, MessageType.ERROR, "click to see details");
                }
                break;
            case PARSE_ERROR:
                errorLB.setText("parse err !!");
                if (Config.getInstant().isToastError()) {
                    Toast.make(Constant.sRootProject, errorLB, MessageType.ERROR, "click to see details");
                }
                break;
            case PATH_ERROR:
                Toast.make(Constant.sRootProject, generateClassP, MessageType.ERROR, "the path is not allowed");
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
