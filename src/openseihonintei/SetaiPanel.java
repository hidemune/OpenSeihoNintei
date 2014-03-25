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

import openseiho.OsClassYMD;
import openseiho.OsComboID;
import com.ibm.icu.text.Transliterator;

/**
 *
 * @author hdm
 */
public class SetaiPanel extends javax.swing.JPanel {
    //共通部分
    public static boolean DebugMode = false;
    public static void logDebug(String str) {
        if (DebugMode) {
            System.out.println(str);
        }
    }
    
    MainFrame frame = null;
    /**
     * Creates new form SetaiPanel
     */
    public SetaiPanel() {
        initComponents();
    }
    public SetaiPanel(MainFrame frm) {
        frame = frm;
        initComponents();
    }
    
    public void setEditable(boolean editable) {
        textName.setEditable(editable);
        textKana.setEditable(editable);
        textYmd.setEditable(editable);
        comboID1.setEnabled(editable);
        comboID2.setEnabled(editable);
        checked.setEnabled(editable);
    }
    public void setNameKj(String str) {
        textName.setText(str);
    }
    public String getNameKj() {
        return textName.getText();
    }
    public void setNameKn(String str) {
        textKana.setText(str);
    }
    public String getNameKn() {
        return textKana.getText();
    }
    public void setSeibetu(String str) {
        comboID1.setID1(str);
    }
    public String getSeibetu() {
        return comboID1.getID1();
    }
    public void setZokugara(String str) {
        comboID2.setID1(str);
    }
    public String getZokugara() {
        return comboID2.getID1();
    }
    public void setChecked(boolean chk) {
        checked.setSelected(chk);
    }
    public String getBirthYmd() {
        return textYmd.getID();
    }
    public void setBirthYmd(String Ymd) {
        textYmd.setID(Ymd);
    }
    public String getNenrei() {
        return textNenrei.getText();
    }
    public void setNenrei(String str) {
        textNenrei.setText(str);
    }
    public boolean isChecked() {
        return checked.isSelected();
    }
    public void setTextYmdErr(boolean error) {
        textYmd.setTextYmdErr(error);
    }
    public boolean isDate() {
        return textYmd.isDate();
    }
    public void setMyouji(String Myouji) {
        String wk = textName.getText();
        if (wk.indexOf(Myouji) >= 0) {
            return;
        }
        textName.setText(Myouji + "　" + wk);
    }
    public void setMyoujiKana(String Myouji) {
        String wk = textKana.getText();
        if (wk.indexOf(Myouji) >= 0) {
            return;
        }
        textKana.setText(Myouji + "　" + wk);
    }
    
