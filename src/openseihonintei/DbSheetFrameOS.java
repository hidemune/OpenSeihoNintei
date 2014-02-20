/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package openseihonintei;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author hdm
 */
public class DbSheetFrameOS extends javax.swing.JFrame {
private javax.swing.JList[] lists;
private javax.swing.JSplitPane[] split;
private String tableNameWk;
private DefaultListModel ModelL[];
private final int maxCols = 26;
private javax.swing.JTextField[] editPre;
private javax.swing.JTextField[] edit;
private javax.swing.JLabel[] name;
private String[][] strRS;

private DbAccessOS dbAc;

    //共通部分
    public static boolean DebugMode = false;
    public static void logDebug(String str) {
        if (DebugMode) {
            System.out.println(str);
        }
    }
    
    /**
     * Creates new form dbSheetFrame
     */
    public DbSheetFrameOS(DbAccessOS dbA) {
        dbAc = dbA;
        tableNameWk = dbA.getTableName();
        logDebug("DB表示・編集開始:" + tableNameWk);
        lists = new javax.swing.JList[maxCols];
        ModelL = new DefaultListModel[maxCols];
        split = new javax.swing.JSplitPane[maxCols];
        editPre = new javax.swing.JTextField[maxCols];
        edit = new javax.swing.JTextField[maxCols];
        name = new javax.swing.JLabel[maxCols];
        
        initComponents();
        
        //スクロール量
        jScrollPaneEdit.getVerticalScrollBar().setUnitIncrement(25);
        jTabbedPaneSheet.setSelectedIndex(0);
        
        //とりあえず放置
        lists[0] = jList1;
        lists[1] = jList2;
        lists[2] = jList3;
        lists[3] = jList4;
        lists[4] = jList5;
        lists[5] = jList6;
        lists[6] = jList7;
        lists[7] = jList8;
        lists[8] = jList9;
        lists[9] = jList10;
        lists[10] = jList11;
        lists[11] = jList12;
        lists[12] = jList13;
        lists[13] = jList14;
        lists[14] = jList15;
        lists[15] = jList16;
        lists[16] = jList17;
        lists[17] = jList18;
        lists[18] = jList19;
        lists[19] = jList20;
        lists[20] = jList21;
        lists[21] = jList22;
        lists[22] = jList23;
        lists[23] = jList24;
        lists[24] = jList25;
        lists[25] = jList26;
        
        split[0] = jSplitPaneCol1;
        split[1] = jSplitPaneCol2;
        split[2] = jSplitPaneCol3;
        split[3] = jSplitPaneCol4;
        split[4] = jSplitPaneCol5;
        split[5] = jSplitPaneCol6;
        split[6] = jSplitPaneCol7;
        split[7] = jSplitPaneCol8;
        split[8] = jSplitPaneCol9;
        split[9] = jSplitPaneCol10;
        split[10] = jSplitPaneCol11;
        split[11] = jSplitPaneCol12;
        split[12] = jSplitPaneCol13;
        split[13] = jSplitPaneCol14;
        split[14] = jSplitPaneCol15;
        split[15] = jSplitPaneCol16;
        split[16] = jSplitPaneCol17;
        split[17] = jSplitPaneCol18;
        split[18] = jSplitPaneCol19;
        split[19] = jSplitPaneCol20;
        split[20] = jSplitPaneCol21;
        split[21] = jSplitPaneCol22;
        split[22] = jSplitPaneCol23;
        split[23] = jSplitPaneCol24;
        split[24] = jSplitPaneCol25;
        split[25] = jSplitPaneCol26;
        
        for (int i = 0; i < maxCols; i++) {
            //リストボックス初期化
            ModelL[i] = new DefaultListModel();
            lists[i].setModel(ModelL[i]);
        }
        jScrollPaneSheet.setPreferredSize(new Dimension(90 * maxCols, 200));
        
        //編集画面を生成(コンストラクタで１回のみ実行)
        jTextFieldTableName.setText(dbA.getTableName());
        jTextFieldWhere.setText("");
        for (int i = 0; i < maxCols; i++) {
            editPre[i] = new javax.swing.JTextField("");
            edit[i] = new javax.swing.JTextField("");
            name[i] = new javax.swing.JLabel("");
            jPanelEdit.add(editPre[i]);
            jPanelEdit.add(edit[i]);
            jPanelEdit.add(name[i]);
            
            editPre[i].setEditable(false);
            edit[i].setVisible(false);
            name[i].setVisible(false);
        }
        Rectangle zero =  jLabelPosision.getBounds();
        Dimension panelsize = new Dimension(645, maxCols * 28 + zero.y + 50);
        jScrollPaneEdit.setPreferredSize(panelsize);
        jPanelEdit.setPreferredSize(panelsize);
        jLabelPosision.setVisible(false);
        for (int i = 0; i < maxCols; i++) {
            editPre[i].setBounds(zero.x + 100, zero.y + i * 28, 200, 25);
            editPre[i].setVisible(true);
            edit[i].setBounds(zero.x + 310, zero.y + i * 28, 300, 25);
            edit[i].setVisible(true);
            name[i].setBounds(zero.x, zero.y + i * 28, 200, 25);
            name[i].setVisible(true);
        }
        //TODO 最後の列が広すぎるのでなんとかしたいが、うまくいかない。
        jScrollPaneSheet.setPreferredSize(panelsize);
        jPanelSheet.setPreferredSize(panelsize);
        
        //画面を中心に表示
        java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        // 変数desktopBoundsにデスクトップ領域を表すRectangleが代入される
        java.awt.Rectangle desktopBounds = env.getMaximumWindowBounds();
        java.awt.Rectangle thisBounds = this.getBounds();
        int x = desktopBounds.width / 2 - thisBounds.width / 2;
        int y = desktopBounds.height / 2 - thisBounds.height / 2;
        this.setBounds(x, y, thisBounds.width, thisBounds.height);
    }

