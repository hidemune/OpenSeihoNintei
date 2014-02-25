/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package openseihonintei;

import openseiho.classYMD;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.text.Transliterator;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author TANAKA Hidemune <auau@kne.biglobe.ne.jp>
 */
public class MainFrame extends javax.swing.JFrame {
private static SetaiPanel[] sp ;
private static String ninteiYMD;
private DbSetai dbSetai = new DbSetai();
private DbKojin dbKojin = new DbKojin();
private String[][] rsSetaiPre;
private String[][] rsKojin;
private ArrayList<String[][]> arrField = new ArrayList<String[][]>();

    //共通部分
    public static boolean DebugMode = false;
    public static void logDebug(String str) {
        if (DebugMode) {
            System.out.println(str);
        }
    }
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        
        sp = new SetaiPanel[OpenSeihoNintei.MaxSetaiIn];
        for (int i = 0; i < OpenSeihoNintei.MaxSetaiIn; i++) {
            sp[i] = new SetaiPanel(this);
        }
        
        initComponents();
        setIconImage(new ImageIcon("hidemune_s.png").getImage());
        //画面を中心に表示
        java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        // 変数desktopBoundsにデスクトップ領域を表すRectangleが代入される
        java.awt.Rectangle desktopBounds = env.getMaximumWindowBounds();
        java.awt.Rectangle thisBounds = this.getBounds();
        int x = desktopBounds.width / 2 - thisBounds.width / 2;
        int y = desktopBounds.height / 2 - thisBounds.height / 2;
        this.setBounds(x, y, thisBounds.width, thisBounds.height);
        //初期化
        //世帯一覧画面：世帯員パネルの生成
        int h = OpenSeihoNintei.MaxSetaiIn * 64;
        Dimension dimP = panelSetaiBase.getPreferredSize();
        panelSetaiBase.setPreferredSize(new Dimension(dimP.width, h + 100));
        panelSetai.setPreferredSize(new Dimension(dimP.width, h));
        jPanelkojin.setPreferredSize(new Dimension(dimP.width, h));
        
        for (int i = 0; i < OpenSeihoNintei.MaxSetaiIn; i++) {
            panelSetai.add(sp[i]);
            sp[i].setPreferredSize(new Dimension(dimP.width, 64));
            sp[i].setVisible(true);
        }
        //IME抑止
        comboIDNinzuu.enableInputMethods(false);
        textCaseNo.enableInputMethods(false);
        //comboIDNinzuu.
        setaiInPanel.setEditable(false);
        
        //スクロール量
        jScrollPaneSetai.getVerticalScrollBar().setUnitIncrement(25);
        
