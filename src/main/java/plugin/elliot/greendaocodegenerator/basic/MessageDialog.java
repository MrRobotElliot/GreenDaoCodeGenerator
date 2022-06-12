package plugin.elliot.greendaocodegenerator.basic;

import javax.swing.*;
import java.awt.event.*;

public class MessageDialog extends JDialog {
    private JPanel contentPane;
    private JTextPane contentTP;
    private JButton okBtn;
    private JButton cancelBtn;


    private String content = "";

    public MessageDialog(String content) {
        this.content = content;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(okBtn);
        initView();
        initListener();
    }

    private void initView() {
        contentTP.setAlignmentX(CENTER_ALIGNMENT);
        contentTP.setAlignmentY(CENTER_ALIGNMENT);
        contentTP.setText(content);
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

    public static void showDlg(String content) {
        MessageDialog dialog = new MessageDialog(content);
        dialog.setLocationRelativeTo(null);
        dialog.setSize(400, 150);
        dialog.setVisible(true);
    }
}