    //レザルトセットを画面に描画
@SuppressWarnings("empty-statement")
    public void setResultSet(String rs[][], String where) {
        jTextFieldWhere.setText(where);
        strRS = rs;
        String ttl = "";
        for (int i = 0; i < rs.length; i++) {
            ModelL[i].clear();
            for (int j = 0; j < rs[i].length; j++) {
                if (j == 0) {
                    ttl = "■";
                } else {
                    ttl = "";
                }
                ModelL[i].addElement(ttl + rs[i][j] + ttl);;
            }
            lists[i].setModel(ModelL[i]);
        }
        //一覧の列の幅を初期化
        for (int i = 0; i < 26; i++) {
            split[i].setDividerLocation(80);
        }
        //一旦ボタンを不可にする
        jButtonUpdt.setEnabled(false);
        jButtonDel.setEnabled(false);
    }
    
    private void selectList(JList lst)  {
        int row = lst.getSelectedIndex();

        for (int i = 0; i < 26; i++) {
            try {
                lists[i].setSelectedIndex(row);
            }catch (Exception e) {
                //何もしない
            }
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPaneSheet = new javax.swing.JTabbedPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jPanelSheet = new javax.swing.JPanel();
        jScrollPaneSheet = new javax.swing.JScrollPane();
        jSplitPaneCol1 = new javax.swing.JSplitPane();
        jSplitPaneCol2 = new javax.swing.JSplitPane();
        jSplitPaneCol3 = new javax.swing.JSplitPane();
        jSplitPaneCol4 = new javax.swing.JSplitPane();
        jSplitPaneCol5 = new javax.swing.JSplitPane();
        jSplitPaneCol6 = new javax.swing.JSplitPane();
        jSplitPaneCol7 = new javax.swing.JSplitPane();
        jSplitPaneCol8 = new javax.swing.JSplitPane();
        jSplitPaneCol9 = new javax.swing.JSplitPane();
        jSplitPaneCol10 = new javax.swing.JSplitPane();
        jSplitPaneCol11 = new javax.swing.JSplitPane();
        jSplitPaneCol12 = new javax.swing.JSplitPane();
        jSplitPaneCol13 = new javax.swing.JSplitPane();
        jSplitPaneCol14 = new javax.swing.JSplitPane();
        jSplitPaneCol15 = new javax.swing.JSplitPane();
        jSplitPaneCol16 = new javax.swing.JSplitPane();
        jSplitPaneCol17 = new javax.swing.JSplitPane();
        jSplitPaneCol18 = new javax.swing.JSplitPane();
        jSplitPaneCol19 = new javax.swing.JSplitPane();
        jSplitPaneCol20 = new javax.swing.JSplitPane();
        jSplitPaneCol21 = new javax.swing.JSplitPane();
        jSplitPaneCol22 = new javax.swing.JSplitPane();
        jSplitPaneCol23 = new javax.swing.JSplitPane();
        jSplitPaneCol24 = new javax.swing.JSplitPane();
        jSplitPaneCol25 = new javax.swing.JSplitPane();
        jScrollPaneCol26 = new javax.swing.JScrollPane();
        jList25 = new javax.swing.JList();
        jSplitPaneCol26 = new javax.swing.JSplitPane();
        jScrollPane27 = new javax.swing.JScrollPane();
        jList26 = new javax.swing.JList();
        jPanelLast = new javax.swing.JPanel();
        jScrollPane25 = new javax.swing.JScrollPane();
        jList24 = new javax.swing.JList();
        jScrollPane24 = new javax.swing.JScrollPane();
        jList23 = new javax.swing.JList();
        jScrollPane23 = new javax.swing.JScrollPane();
        jList22 = new javax.swing.JList();
        jScrollPane22 = new javax.swing.JScrollPane();
        jList21 = new javax.swing.JList();
        jScrollPane21 = new javax.swing.JScrollPane();
        jList20 = new javax.swing.JList();
        jScrollPane20 = new javax.swing.JScrollPane();
        jList19 = new javax.swing.JList();
        jScrollPane19 = new javax.swing.JScrollPane();
        jList18 = new javax.swing.JList();
        jScrollPane18 = new javax.swing.JScrollPane();
        jList17 = new javax.swing.JList();
        jScrollPane17 = new javax.swing.JScrollPane();
        jList16 = new javax.swing.JList();
        jScrollPane16 = new javax.swing.JScrollPane();
        jList15 = new javax.swing.JList();
        jScrollPane15 = new javax.swing.JScrollPane();
        jList14 = new javax.swing.JList();
        jScrollPane14 = new javax.swing.JScrollPane();
        jList13 = new javax.swing.JList();
        jScrollPane13 = new javax.swing.JScrollPane();
        jList12 = new javax.swing.JList();
        jScrollPane12 = new javax.swing.JScrollPane();
        jList11 = new javax.swing.JList();
        jScrollPane11 = new javax.swing.JScrollPane();
        jList10 = new javax.swing.JList();
        jScrollPane10 = new javax.swing.JScrollPane();
        jList9 = new javax.swing.JList();
        jScrollPane9 = new javax.swing.JScrollPane();
        jList8 = new javax.swing.JList();
        jScrollPane8 = new javax.swing.JScrollPane();
        jList7 = new javax.swing.JList();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList6 = new javax.swing.JList();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList5 = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPaneEdit = new javax.swing.JScrollPane();
        jPanelEdit = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldTableName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldWhere = new javax.swing.JTextField();
        jLabelPosision = new javax.swing.JLabel();
        jButtonUpdt = new javax.swing.JButton();
        jButtonDel = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setTitle(org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "dbSheetFrame.title")); // NOI18N

        jSplitPane1.setDividerLocation(25);
        jSplitPane1.setDividerSize(5);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00001 Page" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 552, Short.MAX_VALUE)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 2, Short.MAX_VALUE))
        );

