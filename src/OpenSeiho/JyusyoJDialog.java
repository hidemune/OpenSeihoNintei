/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OpenSeiho;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author hdm
 */
public class JyusyoJDialog extends javax.swing.JDialog {
private DefaultListModel lstM;
private JyusyoPanel jyusyoPanel;
//private ConmoboxModel cmbLst;

    /**
     * Creates new form JyusyoJDialog
     * @deprecated 
     */
    public JyusyoJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    /**
     * こちらを使って下さい。
     * @param parent
     * @param modal
     * @param parentPanel 
     */
    public JyusyoJDialog(java.awt.Frame parent, boolean modal, JyusyoPanel parentPanel) {
        super(parent, modal);
        initComponents();
        
        jyusyoPanel = parentPanel;
        
        //画面を中心に表示
        java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        // 変数desktopBoundsにデスクトップ領域を表すRectangleが代入される
        java.awt.Rectangle desktopBounds = env.getMaximumWindowBounds();
        java.awt.Rectangle thisBounds = this.getBounds();
        int x = desktopBounds.width / 2 - thisBounds.width / 2;
        int y = desktopBounds.height / 2 - thisBounds.height / 2;
        this.setBounds(x, y, thisBounds.width, thisBounds.height);
        
        lstM = new DefaultListModel();
        listJyusyo.setModel(lstM);
        comboTodoufuken.removeAllItems();
        //IME抑止
        textZipCode.enableInputMethods(false);
    }

