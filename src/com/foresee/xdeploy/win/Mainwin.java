package com.foresee.xdeploy.win;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.tmatesoft.svn.core.SVNException;

import com.beust.jcommander.internal.Lists;
import com.foresee.xdeploy.ListToFileHelper;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.utils.ConsoleTextArea;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.svn.SVNRepo;
import com.foresee.xdeploy.utils.svn.SvnResource;

import javax.swing.JTextField;
import javax.swing.JScrollBar;
import java.awt.Component;
import javax.swing.Box;

public class Mainwin {
    //static Logger log  = Logger.getLogger("日志输出");

    private JFrame frame;
    private JTable table;
    JList listSvn;
    JList rightList;

    static ListToFileHelper listTofileHelper =null;
    private JTextField textField;
    private JTextField textField_1;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        showForm1();
    }
    
    public static void showForm1(){
    	Mainwin window = new Mainwin();
        listTofileHelper =new ListToFileHelper();
        
        
        
        
        window.frame.setVisible(true);
    }
    
    public static void showForm(){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Mainwin window = new Mainwin();
                    listTofileHelper =new ListToFileHelper();
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
    
    private DefaultListModel listToListModel(List<SvnResource> xlist){
    	DefaultListModel olist = new DefaultListModel();
    	for(SvnResource sr :xlist){
    		olist.addElement(sr.toString());
    		
    	}
    	
		return olist;
    	
    }
    
    List<SvnResource> llist;
    List<SvnResource> rlist=new ArrayList<SvnResource>();
    private JTextField textField_2;
    
    private void querySVNList(){
    	SVNRepo svnrepo = SVNRepo.getInstance();

        List<SvnResource> llist = svnrepo.LogPathList();
        
        System.out.println(llist);
        
        listSvn.setModel(listToListModel(llist));
        
        rightList.setModel(new DefaultListModel());
        

        
//        final DefaultListModel olist = new DefaultListModel();
//        olist.copyInto(alist.toArray());
        
        //listSvn.setListData(llist.toArray());
    }
    
    /**
     * 增加项
     */
//    private void addItem() {
//           if (field.getText() != null && !field.getText().equals("")) {
//                  ((DefaultListModel) leftList.getModel())
//                                .addElement(field.getText());
//                  field.setText("");
//           }
//    }

    /**
     * 左移项
     */
    private void leftItem() {
           while (rightList.getSelectedIndex() != -1) {
                  Object o = rightList.getSelectedValue();
                  ((DefaultListModel) rightList.getModel()).remove(rightList.getSelectedIndex());
                  ((DefaultListModel) listSvn.getModel()).addElement(o);
           }
    }

    /**
     * 右移项
     */
    private void rightItem() {
           while (listSvn.getSelectedIndex() != -1) {
                  Object o = listSvn.getSelectedValue();
                  ((DefaultListModel) listSvn.getModel()).remove(listSvn.getSelectedIndex());
                  ((DefaultListModel) rightList.getModel()).addElement(o);
                  
           }

    }
    
    private void handleList(){
    	SVNRepo svnrepo = SVNRepo.getInstance();
    	ListModel lm = rightList.getModel();
    	
    	for (int ii = 0;ii<lm.getSize();ii++){
    	
	    	SvnResource sr = SvnResource.parserStr((String)lm.getElementAt(ii));
	    	
	    	String xUrl =PropValue.getInstance().getProperty("svn.url")+"/engineering"+sr.getPath();
	    	String xPath =PropValue.getInstance().getProperty("svn.tofolder")+sr.getPath();
	    	String xVersion = sr.getVersion();
			
	    	try {
				svnrepo.Export(xUrl, xVersion, xPath, "");
			} catch (SVNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
//			JOptionPane.showMessageDialog(null, "svnresource"+sr.getPath(), "标题条文字串"+sr.getSVNVersion(),
//					JOptionPane.INFORMATION_MESSAGE);
    	}
    }

    /**
     * Initialize the contents of the frame.
     * @throws IOException 
     */
    private void initialize() throws IOException {
        frame = new JFrame();
        frame.setTitle("版本清单工具");
        frame.setBounds(100, 100, 1039, 643);
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
        
        JPanel panel_6 = new JPanel();
        panel_6.setBorder(new CompoundBorder());
        tabbedPane.addTab("New tab", null, panel_6, null);
        panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.X_AXIS));
        
        
        listSvn = new JList();
        listSvn.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
        //panel_6.add(listSvn, "name_127355397031124");
        
        JScrollPane scrollPane_list = new JScrollPane(listSvn);
        panel_6.add(scrollPane_list);
        
        JPanel panel_7 = new JPanel();
        scrollPane_list.setColumnHeaderView(panel_7);
        
        JButton btnQuery = new JButton("查询");
        btnQuery.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		
        		querySVNList();
                
                
        	}
        });
        
        JLabel label = new JLabel("开始版本号：");
        panel_7.add(label);
        
        textField_1 = new JTextField();
        panel_7.add(textField_1);
        textField_1.setColumns(10);
        
        JLabel label_1 = new JLabel("结束版本号：");
        panel_7.add(label_1);
        
        textField = new JTextField();
        panel_7.add(textField);
        textField.setColumns(10);
        panel_7.add(btnQuery);
        
        JButton button = new JButton(">>");
        button.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		rightItem();
        	}
        });
        panel_7.add(button);
        
        Box horizontalBox = Box.createHorizontalBox();
        panel_6.add(horizontalBox);
        
        Component verticalGlue = Box.createVerticalGlue();
        panel_6.add(verticalGlue);
        
        JScrollPane scrollPane_1 = new JScrollPane();
        panel_6.add(scrollPane_1);
        
        rightList = new JList();
        scrollPane_1.setViewportView(rightList);
        
        JPanel panel_8 = new JPanel();
        scrollPane_1.setColumnHeaderView(panel_8);
        
        JButton button_1 = new JButton("<<");
        button_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		leftItem();
        	}
        });
        panel_8.add(button_1);
        
        textField_2 = new JTextField();
        textField_2.setText("                                          ");
        textField_2.setEnabled(false);
        textField_2.setEditable(false);
        panel_8.add(textField_2);
        textField_2.setColumns(10);
        
        JButton btnExportExcel = new JButton("Export Excel");
        btnExportExcel.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		handleList();
        		
        	}
        });
        panel_8.add(btnExportExcel);
        
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
        			.addContainerGap()
        			.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(panel_5, GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)
        			.addGap(4))
        );
        gl_panel.setVerticalGroup(
        	gl_panel.createParallelGroup(Alignment.TRAILING)
        		.addGroup(gl_panel.createSequentialGroup()
        			.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
        				.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
        				.addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
        				.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