        jSplitPane1.setTopComponent(jPanel1);

        jPanelSheet.setLayout(new javax.swing.OverlayLayout(jPanelSheet));

        jScrollPaneSheet.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPaneSheet.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jSplitPaneCol1.setBorder(null);

        jSplitPaneCol2.setBorder(null);

        jSplitPaneCol3.setBorder(null);

        jSplitPaneCol4.setBorder(null);

        jSplitPaneCol5.setBorder(null);

        jSplitPaneCol6.setBorder(null);

        jSplitPaneCol7.setBorder(null);

        jSplitPaneCol8.setBorder(null);

        jSplitPaneCol9.setBorder(null);

        jSplitPaneCol10.setBorder(null);

        jSplitPaneCol11.setBorder(null);

        jSplitPaneCol12.setBorder(null);

        jSplitPaneCol13.setBorder(null);

        jSplitPaneCol14.setBorder(null);

        jSplitPaneCol15.setBorder(null);

        jSplitPaneCol16.setBorder(null);

        jSplitPaneCol17.setBorder(null);

        jSplitPaneCol18.setBorder(null);

        jSplitPaneCol19.setBorder(null);

        jSplitPaneCol20.setBorder(null);

        jSplitPaneCol21.setBorder(null);

        jSplitPaneCol22.setBorder(null);