    /**
     * 認定年齢を算出：4月1日時点の年齢、ただし、マイナスになる場合は０とする。
     * @param ninteiYMD : YmdID
     */
    public void setNenreiCalc(String ninteiYMD) {
        if (!OsClassYMD.isNumeric(ninteiYMD)) {
            return;
        }
        int nendo = OsClassYMD.getNendo(ninteiYMD);
        setNenrei(nendo);
    }
    /**
     * 認定年齢を算出：4月1日時点の年齢、ただし、マイナスになる場合は０とする。
     * @param nendo 
     */
    public void setNenrei(int nendo) {
        //日付を取得(生年月日)
        String ymdID = textYmd.getID();
        if (!isDate()) {
            textNenrei.setText("");
            return;
        }
        
        String y = ymdID.substring(0, 4);
        String m = ymdID.substring(4, 6);
        String d = ymdID.substring(6, 8);
        //指定年度4月1日時点の年齢を算出
        //DebugMode = true;
        logDebug(y + "//" + m + "//" + d);
        String y41 = "" + nendo;
        //当年度の誕生日を算出
        int birthYmdThisYear = Integer.parseInt(nendo + m + d);
        if (Integer.parseInt(m) <= 3) {
            //1-3月生まれの場合は年度＋１ (月日部分:0000)
            birthYmdThisYear = birthYmdThisYear + 10000;
        }
        //ret : 年齢（戻り値） floor:切り捨て　ceil:切り上げ
        int yyyy1 = Integer.parseInt(y);
        int yyyy2 = nendo;
        if (Integer.parseInt(m) <= 3) {
            //1-3月生まれの場合は年度＋１ (月日部分:0000)
            yyyy2 = yyyy2 + 1;
        }
        int ret = yyyy2 - yyyy1;
        //当年度の誕生日を過ぎていなかったら調整(4月1日前後の誕生日に注意)
        int thisYear41 = (nendo * 10000 + 401);
        logDebug("当年度の４月１日：" + thisYear41);
        logDebug("当年度の誕生日：" + birthYmdThisYear);
        //誕生日過ぎているか
        if (thisYear41 < birthYmdThisYear) {
            ret = ret - 1;
        }
        //年齢がマイナスになる場合は０に変更
        logDebug("算出値：" + ret);
        if (ret < 0) {
            ret = 0;
        }
        textNenrei.setText("" + ret);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        checked = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        textYmd = new openseiho.OsTextYmd();
        comboID1 = new openseiho.OsComboID();
        comboID2 = new openseiho.OsComboID();
        jLabel3 = new javax.swing.JLabel();
        textName = new openseiho.OsText();
        textKana = new openseiho.OsText();
        textNenrei = new openseiho.OsTextNum();

        setFocusTraversalPolicyProvider(true);
        setMinimumSize(new java.awt.Dimension(0, 64));
        setPreferredSize(new java.awt.Dimension(686, 75));

        checked.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        checked.setText("構成員");
        checked.setToolTipText("一番上の構成員が世帯主になります");
        checked.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkedActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText("認定年齢");

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel2.setText("歳");

        textYmd.setCaption("生年月日");
        textYmd.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                textYmdPropertyChange(evt);
            }
        });

        comboID1.setCaption("性別 ");
        comboID1.setComboWidth(new java.lang.Integer(50));
        comboID1.setId0(new java.lang.Integer(1));
        comboID1.setPostCap("");

        comboID2.setCaption("続柄");
        comboID2.setComboWidth(new java.lang.Integer(100));
        comboID2.setId0(new java.lang.Integer(2));
        comboID2.setPostCap("");

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel3.setText("カナ氏名");

        textKana.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textKanaActionPerformed(evt);
            }
        });

        textNenrei.setEditable(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(checked)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(textName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 152, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(textKana, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 162, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(21, 21, 21)
                        .add(comboID1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comboID2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 139, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(textYmd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 258, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(textNenrei, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(checked)
                        .add(jLabel3)
                        .add(textName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(textKana, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(comboID2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comboID1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(textYmd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(textNenrei, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void checkedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkedActionPerformed
        // TODO add your handling code here:
        //this.setChecked(!(this.isChecked()));
    }//GEN-LAST:event_checkedActionPerformed

    private void textYmdPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_textYmdPropertyChange
        // TODO add your handling code here:
        //DebugMode = true;
        logDebug("textYmdPropertyChange:" + evt.getPropertyName());
        logDebug("ninteiYMD:" + frame.getNinteiYMD());
        logDebug("BirthYMD:" + textYmd.getID());
        setNenreiCalc(frame.getNinteiYMD());
    }//GEN-LAST:event_textYmdPropertyChange

    private void textKanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textKanaActionPerformed
        //ひらがな→カタカナ変換
        Transliterator tr = Transliterator.getInstance("Hiragana-Katakana");
        textKana.setText(tr.transform(textKana.getText()));
    }//GEN-LAST:event_textKanaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checked;
    private openseiho.OsComboID comboID1;
    private openseiho.OsComboID comboID2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private openseiho.OsText textKana;
    private openseiho.OsText textName;
    private openseiho.OsTextNum textNenrei;
    private openseiho.OsTextYmd textYmd;
    // End of variables declaration//GEN-END:variables

}