        //画面クリア
        init();
    }

    public void init() {
        //認定日・起案日
        String strWk = "";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        strWk = df.format(cal.getTime());
        textYmdKian.setID(strWk);
        SimpleDateFormat dfY = new SimpleDateFormat("yyyy");
        SimpleDateFormat dfM = new SimpleDateFormat("MM");
        SimpleDateFormat dfD = new SimpleDateFormat("dd");
        if (!(dfD.format(cal.getTime())).equals("01")) {
            cal.set(Integer.parseInt(dfY.format(cal.getTime())) , Integer.parseInt(dfM.format(cal.getTime())), 1);  //月は０から：月＋１と同じ
        }
        strWk = df.format(cal.getTime());
        textYmdNintei.setID(strWk);
        setNinteiYMD(strWk);
        
        //コンボボックス デフォルト値を設定
        //comboIDNinzuu.setDefaultID1("1");  //人数のデフォルト
        comboIDKyuti.setDefaultID1(11);
        comboIDTouki.setDefaultID1(6);
        comboIDSeikatuKeitai.setDefaultID1(1);
        //初期化
        comboIDNinzuu.setSelectedIndexID1(0);
        comboIDKyuti.setSelectedIndexID1(0);
        comboIDTouki.setSelectedIndexID1(0);
        comboIDSeikatuKeitai.setSelectedIndexID1(0);
        
        //住所パネル初期化
        panelJyusyo.setAddress("", "", "");
        
        //世帯員パネル初期化
        for (int i = 0; i < OpenSeihoNintei.MaxSetaiIn; i++) {
            sp[i].setChecked(false);
            sp[i].setBirthYmd("00000000");
            sp[i].setNameKj("");
            sp[i].setNameKn("");
            sp[i].setNenrei("");
            sp[i].setSeibetu("");
            sp[i].setZokugara("");
        }
        
        rsSetaiPre = null;
        arrField.clear();
    }
    
    public void findSetai(String caseNo) {
        init();
//        String[][] rs = dbSetai.getResultSetTable("WHERE caseNo = '" + caseNo + "'");
        String SQL = "SELECT * FROM setai WHERE caseNo = '" + caseNo + "'";
        String[][] rs = dbSetai.getResultSetTableBySQL(SQL);
        dbSetai.DebugMode = true;
        //世帯共通部分
        if (rs.length <= 1) {
            //Not found.
            textCaseNo.setText(caseNo);
            JOptionPane.showMessageDialog(this, "レコードが見つかりません。");
            return;
        }
        
        textCaseNo.setText(dbSetai.getValue(rs, "caseNo", 1));
        checkBoxSyokken.setSelected(dbSetai.getValueB(rs, "syokkenFlg", 1));
        panelJyusyo.setAddress(dbSetai.getValue(rs, "yubinNo", 1), dbSetai.getValue(rs, "Address1", 1), dbSetai.getValue(rs, "Address2", 1));
        for (int i = 1; i < rs.length; i++) {        //長さ１つ少ないのに注意（0行目はカラム名となる）
            //世帯員個別部分
            int idx = dbSetai.getValueI(rs, "inNo", i) - 1; //員番号は１から始まる
            sp[idx].setChecked(dbSetai.getValueB(rs, "kouseiIn", i));
            sp[idx].setBirthYmd(dbSetai.getValue(rs, "birthYmd", i));
            sp[idx].setNameKj(dbSetai.getValue(rs, "nameKj", i));
            sp[idx].setNameKn(dbSetai.getValue(rs, "nameKn", i));
            //sp[i].setNenrei(dbSetai.getValue(rs, "birthYmd", i)); 計算で出る
            sp[idx].setSeibetu(dbSetai.getValue(rs, "seibetu", i));
            sp[idx].setZokugara(dbSetai.getValue(rs, "zokuCd", i));
        }
        //更新時のために退避しておく
        rsSetaiPre = rs;
        
        //起案選択
        KianSentakuDialog kian = new KianSentakuDialog(this, true);
        //kian.setVisible(true);
        kian.show(caseNo);// このタイミングで表示・ストップする。
        String[] kianRet = kian.getReturn();
        DebugMode = true;
        logDebug("認定日：" + kianRet[0]);
        logDebug("起案日：" + kianRet[1]);
        String ninteiYmd = kian.getReturn()[0];
        String kianYmd = kian.getReturn()[1];
        
        if (DbAccessOS.getValueI(ninteiYmd) <= 0) {
            //新規登録
            return;
        }
        
        //個人状況を取得・退避
        rsKojin = dbKojin.getResultSetTable("WHERE kianYmd = '" + kianYmd + "' AND ninteiYmd = '" + ninteiYmd + "' AND caseNo = '" + caseNo + "'");
        
        //構成員チェック付け直し
        for (int i = 0; i < OpenSeihoNintei.MaxSetaiIn; i++) {
            sp[i].setChecked(false);
        }
        for (int i = 1; i < rsKojin[0].length; i++) {
            int inNo = dbKojin.getValueI(rsKojin, "inNo", i);
            sp[inNo - 1].setChecked(true);
        }
        
        //認定情報を取得・退避
        
    }
    public static SetaiPanel getSetaiPanel(int idx) {
        return sp[idx];
    }
    public static void setNinteiYMD(String ymd_ID) {
        ninteiYMD = ymd_ID;
    }
    public static String getNinteiYMD() {
        return ninteiYMD;
    }
    
    public void insertKojin(){
        //更新前の確認
        if ((JOptionPane.showConfirmDialog(this, "更新しますか？", "確認", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)) {
            JOptionPane.showMessageDialog(this, "処理を中止しました。");
            return;
        };
        
        //個人インサート処理
        String valuePre = "";
        ArrayList lst = new ArrayList();
        /*
        //インサート前にデリート(1件ずつ全件：１つのSQLで複数件削除はエラーとなることに注意)
        if (rsSetaiPre != null) {
            for (int i = 1; i < rsSetaiPre.length; i++) {
                String[][] field = {
                    {"caseNo", dbSetai.getValue(rsSetaiPre, "caseNo", i), ""},		//TEXT
                    {"inNo", dbSetai.getValue(rsSetaiPre, "inNo", i), ""},		//INTEGER
                };
                //前レコードが見つかったため削除しておく
                String wk = dbSetai.deleteSQL(field);
                lst.add(wk);
                logDebug(wk);
            }
        }
        */
        for (int i = 0; i < arrField.size(); i++) {
            //インサート処理
            String wk = dbKojin.insertSQL(arrField.get(i));
            lst.add(wk);
            logDebug(wk);
        }
        //更新処理
        String[] SQL=(String[])lst.toArray(new String[0]);
        String msg = dbSetai.execSQLUpdate(SQL);
        if (msg.equals("")) {
            JOptionPane.showMessageDialog(this, "更新しました。");
        } else {
            JOptionPane.showMessageDialog(this, msg);
        }
    }
    
    public void insertSetai(){
        //更新前の確認
        if ((JOptionPane.showConfirmDialog(this, "更新しますか？", "確認", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)) {
            JOptionPane.showMessageDialog(this, "処理を中止しました。");
            return;
        };
        
        //世帯インサート処理
        String valuePre = "";
        ArrayList lst = new ArrayList();
        
        //インサート前にデリート(1件ずつ全件：１つのSQLで複数件削除はエラーとなることに注意)
        if (rsSetaiPre != null) {
            for (int i = 1; i < rsSetaiPre.length; i++) {
                String[][] field = {
                    {"caseNo", dbSetai.getValue(rsSetaiPre, "caseNo", i), ""},		//TEXT
                    {"inNo", dbSetai.getValue(rsSetaiPre, "inNo", i), ""},		//INTEGER
                };
                //前レコードが見つかったため削除しておく
                String wk = dbSetai.deleteSQL(field);
                lst.add(wk);
                logDebug(wk);
            }
        }
        
        for (int i = 0; i < OpenSeihoNintei.MaxSetaiIn; i++) {
            //漢字氏名が無ければそれ以降は無視する。
            if (sp[i].getNameKj().equals("")) {
                break;
            }
            
            String[][] field = {
                {"caseNo", valuePre, textCaseNo.getText()},		//TEXT
                {"inNo", valuePre, "" + (i + 1)},		//INTEGER               1から始まるものとする
                {"syokkenFlg", valuePre, DbAccessOS.isBoolean(checkBoxSyokken.isSelected())},		//INTEGER
                {"yubinNo", valuePre, panelJyusyo.getYubinNo()},		//TEXT
                {"Address1", valuePre, panelJyusyo.getJyusyo1()},		//TEXT
                {"Address2", valuePre, panelJyusyo.getJyusyo2()},		//TEXT
                {"kouseiIn", valuePre, DbAccessOS.isBoolean(sp[i].isChecked())},		//INTEGER
                {"nameKj", valuePre, sp[i].getNameKj()},		//TEXT
                {"nameKn", valuePre, sp[i].getNameKn()},		//TEXT
                {"seibetu", valuePre, sp[i].getSeibetu()},		//INTEGER
                {"zokuCd", valuePre, sp[i].getZokugara()},		//INTEGER
                {"birthYmd", valuePre, sp[i].getBirthYmd()}		//TEXT
            };
            //インサート処理
            String wk = dbSetai.insertSQL(field);
            lst.add(wk);
            logDebug(wk);
        }
        //更新処理
        String[] SQL=(String[])lst.toArray(new String[0]);
        String msg = dbSetai.execSQLUpdate(SQL);
        if (msg.equals("")) {
            JOptionPane.showMessageDialog(this, "更新しました。");
        } else {
            JOptionPane.showMessageDialog(this, msg);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPaneSetai = new javax.swing.JScrollPane();
        panelSetaiBase = new javax.swing.JPanel();
        panelSetai = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        textCaseNo = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        textMyouji = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        textMyoujiKana = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        comboIDNinzuu = new openseiho.comboID();
        checkBoxSyokken = new javax.swing.JCheckBox();
        textYmdNintei = new openseiho.textYmdPanel();
        textYmdKian = new openseiho.textYmdPanel();
        panelJyusyo = new openseiho.JyusyoPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanelkojin = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanelKasan = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        chkNinsanpu = new javax.swing.JCheckBox();
        comboIDKasanNinpu = new openseiho.comboID();
        comboIDKasanSanpu = new openseiho.comboID();
        textSyussanYmd = new openseiho.textYmdPanel();
        jPanel7 = new javax.swing.JPanel();
        chkSyougai = new javax.swing.JCheckBox();
        comboIDKasanSyougai = new openseiho.comboID();
        jLabel4 = new javax.swing.JLabel();
        textKaigoHi = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        chkKaigoSisetu = new javax.swing.JCheckBox();
        textKaigoSisetu = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        chkZaitaku = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        chkHousya = new javax.swing.JCheckBox();
        comboIDHousyasen = new openseiho.comboID();
        jPanel11 = new javax.swing.JPanel();
        chkJidouYouiku = new javax.swing.JCheckBox();
        comboIDKasanJidouYouiku = new openseiho.comboID();
        jPanel12 = new javax.swing.JPanel();
        chkKaigoHokenRyou = new javax.swing.JCheckBox();
        textKasanKaigoHokenRyou = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        chkBoshi = new javax.swing.JCheckBox();
        comboIDKasanBoshi = new openseiho.comboID();
        textKasanBoshiNinzuu = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        chkTyouhuku = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        comboIDSeikatuKeitai = new openseiho.comboID();
        jComboBoxKojin = new javax.swing.JComboBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        comboIDKyuti = new openseiho.comboID();
        comboIDTouki = new openseiho.comboID();
        jButtonKojinCheck = new javax.swing.JButton();
        jButtonKojinInst = new javax.swing.JButton();
        setaiInPanel = new openseihonintei.SetaiPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("生活保護認定");

        jTabbedPane1.setFocusable(false);
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jScrollPaneSetai.setAlignmentY(1.0F);

        panelSetaiBase.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelSetaiBase.setPreferredSize(new java.awt.Dimension(713, 20));

        panelSetai.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelSetai.setDoubleBuffered(false);
        panelSetai.setMinimumSize(new java.awt.Dimension(0, 896));
        panelSetai.setLayout(new java.awt.GridLayout(0, 1));

        jButton2.setText("チェック及び更新");
        jButton2.setFocusable(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel6.setText("ケースNo");

        textCaseNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textCaseNoActionPerformed(evt);
            }
        });

        jButton7.setText("自動採番");
        jButton7.setToolTipText("");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jButton7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton7KeyPressed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("苗字");

        jLabel2.setText("苗字カナ");

        textMyoujiKana.setToolTipText("ひらがな入力してEnterを押すと、全角カナになります");
        textMyoujiKana.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textMyoujiKanaActionPerformed(evt);
            }
        });

        jButton1.setText("苗字セット");
        jButton1.setToolTipText("構成員チェックした行のみ苗字が反映されます。\nこの機能は無理に使う必要はありません。");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
            }
        });

        comboIDNinzuu.setCaption("");
        comboIDNinzuu.setComboWidth(new java.lang.Integer(70));
        comboIDNinzuu.setId0(new java.lang.Integer(3));
        comboIDNinzuu.setPostCap("人世帯");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(textMyouji, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(textMyoujiKana, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(comboIDNinzuu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1)
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(1, 1, 1)
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton1)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, comboIDNinzuu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, textMyoujiKana, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, textMyouji, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        checkBoxSyokken.setText("職権保護");
        checkBoxSyokken.setFocusable(false);

        textYmdNintei.setCaption("認定日");
        textYmdNintei.setDebugGraphicsOptions(0);
        textYmdNintei.setTextYmdErr(false);
        textYmdNintei.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                textYmdNinteiPropertyChange(evt);
            }
        });

        textYmdKian.setCaption("起案日");

        org.jdesktop.layout.GroupLayout panelSetaiBaseLayout = new org.jdesktop.layout.GroupLayout(panelSetaiBase);
        panelSetaiBase.setLayout(panelSetaiBaseLayout);
        panelSetaiBaseLayout.setHorizontalGroup(
            panelSetaiBaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelSetaiBaseLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelSetaiBaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelSetaiBaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, panelSetaiBaseLayout.createSequentialGroup()
                            .add(panelSetaiBaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(panelSetaiBaseLayout.createSequentialGroup()
                                    .add(jLabel6)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(textCaseNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(jButton7))
                                .add(checkBoxSyokken))
                            .add(18, 18, 18)
                            .add(panelSetaiBaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(textYmdNintei, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 252, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(textYmdKian, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 252, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(panelJyusyo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(126, 126, 126))
            .add(panelSetaiBaseLayout.createSequentialGroup()
                .add(panelSetai, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 814, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        panelSetaiBaseLayout.setVerticalGroup(
            panelSetaiBaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelSetaiBaseLayout.createSequentialGroup()
                .add(panelSetaiBaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelSetaiBaseLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(panelSetaiBaseLayout.createSequentialGroup()
                        .add(panelSetaiBaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(panelSetaiBaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(jLabel6)
                                .add(textCaseNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jButton7))
                            .add(textYmdNintei, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(panelSetaiBaseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(panelSetaiBaseLayout.createSequentialGroup()
                                .add(8, 8, 8)
                                .add(checkBoxSyokken))
                            .add(panelSetaiBaseLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(textYmdKian, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panelJyusyo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(2, 2, 2)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelSetai, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 769, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        textYmdNintei.getAccessibleContext().setAccessibleParent(this);

        jScrollPaneSetai.setViewportView(panelSetaiBase);

        jTabbedPane1.addTab("世帯一覧", jScrollPaneSetai);

        jLabel3.setText("加算");

        jPanelKasan.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelKasan.setLayout(new java.awt.GridLayout(8, 1));

        chkNinsanpu.setText("妊産婦");

        comboIDKasanNinpu.setCaption("妊婦");
        comboIDKasanNinpu.setComboWidth(new java.lang.Integer(120));
        comboIDKasanNinpu.setId0(new java.lang.Integer(11));
        comboIDKasanNinpu.setPostCap("");

        comboIDKasanSanpu.setCaption("産婦");
        comboIDKasanSanpu.setComboWidth(new java.lang.Integer(150));
        comboIDKasanSanpu.setId0(new java.lang.Integer(12));
        comboIDKasanSanpu.setPostCap("");

        textSyussanYmd.setCaption("出産(予定)日");

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(chkNinsanpu)
                .add(37, 37, 37)
                .add(comboIDKasanNinpu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 151, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(comboIDKasanSanpu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 178, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(textSyussanYmd, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chkNinsanpu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comboIDKasanNinpu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comboIDKasanSanpu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(textSyussanYmd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 4, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel6);

        chkSyougai.setText("障害者");
        chkSyougai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSyougaiActionPerformed(evt);
            }
        });

        comboIDKasanSyougai.setCaption("");
        comboIDKasanSyougai.setComboWidth(new java.lang.Integer(250));
        comboIDKasanSyougai.setId0(new java.lang.Integer(13));
        comboIDKasanSyougai.setPostCap("");

        jLabel4.setText("介護人費用");

        textKaigoHi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textKaigoHi.setText("\\0");

        jLabel7.setText("円");

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(chkSyougai)
                .add(45, 45, 45)
                .add(comboIDKasanSyougai, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 256, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(textKaigoHi, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel7)
                .addContainerGap(158, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chkSyougai, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comboIDKasanSyougai, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel4)
                        .add(textKaigoHi, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel7)))
                .add(0, 5, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel7);

        chkKaigoSisetu.setText("介護施設入所者");

        textKaigoSisetu.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textKaigoSisetu.setText("\\0");

        jLabel8.setText("円");

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(chkKaigoSisetu)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(textKaigoSisetu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel8)
                .addContainerGap(488, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chkKaigoSisetu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(textKaigoSisetu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .add(0, 6, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel8);

        chkZaitaku.setText("在宅患者");

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(chkZaitaku)
                .addContainerGap(668, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(chkZaitaku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 6, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel9);

        chkHousya.setText("放射線障害者");
        chkHousya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHousyaActionPerformed(evt);
            }
        });

        comboIDHousyasen.setCaption("");
        comboIDHousyasen.setId0(new java.lang.Integer(16));
        comboIDHousyasen.setPostCap("");

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(chkHousya)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(comboIDHousyasen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(529, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel10Layout.createSequentialGroup()
                        .add(chkHousya, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(1, 1, 1))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, comboIDHousyasen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 5, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel10);

        chkJidouYouiku.setText("児童養育");

        comboIDKasanJidouYouiku.setCaption("");
        comboIDKasanJidouYouiku.setComboWidth(new java.lang.Integer(150));
        comboIDKasanJidouYouiku.setId0(new java.lang.Integer(17));
        comboIDKasanJidouYouiku.setPostCap("");

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(chkJidouYouiku)
                .add(33, 33, 33)
                .add(comboIDKasanJidouYouiku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 163, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(472, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, comboIDKasanJidouYouiku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, chkJidouYouiku, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(0, 3, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel11);

        chkKaigoHokenRyou.setText("介護保険料");

        textKasanKaigoHokenRyou.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textKasanKaigoHokenRyou.setText("\\0");

        jLabel9.setText("円");

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(chkKaigoHokenRyou)
                .add(21, 21, 21)
                .add(textKasanKaigoHokenRyou, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel9)
                .addContainerGap(509, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chkKaigoHokenRyou, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(textKasanKaigoHokenRyou, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9))
                .add(0, 6, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel12);

        chkBoshi.setText("母子");

        comboIDKasanBoshi.setCaption("");
        comboIDKasanBoshi.setId0(new java.lang.Integer(19));
        comboIDKasanBoshi.setPostCap("");

        textKasanBoshiNinzuu.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textKasanBoshiNinzuu.setText("0");

        jLabel5.setText("人");

        chkTyouhuku.setText("重複調整");

        org.jdesktop.layout.GroupLayout jPanel13Layout = new org.jdesktop.layout.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .add(chkBoshi)
                .add(60, 60, 60)
                .add(comboIDKasanBoshi, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(textKasanBoshiNinzuu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5)
                .add(86, 86, 86)
                .add(chkTyouhuku)
                .add(0, 308, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(comboIDKasanBoshi, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(chkBoshi, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(textKasanBoshiNinzuu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(chkTyouhuku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(0, 5, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel13);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        comboIDSeikatuKeitai.setCaption("生活形態 ");
        comboIDSeikatuKeitai.setComboWidth(new java.lang.Integer(200));
        comboIDSeikatuKeitai.setId0(new java.lang.Integer(4));
        comboIDSeikatuKeitai.setPostCap("");

        jComboBoxKojin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxKojin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxKojinActionPerformed(evt);
            }
        });

        jCheckBox1.setText("世帯主");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        comboIDKyuti.setCaption("級地");
        comboIDKyuti.setComboWidth(new java.lang.Integer(70));
        comboIDKyuti.setId0(new java.lang.Integer(5));
        comboIDKyuti.setPostCap("");

        comboIDTouki.setCaption("冬季区分");
        comboIDTouki.setComboWidth(new java.lang.Integer(100));
        comboIDTouki.setId0(new java.lang.Integer(6));
        comboIDTouki.setPostCap("");

        jButtonKojinCheck.setText("整合性チェック（個人毎）");
        jButtonKojinCheck.setToolTipText("整合性チェック後、他の世帯員を選択し直して下さい。");
        jButtonKojinCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonKojinCheckActionPerformed(evt);
            }
        });

        jButtonKojinInst.setText("更新");
        jButtonKojinInst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonKojinInstActionPerformed(evt);
            }
        });

        setaiInPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jCheckBox1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(comboIDSeikatuKeitai, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 269, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comboIDKyuti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comboIDTouki, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jComboBoxKojin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 167, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(37, 37, 37)
                        .add(jButtonKojinCheck)
                        .add(18, 18, 18)
                        .add(jButtonKojinInst, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 135, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(setaiInPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 709, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBoxKojin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonKojinCheck)
                    .add(jButtonKojinInst))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(setaiInPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jCheckBox1)
                        .add(comboIDSeikatuKeitai, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, comboIDKyuti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, comboIDTouki, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanelkojinLayout = new org.jdesktop.layout.GroupLayout(jPanelkojin);
        jPanelkojin.setLayout(jPanelkojinLayout);
        jPanelkojinLayout.setHorizontalGroup(
            jPanelkojinLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelkojinLayout.createSequentialGroup()
                .add(jPanelkojinLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanelkojinLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel3))
                    .add(jPanelkojinLayout.createSequentialGroup()
                        .add(13, 13, 13)
                        .add(jPanelKasan, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 757, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelkojinLayout.setVerticalGroup(
            jPanelkojinLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelkojinLayout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelKasan, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 227, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(jPanelkojin);

        jTabbedPane1.addTab("個人状況", jScrollPane2);

        jButton5.setText("jButton1");

        jButton6.setText("jButton2");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(965, Short.MAX_VALUE)
                .add(jButton6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton5)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton5)
                    .add(jButton6))
                .addContainerGap(768, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(jPanel3);

        jTabbedPane1.addTab("認定", jScrollPane3);

        jMenu2.setText("管理者メニュー");

        jMenuItem3.setText("データベース管理");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        jMenu1.setText("Help");

        jMenuItem2.setText("バージョン情報");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setText("ヘルプ");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 461, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        //人数がセットされていれば、チェックをつける(人数セットは必須ではない。手動チェックでOK)
        int ninzuu = comboIDNinzuu.getValue();
        if (ninzuu > 0) {
            for (int i = 0; i < ninzuu; i++) {
                sp[i].setChecked(true);
            }
        }
        
        //苗字セット
        for (int i = 0; i < OpenSeihoNintei.MaxSetaiIn; i++) {
            if (sp[i].isChecked()) {
                sp[i].setMyouji(textMyouji.getText());
                sp[i].setMyoujiKana(textMyoujiKana.getText());
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        //チェック及び確定
        String msg = "";
        
        //年齢算出
        int nendo = classYMD.getNendo(textYmdNintei.getID());
        for (int i = 0; i < OpenSeihoNintei.MaxSetaiIn; i++) {
            sp[i].setNenrei(nendo);
            if ((sp[i].isChecked()) && !(sp[i].isDate())) {
                sp[i].setTextYmdErr(true);
                msg = msg + "生年月日を確認してください。\n";
            } else {
                sp[i].setTextYmdErr(false);
            }
        }
        
        //必須チェック
        msg = msg + DbAccessOS.hissuChkText(textCaseNo.getText(), "ケースNo");
        msg = msg + DbAccessOS.hissuChkYmd(textYmdNintei.getID(), "認定日");
        msg = msg + DbAccessOS.hissuChkYmd(textYmdKian.getID(), "起案日");
        msg = msg + DbAccessOS.hissuChkText(panelJyusyo.getYubinNo(), "郵便番号");
        msg = msg + DbAccessOS.hissuChkText(panelJyusyo.getJyusyo1(), "住所１");
        boolean flg = false;
        for (int i = 0; i < sp.length; i++) {
            if (sp[i].isChecked()) {
                //項目入ってないとエラー
                msg = msg + DbAccessOS.hissuChkText(sp[i].getNameKj(), "氏名" + (i + 1));
                msg = msg + DbAccessOS.hissuChkText(sp[i].getNameKn(), "氏名カナ" + (i + 1));
                msg = msg + DbAccessOS.hissuChkNum(sp[i].getSeibetu(), "性別" + (i + 1));
                msg = msg + DbAccessOS.hissuChkYmd(sp[i].getBirthYmd(), "生年月日" + (i + 1));
                msg = msg + DbAccessOS.hissuChkNum(sp[i].getZokugara(), "続柄" + (i + 1));
            } else {
                //項目は入っててもOKだが、世帯員漢字氏名が空の場合、以降の更新が走らない仕様。チェックを行う。
                if (sp[i].getNameKj().equals("")) {
                    flg = true;
                }
                if ((flg) && (!(sp[i].getNameKj().equals("")))) {
                    //名前なしの後に名前有りの場合、エラーとする
                    msg = msg + "氏名が空欄ならば、それ以降は登録されません。確認して下さい。" + (i + 1) + "\n";
                }
            }
        }
        
        if (!(msg.equals(""))) {
            JOptionPane.showMessageDialog(this, msg);
            return;
        }
        
        //更新処理
        insertSetai();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void textMyoujiKanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textMyoujiKanaActionPerformed
        // TODO add your handling code here:
        //ひらがな→カタカナ変換
        Transliterator tr = Transliterator.getInstance("Hiragana-Katakana");
        textMyoujiKana.setText(tr.transform(textMyoujiKana.getText()));
    }//GEN-LAST:event_textMyoujiKanaActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        // TODO add your handling code here:
        JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
        DebugMode = true;
        logDebug("Tab changed to: " + index + "/" + sourceTabbedPane.getTitleAt(index));
        //個人状況が選択された場合
        if (index == 1) {
            jComboBoxKojin.removeAllItems();
            for (int i = 0; i < sp.length; i++) {
                if (sp[i].isChecked()) {
                    jComboBoxKojin.addItem(sp[i].getNameKj());
                }
            }
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jComboBoxKojinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxKojinActionPerformed
        // TODO add your handling code here:
        //世帯員選択
        if (jComboBoxKojin.getSelectedIndex() < 0) {
            return;
        }
        setaiInPanel.setChecked(sp[jComboBoxKojin.getSelectedIndex()].isChecked());
        setaiInPanel.setNameKj(sp[jComboBoxKojin.getSelectedIndex()].getNameKj());
        setaiInPanel.setNameKn(sp[jComboBoxKojin.getSelectedIndex()].getNameKn());
        setaiInPanel.setSeibetu(sp[jComboBoxKojin.getSelectedIndex()].getSeibetu());
        setaiInPanel.setZokugara(sp[jComboBoxKojin.getSelectedIndex()].getZokugara());
        setaiInPanel.setBirthYmd(sp[jComboBoxKojin.getSelectedIndex()].getBirthYmd());
        setaiInPanel.setNenrei(sp[jComboBoxKojin.getSelectedIndex()].getNenrei());
        //チェックが終わるまで選択不可能にする
        jComboBoxKojin.setEnabled(false);
        jButtonKojinInst.setEnabled(false);
    }//GEN-LAST:event_jComboBoxKojinActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        // TODO add your handling code here:
        logDebug("KeyPressed:" + evt.getKeyCode());
        if ((evt.getKeyCode() == 10) || (evt.getKeyCode() == 32)) {
            jButton1ActionPerformed(null);
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void textCaseNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textCaseNoActionPerformed
        // TODO add your handling code here:
        findSetai(textCaseNo.getText());
    }//GEN-LAST:event_textCaseNoActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton7KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7KeyPressed

    private void chkHousyaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHousyaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkHousyaActionPerformed

    private void chkSyougaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSyougaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkSyougaiActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, "openseiho 認定画面\nVer " + OpenSeihoNintei.version + "\n\n作者：田中 秀宗\nAuthor : TANAKA Hidemune", "バージョン情報", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        File file = null;
        URI uri = null;
        try {
            Desktop desktop = Desktop.getDesktop();
            String jarPath = System.getProperty("java.class.path");
            String dirPath = jarPath.substring(0, jarPath.lastIndexOf(File.separator)+1);
            //props.load(new FileInputStream(dirPath + "hoge.properties"));
            file = new File(dirPath + "/OS_NinteiHelp/index.html");
            uri = file.toURI();
            desktop.browse(uri);
        }catch (Exception e) {
            //e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "ヘルプファイルがみつかりません。\n" + uri.getPath(), "ヘルプ", JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(this, "ヘルプファイルがみつかりません。\n" + file.getAbsolutePath(), "ヘルプ", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void textYmdNinteiPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_textYmdNinteiPropertyChange
        // TODO add your handling code here:
        DebugMode = true;
        logDebug("textYmdNinteiPropertyChange");
        setNinteiYMD(textYmdNintei.getID());
    }//GEN-LAST:event_textYmdNinteiPropertyChange

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        //管理者メニュー
        AdminFrame admin = new AdminFrame();
        admin.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButtonKojinCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonKojinCheckActionPerformed
        // TODO add your handling code here:
        //個人状況更新
        //更新は、世帯分まとめて、後で行うものとする
        
        //画面項目チェック
        String msg = "";
//        {"kasanNinpu", "TEXT"},
//        {"kasanSanpu", "TEXT"},
//        {"kasanSyussanYmd", "TEXT"},
        if (chkNinsanpu.isSelected()) {
            if (!(DbAccessOS.isNumeric(comboIDKasanNinpu.getID1()))) {
                if (!(DbAccessOS.isNumeric(comboIDKasanSanpu.getID1()))) {
                    msg = msg + "妊産婦加算がチェックされているのに項目が選択されていません。\n";
                }
            }
        } else {
            if ((DbAccessOS.isNumeric(comboIDKasanNinpu.getID1())) || (DbAccessOS.isNumeric(comboIDKasanSanpu.getID1()))) {
                    msg = msg + "妊産婦加算がチェックされていないのに項目が選択されています。\n";
            }
        }
//        {"kasanSyougai", "TEXT"},
//        {"kasanKaigoHiyou", "INTEGER"},
        if (chkSyougai.isSelected()) {
            if (!(DbAccessOS.isNumeric(comboIDKasanSyougai.getID1()))) {
                msg = msg + "障害者加算がチェックされているのに項目が選択されていません。\n";
            }
        } else {
            if ((DbAccessOS.isNumeric(comboIDKasanSyougai.getID1()))) {
                msg = msg + "障害者加算がチェックされていないのに項目が選択されています。\n";
            }
            if ((DbAccessOS.getValueI(textKaigoHi.getText())) != 0) {
                msg = msg + "障害者加算がチェックされていないのに介護費用が入力されています。\n";
            }
        }
//        {"kasanKaigoNyusyo", "INTEGER"},
        if (chkKaigoSisetu.isSelected()) {
            if ((DbAccessOS.getValueI(textKaigoSisetu.getText())) == 0) {
                msg = msg + "介護施設入所者加算がチェックされているのに金額が入力されていません。\n";
            }
        } else {
            if ((DbAccessOS.getValueI(textKaigoSisetu.getText())) != 0) {
                msg = msg + "介護施設入所者加算がチェックされていないのに金額が入力されています。\n";
            }
        }
//        {"kasanZaitakuFlg", "INTEGER"},
        //チェック項目なし
//        {"kasanHousyasen", "TEXT"},
        if (chkHousya.isSelected()) {
            if (!(DbAccessOS.isNumeric(comboIDHousyasen.getID1()))) {
                msg = msg + "放射線障害者加算がチェックされているのに項目が選択されていません。\n";
            }
        } else {
            if ((DbAccessOS.isNumeric(comboIDHousyasen.getID1()))) {
                msg = msg + "放射線障害者加算がチェックされていないのに項目が選択されています。\n";
            }
        }
//        {"kasanJidouYouiku", "TEXT"},
        if (chkJidouYouiku.isSelected()) {
            if (!(DbAccessOS.isNumeric(comboIDKasanJidouYouiku.getID1()))) {
                msg = msg + "児童養育加算がチェックされているのに項目が選択されていません。\n";
            }
        } else {
            if ((DbAccessOS.isNumeric(comboIDKasanJidouYouiku.getID1()))) {
                msg = msg + "児童養育加算がチェックされていないのに項目が選択されています。\n";
            }
        }
//        {"kasanKaigoHokenRyou", "INTEGER"},
        if (chkKaigoHokenRyou.isSelected()) {
            if ((DbAccessOS.getValueI(textKasanKaigoHokenRyou.getText())) == 0) {
                msg = msg + "介護保険料加算がチェックされているのに金額が入力されていません。\n";
            }
        } else {
            if ((DbAccessOS.getValueI(textKasanKaigoHokenRyou.getText())) != 0) {
                msg = msg + "介護保険料加算がチェックされていないのに金額が入力されています。\n";
            }
        }
//        {"kasanBoshi", "TEXT"},
//        {"kasanBoshiNinzu", "INTEGER"},
        if (chkBoshi.isSelected()) {
            if (!(DbAccessOS.isNumeric(comboIDKasanBoshi.getID1()))) {
                msg = msg + "母子加算がチェックされているのに項目が選択されていません。\n";
            }
            if ((DbAccessOS.getValueI(textKasanBoshiNinzuu.getText())) == 0) {
                msg = msg + "母子加算がチェックされているのに人数が入力されていません。\n";
            }
        } else {
            if ((DbAccessOS.isNumeric(comboIDKasanBoshi.getID1()))) {
                msg = msg + "母子加算がチェックされていないのに項目が選択されています。\n";
            }
            if ((DbAccessOS.getValueI(textKasanBoshiNinzuu.getText())) != 0) {
                msg = msg + "母子加算がチェックされていないのに人数が入力されています。\n";
            }
        }
//        {"kasanTyohukuFlg", "INTEGER"}
        if (chkTyouhuku.isSelected()) {
            if (chkSyougai.isSelected() && chkBoshi.isSelected()) {
                //OK
            } else {
                msg = msg + "重複調整は、障害者加算と母子加算の両方にチェックが入っていないと指定できません。\n";
            }
        }
        //できれば、「前回の起案から変わっていない場合」エラーメッセージを出したい
        //→個人単位では無意味。ここではやらない。
        
        //数値項目チェック
        int inNo = jComboBoxKojin.getSelectedIndex() + 1;
        int kaigoHiyou = DbAccessOS.getValueI(textKaigoHi.getText());
        int kaigoNyusyo = DbAccessOS.getValueI(textKaigoSisetu.getText());
        int kaigoHokenRyou = DbAccessOS.getValueI(textKasanKaigoHokenRyou.getText());
        int boshiNinzuu = DbAccessOS.getValueI(textKasanBoshiNinzuu.getText());
        
        if (kaigoHiyou == DbAccessOS.defaultErrorIntValue) {
            msg = msg + "介護人費用の入力が数値ではありません。\n";
        }
        if (kaigoNyusyo == DbAccessOS.defaultErrorIntValue) {
            msg = msg + "介護施設入所者の入力が数値ではありません。\n";
        }
        if (kaigoHokenRyou == DbAccessOS.defaultErrorIntValue) {
            msg = msg + "介護保険料の入力が数値ではありません。\n";
        }
        if (boshiNinzuu == DbAccessOS.defaultErrorIntValue) {
            msg = msg + "母子加算人数の入力が数値ではありません。\n";
        }
        
        if (!(msg.equals(""))) {
            JOptionPane.showMessageDialog(this, msg);
            return;
        }
        
        //チェック完了：OK
        //画面の内容を保存用配列にセット
        String valueBefore = "";
        String valueAfter = "";
        

        
String[][] field = {
    {"caseNo", valueBefore, textCaseNo.getText()},		//TEXT
    {"inNo", valueBefore, "" + inNo},		//INTEGER
    {"kianYmd", valueBefore, textYmdKian.getID()},		//TEXT
    {"ninteiYmd", valueBefore, textYmdNintei.getID()},		//TEXT
    {"seikatuKeitai", valueBefore, comboIDSeikatuKeitai.getID1()},		//TEXT
    {"kyuti", valueBefore, comboIDKyuti.getID1()},		//TEXT
    {"touki", valueBefore, comboIDTouki.getID1()},		//TEXT
    {"nameKj", valueBefore, setaiInPanel.getNameKj()},		//TEXT
    {"nameKn", valueBefore, setaiInPanel.getNameKn()},		//TEXT
    {"zokuCd", valueBefore, setaiInPanel.getZokugara()},		//TEXT
    {"ninteiNenrei", valueBefore, setaiInPanel.getNenrei()},		//TEXT
    {"kasanNinpu", valueBefore, comboIDKasanNinpu.getID1()},		//TEXT
    {"kasanSanpu", valueBefore, comboIDKasanSanpu.getID1()},		//TEXT
    {"kasanSyussanYmd", valueBefore, textSyussanYmd.getID()},		//TEXT
    {"kasanSyougai", valueBefore, comboIDKasanSyougai.getID1()},		//TEXT
    {"kasanKaigoHiyou", valueBefore, "" + kaigoHiyou},		//INTEGER
    {"kasanKaigoNyusyo", valueBefore, "" + kaigoNyusyo},		//INTEGER
    {"kasanZaitakuFlg", valueBefore, DbAccessOS.isBoolean(chkZaitaku.isSelected())},		//INTEGER
    {"kasanHousyasen", valueBefore, comboIDHousyasen.getID1()},		//TEXT
    {"kasanJidouYouiku", valueBefore, comboIDKasanJidouYouiku.getID1()},		//TEXT
    {"kasanKaigoHokenRyou", valueBefore, "" + kaigoHokenRyou},		//INTEGER
    {"kasanBoshi", valueBefore, comboIDKasanBoshi.getID1()},		//TEXT
    {"kasanBoshiNinzu", valueBefore, "" + boshiNinzuu},		//INTEGER
    {"kasanTyohukuFlg", valueBefore, DbAccessOS.isBoolean(chkTyouhuku.isSelected())}		//INTEGER
};
        //メモリ上に退避
        //既に同じのがないか確認し、あれば削除しておく
        for (int i = 0; i < arrField.size(); i++) {
            //Fieldの２行目の３列目([1][2])で比較（世帯員番号）
            
            if (DbAccessOS.getValueI(arrField.get(i)[1][2]) == inNo) {
                arrField.remove(i);
            }
        }
        arrField.add(field);
        
        //チェックが終わって、初めて選択可能にする
        jComboBoxKojin.setEnabled(true);
        jButtonKojinInst.setEnabled(true);
        JOptionPane.showMessageDialog(this, "チェックが完了しました。\n別の世帯員を設定できます。");
    }//GEN-LAST:event_jButtonKojinCheckActionPerformed

    private void jButtonKojinInstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonKojinInstActionPerformed
        insertKojin();
    }//GEN-LAST:event_jButtonKojinInstActionPerformed

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
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkBoxSyokken;
    private javax.swing.JCheckBox chkBoshi;
    private javax.swing.JCheckBox chkHousya;
    private javax.swing.JCheckBox chkJidouYouiku;
    private javax.swing.JCheckBox chkKaigoHokenRyou;
    private javax.swing.JCheckBox chkKaigoSisetu;
    private javax.swing.JCheckBox chkNinsanpu;
    private javax.swing.JCheckBox chkSyougai;
    private javax.swing.JCheckBox chkTyouhuku;
    private javax.swing.JCheckBox chkZaitaku;
    private openseiho.comboID comboIDHousyasen;
    private openseiho.comboID comboIDKasanBoshi;
    private openseiho.comboID comboIDKasanJidouYouiku;
    private openseiho.comboID comboIDKasanNinpu;
    private openseiho.comboID comboIDKasanSanpu;
    private openseiho.comboID comboIDKasanSyougai;
    private openseiho.comboID comboIDKyuti;
    private openseiho.comboID comboIDNinzuu;
    private openseiho.comboID comboIDSeikatuKeitai;
    private openseiho.comboID comboIDTouki;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButtonKojinCheck;
    private javax.swing.JButton jButtonKojinInst;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBoxKojin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelKasan;
    private javax.swing.JPanel jPanelkojin;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPaneSetai;
    private javax.swing.JTabbedPane jTabbedPane1;
    private openseiho.JyusyoPanel panelJyusyo;
    private javax.swing.JPanel panelSetai;
    private javax.swing.JPanel panelSetaiBase;
    private openseihonintei.SetaiPanel setaiInPanel;
    private javax.swing.JTextField textCaseNo;
    private javax.swing.JTextField textKaigoHi;
    private javax.swing.JTextField textKaigoSisetu;
    private javax.swing.JTextField textKasanBoshiNinzuu;
    private javax.swing.JTextField textKasanKaigoHokenRyou;
    private javax.swing.JTextField textMyouji;
    private javax.swing.JTextField textMyoujiKana;
    private openseiho.textYmdPanel textSyussanYmd;
    private openseiho.textYmdPanel textYmdKian;
    private openseiho.textYmdPanel textYmdNintei;
    // End of variables declaration//GEN-END:variables

}