        jSplitPaneCol23.setBorder(null);

        jSplitPaneCol24.setBorder(null);

        jSplitPaneCol25.setBorder(null);
        jSplitPaneCol25.setMaximumSize(new java.awt.Dimension(200, 2147483647));

        jList25.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList25.setMaximumSize(new java.awt.Dimension(80, 80));
        jList25.setMinimumSize(new java.awt.Dimension(80, 80));
        jList25.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPaneCol26.setViewportView(jList25);

        jSplitPaneCol25.setLeftComponent(jScrollPaneCol26);

        jList26.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList26.setMaximumSize(new java.awt.Dimension(80, 80));
        jList26.setMinimumSize(new java.awt.Dimension(80, 80));
        jList26.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane27.setViewportView(jList26);

        jSplitPaneCol26.setLeftComponent(jScrollPane27);

        jPanelLast.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanelLast.setMinimumSize(new java.awt.Dimension(1, 100));
        jPanelLast.setPreferredSize(new java.awt.Dimension(1, 100));
        jPanelLast.setLayout(null);
        jSplitPaneCol26.setRightComponent(jPanelLast);

        jSplitPaneCol25.setRightComponent(jSplitPaneCol26);

        jSplitPaneCol24.setRightComponent(jSplitPaneCol25);

        jList24.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList24.setMaximumSize(new java.awt.Dimension(80, 80));
        jList24.setMinimumSize(new java.awt.Dimension(80, 80));
        jList24.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane25.setViewportView(jList24);

        jSplitPaneCol24.setLeftComponent(jScrollPane25);

        jSplitPaneCol23.setRightComponent(jSplitPaneCol24);

        jList23.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList23.setMaximumSize(new java.awt.Dimension(80, 80));
        jList23.setMinimumSize(new java.awt.Dimension(80, 80));
        jList23.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane24.setViewportView(jList23);

        jSplitPaneCol23.setLeftComponent(jScrollPane24);

        jSplitPaneCol22.setRightComponent(jSplitPaneCol23);

        jList22.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList22.setMaximumSize(new java.awt.Dimension(80, 80));
        jList22.setMinimumSize(new java.awt.Dimension(80, 80));
        jList22.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane23.setViewportView(jList22);

        jSplitPaneCol22.setLeftComponent(jScrollPane23);

        jSplitPaneCol21.setRightComponent(jSplitPaneCol22);

        jList21.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList21.setMaximumSize(new java.awt.Dimension(80, 80));
        jList21.setMinimumSize(new java.awt.Dimension(80, 80));
        jList21.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane22.setViewportView(jList21);

        jSplitPaneCol21.setLeftComponent(jScrollPane22);

        jSplitPaneCol20.setRightComponent(jSplitPaneCol21);

        jList20.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList20.setMaximumSize(new java.awt.Dimension(80, 80));
        jList20.setMinimumSize(new java.awt.Dimension(80, 80));
        jList20.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane21.setViewportView(jList20);

        jSplitPaneCol20.setLeftComponent(jScrollPane21);

        jSplitPaneCol19.setRightComponent(jSplitPaneCol20);

        jList19.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList19.setMaximumSize(new java.awt.Dimension(80, 80));
        jList19.setMinimumSize(new java.awt.Dimension(80, 80));
        jList19.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane20.setViewportView(jList19);

        jSplitPaneCol19.setLeftComponent(jScrollPane20);

        jSplitPaneCol18.setRightComponent(jSplitPaneCol19);

        jList18.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList18.setMaximumSize(new java.awt.Dimension(80, 80));
        jList18.setMinimumSize(new java.awt.Dimension(80, 80));
        jList18.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane19.setViewportView(jList18);

        jSplitPaneCol18.setLeftComponent(jScrollPane19);

        jSplitPaneCol17.setRightComponent(jSplitPaneCol18);

