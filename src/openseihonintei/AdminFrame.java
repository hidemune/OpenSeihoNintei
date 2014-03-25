/*
 * Copyright (C) 2014 hdm
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package openseihonintei;

import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author hdm
 */
public class AdminFrame extends javax.swing.JFrame {
//private DbSetai dbSetai;

    /**
     * Creates new form AdminFrame
     */
    public AdminFrame() {
        initComponents();
        //DBアクセスクラス
        //dbSetai = new DbSetai();
        //画面を中心に表示
        java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        // 変数desktopBoundsにデスクトップ領域を表すRectangleが代入される
        java.awt.Rectangle desktopBounds = env.getMaximumWindowBounds();
        java.awt.Rectangle thisBounds = this.getBounds();
        int x = desktopBounds.width / 2 - thisBounds.width / 2;
        int y = desktopBounds.height / 2 - thisBounds.height / 2;
        this.setBounds(x, y, thisBounds.width, thisBounds.height);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        comboDB = new javax.swing.JComboBox();
        comboAction = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textPG = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        textRS = new javax.swing.JTextArea();

        setTitle("データベース管理");

        jLabel1.setText("データベース");

        comboDB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "テーブルを選択", "id_text", "setai", "kojin", "saiseihi", "chosyo2", "kijyun" }));

        comboAction.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "操作を選択", "create", "edit", "drop", "program", "SQL Test" }));

        jButton1.setText("実行");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        textPG.setColumns(20);
        textPG.setLineWrap(true);
        textPG.setRows(5);
        jScrollPane1.setViewportView(textPG);

        textRS.setColumns(20);
        textRS.setLineWrap(true);
        textRS.setRows(5);
        jScrollPane2.setViewportView(textRS);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jScrollPane2)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE))
                .add(0, 0, Short.MAX_VALUE))
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(comboAction, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(comboDB, 0, 137, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 109, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel1)
                            .add(comboDB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comboAction, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(13, 13, 13))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        //実行ボタン
        if (comboDB.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "テーブルを選択して下さい。");
            return;
        }
        String table = (String) comboDB.getSelectedItem();
        
        if (comboAction.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "操作を選択して下さい。");
            return;
        }
        String action = (String) comboAction.getSelectedItem();
        
        //確認
        if (action.equals("drop")) {
            if (JOptionPane.showConfirmDialog(this, "この操作を行うと、テーブルの全てのデータが削除されます。\nバックアップはとってありますか？", "確認", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }
        if (JOptionPane.showConfirmDialog(this, "実行してよろしいですか？", "確認", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        
        //DBアクセサーを選択
        DbAccessOS accesser = null;
/*        if (table.equals("id_text")) {
            //DbAccessOS.DebugMode = true;
            accesser = new openseiho.dbIdText();            
        } // */
        if (table.equals("setai")) {
            //DbAccessOS.DebugMode = true;
            accesser = new DbSetai();            
        }
        if (table.equals("kojin")) {
            //DbAccessOS.DebugMode = true;
            accesser = new DbKojin();            
        }
        if (table.equals("kijyun")) {
            //DbAccessOS.DebugMode = true;
            accesser = new DbKijyun();            
        }
        if (table.equals("saiseihi")) {
            //DbAccessOS.DebugMode = true;
            accesser = new DbSaiseihi();            
        }
        if (table.equals("chosyo2")) {
            //DbAccessOS.DebugMode = true;
            accesser = new DbChosyo2();            
        }
        if (accesser == null) {
            JOptionPane.showMessageDialog(this, "実装されていない操作です。");
            return;
        }
        
        //SQL実行
        String msg = "";
        boolean flg = false; // ちゃんと実行したか確認用
        
        if (action.equals("create")) {
            msg = accesser.createTable();
            flg = true;
        }
        if (action.equals("drop")) {
            msg = accesser.dropTable();
            flg = true;
        }
        if (action.equals("edit")) {
            accesser.editTable(accesser, "");
            flg = true;
            //不要なメッセージを回避
            return;
        }
        
        if (action.equals("program")) {
            String str = accesser.getProgram();
            textPG.setText(str);
            //不要なメッセージを回避
            return;
        }
        if (action.equals("SQL Test")) {
            flg = true;
            String SQL = textPG.getText();
//            int cols = DbAccessOS.getValueI(textCols.getText());
            String[][] rs = accesser.getResultSetTableBySQL(SQL);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rs.length; i++) {
                for (int j = 0; j < rs[i].length; j++) {
                    sb.append("rs[");
                    sb.append("" + i);
                    sb.append("]");
                    sb.append("[");
                    sb.append("" + j);
                    sb.append("]");
                    sb.append(rs[i][j]);
                    sb.append("\n");
                }
            }
            textRS.setText(sb.toString());
        }
        
        if (!(msg.equals(""))) {
            JOptionPane.showMessageDialog(this, msg);
            return;
        }
        
        if (flg) {
            JOptionPane.showMessageDialog(this, "実行しました。");
        } else {
            JOptionPane.showMessageDialog(this, "実装されていない操作です。");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboAction;
    private javax.swing.JComboBox comboDB;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea textPG;
    private javax.swing.JTextArea textRS;
    // End of variables declaration//GEN-END:variables
}
