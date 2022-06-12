package plugin.elliot.greendaocodegenerator.json;

import javax.swing.*;
import java.awt.event.*;

public class DataTypeJudgDialog extends JDialog {
    private JPanel contentPane;
    private JButton okBtn;
    private JButton cancelBtn;
    private JTextPane keyTextPane;
    private JTextPane dataTypeTextPane;
    private JTextPane fieldNameTextPane;
    private JTextPane classNameTP;
    private JPanel ParamP;
    private JPanel OkCancelP;
    private JTextPane valueTP;
    private JPanel keyP;
    private JPanel valueP;
    private JPanel dateTypeP;
    private JPanel fieldNameP;

    public DataTypeJudgDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(okBtn);
        initView();
        initListener();
    }

    private void initView() {

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
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        DataTypeJudgDialog dialog = new DataTypeJudgDialog();
        dialog.setSize(1080, 512);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }


}