        jList17.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList17.setMaximumSize(new java.awt.Dimension(80, 80));
        jList17.setMinimumSize(new java.awt.Dimension(80, 80));
        jList17.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane18.setViewportView(jList17);

        jSplitPaneCol17.setLeftComponent(jScrollPane18);

        jSplitPaneCol16.setRightComponent(jSplitPaneCol17);

        jList16.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList16.setMaximumSize(new java.awt.Dimension(80, 80));
        jList16.setMinimumSize(new java.awt.Dimension(80, 80));
        jList16.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane17.setViewportView(jList16);

        jSplitPaneCol16.setLeftComponent(jScrollPane17);

        jSplitPaneCol15.setRightComponent(jSplitPaneCol16);

        jList15.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList15.setMaximumSize(new java.awt.Dimension(80, 80));
        jList15.setMinimumSize(new java.awt.Dimension(80, 80));
        jList15.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane16.setViewportView(jList15);

        jSplitPaneCol15.setLeftComponent(jScrollPane16);

        jSplitPaneCol14.setRightComponent(jSplitPaneCol15);

        jList14.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList14.setMaximumSize(new java.awt.Dimension(80, 80));
        jList14.setMinimumSize(new java.awt.Dimension(80, 80));
        jList14.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane15.setViewportView(jList14);

        jSplitPaneCol14.setLeftComponent(jScrollPane15);

        jSplitPaneCol13.setRightComponent(jSplitPaneCol14);

        jList13.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList13.setMaximumSize(new java.awt.Dimension(80, 80));
        jList13.setMinimumSize(new java.awt.Dimension(80, 80));
        jList13.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane14.setViewportView(jList13);

        jSplitPaneCol13.setLeftComponent(jScrollPane14);

        jSplitPaneCol12.setRightComponent(jSplitPaneCol13);

        jList12.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList12.setMaximumSize(new java.awt.Dimension(80, 80));
        jList12.setMinimumSize(new java.awt.Dimension(80, 80));
        jList12.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane13.setViewportView(jList12);

        jSplitPaneCol12.setLeftComponent(jScrollPane13);

        jSplitPaneCol11.setRightComponent(jSplitPaneCol12);

        jList11.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList11.setMaximumSize(new java.awt.Dimension(80, 80));
        jList11.setMinimumSize(new java.awt.Dimension(80, 80));
        jList11.setPreferredSize(null);
        jList11.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane12.setViewportView(jList11);

        jSplitPaneCol11.setLeftComponent(jScrollPane12);

        jSplitPaneCol10.setRightComponent(jSplitPaneCol11);

        jList10.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList10.setMaximumSize(new java.awt.Dimension(80, 80));
        jList10.setMinimumSize(new java.awt.Dimension(80, 80));
        jList10.setPreferredSize(null);
        jList10.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane11.setViewportView(jList10);

        jSplitPaneCol10.setLeftComponent(jScrollPane11);

        jSplitPaneCol9.setRightComponent(jSplitPaneCol10);

        jList9.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList9.setMaximumSize(new java.awt.Dimension(80, 80));
        jList9.setMinimumSize(new java.awt.Dimension(80, 80));
        jList9.setPreferredSize(null);
        jList9.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane10.setViewportView(jList9);

        jSplitPaneCol9.setLeftComponent(jScrollPane10);

        jSplitPaneCol8.setRightComponent(jSplitPaneCol9);

        jList8.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList8.setMaximumSize(new java.awt.Dimension(80, 80));
        jList8.setMinimumSize(new java.awt.Dimension(80, 80));
        jList8.setPreferredSize(null);
        jList8.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane9.setViewportView(jList8);

        jSplitPaneCol8.setLeftComponent(jScrollPane9);

        jSplitPaneCol7.setRightComponent(jSplitPaneCol8);

        jList7.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList7.setMaximumSize(new java.awt.Dimension(80, 80));
        jList7.setMinimumSize(new java.awt.Dimension(80, 80));
        jList7.setPreferredSize(null);
        jList7.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane8.setViewportView(jList7);

        jSplitPaneCol7.setLeftComponent(jScrollPane8);

        jSplitPaneCol6.setRightComponent(jSplitPaneCol7);

