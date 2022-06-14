package plugin.elliot.greendaocodegenerator.ui;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import plugin.elliot.greendaocodegenerator.ConvertBridge;
import plugin.elliot.greendaocodegenerator.DataWriter;
import plugin.elliot.greendaocodegenerator.config.Config;
import plugin.elliot.greendaocodegenerator.common.PsiClassUtil;
import plugin.elliot.greendaocodegenerator.common.StringUtils;
import plugin.elliot.greendaocodegenerator.entity.ClassEntity;
import plugin.elliot.greendaocodegenerator.entity.FieldEntity;
import plugin.elliot.greendaocodegenerator.tools.checktreetable.FiledTreeTableModel;
import plugin.elliot.greendaocodegenerator.tools.ux.CheckTreeTableManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class FieldsDialog extends JDialog {



    private JPanel contentPane;
    private JLabel generateClass;
    private JScrollPane fieldsSP;
    private JButton okBtn;
    private JButton cancelBtn;
    private JButton previousBtn;


    private ConvertBridge.Operator operator;
    private PsiElementFactory factory;
    private PsiClass aClass;
    private PsiFile file;
    private Project project;
    private PsiClass psiClass;

    private String generateClassStr;
    private ArrayList<DefaultMutableTreeTableNode> defaultMutableTreeTableNodeList;
    private ClassEntity classEntity;
    public FieldsDialog(){

    }
    public FieldsDialog(ConvertBridge.Operator operator, ClassEntity classEntity,
                        PsiElementFactory factory, PsiClass psiClass, PsiClass aClass, PsiFile file, Project project
            , String generateClassStr) {
        this.operator = operator;
        this.factory = factory;
        this.aClass = aClass;
        this.file = file;
        this.project = project;
        this.psiClass = psiClass;
        this.generateClassStr = generateClassStr;
        initView(classEntity);
        initListener();
    }

    private void initView(ClassEntity classEntity) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(okBtn);
        setTitle("Field Edit");
        this.setAlwaysOnTop(true);
        this.classEntity = classEntity;
        defaultMutableTreeTableNodeList = new ArrayList<DefaultMutableTreeTableNode>();
        JXTreeTable treetable = new JXTreeTable(new FiledTreeTableModel(createData(classEntity)));
        CheckTreeTableManager manager = new CheckTreeTableManager(treetable);
        manager.getSelectionModel().addPathsByNodes(defaultMutableTreeTableNodeList);
        treetable.getColumnModel().getColumn(0).setPreferredWidth(150);
//        treetable.setSelectionBackground(treetable.getBackground());
        treetable.expandAll();
        treetable.setCellSelectionEnabled(false);
        final DefaultListSelectionModel defaultListSelectionModel = new DefaultListSelectionModel();
        treetable.setSelectionModel(defaultListSelectionModel);

        defaultListSelectionModel.setSelectionMode(SINGLE_SELECTION);
        defaultListSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                defaultListSelectionModel.clearSelection();
            }
        });
        defaultMutableTreeTableNodeList = null;
        treetable.setRowHeight(30);
        fieldsSP.setViewportView(treetable);
        generateClass.setText(generateClassStr);
    }

    private void initListener() {
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
        this.setAlwaysOnTop(false);

        WriteCommandAction.runWriteCommandAction(project, new Runnable() {

            @Override
            public void run() {
                if (psiClass == null) {
                    try {
                        psiClass = PsiClassUtil.getPsiClass(file, project, generateClassStr);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        operator.showError(ConvertBridge.Error.DATA_ERROR);
                        Writer writer = new StringWriter();
                        PrintWriter printWriter = new PrintWriter(writer);
                        throwable.printStackTrace(printWriter);
                        printWriter.close();
                        operator.setErrorInfo(writer.toString());
                        operator.setVisible(true);
                        operator.showError(ConvertBridge.Error.PATH_ERROR);
                    }
                }

                if (psiClass != null) {
                    String[] arg = generateClassStr.split("\\.");
                    if (arg.length > 1) {
                        Config.getInstant().setEntityPackName(generateClassStr.substring(0, generateClassStr.length() - arg[arg.length - 1].length()));
                        Config.getInstant().save();
                    }
                    try {
                        setVisible(false);
                        DataWriter dataWriter = new DataWriter(file, project, psiClass);
                        dataWriter.execute(classEntity);
                        Config.getInstant().saveCurrentPackPath(StringUtils.getPackage(generateClassStr));
                        operator.dispose();
                        dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                        operator.showError(ConvertBridge.Error.PARSE_ERROR);
                        Writer writer = new StringWriter();
                        PrintWriter printWriter = new PrintWriter(writer);
                        e.printStackTrace(printWriter);
                        printWriter.close();
                        operator.setErrorInfo(writer.toString());
                        operator.setVisible(true);
                        dispose();
                    }
                }
            }
        });
    }



    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


    private DefaultMutableTreeTableNode createData(ClassEntity classEntity) {
        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode(classEntity);
        createDataNode(root, classEntity);
        return root;
    }

    private void createDataNode(DefaultMutableTreeTableNode root, ClassEntity innerClassEntity) {
        for (FieldEntity field : innerClassEntity.getFields()) {
            DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(field);
            root.add(node);
            defaultMutableTreeTableNodeList.add(node);
        }
    }

    public static void showDlg() {
        FieldsDialog dialog = new FieldsDialog();
        dialog.setSize(1080, 512);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