    public void setParentJyusyoPanel(JyusyoPanel parentPanel) {
        jyusyoPanel = parentPanel;
    }
    public void setZipCode(String zipcode) {
        textZipCode.setText(zipcode);
        searchZipCode(zipcode);
        this.setVisible(true);
        //モーダルの場合ここで止まる
        
    }
    public void setJyusyo1(String jyusyo1) {
        textJyusyo1.setText(jyusyo1);
        searchJyusyo1(jyusyo1);
        this.setVisible(true);
        //モーダルの場合ここで止まる
    }
    private void searchJyusyo1(String jyusyo1) {
        comboTodoufuken.removeAllItems();
        comboTodoufuken.addItem("絞り込み");
        comboTodoufuken.setSelectedIndex(0);
        lstM.clear();
        //CSVファイルの読み込み
        File csv = null;
        try {
            csv = new File("KEN_ALL.CSV");
            BufferedReader br;      // close 忘れずに！！！
            br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), "MS932"));
            String line = "";
            boolean find = false;
            while ((line = br.readLine()) != null) {
                String[] strArr = line.split(",");
                //System.err.println(strArr[2]);
                String zipcode = strArr[2].replaceAll("\"", "");
                //System.err.println(zipcode);
                if (line.indexOf(jyusyo1) >= 0) {
                    //                    textJyusyo1.setText(strArr[6].replaceAll("\"", "") + strArr[7].replaceAll("\"", ""));
                    //                    textJyusyo2.setText(strArr[8].replaceAll("\"", ""));
                    lstM.addElement(line);
                    //都道府県絞り込み用コンボボックス設定
                    String ken = strArr[6].replaceAll("\"", "");
                    boolean todo = false;
                    for (int i = 0; i < comboTodoufuken.getItemCount(); i++) {
                        
                        if (comboTodoufuken.getItemAt(i).equals(ken)) {
                            todo = true;
                        }
                    }
                    if (!todo) {
                        comboTodoufuken.addItem(ken);
                    }
                    find = true;
                }
            }
            br.close();
            if (!find) {
                JOptionPane.showMessageDialog(this, "指定された住所が郵便番号CSVに見つかりません。");
            }
        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "郵便番号CSVファイルがありません。\n" + csv.getAbsolutePath());
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "エラーが発生しました");
        }
    }
    private void searchZipCode(String zipcodeOrg) {
        lstM.clear();
        
        //郵便番号検索
        String serchZip = zipcodeOrg.replaceAll("-", "");
        
        int serchL = serchZip.length();
        if ((serchL == 3) || (serchL == 5)) {
            //３桁・５桁指定
            //CSVファイルの読み込み
            File csv = null;
            try {
                csv = new File("KEN_ALL.CSV");
                BufferedReader br;      // close 忘れずに！！！
                br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), "MS932"));
                String line = "";
                boolean find = false;
                while ((line = br.readLine()) != null) {
                    String[] strArr = line.split(",");
                    String findZip = strArr[1].replaceAll("\"", "").substring(0, serchL);
                    //System.out.println(findZip);
                    if (serchZip.equals(findZip)) {
                        lstM.addElement(line);
                        find = true;
                    }
                }
                br.close();
                if (!find) {
                    JOptionPane.showMessageDialog(this, "指定された郵便番号が見つかりません。");
                }
            } catch (FileNotFoundException e) {
                // Fileオブジェクト生成時の例外捕捉
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "郵便番号CSVファイルがありません。\n" + csv.getAbsolutePath());
            } catch (IOException e) {
                // BufferedReaderオブジェクトのクローズ時の例外捕捉
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "エラーが発生しました");
            }

            return;
        }

        if (serchL != 7) {
            JOptionPane.showMessageDialog(this, "郵便番号の桁数をご確認下さい。\n３桁、５桁、７桁が指定できます。");
            return;
        }

        //CSVファイルの読み込み
        File csv = null;
        try {
            csv = new File("KEN_ALL.CSV");
            BufferedReader br;      // close 忘れずに！！！
            br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), "MS932"));
            String line = "";
            boolean find = false;
            while ((line = br.readLine()) != null) {
                String[] strArr = line.split(",");
                //System.err.println(strArr[2]);
                String zipcode = strArr[2].replaceAll("\"", "");
                //System.err.println(zipcode);
                if (serchZip.equals(zipcode)) {
                    //                    textJyusyo1.setText(strArr[6].replaceAll("\"", "") + strArr[7].replaceAll("\"", ""));
                    //                    textJyusyo2.setText(strArr[8].replaceAll("\"", ""));
                    textJyusyo1.setText(strArr[6].replaceAll("\"", "") + strArr[7].replaceAll("\"", "") + strArr[8].replaceAll("\"", ""));

                    find = true;
                    break;
                }
            }
            br.close();
            if (!find) {
                JOptionPane.showMessageDialog(this, "指定された郵便番号が見つかりません。");
            }
        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "郵便番号CSVファイルがありません。\n" + csv.getAbsolutePath());
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "エラーが発生しました");
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

        jLabel1 = new javax.swing.JLabel();
        textZipCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        textJyusyo1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        listJyusyo = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        comboTodoufuken = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(JyusyoJDialog.class, "JyusyoJDialog.jLabel1.text")); // NOI18N

        textZipCode.setText(org.openide.util.NbBundle.getMessage(JyusyoJDialog.class, "JyusyoJDialog.textZipCode.text")); // NOI18N
        textZipCode.setMinimumSize(new java.awt.Dimension(4, 23));
        textZipCode.setPreferredSize(new java.awt.Dimension(68, 23));
        textZipCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textZipCodeActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(JyusyoJDialog.class, "JyusyoJDialog.jLabel2.text")); // NOI18N

        textJyusyo1.setText(org.openide.util.NbBundle.getMessage(JyusyoJDialog.class, "JyusyoJDialog.textJyusyo1.text")); // NOI18N
        textJyusyo1.setMinimumSize(new java.awt.Dimension(4, 23));
        textJyusyo1.setPreferredSize(new java.awt.Dimension(4, 23));
        textJyusyo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textJyusyo1ActionPerformed(evt);
            }
        });

        listJyusyo.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listJyusyo);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(JyusyoJDialog.class, "JyusyoJDialog.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        comboTodoufuken.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboTodoufuken.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboTodoufukenActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(textZipCode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(textJyusyo1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 159, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comboTodoufuken, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 117, Short.MAX_VALUE)
                        .add(jButton1))
                    .add(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(textZipCode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(textJyusyo1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1)
                    .add(comboTodoufuken, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .add(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void textZipCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textZipCodeActionPerformed
        // TODO add your handling code here:
        
        searchZipCode(textZipCode.getText());
    }//GEN-LAST:event_textZipCodeActionPerformed

    private void textJyusyo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textJyusyo1ActionPerformed
        // TODO add your handling code here:
        
        String searchStr = textJyusyo1.getText();
        searchJyusyo1(searchStr);
    }//GEN-LAST:event_textJyusyo1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        int idx = listJyusyo.getSelectedIndex();
        if (idx < 0) {
             JOptionPane.showMessageDialog(this, "選択されていません。");
            return;
        }
        String line = (String) listJyusyo.getSelectedValue();
        System.err.println(line);
        String[] strArr = line.split(",");
        //System.err.println(strArr[2]);
        String zipcode = strArr[2].replaceAll("\"", "");
        zipcode = zipcode.substring(0, 3) + "-" + zipcode.substring(3);
        String jyusyo1 = strArr[6].replaceAll("\"", "") + strArr[7].replaceAll("\"", "") + strArr[8].replaceAll("\"", "");
        
        System.err.println(zipcode);
        
        jyusyoPanel.setAddress(zipcode, jyusyo1);
        //閉じて終了
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void comboTodoufukenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboTodoufukenActionPerformed
        // TODO add your handling code here:
        //都道府県絞り込み
        if (comboTodoufuken.getSelectedIndex() <= 0) {
            return;
        }
        String searchTodo = (String) comboTodoufuken.getSelectedItem();
        int max = lstM.getSize();
        for (int i = max - 1; i >= 0; i--) {
            String wk = (String) lstM.getElementAt(i);
            if (wk.indexOf(searchTodo) >= 0) {
                //該当
            } else {
                lstM.removeElementAt(i);
            }
        }
        //都道府県コンボも再設定しとく
        comboTodoufuken.removeAllItems();
        comboTodoufuken.addItem(searchTodo);
    }//GEN-LAST:event_comboTodoufukenActionPerformed

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
            java.util.logging.Logger.getLogger(JyusyoJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JyusyoJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JyusyoJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JyusyoJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JyusyoJDialog dialog = new JyusyoJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboTodoufuken;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList listJyusyo;
    private javax.swing.JTextField textJyusyo1;
    private javax.swing.JTextField textZipCode;
    // End of variables declaration//GEN-END:variables
}