        jList6.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList6.setMaximumSize(new java.awt.Dimension(80, 80));
        jList6.setMinimumSize(new java.awt.Dimension(80, 80));
        jList6.setPreferredSize(null);
        jList6.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane7.setViewportView(jList6);

        jSplitPaneCol6.setLeftComponent(jScrollPane7);

        jSplitPaneCol5.setRightComponent(jSplitPaneCol6);

        jList5.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList5.setMaximumSize(new java.awt.Dimension(80, 80));
        jList5.setMinimumSize(new java.awt.Dimension(80, 80));
        jList5.setPreferredSize(null);
        jList5.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane6.setViewportView(jList5);

        jSplitPaneCol5.setLeftComponent(jScrollPane6);

        jSplitPaneCol4.setRightComponent(jSplitPaneCol5);

        jList4.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList4.setMaximumSize(new java.awt.Dimension(80, 80));
        jList4.setMinimumSize(new java.awt.Dimension(80, 80));
        jList4.setPreferredSize(null);
        jList4.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane5.setViewportView(jList4);

        jSplitPaneCol4.setLeftComponent(jScrollPane5);

        jSplitPaneCol3.setRightComponent(jSplitPaneCol4);

        jList3.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList3.setMaximumSize(new java.awt.Dimension(80, 80));
        jList3.setMinimumSize(new java.awt.Dimension(80, 80));
        jList3.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane4.setViewportView(jList3);

        jSplitPaneCol3.setLeftComponent(jScrollPane4);

        jSplitPaneCol2.setRightComponent(jSplitPaneCol3);

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList2.setMaximumSize(new java.awt.Dimension(80, 80));
        jList2.setMinimumSize(new java.awt.Dimension(80, 80));
        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane3.setViewportView(jList2);

        jSplitPaneCol2.setLeftComponent(jScrollPane3);

