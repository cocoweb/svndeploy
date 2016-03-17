package com.foresee.xdeploy.win;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import com.foresee.xdeploy.ToFileHelper;
import com.foresee.xdeploy.utils.ConsoleTextArea;

public class Mainwin {
    //static Logger log  = Logger.getLogger("日志输出");

    private JFrame frame;
    private JTable table;

    static ToFileHelper listTofileHelper =null;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        showForm();
    }
    
    public static void showForm(){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Mainwin window = new Mainwin();
                    listTofileHelper =new ToFileHelper();
                    window.frame.setVisible(true);
                    //listTofileHelper.pv.getProperty("").equals("true");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
    }

    /**
     * Create the application.
     */
    public Mainwin() {
        try {
            initialize();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void disableButton(JButton... btns){
        for(JButton btn : btns){
            btn.setEnabled(false);
        }
        
        
    }
    
    private void enableButton(JButton... btns){
        for(JButton btn : btns){
            btn.setEnabled(true);
        }
        
    }

    /**
     * Initialize the contents of the frame.
     * @throws IOException 
     */
    private void initialize() throws IOException {
        frame = new JFrame();
        frame.setTitle("版本清单工具");
        frame.setBounds(100, 100, 762, 545);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(5, 5));
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        JScrollPane scrollPane = new JScrollPane();
        tabbedPane.addTab("日志输出", null, scrollPane, null);
        tabbedPane.setDisplayedMnemonicIndexAt(0, 1);
        tabbedPane.setEnabledAt(0, true);
        scrollPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        final ConsoleTextArea textArea = new ConsoleTextArea();
        textArea.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
            }
        });
        scrollPane.setViewportView(textArea);
        textArea.setEditable(false);
        
        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("参数显示", null, panel_1, null);
        tabbedPane.setDisplayedMnemonicIndexAt(1, 2);
        tabbedPane.setEnabledAt(1, true);
        panel_1.setLayout(new BorderLayout(0, 0));
        
        table = new JTable();
        table.setModel(new DefaultTableModel(
            new Object[][] {
            },
            new String[] {
                "New column", "New column"
            }
        ));
        panel_1.add(table);
        
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        
        JPanel panel_3 = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) panel_3.getLayout();
        flowLayout_1.setAlignment(FlowLayout.LEFT);
        
        final JButton btnNewButton = new JButton("List 输出清单");
        panel_3.add(btnNewButton);
        
        final JButton btnNewButton_1 = new JButton("从清单导出文件");
        panel_3.add(btnNewButton_1);
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disableButton(btnNewButton,btnNewButton_1);
                listTofileHelper.scanSvnToPath();
                enableButton(btnNewButton,btnNewButton_1);
            }
        });
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disableButton(btnNewButton,btnNewButton_1);
                listTofileHelper.scanPrintList();
                enableButton(btnNewButton,btnNewButton_1);
            }
        });
        
        JPanel panel_4 = new JPanel();
        FlowLayout flowLayout_2 = (FlowLayout) panel_4.getLayout();
        flowLayout_2.setAlignment(FlowLayout.RIGHT);
        
        JButton btnNewButton_2 = new JButton("退出");
        panel_4.add(btnNewButton_2);
        
        JPanel panel_5 = new JPanel();
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addComponent(panel_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(panel_5, GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE))
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
                    .addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(panel_5, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                        .addComponent(panel_3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel_4, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.X_AXIS));
        
        final JLabel lblMessage = new JLabel("");
        lblMessage.setVerticalAlignment(SwingConstants.BOTTOM);
        panel_5.add(lblMessage);
        
        final JCheckBox chckbxNewCheckBox = new JCheckBox("输出日志文件");
        chckbxNewCheckBox.setEnabled(false);
        chckbxNewCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                textArea.setLogEnabled(chckbxNewCheckBox.isSelected());
            }
        });
        chckbxNewCheckBox.setSelected(true);
        panel_5.add(chckbxNewCheckBox);
        panel.setLayout(gl_panel);
        btnNewButton_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispatchEvent(new WindowEvent(frame,WindowEvent.WINDOW_CLOSING) );
            }
        });
        //textArea.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        //textArea.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        JPanel panel_2 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
        flowLayout.setVgap(10);
        flowLayout.setHgap(10);
        frame.getContentPane().add(panel_2, BorderLayout.SOUTH);
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //log.info(textArea.getText()+"\n");

                System.exit(0);
            }
            @Override
            public void windowActivated(WindowEvent e) {
                
                lblMessage.setText("");
                
                
            }
        });

    }
}
