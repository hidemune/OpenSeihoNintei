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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author hdm
 */
public class DbSheetFrameOS extends javax.swing.JFrame {
private javax.swing.JList[] lists;
//private javax.swing.JSplitPane[] split;
private String tableNameWk;
private DefaultListModel ModelL[];
private final int maxCols = 26;
private javax.swing.JTextField[] editPre;
private javax.swing.JTextField[] edit;
private javax.swing.JLabel[] name;
private String[][] strRS;

private DbAccessOS dbAc;
//private ListSelectionListener lisner;

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
//        split = new javax.swing.JSplitPane[maxCols];
        editPre = new javax.swing.JTextField[maxCols];
        edit = new javax.swing.JTextField[maxCols];
        name = new javax.swing.JLabel[maxCols];
        
        initComponents();
        
        //スクロール量
        jScrollPaneEdit.getVerticalScrollBar().setUnitIncrement(25);
        jTabbedPaneSheet.setSelectedIndex(0);
        
        //リストの生成
        for (int i = 0; i < maxCols; i++) {
            lists[i] = new javax.swing.JList();
            panelSheet.add(lists[i]);
            lists[i].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            lists[i].addListSelectionListener(MyListSelectionHandler());
        }
        
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
        labelTableName.setText(dbAc.getTableName());
        jTextFieldWhere.setText(where);
        strRS = rs;
        String ttl = "";
        for (int col = 0; col < rs[0].length; col++) {
            ModelL[col].clear();
            for (int row = 0; row < rs.length; row++) {
                if (row == 0) {
                    ttl = "■";
                } else {
                    ttl = "";
                }
                ModelL[col].addElement(ttl + rs[row][col] + ttl);;
            }
            lists[col].setModel(ModelL[col]);
        }
        //1列目を選択
        lists[0].setSelectedIndex(0);
        selectList(lists[0]);
    }
    
    private void selectList(JList lst)  {
        int row = lst.getSelectedIndex();

        for (int i = 0; i < maxCols; i++) {
            try {
                lists[i].setSelectedIndex(row);
            }catch (Exception e) {
                //何もしない
            }
        }
        
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
        labelTableName = new javax.swing.JLabel();
        jPanelSheet = new javax.swing.JPanel();
        jScrollPaneSheet = new javax.swing.JScrollPane();
        panelSheet = new javax.swing.JPanel();
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

//        setTitle(org.openide.util.NbBundle.getMessage(DbSheetFrameOS.class, "dbSheetFrame.title_1")); // NOI18N
        setIconImages(null);

        jSplitPane1.setDividerLocation(25);
        jSplitPane1.setDividerSize(5);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00001 Page" }));

        labelTableName.setText("テーブル名");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelTableName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 478, Short.MAX_VALUE)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTableName))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jSplitPane1.setTopComponent(jPanel1);

        jPanelSheet.setLayout(new javax.swing.OverlayLayout(jPanelSheet));

        jScrollPaneSheet.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPaneSheet.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPaneSheet.setViewportView(panelSheet);

        jPanelSheet.add(jScrollPaneSheet);

        jSplitPane1.setRightComponent(jPanelSheet);

        jTabbedPaneSheet.addTab("一覧", jSplitPane1);

        jLabel1.setText("テーブル名");

        jTextFieldTableName.setEditable(false);
        jTextFieldTableName.setText("jTextFieldTableName");

        jLabel2.setText("WHERE句");

        jTextFieldWhere.setEditable(false);
        jTextFieldWhere.setText("jTextFieldWhere");

        jLabelPosision.setText("+");

        jButtonUpdt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/openseiho/checkbox.png"))); // NOI18N
        jButtonUpdt.setText("更新");
        jButtonUpdt.setToolTipText("一番上の行を選択しておけば、新規登録になります。");
        jButtonUpdt.setEnabled(false);
        jButtonUpdt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdtActionPerformed(evt);
            }
        });

        jButtonDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/openseiho/document-close.png"))); // NOI18N
        jButtonDel.setText("削除");
        jButtonDel.setToolTipText("削除したい場合は、有効な列を選択しておいて下さい。");
        jButtonDel.setEnabled(false);
        jButtonDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelActionPerformed(evt);
            }
        });

        jLabel3.setText("変更前");

        jLabel4.setText("変更後");

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

        jTabbedPaneSheet.addTab("編集", jScrollPaneEdit);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 668, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPaneSheet))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 445, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPaneSheet, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelSheet;
    private javax.swing.JScrollPane jScrollPaneEdit;
    private javax.swing.JScrollPane jScrollPaneSheet;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPaneSheet;
    private javax.swing.JTextField jTextFieldTableName;
    private javax.swing.JTextField jTextFieldWhere;
    private javax.swing.JLabel labelTableName;
    private javax.swing.JPanel panelSheet;
    // End of variables declaration//GEN-END:variables

    private ListSelectionListener MyListSelectionHandler() {
        return new MyListSelectionHandler();
    }
    class MyListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            JList lst = (javax.swing.JList)e.getSource();
            
            //リストをクリック等で選択した場合の処理
            //すべての列で選択し直し
            selectList(lst);

        }
    }
}