        jSplitPaneCol1.setRightComponent(jSplitPaneCol2);

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "■Title■", "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setMaximumSize(new java.awt.Dimension(80, 80));
        jList1.setMinimumSize(new java.awt.Dimension(80, 80));
        jList1.setPreferredSize(null);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChange(evt);
            }
        });
        jScrollPane2.setViewportView(jList1);

        jSplitPaneCol1.setLeftComponent(jScrollPane2);

        jScrollPaneSheet.setViewportView(jSplitPaneCol1);

        jPanelSheet.add(jScrollPaneSheet);

        jSplitPane1.setRightComponent(jPanelSheet);

        jTabbedPaneSheet.addTab(org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jSplitPane1.TabConstraints.tabTitle"), jSplitPane1); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jLabel1.text")); // NOI18N

        jTextFieldTableName.setEditable(false);
        jTextFieldTableName.setText(org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jTextFieldTableName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jLabel2.text")); // NOI18N

        jTextFieldWhere.setEditable(false);
        jTextFieldWhere.setText(org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jTextFieldWhere.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabelPosision, org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jLabelPosision.text")); // NOI18N

        jButtonUpdt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/OpenSeiho/checkbox.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonUpdt, org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jButtonUpdt.text")); // NOI18N
        jButtonUpdt.setToolTipText(org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jButtonUpdt.toolTipText")); // NOI18N
        jButtonUpdt.setEnabled(false);
        jButtonUpdt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdtActionPerformed(evt);
            }
        });

        jButtonDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/OpenSeiho/document-close.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonDel, org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jButtonDel.text")); // NOI18N
        jButtonDel.setToolTipText(org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jButtonDel.toolTipText")); // NOI18N
        jButtonDel.setEnabled(false);
        jButtonDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout jPanelEditLayout = new javax.swing.GroupLayout(jPanelEdit);
        jPanelEdit.setLayout(jPanelEditLayout);
        jPanelEditLayout.setHorizontalGroup(
            jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelEditLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldWhere, javax.swing.GroupLayout.PREFERRED_SIZE, 504, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelEditLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTableName, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonDel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonUpdt)
                        .addGap(30, 30, 30))
                    .addGroup(jPanelEditLayout.createSequentialGroup()
                        .addComponent(jLabelPosision, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jPanelEditLayout.createSequentialGroup()
                .addGap(121, 121, 121)
                .addComponent(jLabel3)
                .addGap(179, 179, 179)
                .addComponent(jLabel4)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelEditLayout.setVerticalGroup(
            jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEditLayout.createSequentialGroup()
                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelEditLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextFieldTableName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonUpdt)
                        .addComponent(jButtonDel)))
                .addGap(18, 18, 18)
                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldWhere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(15, 15, 15)
                .addComponent(jLabelPosision)
                .addContainerGap(332, Short.MAX_VALUE))
        );

        jScrollPaneEdit.setViewportView(jPanelEdit);

        jTabbedPaneSheet.addTab(org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "DbSheetFrameOS.jScrollPaneEdit.TabConstraints.tabTitle"), jScrollPaneEdit); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 668, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPaneSheet, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 445, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPaneSheet, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListValueChange(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListValueChange
        // TODO add your handling code here:
        //リストをクリック等で選択した場合の処理
        //すべての列で選択し直し
        selectList((JList)evt.getSource());
        //編集画面に転送
        if (lists[0].getSelectedIndex() <= 0) {
            //データが選択されていない：新規作成
            jButtonUpdt.setEnabled(true);
            jButtonDel.setEnabled(false);
            for (int i = 0; i < strRS.length; i++) {
                name[i].setText(strRS[i][0]);
                editPre[i].setText("");
                edit[i].setText("");
            }
        } else {
            //データを選択している場合
            jButtonUpdt.setEnabled(true);
            jButtonDel.setEnabled(true);
            for (int i = 0; i < strRS.length; i++) {
                name[i].setText(strRS[i][0]);
                editPre[i].setText((String)lists[i].getSelectedValue());
                edit[i].setText((String)lists[i].getSelectedValue());
            }
        }
    }//GEN-LAST:event_jListValueChange

    private void jButtonUpdtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdtActionPerformed
        // TODO add your handling code here:
        //更新ボタン
        
        String msg = "";    //ErrMsg
        
        //項目名と値を取得
        ArrayList<String[]> adtable = new ArrayList<String[]>();
        
        for (int i = 0; i < maxCols; i++) {
            if (name[i].getText().equals("")) {
                break;
            }
            ArrayList<String> adlist = new ArrayList<String>();
            adlist.add(name[i].getText());
            adlist.add(editPre[i].getText());
            adlist.add(edit[i].getText());
            
            adtable.add(adlist.toArray(new String[0]));
        }
        
        //Insert, Updateの判定
        if (jButtonDel.isEnabled()) {
            //Update
            if (JOptionPane.showConfirmDialog(this, "更新(Update)します。\nよろしいですか？", "Update", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            String[][] field = adtable.toArray(new String[0][0]);
            String SQL[] = {dbAc.updateSQL(field)};
            msg = dbAc.execSQLUpdate(SQL);
        } else {
            //Insert
            if (JOptionPane.showConfirmDialog(this, "更新(Insert)します。\nよろしいですか？", "Insert", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            String[][] field = adtable.toArray(new String[0][0]);
            String SQL[] = {dbAc.insertSQL(field)};
            msg = dbAc.execSQLUpdate(SQL);
        }
        
        //エラーメッセージ表示
        if (!msg.equals("")) {
            JOptionPane.showMessageDialog(this, msg, "エラー", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this, "更新しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
        
        //一覧を再表示
        //テーブルのレザルトセットを取得
        String where = jTextFieldWhere.getText();
        String[][] str = dbAc.getResultSetTable(where);
        //結果を一覧にセット
        setResultSet(str, where);
        
        //更新直後は追加可能にする
        jButtonUpdt.setEnabled(true);
        
        return;
    }//GEN-LAST:event_jButtonUpdtActionPerformed

    private void jButtonDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDelActionPerformed
        // TODO add your handling code here:
        //削除ボタン
        
        String msg = "";    //ErrMsg
        
        //項目名と値を取得
        /**
         * field[colNo][0:name, 1:before, 2:after]
         */
        ArrayList<String[]> adtable = new ArrayList<String[]>();
        
        for (int i = 0; i < maxCols; i++) {
            if (name[i].getText().equals("")) {
                break;
            }
            
            ArrayList<String> adlist = new ArrayList<String>();
            adlist.add(name[i].getText());
            adlist.add(editPre[i].getText());
            adlist.add(edit[i].getText());
            
            adtable.add(adlist.toArray(new String[0]));
        }
        
        //Delete
        if (JOptionPane.showConfirmDialog(this, "削除します。\nよろしいですか？", "Delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        
        String[][] field = adtable.toArray(new String[0][0]);
        String SQL[] = {dbAc.deleteSQL(field)};
        msg = dbAc.execSQLUpdate(SQL);
        
        //エラーメッセージ表示
        if (!msg.equals("")) {
            JOptionPane.showMessageDialog(this, msg, "エラー", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this, "削除しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
        
        //一覧を再表示
        //テーブルのレザルトセットを取得
        String where = jTextFieldWhere.getText();
        String[][] str = dbAc.getResultSetTable(where);
        //結果を一覧にセット
        setResultSet(str, where);
        
        return;
    }//GEN-LAST:event_jButtonDelActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DbSheetFrameOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DbSheetFrameOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DbSheetFrameOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DbSheetFrameOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        //final String tableNameMain = args[1];
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DbSheetFrameOS(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDel;
    private javax.swing.JButton jButtonUpdt;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelPosision;
    private javax.swing.JList jList1;
    private javax.swing.JList jList10;
    private javax.swing.JList jList11;
    private javax.swing.JList jList12;
    private javax.swing.JList jList13;
    private javax.swing.JList jList14;
    private javax.swing.JList jList15;
    private javax.swing.JList jList16;
    private javax.swing.JList jList17;
    private javax.swing.JList jList18;
    private javax.swing.JList jList19;
    private javax.swing.JList jList2;
    private javax.swing.JList jList20;
    private javax.swing.JList jList21;
    private javax.swing.JList jList22;
    private javax.swing.JList jList23;
    private javax.swing.JList jList24;
    private javax.swing.JList jList25;
    private javax.swing.JList jList26;
    private javax.swing.JList jList3;
    private javax.swing.JList jList4;
    private javax.swing.JList jList5;
    private javax.swing.JList jList6;
    private javax.swing.JList jList7;
    private javax.swing.JList jList8;
    private javax.swing.JList jList9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelLast;
    private javax.swing.JPanel jPanelSheet;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane25;
    private javax.swing.JScrollPane jScrollPane27;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JScrollPane jScrollPaneCol26;
    private javax.swing.JScrollPane jScrollPaneEdit;
    private javax.swing.JScrollPane jScrollPaneSheet;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPaneCol1;
    private javax.swing.JSplitPane jSplitPaneCol10;
    private javax.swing.JSplitPane jSplitPaneCol11;
    private javax.swing.JSplitPane jSplitPaneCol12;
    private javax.swing.JSplitPane jSplitPaneCol13;
    private javax.swing.JSplitPane jSplitPaneCol14;
    private javax.swing.JSplitPane jSplitPaneCol15;
    private javax.swing.JSplitPane jSplitPaneCol16;
    private javax.swing.JSplitPane jSplitPaneCol17;
    private javax.swing.JSplitPane jSplitPaneCol18;
    private javax.swing.JSplitPane jSplitPaneCol19;
    private javax.swing.JSplitPane jSplitPaneCol2;
    private javax.swing.JSplitPane jSplitPaneCol20;
    private javax.swing.JSplitPane jSplitPaneCol21;
    private javax.swing.JSplitPane jSplitPaneCol22;
    private javax.swing.JSplitPane jSplitPaneCol23;
    private javax.swing.JSplitPane jSplitPaneCol24;
    private javax.swing.JSplitPane jSplitPaneCol25;
    private javax.swing.JSplitPane jSplitPaneCol26;
    private javax.swing.JSplitPane jSplitPaneCol3;
    private javax.swing.JSplitPane jSplitPaneCol4;
    private javax.swing.JSplitPane jSplitPaneCol5;
    private javax.swing.JSplitPane jSplitPaneCol6;
    private javax.swing.JSplitPane jSplitPaneCol7;
    private javax.swing.JSplitPane jSplitPaneCol8;
    private javax.swing.JSplitPane jSplitPaneCol9;
    private javax.swing.JTabbedPane jTabbedPaneSheet;
    private javax.swing.JTextField jTextFieldTableName;
    private javax.swing.JTextField jTextFieldWhere;
    // End of variables declaration//GEN-END:variables
}
