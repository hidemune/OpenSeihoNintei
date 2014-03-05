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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
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
private DbKijyun dbKijyun = new DbKijyun();
private DbSaiseihi dbSaiseihi = new DbSaiseihi();
private String[][] rsSetaiPre;
private String[][] rsKojin;
private String[][] rsSaiseihi;
private ArrayList<String[][]> arrFieldKojin = new ArrayList<String[][]>();
//private ArrayList<String[][]> arrFieldSaiseihi = new ArrayList<String[][]>();

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
        setIconImage(new ImageIcon("OpenSeiho.png").getImage());
        //画面を中心に表示
        java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        // 変数desktopBoundsにデスクトップ領域を表すRectangleが代入される
        java.awt.Rectangle desktopBounds = env.getMaximumWindowBounds();
        java.awt.Rectangle thisBounds = this.getBounds();
        thisBounds.setSize(thisBounds.width, 700);                  //画面大きすぎるため
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
        jScrollPaneGetugaku.getVerticalScrollBar().setUnitIncrement(25);
        jScrollPaneHiwari.getVerticalScrollBar().setUnitIncrement(25);
        
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
        
        checkBoxSyokken.setSelected(false);
        
        //コンボボックス デフォルト値を設定
        //comboIDNinzuu.setDefaultID1("1");  //人数のデフォルト
        comboIDKyuti.setDefaultID1(OpenSeihoNintei.DefaultKyuti);
        comboIDTouki.setDefaultID1(OpenSeihoNintei.DefaultTouki);
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
        
        initKojin();
        
        rsSetaiPre = null;
        rsKojin = null;
        arrFieldKojin.clear();
        
        listSetaiIn.setModel(new DefaultListModel());
        list1Ippan.setModel(new DefaultListModel());
        list1Kasan.setModel(new DefaultListModel());
        listGakunen.setModel(new DefaultListModel());
        listIryoKijyun.setModel(new DefaultListModel());
        listIryouHoken.setModel(new DefaultListModel());
        listIryouSonota.setModel(new DefaultListModel());
        listKaigoHoken.setModel(new DefaultListModel());
        listKaigoKijyun.setModel(new DefaultListModel());
        listKaigoSonota.setModel(new DefaultListModel());
        listSetaiIn2.setModel(new DefaultListModel());
        listSetaiIn3.setModel(new DefaultListModel());
        listSetaiIn4.setModel(new DefaultListModel());
        listKyouikuKijyun.setModel(new DefaultListModel());
        listKyouikuKoutuu.setModel(new DefaultListModel());
        listKyouikuKyouzai.setModel(new DefaultListModel());
        listKyouikuKyuusyoku.setModel(new DefaultListModel());
        listKyouikuSienhi.setModel(new DefaultListModel());
        listKyouikuSonota.setModel(new DefaultListModel());
        list1KasanSbt.setModel(new DefaultListModel());
        
        listSetaiIn1.setModel(new DefaultListModel());
        list1Ippan1.setModel(new DefaultListModel());
        list1Kasan1.setModel(new DefaultListModel());
        listGakunen1.setModel(new DefaultListModel());
        listIryoKijyun1.setModel(new DefaultListModel());
        listIryouHoken1.setModel(new DefaultListModel());
        listIryouSonota1.setModel(new DefaultListModel());
        listKaigoHoken1.setModel(new DefaultListModel());
        listKaigoKijyun1.setModel(new DefaultListModel());
        listKaigoSonota1.setModel(new DefaultListModel());
        listSetaiIn5.setModel(new DefaultListModel());
        listSetaiIn6.setModel(new DefaultListModel());
        listSetaiIn7.setModel(new DefaultListModel());
        listKyouikuKijyun1.setModel(new DefaultListModel());
        listKyouikuKoutuu1.setModel(new DefaultListModel());
        listKyouikuKyouzai1.setModel(new DefaultListModel());
        listKyouikuKyuusyoku1.setModel(new DefaultListModel());
        listKyouikuSienhi1.setModel(new DefaultListModel());
        listKyouikuSonota1.setModel(new DefaultListModel());
        list1KasanSbt1.setModel(new DefaultListModel());
        
        text1Ippan.setText("");
        text1Ippan1.setText("");
        text1Kasan.setText("");
        text1Kasan1.setText("");
        text1Total.setText("");
        text1Total1.setText("");
        text2Total.setText("");
        text2Total1.setText("");
        textIryou.setText("");
        textIryou1.setText("");
        textJyutaku.setText("");
        textJyutaku1.setText("");
        textKaigo.setText("");
        textKaigo1.setText("");
        textKimatu.setText("");
        textKimatu1.setText("");
        textKyouiku.setText("");
        textKyouiku1.setText("");
        textSeikatuKei.setText("");
        textSeikatuKei1.setText("");
        textTeigenRitu.setText("");
        textTeigenRitu1.setText("");
        textTotal.setText("");
        textTotal1.setText("");
        textTouki.setText("");
        textTouki1.setText("");
        
    }
    
    public void findSetai(String caseNo) {
        init();
//        String[][] rs = dbSetai.getResultSetTable("WHERE caseNo = '" + caseNo + "'");
        String SQL = "SELECT * FROM setai WHERE caseNo = '" + caseNo + "'";
        String[][] rs = dbSetai.getResultSetTableBySQL(SQL);
        //dbSetai.DebugMode = true;
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
        //DebugMode = true;
        logDebug("認定日：" + kianRet[0]);
        logDebug("起案日：" + kianRet[1]);
        String ninteiYmd = kian.getReturn()[0];
        String kianYmd = kian.getReturn()[1];
        
        if (DbAccessOS.getValueI(ninteiYmd) == 0) {
            //新規登録
            logDebug("個人状況：新規登録");
            return;
        }
        //認定日・起案日をセット
        textYmdKian.setID(kianYmd);
        textYmdNintei.setID(ninteiYmd);
        
        //個人状況を取得・退避
        getKojin();
        
        //認定情報を取得・退避
        
    }
    private void getKojin() {
        rsKojin = dbKojin.getResultSetTable("WHERE kianYmd = '" + textYmdKian.getID() + "' AND ninteiYmd = '" + textYmdNintei.getID() + "' AND caseNo = '" + textCaseNo.getText() + "'");
        
        //構成員チェック付け直し
        for (int i = 0; i < OpenSeihoNintei.MaxSetaiIn; i++) {
            sp[i].setChecked(false);
        }
        //DBに存在するか
        for (int i = 1; i < rsKojin.length; i++) {
            int inNo = dbKojin.getValueI(rsKojin, "inNo", i);
            sp[inNo - 1].setChecked(true);
        }
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
        //全員チェック済か？
        if (jComboBoxKojin.getItemCount() != arrFieldKojin.size()) {
            JOptionPane.showMessageDialog(this, "全員チェックする必要があります。");
            return;
        }
        
        //更新前の確認
        if ((JOptionPane.showConfirmDialog(this, "更新しますか？", "確認", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)) {
            JOptionPane.showMessageDialog(this, "処理を中止しました。");
            return;
        };
        
        //個人インサート処理
        ArrayList lst = new ArrayList();
        
        //インサート前にデリート(1件ずつ全件：１つのSQLで複数件削除はエラーとなることに注意)
        if (rsKojin != null) {
            for (int i = 1; i < rsKojin.length; i++) {
                String[][] field = {
                    {"caseNo", dbKojin.getValue(rsKojin, "caseNo", i), ""},		//TEXT
                    {"inNo", dbKojin.getValue(rsKojin, "inNo", i), ""},		//INTEGER
                    {"kianYmd", dbKojin.getValue(rsKojin, "kianYmd", i), ""},		//INTEGER
                    {"ninteiYmd", dbKojin.getValue(rsKojin, "ninteiYmd", i), ""},		//INTEGER
                };
                //前レコードが見つかったため削除しておく
                String wk = dbKojin.deleteSQL(field);
                lst.add(wk);
                logDebug(wk);
            }
        }
        
        for (int i = 0; i < arrFieldKojin.size(); i++) {
            //インサート処理
            String wk = dbKojin.insertSQL(arrFieldKojin.get(i));
            lst.add(wk);
            logDebug(wk);
        }
        //更新処理
        String[] SQL=(String[])lst.toArray(new String[0]);
        String msg = dbSetai.execSQLUpdate(SQL);
        if (msg.equals("")) {
            JOptionPane.showMessageDialog(this, "更新しました。");
            //同じキーで再読み込み
            getKojin();
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
        jLabel80 = new javax.swing.JLabel();
        textKasanBoshiNinzuuS = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        comboIDSeikatuKeitai = new openseiho.comboID();
        jComboBoxKojin = new javax.swing.JComboBox();
        chkNushi = new javax.swing.JCheckBox();
        comboIDKyuti = new openseiho.comboID();
        comboIDTouki = new openseiho.comboID();
        jButtonKojinCheck = new javax.swing.JButton();
        jButtonKojinInst = new javax.swing.JButton();
        setaiInPanel = new openseihonintei.SetaiPanel();
        jScrollPaneGetugaku = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButtonSaikeisan = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listSetaiIn = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        list1Ippan = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        list1Kasan = new javax.swing.JList();
        jLabel13 = new javax.swing.JLabel();
        text1Ippan = new javax.swing.JTextField();
        text1Kasan = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        textTeigenRitu = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        text1Total = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        text2Total = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        textKimatu = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        textSeikatuKei = new javax.swing.JTextField();
        jScrollPane21 = new javax.swing.JScrollPane();
        list1KasanSbt = new javax.swing.JList();
        jLabel78 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        textTouki = new javax.swing.JTextField();
        text1TotalKyotaku = new javax.swing.JTextField();
        jLabel82 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        textJyutaku = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        listSetaiIn2 = new javax.swing.JList();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        listGakunen = new javax.swing.JList();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        listKyouikuKijyun = new javax.swing.JList();
        jLabel23 = new javax.swing.JLabel();
        textKyouiku = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        listKyouikuKyouzai = new javax.swing.JList();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        listKyouikuKyuusyoku = new javax.swing.JList();
        jLabel26 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        listKyouikuKoutuu = new javax.swing.JList();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        listKyouikuSienhi = new javax.swing.JList();
        jLabel77 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jScrollPane43 = new javax.swing.JScrollPane();
        listKyouikuSonota = new javax.swing.JList();
        jPanel15 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        listSetaiIn3 = new javax.swing.JList();
        jLabel29 = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        listKaigoKijyun = new javax.swing.JList();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        listKaigoHoken = new javax.swing.JList();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane16 = new javax.swing.JScrollPane();
        listKaigoSonota = new javax.swing.JList();
        jLabel33 = new javax.swing.JLabel();
        textKaigo = new javax.swing.JTextField();
        jPanel16 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane17 = new javax.swing.JScrollPane();
        listSetaiIn4 = new javax.swing.JList();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane18 = new javax.swing.JScrollPane();
        listIryoKijyun = new javax.swing.JList();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane19 = new javax.swing.JScrollPane();
        listIryouHoken = new javax.swing.JList();
        jLabel36 = new javax.swing.JLabel();
        jScrollPane20 = new javax.swing.JScrollPane();
        listIryouSonota = new javax.swing.JList();
        jLabel37 = new javax.swing.JLabel();
        textIryou = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        textTotal = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        textBikou = new javax.swing.JTextArea();
        jScrollPaneHiwari = new javax.swing.JScrollPane();
        jPanel17 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jScrollPane22 = new javax.swing.JScrollPane();
        listSetaiIn1 = new javax.swing.JList();
        jScrollPane23 = new javax.swing.JScrollPane();
        list1Ippan1 = new javax.swing.JList();
        jScrollPane24 = new javax.swing.JScrollPane();
        list1Kasan1 = new javax.swing.JList();
        jLabel41 = new javax.swing.JLabel();
        text1Ippan1 = new javax.swing.JTextField();
        text1Kasan1 = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        textTeigenRitu1 = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        text1Total1 = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        text2Total1 = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        textKimatu1 = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        textSeikatuKei1 = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        jScrollPane42 = new javax.swing.JScrollPane();
        list1KasanSbt1 = new javax.swing.JList();
        jLabel83 = new javax.swing.JLabel();
        textTouki1 = new javax.swing.JTextField();
        jPanel19 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        textJyutaku1 = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jScrollPane25 = new javax.swing.JScrollPane();
        listSetaiIn5 = new javax.swing.JList();
        jLabel49 = new javax.swing.JLabel();
        jScrollPane26 = new javax.swing.JScrollPane();
        listGakunen1 = new javax.swing.JList();
        jLabel50 = new javax.swing.JLabel();
        jScrollPane27 = new javax.swing.JScrollPane();
        listKyouikuKijyun1 = new javax.swing.JList();
        jLabel51 = new javax.swing.JLabel();
        textKyouiku1 = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jScrollPane28 = new javax.swing.JScrollPane();
        listKyouikuKyouzai1 = new javax.swing.JList();
        jLabel53 = new javax.swing.JLabel();
        jScrollPane29 = new javax.swing.JScrollPane();
        listKyouikuKyuusyoku1 = new javax.swing.JList();
        jLabel54 = new javax.swing.JLabel();
        jScrollPane30 = new javax.swing.JScrollPane();
        listKyouikuKoutuu1 = new javax.swing.JList();
        jLabel55 = new javax.swing.JLabel();
        jScrollPane31 = new javax.swing.JScrollPane();
        listKyouikuSienhi1 = new javax.swing.JList();
        jScrollPane44 = new javax.swing.JScrollPane();
        listKyouikuSonota1 = new javax.swing.JList();
        jLabel76 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        jScrollPane32 = new javax.swing.JScrollPane();
        listSetaiIn6 = new javax.swing.JList();
        jLabel57 = new javax.swing.JLabel();
        jScrollPane33 = new javax.swing.JScrollPane();
        listKaigoKijyun1 = new javax.swing.JList();
        jLabel58 = new javax.swing.JLabel();
        jScrollPane34 = new javax.swing.JScrollPane();
        listKaigoHoken1 = new javax.swing.JList();
        jLabel59 = new javax.swing.JLabel();
        jScrollPane35 = new javax.swing.JScrollPane();
        listKaigoSonota1 = new javax.swing.JList();
        jLabel60 = new javax.swing.JLabel();
        textKaigo1 = new javax.swing.JTextField();
        jPanel22 = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        jScrollPane36 = new javax.swing.JScrollPane();
        listSetaiIn7 = new javax.swing.JList();
        jLabel62 = new javax.swing.JLabel();
        jScrollPane37 = new javax.swing.JScrollPane();
        listIryoKijyun1 = new javax.swing.JList();
        jLabel63 = new javax.swing.JLabel();
        jScrollPane38 = new javax.swing.JScrollPane();
        listIryouHoken1 = new javax.swing.JList();
        jLabel64 = new javax.swing.JLabel();
        jScrollPane39 = new javax.swing.JScrollPane();
        listIryouSonota1 = new javax.swing.JList();
        jLabel65 = new javax.swing.JLabel();
        textIryou1 = new javax.swing.JTextField();
        jPanel23 = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        jScrollPane40 = new javax.swing.JScrollPane();
        listSetaiIn8 = new javax.swing.JList();
        jScrollPane41 = new javax.swing.JScrollPane();
        listSetaiIn9 = new javax.swing.JList();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        textTotal1 = new javax.swing.JTextField();
        jLabel71 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("生活保護認定");

        jTabbedPane1.setFocusable(false);
        jTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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

        jButton2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButton2.setText("チェック及び更新");
        jButton2.setFocusable(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel6.setText("ケースNo");

        textCaseNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textCaseNoActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText("苗字");

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel2.setText("苗字カナ");

        textMyoujiKana.setToolTipText("ひらがな入力してEnterを押すと、全角カナになります");
        textMyoujiKana.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textMyoujiKanaActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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

        checkBoxSyokken.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel3.setText("加算");

        jPanelKasan.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelKasan.setLayout(new java.awt.GridLayout(8, 1));

        chkNinsanpu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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
        textSyussanYmd.setDebugGraphicsOptions(0);

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
                .add(textSyussanYmd, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
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

        chkSyougai.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel4.setText("介護人費用");

        textKaigoHi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textKaigoHi.setText("\\0");

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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
                .addContainerGap(167, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chkSyougai, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comboIDKasanSyougai, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel4)
                        .add(jLabel7))
                    .add(textKaigoHi, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel7);

        chkKaigoSisetu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        chkKaigoSisetu.setText("介護施設入所者");

        textKaigoSisetu.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textKaigoSisetu.setText("\\0");

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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
                .addContainerGap(496, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chkKaigoSisetu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(textKaigoSisetu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .add(0, 6, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel8);

        chkZaitaku.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        chkZaitaku.setText("在宅患者");

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(chkZaitaku)
                .addContainerGap(672, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(chkZaitaku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 6, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel9);

        chkHousya.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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
                .addContainerGap(535, Short.MAX_VALUE))
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

        chkJidouYouiku.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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
                .addContainerGap(476, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chkJidouYouiku)
                    .add(comboIDKasanJidouYouiku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel11);

        chkKaigoHokenRyou.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        chkKaigoHokenRyou.setText("介護保険料");

        textKasanKaigoHokenRyou.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textKasanKaigoHokenRyou.setText("\\0");

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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
                .addContainerGap(515, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chkKaigoHokenRyou, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(textKasanKaigoHokenRyou, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9))
                .add(0, 6, Short.MAX_VALUE))
        );

        jPanelKasan.add(jPanel12);

        chkBoshi.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        chkBoshi.setText("母子");

        comboIDKasanBoshi.setCaption("");
        comboIDKasanBoshi.setId0(new java.lang.Integer(19));
        comboIDKasanBoshi.setPostCap("");

        textKasanBoshiNinzuu.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textKasanBoshiNinzuu.setText("0");

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel5.setText("人(内施設入所");

        chkTyouhuku.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        chkTyouhuku.setText("重複調整");

        jLabel80.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel80.setText("人）");

        textKasanBoshiNinzuuS.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textKasanBoshiNinzuuS.setText("0");

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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(textKasanBoshiNinzuuS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel80)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 145, Short.MAX_VALUE)
                .add(chkTyouhuku)
                .add(115, 115, 115))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(comboIDKasanBoshi, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, chkBoshi, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel80, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(textKasanBoshiNinzuuS)
                    .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(chkTyouhuku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(textKasanBoshiNinzuu))
                .add(5, 5, 5))
        );

        jPanelKasan.add(jPanel13);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        comboIDSeikatuKeitai.setCaption("生活形態 ");
        comboIDSeikatuKeitai.setComboWidth(new java.lang.Integer(200));
        comboIDSeikatuKeitai.setId0(new java.lang.Integer(4));
        comboIDSeikatuKeitai.setPostCap("");

        jComboBoxKojin.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jComboBoxKojin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxKojin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxKojinActionPerformed(evt);
            }
        });

        chkNushi.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        chkNushi.setText("世帯主");
        chkNushi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNushiActionPerformed(evt);
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

        jButtonKojinCheck.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonKojinCheck.setText("整合性チェック（個人毎）");
        jButtonKojinCheck.setToolTipText("整合性チェック後、他の世帯員を選択し直して下さい。");
        jButtonKojinCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonKojinCheckActionPerformed(evt);
            }
        });

        jButtonKojinInst.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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
                        .add(chkNushi)
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
                        .add(chkNushi)
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
                .addContainerGap(666, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(jPanelkojin);

        jTabbedPane1.addTab("個人状況", jScrollPane2);

        jScrollPaneGetugaku.setDoubleBuffered(true);

        jButton5.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButton5.setText("月額テーブル登録");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButtonSaikeisan.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonSaikeisan.setText("再計算");
        jButtonSaikeisan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaikeisanActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel10.setText("氏名");

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel11.setText("一般分");

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel12.setText("加算額");

        listSetaiIn.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listSetaiIn.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listSetaiIn);

        list1Ippan.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        list1Ippan.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(list1Ippan);

        list1Kasan.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        list1Kasan.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(list1Kasan);

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel13.setText("小計");

        text1Ippan.setText("jTextField1");

        text1Kasan.setText("jTextField1");

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel14.setText("低減率");

        textTeigenRitu.setText("jTextField1");

        jLabel15.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel15.setText("第１類計");

        text1Total.setText("jTextField1");

        jLabel16.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel16.setText("第２類");

        text2Total.setText("jTextField1");

        jLabel17.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel17.setText("期末一時扶助");

        textKimatu.setText("jTextField1");

        jLabel18.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel18.setText("生活費計");

        textSeikatuKei.setText("jTextField1");

        list1KasanSbt.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        list1KasanSbt.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane21.setViewportView(list1KasanSbt);

        jLabel78.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel78.setText("加算種別");

        jLabel81.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel81.setText("冬季加算");

        textTouki.setText("jTextField1");

        text1TotalKyotaku.setText("jTextField1");

        jLabel82.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel82.setText("内居宅一般分");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                                .add(jLabel14)
                                .add(65, 65, 65)
                                .add(textTeigenRitu))
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel10)
                                    .add(jLabel13)
                                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel11)
                                    .add(text1Ippan, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(text1Kasan, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jPanel4Layout.createSequentialGroup()
                                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                            .add(jLabel12))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(jLabel78)
                                            .add(jScrollPane21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))))
                        .add(0, 0, Short.MAX_VALUE))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(jLabel81)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(textTouki, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(jLabel18)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(textSeikatuKei, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4Layout.createSequentialGroup()
                                .add(jLabel17)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(textKimatu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4Layout.createSequentialGroup()
                                .add(jLabel16)
                                .add(65, 65, 65)
                                .add(text2Total))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4Layout.createSequentialGroup()
                                .add(jLabel15)
                                .add(52, 52, 52)
                                .add(text1Total, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel82)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(text1TotalKyotaku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(62, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .add(0, 0, Short.MAX_VALUE)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel10)
                            .add(jLabel11))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel13)
                            .add(text1Ippan, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel12)
                                    .add(jLabel78))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jScrollPane21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(text1Kasan, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel14)
                    .add(textTeigenRitu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel15)
                    .add(text1TotalKyotaku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(text1Total, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel82)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel16)
                    .add(text2Total, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel81)
                    .add(textTouki, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel17)
                    .add(textKimatu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel18)
                    .add(textSeikatuKei, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel19.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel19.setText("住宅費計");

        textJyutaku.setText("jTextField1");

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel19)
                .add(52, 52, 52)
                .add(textJyutaku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(126, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel19)
                    .add(textJyutaku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel20.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel20.setText("氏名");

        listSetaiIn2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listSetaiIn2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane6.setViewportView(listSetaiIn2);

        jLabel21.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel21.setText("学年");

        listGakunen.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listGakunen.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "中学３年", "小学６年", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane7.setViewportView(listGakunen);

        jLabel22.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel22.setText("基準額");

        listKyouikuKijyun.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKyouikuKijyun.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "99,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane8.setViewportView(listKyouikuKijyun);

        jLabel23.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel23.setText("教育費計");

        textKyouiku.setText("jTextField1");

        jLabel24.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel24.setText("教材代");

        listKyouikuKyouzai.setBackground(new java.awt.Color(255, 204, 204));
        listKyouikuKyouzai.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKyouikuKyouzai.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listKyouikuKyouzai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKyouikuKyouzaiKeyPressed(evt);
            }
        });
        jScrollPane9.setViewportView(listKyouikuKyouzai);

        jLabel25.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel25.setText("給食費");

        listKyouikuKyuusyoku.setBackground(new java.awt.Color(255, 204, 204));
        listKyouikuKyuusyoku.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKyouikuKyuusyoku.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listKyouikuKyuusyoku.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKyouikuKyuusyokuKeyPressed(evt);
            }
        });
        jScrollPane10.setViewportView(listKyouikuKyuusyoku);

        jLabel26.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel26.setText("交通費");

        listKyouikuKoutuu.setBackground(new java.awt.Color(255, 204, 204));
        listKyouikuKoutuu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKyouikuKoutuu.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listKyouikuKoutuu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKyouikuKoutuuKeyPressed(evt);
            }
        });
        jScrollPane11.setViewportView(listKyouikuKoutuu);

        jLabel27.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel27.setText("支援費");

        listKyouikuSienhi.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKyouikuSienhi.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listKyouikuSienhi.setToolTipText("");
        listKyouikuSienhi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKyouikuSienhiKeyPressed(evt);
            }
        });
        jScrollPane12.setViewportView(listKyouikuSienhi);

        jLabel77.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel77.setText("その他には学級費や再支給分を加算してください。");

        jLabel84.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel84.setText("その他");

        listKyouikuSonota.setBackground(new java.awt.Color(255, 204, 204));
        listKyouikuSonota.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKyouikuSonota.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listKyouikuSonota.setToolTipText("");
        listKyouikuSonota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKyouikuSonotaKeyPressed(evt);
            }
        });
        jScrollPane43.setViewportView(listKyouikuSonota);

        org.jdesktop.layout.GroupLayout jPanel14Layout = new org.jdesktop.layout.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel20)
                            .add(jScrollPane6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel21)
                            .add(jScrollPane7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel22)
                            .add(jScrollPane8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel24)
                            .add(jScrollPane9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel25)
                            .add(jScrollPane10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel26)
                            .add(jScrollPane11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel27)
                            .add(jScrollPane12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel84)
                            .add(jScrollPane43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(0, 36, Short.MAX_VALUE))
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(jLabel23)
                        .add(52, 52, 52)
                        .add(textKyouiku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel77)
                        .add(37, 37, 37))))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel14Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel14Layout.createSequentialGroup()
                            .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(jLabel20)
                                .add(jLabel21))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jScrollPane6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jScrollPane7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel14Layout.createSequentialGroup()
                                .add(jLabel22)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel14Layout.createSequentialGroup()
                                .add(jLabel24)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(jLabel25)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(jLabel26)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(jLabel27)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(jLabel84)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel23)
                    .add(textKyouiku, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel77)))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel28.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel28.setText("氏名");

        listSetaiIn3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listSetaiIn3.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane13.setViewportView(listSetaiIn3);

        jLabel29.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel29.setText("概算月額");

        listKaigoKijyun.setBackground(new java.awt.Color(255, 204, 204));
        listKaigoKijyun.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKaigoKijyun.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "999,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listKaigoKijyun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKaigoKijyunKeyPressed(evt);
            }
        });
        jScrollPane14.setViewportView(listKaigoKijyun);

        jLabel30.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel30.setText("介護保険");

        listKaigoHoken.setBackground(new java.awt.Color(255, 204, 204));
        listKaigoHoken.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKaigoHoken.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "999,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listKaigoHoken.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKaigoHokenKeyPressed(evt);
            }
        });
        jScrollPane15.setViewportView(listKaigoHoken);

        jLabel31.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel31.setText("その他公費");

        listKaigoSonota.setBackground(new java.awt.Color(255, 204, 204));
        listKaigoSonota.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKaigoSonota.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "999,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listKaigoSonota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKaigoSonotaKeyPressed(evt);
            }
        });
        jScrollPane16.setViewportView(listKaigoSonota);

        jLabel33.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel33.setText("介護費計");

        textKaigo.setText("jTextField1");

        org.jdesktop.layout.GroupLayout jPanel15Layout = new org.jdesktop.layout.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel15Layout.createSequentialGroup()
                        .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel28)
                            .add(jScrollPane13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel29)
                            .add(jScrollPane14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel15Layout.createSequentialGroup()
                        .add(jLabel33)
                        .add(52, 52, 52)
                        .add(textKaigo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel15Layout.createSequentialGroup()
                        .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel28)
                            .add(jLabel29))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jPanel15Layout.createSequentialGroup()
                            .add(jLabel30)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanel15Layout.createSequentialGroup()
                            .add(jLabel31)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel33)
                    .add(textKaigo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel32.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel32.setText("氏名");

        listSetaiIn4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listSetaiIn4.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane17.setViewportView(listSetaiIn4);

        jLabel34.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel34.setText("概算月額");

        listIryoKijyun.setBackground(new java.awt.Color(255, 204, 204));
        listIryoKijyun.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listIryoKijyun.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "999,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listIryoKijyun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listIryoKijyunKeyPressed(evt);
            }
        });
        jScrollPane18.setViewportView(listIryoKijyun);

        jLabel35.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel35.setText("医療保険");

        listIryouHoken.setBackground(new java.awt.Color(255, 204, 204));
        listIryouHoken.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listIryouHoken.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "999,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listIryouHoken.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listIryouHokenKeyPressed(evt);
            }
        });
        jScrollPane19.setViewportView(listIryouHoken);

        jLabel36.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel36.setText("その他公費      ");

        listIryouSonota.setBackground(new java.awt.Color(255, 204, 204));
        listIryouSonota.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listIryouSonota.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "999,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listIryouSonota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listIryouSonotaKeyPressed(evt);
            }
        });
        jScrollPane20.setViewportView(listIryouSonota);

        jLabel37.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel37.setText("医療費計");

        textIryou.setText("jTextField1");

        org.jdesktop.layout.GroupLayout jPanel16Layout = new org.jdesktop.layout.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel16Layout.createSequentialGroup()
                        .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel32)
                            .add(jScrollPane17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel34)
                            .add(jScrollPane18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel36)
                            .add(jScrollPane20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel16Layout.createSequentialGroup()
                        .add(jLabel37)
                        .add(52, 52, 52)
                        .add(textIryou, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel16Layout.createSequentialGroup()
                        .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel32)
                            .add(jLabel34))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jPanel16Layout.createSequentialGroup()
                            .add(jLabel35)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanel16Layout.createSequentialGroup()
                            .add(jLabel36)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel37)
                    .add(textIryou, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel70.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel70.setText("月額テーブルを更新する画面です。状況変更の度に起案してください。");

        jLabel72.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel72.setText("月額データは、別画面の「一括認定」で自動作成される場合があります。");

        jLabel73.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel73.setText("月額計");

        textTotal.setText("jTextField1");

        jLabel75.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel75.setText("リストは、手修正が可能です。選択してEnterを押してください。");

        textBikou.setColumns(20);
        textBikou.setFont(new java.awt.Font("VL ゴシック", 0, 14)); // NOI18N
        textBikou.setRows(5);
        jScrollPane3.setViewportView(textBikou);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel70)
                        .add(36, 36, 36)
                        .add(jLabel73)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(textTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonSaikeisan)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton5))
                    .add(jLabel72)
                    .add(jLabel75)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 384, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jScrollPane3))))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel70)
                    .add(jLabel73)
                    .add(textTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonSaikeisan)
                    .add(jButton5))
                .add(2, 2, 2)
                .add(jLabel72)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel75)
                .add(14, 14, 14)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane3))
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jScrollPaneGetugaku.setViewportView(jPanel3);

        jTabbedPane1.addTab("最低生活費月額", jScrollPaneGetugaku);

        jPanel18.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel38.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel38.setText("氏名");

        jLabel39.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel39.setText("一般分");

        jLabel40.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel40.setText("加算額");

        listSetaiIn1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listSetaiIn1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane22.setViewportView(listSetaiIn1);

        list1Ippan1.setBackground(new java.awt.Color(255, 204, 204));
        list1Ippan1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        list1Ippan1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane23.setViewportView(list1Ippan1);

        list1Kasan1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        list1Kasan1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane24.setViewportView(list1Kasan1);

        jLabel41.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel41.setText("小計");

        text1Ippan1.setText("jTextField1");

        text1Kasan1.setText("jTextField1");

        jLabel42.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel42.setText("低減率");

        textTeigenRitu1.setText("jTextField1");

        jLabel43.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel43.setText("第１類計");

        text1Total1.setText("jTextField1");

        jLabel44.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel44.setText("第２類");

        text2Total1.setText("jTextField1");

        jLabel45.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel45.setText("期末一時扶助");

        textKimatu1.setText("jTextField1");

        jLabel46.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel46.setText("生活費計");

        textSeikatuKei1.setText("jTextField1");

        jLabel79.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel79.setText("加算種別");

        list1KasanSbt1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        list1KasanSbt1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane42.setViewportView(list1KasanSbt1);

        jLabel83.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel83.setText("冬季加算");

        textTouki1.setText("jTextField1");

        org.jdesktop.layout.GroupLayout jPanel18Layout = new org.jdesktop.layout.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(jPanel18Layout.createSequentialGroup()
                            .add(jLabel46)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(textSeikatuKei1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel18Layout.createSequentialGroup()
                            .add(jLabel44)
                            .add(65, 65, 65)
                            .add(text2Total1))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel18Layout.createSequentialGroup()
                            .add(jLabel43)
                            .add(52, 52, 52)
                            .add(text1Total1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel18Layout.createSequentialGroup()
                        .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel38)
                            .add(jLabel41)
                            .add(jScrollPane22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel39)
                            .add(text1Ippan1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel40)
                            .add(text1Kasan1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel79)
                            .add(jScrollPane42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 142, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel18Layout.createSequentialGroup()
                        .add(jLabel42)
                        .add(65, 65, 65)
                        .add(textTeigenRitu1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 300, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel18Layout.createSequentialGroup()
                        .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel18Layout.createSequentialGroup()
                                .add(101, 101, 101)
                                .add(textTouki1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jLabel83))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel45)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(textKimatu1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel18Layout.createSequentialGroup()
                .add(0, 6, Short.MAX_VALUE)
                .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel18Layout.createSequentialGroup()
                        .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel38)
                            .add(jLabel39))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel41)
                            .add(text1Ippan1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel18Layout.createSequentialGroup()
                        .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel18Layout.createSequentialGroup()
                                .add(jLabel40)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel18Layout.createSequentialGroup()
                                .add(jLabel79)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(text1Kasan1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel42)
                    .add(textTeigenRitu1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel43)
                    .add(text1Total1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel44)
                    .add(text2Total1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel83)
                    .add(textTouki1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel45)
                    .add(textKimatu1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel46)
                    .add(textSeikatuKei1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel19.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel47.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel47.setText("住宅費計");

        textJyutaku1.setText("jTextField1");

        org.jdesktop.layout.GroupLayout jPanel19Layout = new org.jdesktop.layout.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel47)
                .add(52, 52, 52)
                .add(textJyutaku1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(117, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel47)
                    .add(textJyutaku1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel48.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel48.setText("氏名");

        listSetaiIn5.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listSetaiIn5.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane25.setViewportView(listSetaiIn5);

        jLabel49.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel49.setText("学年");

        listGakunen1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listGakunen1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "中学３年", "小学６年", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane26.setViewportView(listGakunen1);

        jLabel50.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel50.setText("基準額");

        listKyouikuKijyun1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKyouikuKijyun1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "99,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane27.setViewportView(listKyouikuKijyun1);

        jLabel51.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel51.setText("教育費計");

        textKyouiku1.setText("jTextField1");

        jLabel52.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel52.setText("教材代");

        listKyouikuKyouzai1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKyouikuKyouzai1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane28.setViewportView(listKyouikuKyouzai1);

        jLabel53.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel53.setText("給食費");

        listKyouikuKyuusyoku1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKyouikuKyuusyoku1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane29.setViewportView(listKyouikuKyuusyoku1);

        jLabel54.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel54.setText("交通費");

        listKyouikuKoutuu1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKyouikuKoutuu1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane30.setViewportView(listKyouikuKoutuu1);

        jLabel55.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel55.setText("支援費");

        listKyouikuSienhi1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKyouikuSienhi1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane31.setViewportView(listKyouikuSienhi1);

        listKyouikuSonota1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKyouikuSonota1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane44.setViewportView(listKyouikuSonota1);

        jLabel76.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel76.setText("その他");

        org.jdesktop.layout.GroupLayout jPanel20Layout = new org.jdesktop.layout.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel20Layout.createSequentialGroup()
                        .add(jLabel51)
                        .add(52, 52, 52)
                        .add(textKyouiku1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel20Layout.createSequentialGroup()
                        .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel48)
                            .add(jScrollPane25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel49)
                            .add(jScrollPane26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel50)
                            .add(jScrollPane27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel52)
                            .add(jScrollPane28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel53)
                            .add(jScrollPane29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel54)
                            .add(jScrollPane30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel55)
                            .add(jScrollPane31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel76)
                            .add(jScrollPane44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel20Layout.createSequentialGroup()
                            .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(jLabel48)
                                .add(jLabel49))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jScrollPane25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jScrollPane26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel20Layout.createSequentialGroup()
                                .add(jLabel50)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel20Layout.createSequentialGroup()
                                .add(jLabel52)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(jPanel20Layout.createSequentialGroup()
                        .add(jLabel53)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel20Layout.createSequentialGroup()
                        .add(jLabel54)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel20Layout.createSequentialGroup()
                        .add(jLabel55)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel20Layout.createSequentialGroup()
                        .add(jLabel76)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel51)
                    .add(textKyouiku1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel21.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel56.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel56.setText("氏名");

        listSetaiIn6.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listSetaiIn6.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane32.setViewportView(listSetaiIn6);

        jLabel57.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel57.setText("概算月額");

        listKaigoKijyun1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKaigoKijyun1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "999,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane33.setViewportView(listKaigoKijyun1);

        jLabel58.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel58.setText("介護保険");

        listKaigoHoken1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKaigoHoken1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "999,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane34.setViewportView(listKaigoHoken1);

        jLabel59.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel59.setText("その他公費");

        listKaigoSonota1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listKaigoSonota1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane35.setViewportView(listKaigoSonota1);

        jLabel60.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel60.setText("介護費計");

        textKaigo1.setText("jTextField1");

        org.jdesktop.layout.GroupLayout jPanel21Layout = new org.jdesktop.layout.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel21Layout.createSequentialGroup()
                        .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel56)
                            .add(jScrollPane32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel57)
                            .add(jScrollPane33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel59)
                            .add(jScrollPane35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel21Layout.createSequentialGroup()
                        .add(jLabel60)
                        .add(52, 52, 52)
                        .add(textKaigo1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel21Layout.createSequentialGroup()
                        .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel56)
                            .add(jLabel57))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jPanel21Layout.createSequentialGroup()
                            .add(jLabel58)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanel21Layout.createSequentialGroup()
                            .add(jLabel59)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel60)
                    .add(textKaigo1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel22.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel61.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel61.setText("氏名");

        listSetaiIn7.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listSetaiIn7.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane36.setViewportView(listSetaiIn7);

        jLabel62.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel62.setText("概算月額");

        listIryoKijyun1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listIryoKijyun1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "999,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane37.setViewportView(listIryoKijyun1);

        jLabel63.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel63.setText("医療保険");

        listIryouHoken1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listIryouHoken1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "999,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane38.setViewportView(listIryouHoken1);

        jLabel64.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel64.setText("その他公費      ");

        listIryouSonota1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listIryouSonota1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "999,999", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane39.setViewportView(listIryouSonota1);

        jLabel65.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel65.setText("医療費計");

        textIryou1.setText("jTextField1");

        org.jdesktop.layout.GroupLayout jPanel22Layout = new org.jdesktop.layout.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel22Layout.createSequentialGroup()
                        .add(jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel61)
                            .add(jScrollPane36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel62)
                            .add(jScrollPane37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel64)
                            .add(jScrollPane39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel22Layout.createSequentialGroup()
                        .add(jLabel65)
                        .add(52, 52, 52)
                        .add(textIryou1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel22Layout.createSequentialGroup()
                        .add(jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel61)
                            .add(jLabel62))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jPanel22Layout.createSequentialGroup()
                            .add(jLabel63)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanel22Layout.createSequentialGroup()
                            .add(jLabel64)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel65)
                    .add(textIryou1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel23.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel66.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel66.setText("生活歴");

        listSetaiIn8.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listSetaiIn8.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "認定日:H26/04/01（起案日:H26/03/24）", "認定日:H26/04/10（起案日:H26/04/10）", "認定日:H26/04/20（起案日:H26/04/20）", " " };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane40.setViewportView(listSetaiIn8);

        listSetaiIn9.setBackground(new java.awt.Color(255, 204, 204));
        listSetaiIn9.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        listSetaiIn9.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "9", "10", "11" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane41.setViewportView(listSetaiIn9);

        jLabel67.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel67.setText("分母は、２月のみ実日数、その他は30日となります。");

        jLabel68.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel68.setText("日数");

        jLabel69.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel69.setText("日数は、手修正が可能です。選択してEnterを押してください。");

        jLabel74.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel74.setText("月額計");

        textTotal1.setText("jTextField1");

        org.jdesktop.layout.GroupLayout jPanel23Layout = new org.jdesktop.layout.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel66)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 252, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel68)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jPanel23Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel23Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel23Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel67)
                            .add(jLabel69))
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel23Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel74)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(textTotal1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(38, 38, 38))))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel23Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 106, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel66)
                    .add(jScrollPane40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 106, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel23Layout.createSequentialGroup()
                        .add(8, 8, 8)
                        .add(jPanel23Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel74)
                            .add(textTotal1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel69)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel67))
                    .add(jLabel68))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel71.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel71.setText("認定テーブルを更新する画面です。毎月１レコード作成します。別画面の「一括認定」後に修正することもできます。");

        jButton3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButton3.setText("登録・印刷");

        org.jdesktop.layout.GroupLayout jPanel17Layout = new org.jdesktop.layout.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel17Layout.createSequentialGroup()
                        .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel17Layout.createSequentialGroup()
                                .add(jPanel18, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(18, 18, 18)
                                .add(jPanel19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel17Layout.createSequentialGroup()
                                .add(jLabel71)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 98, Short.MAX_VALUE)
                                .add(jButton3)))
                        .add(29, 29, 29))
                    .add(jPanel17Layout.createSequentialGroup()
                        .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel17Layout.createSequentialGroup()
                                .add(jPanel21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(jPanel22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel71)
                    .add(jButton3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(12, 12, 12)
                .add(jPanel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jScrollPaneHiwari.setViewportView(jPanel17);

        jTabbedPane1.addTab("日割計算・調書２作成", jScrollPaneHiwari);

        jMenu3.setText("ファイル");
        jMenu3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        jMenuItem4.setText("閉じる");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        jMenuBar1.add(jMenu3);

        jMenu2.setText("管理者メニュー");
        jMenu2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        jMenuItem3.setText("データベース管理");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        jMenu1.setText("Help");
        jMenu1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

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
            .add(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 971, Short.MAX_VALUE)
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
        //DebugMode = true;
        logDebug("Tab changed to: " + index + "/" + sourceTabbedPane.getTitleAt(index));
        //個人状況が選択された場合
        if (index == 1) {
            jComboBoxKojin.removeAllItems();
            for (int i = 0; i < sp.length; i++) {
                if (sp[i].isChecked()) {
                    jComboBoxKojin.addItem((i + 1) + "." + sp[i].getNameKj());
                }
            }
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jComboBoxKojinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxKojinActionPerformed
        //世帯員選択
        int inNo = jComboBoxKojin.getSelectedIndex();
        if (inNo < 0) {
            return;
        }
        
        initKojin();
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
        
        //DBに存在するか
        if (rsKojin == null) {
            return;
        }
        
        for (int i = 1; i < rsKojin.length; i++) {
            if (inNo == (dbKojin.getValueI(rsKojin, "inNo", i) - 1)) {
                setKojin(i);
                break;
            }
        }

    }//GEN-LAST:event_jComboBoxKojinActionPerformed
    private void initKojin() {
        setaiInPanel.setChecked(false);
        setaiInPanel.setNameKj("");
        setaiInPanel.setNameKn("");
        setaiInPanel.setSeibetu("");
        setaiInPanel.setZokugara("");
        setaiInPanel.setBirthYmd("");
        setaiInPanel.setNenrei("");
        
        chkNushi.setSelected(false);
        //加算画面初期化
        chkHousya.setSelected(false);
        chkBoshi.setSelected(false);
        chkJidouYouiku.setSelected(false);
        chkKaigoHokenRyou.setSelected(false);
        chkKaigoSisetu.setSelected(false);
        chkNinsanpu.setSelected(false);
        chkSyougai.setSelected(false);
        chkTyouhuku.setSelected(false);
        chkZaitaku.setSelected(false);
        comboIDHousyasen.setID1("");
        comboIDKasanBoshi.setID1("");
        comboIDKasanJidouYouiku.setID1("");
        comboIDKasanNinpu.setID1("");
        comboIDKasanSanpu.setID1("");
        comboIDKasanSyougai.setID1("");
        textKaigoHi.setText("0");
        textKaigoSisetu.setText("0");
        textKasanBoshiNinzuu.setText("0");
        textKasanBoshiNinzuuS.setText("0");
        textKasanKaigoHokenRyou.setText("0");
        textSyussanYmd.setID("00000000");
        
        chkBoshi.setSelected(false);
        chkBoshi.setEnabled(false);
    }
    private void setKojin(int idx) {
        //認定日・起案日をセット
        //textYmdKian.setID(dbKojin.getValue(rsKojin, "kianYmd", idx));
        //textYmdNintei.setID(dbKojin.getValue(rsKojin, "ninteiYmd", idx));
        
        chkNushi.setSelected(dbKojin.getValueB(rsKojin, "nushiFlg", idx));
//        {"kasanNinpu", "TEXT"},
        comboIDKasanNinpu.setID1(dbKojin.getValueI(rsKojin, "kasanNinpu", idx));
//        {"kasanSanpu", "TEXT"},
        comboIDKasanSanpu.setID1(dbKojin.getValueI(rsKojin, "kasanSanpu", idx));
//        {"kasanSyussanYmd", "TEXT"},
        textSyussanYmd.setID(dbKojin.getValue(rsKojin, "kasanSyussanYmd", idx));
//        {"kasanSyougai", "TEXT"},
        comboIDKasanSyougai.setID1(dbKojin.getValueI(rsKojin, "kasanSyougai", idx));
//        {"kasanKaigoHiyou", "INTEGER"},
        textKaigoHi.setText("" + dbKojin.getValueI(rsKojin, "kasanKaigoHiyou", idx));
//        {"kasanKaigoNyusyo", "INTEGER"},
        textKaigoSisetu.setText("" + dbKojin.getValueI(rsKojin, "kasanKaigoNyusyo", idx));
//        {"kasanZaitakuFlg", "INTEGER"},
        chkZaitaku.setSelected(dbKojin.getValueB(rsKojin, "kasanZaitakuFlg", idx));
//        {"kasanHousyasen", "TEXT"},
        comboIDHousyasen.setID1(dbKojin.getValueI(rsKojin, "kasanHousyasen", idx));
//        {"kasanJidouYouiku", "TEXT"},
        comboIDKasanJidouYouiku.setID1(dbKojin.getValueI(rsKojin, "kasanJidouYouiku", idx));
//        {"kasanKaigoHokenRyou", "INTEGER"},
        textKasanKaigoHokenRyou.setText("" + dbKojin.getValueI(rsKojin, "kasanKaigoHokenRyou", idx));
//        {"kasanBoshi", "TEXT"},
        comboIDKasanBoshi.setID1(dbKojin.getValueI(rsKojin, "kasanBoshi", idx));
//        {"kasanBoshiNinzu", "INTEGER"},
        textKasanBoshiNinzuu.setText("" + dbKojin.getValueI(rsKojin, "kasanBoshiNinzu", idx));
        textKasanBoshiNinzuuS.setText("" + dbKojin.getValueI(rsKojin, "kasanBoshiNinzuS", idx));
//        {"kasanTyohukuFlg", "INTEGER"}
        chkTyouhuku.setSelected(dbKojin.getValueB(rsKojin, "kasanTyohukuFlg", idx));
        //チェック付け直し
        if (DbAccessOS.isNumeric(comboIDKasanNinpu.getID1())) {
            chkNinsanpu.setSelected(true);
        }
        if (DbAccessOS.isNumeric(comboIDKasanSanpu.getID1())) {
            chkNinsanpu.setSelected(true);
        }
        if (DbAccessOS.isNumeric(comboIDKasanSyougai.getID1())) {
            chkSyougai.setSelected(true);
        }
        if (DbAccessOS.getValueI(textKaigoSisetu.getText()) > 0) {
            chkKaigoSisetu.setSelected(true);
        }
        if (DbAccessOS.isNumeric(comboIDHousyasen.getID1())) {
            chkHousya.setSelected(true);
        }
        if (DbAccessOS.isNumeric(comboIDKasanJidouYouiku.getID1())) {
            chkJidouYouiku.setSelected(true);
        }
        if (DbAccessOS.getValueI(textKasanKaigoHokenRyou.getText()) > 0) {
            chkKaigoHokenRyou.setSelected(true);
        }
        if (DbAccessOS.isNumeric(comboIDKasanBoshi.getID1())) {
            chkBoshi.setSelected(true);
        }
        
        //世帯主を選択した場合、母子加算をアクティブ化
        if (chkNushi.isSelected()) {
            chkBoshi.setEnabled(true);
        } else {
            chkBoshi.setSelected(false);
            chkBoshi.setEnabled(false);
        }
    }
    
    private void chkNushiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNushiActionPerformed
        //世帯主を選択した場合、母子加算をアクティブ化
        if (chkNushi.isSelected()) {
            chkBoshi.setEnabled(true);
        } else {
            chkBoshi.setSelected(false);
            chkBoshi.setEnabled(false);
        }
    }//GEN-LAST:event_chkNushiActionPerformed

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
        //DebugMode = true;
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
        //int inNo = jComboBoxKojin.getSelectedIndex() + 1; 間違い！！！
        String[] str = ((String)jComboBoxKojin.getSelectedItem()).split("\\.");
        int inNo = DbAccessOS.getValueI(str[0]);
        if (inNo == DbAccessOS.defaultErrorIntValue) {
            JOptionPane.showMessageDialog(this, "員番号が取得できません。");
            return;
        }
        if (inNo == 0) {
            JOptionPane.showMessageDialog(this, "員番号が取得できません。");
            return;
        }
        int kaigoHiyou = DbAccessOS.getValueI(textKaigoHi.getText());
        int kaigoNyusyo = DbAccessOS.getValueI(textKaigoSisetu.getText());
        int kaigoHokenRyou = DbAccessOS.getValueI(textKasanKaigoHokenRyou.getText());
        int boshiNinzuu = DbAccessOS.getValueI(textKasanBoshiNinzuu.getText());
        int boshiNinzuuS = DbAccessOS.getValueI(textKasanBoshiNinzuuS.getText());
        
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
        if (boshiNinzuuS == DbAccessOS.defaultErrorIntValue) {
            msg = msg + "母子加算人数(施設分)の入力が数値ではありません。\n";
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
    {"nushiFlg", valueBefore, DbAccessOS.isBoolean(chkNushi.isSelected())},		//INTEGER
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
    {"kasanBoshiNinzuS", valueBefore, "" + boshiNinzuuS},		//INTEGER
    {"kasanTyohukuFlg", valueBefore, DbAccessOS.isBoolean(chkTyouhuku.isSelected())}		//INTEGER
};
        //メモリ上に退避
        //既に同じのがないか確認し、あれば削除しておく
        for (int i = 0; i < arrFieldKojin.size(); i++) {
            //Fieldの２行目の３列目([1][2])で比較（世帯員番号）
            
            if (DbAccessOS.getValueI(arrFieldKojin.get(i)[1][2]) == inNo) {
                arrFieldKojin.remove(i);
            }
        }
        arrFieldKojin.add(field);
        
        //チェックが終わって、初めて選択可能にする
        jComboBoxKojin.setEnabled(true);
        jButtonKojinInst.setEnabled(true);
        JOptionPane.showMessageDialog(this, "チェックが完了しました。\n別の世帯員を設定できます。");
    }//GEN-LAST:event_jButtonKojinCheckActionPerformed

    private void jButtonKojinInstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonKojinInstActionPerformed
        insertKojin();
    }//GEN-LAST:event_jButtonKojinInstActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // Close
        System.exit(0);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jButtonSaikeisanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaikeisanActionPerformed
        if (listSetaiIn.getModel().getSize() == 0) {
            //最低生活費認定　設定
            calc();
            recalc();
        } else {
            //画面から再計算
            recalc();
        }
    }//GEN-LAST:event_jButtonSaikeisanActionPerformed

    private void recalc() {
        //教育費算出
        int kyouiku = 0;
        for (int i = 0; i < listKyouikuKijyun.getModel().getSize(); i++) {
            kyouiku = kyouiku + DbAccessOS.getValueI((String) listKyouikuKijyun.getModel().getElementAt(i));
            kyouiku = kyouiku + DbAccessOS.getValueI((String) listKyouikuKoutuu.getModel().getElementAt(i));
            kyouiku = kyouiku + DbAccessOS.getValueI((String) listKyouikuKyouzai.getModel().getElementAt(i));
            kyouiku = kyouiku + DbAccessOS.getValueI((String) listKyouikuKyuusyoku.getModel().getElementAt(i));
            kyouiku = kyouiku + DbAccessOS.getValueI((String) listKyouikuSienhi.getModel().getElementAt(i));
            kyouiku = kyouiku + DbAccessOS.getValueI((String) listKyouikuSonota.getModel().getElementAt(i));
        }
        textKyouiku.setText("" + kyouiku);
        
        //医療費算出
        int iryo = 0;
        for (int i = 0; i < listIryoKijyun.getModel().getSize(); i++) {
            iryo = iryo + DbAccessOS.getValueI((String) listIryoKijyun.getModel().getElementAt(i));
            iryo = iryo - DbAccessOS.getValueI((String) listIryouHoken.getModel().getElementAt(i));
            iryo = iryo - DbAccessOS.getValueI((String) listIryouSonota.getModel().getElementAt(i));
        }
        textIryou.setText("" + iryo);
        
        //介護費算出
        int kaigo = 0;
        for (int i = 0; i < listIryoKijyun.getModel().getSize(); i++) {
            kaigo = kaigo + DbAccessOS.getValueI((String) listIryoKijyun.getModel().getElementAt(i));
            kaigo = kaigo - DbAccessOS.getValueI((String) listIryouHoken.getModel().getElementAt(i));
            kaigo = kaigo - DbAccessOS.getValueI((String) listIryouSonota.getModel().getElementAt(i));
        }
        textKaigo.setText("" + kaigo);
        
        int jyutaku = DbAccessOS.getValueI(textJyutaku.getText());
        
        //生活扶助
        int seikatu = 0;
        
        
        seikatu = DbAccessOS.getValueI(textSeikatuKei.getText());
        textTotal.setText("" + (seikatu + jyutaku + kyouiku + kaigo + iryo));
    }
    private void listNumSet(java.awt.event.KeyEvent evt) {
        if (evt.getKeyChar() ==  '\n') {
            JList lst = (JList)evt.getSource();
            DefaultListModel lstM = ((DefaultListModel)lst.getModel());
            String strNum = (String) lst.getSelectedValue();
            inputNumDialog inp = new inputNumDialog(this, true, DbAccessOS.getValueI(strNum));
            int ret = inp.getNumber();
            lstM.setElementAt("" + ret, lst.getSelectedIndex());
        }
    }
    private void listKyouikuKyouzaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKyouikuKyouzaiKeyPressed
        //list*KeyPressed
        listNumSet(evt);
    }//GEN-LAST:event_listKyouikuKyouzaiKeyPressed

    private void listKyouikuKyuusyokuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKyouikuKyuusyokuKeyPressed
        //list*KeyPressed
        listNumSet(evt);
    }//GEN-LAST:event_listKyouikuKyuusyokuKeyPressed

    private void listKyouikuKoutuuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKyouikuKoutuuKeyPressed
        //list*KeyPressed
        listNumSet(evt);
    }//GEN-LAST:event_listKyouikuKoutuuKeyPressed

    private void listKyouikuSienhiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKyouikuSienhiKeyPressed
        //list*KeyPressed
        listNumSet(evt);
    }//GEN-LAST:event_listKyouikuSienhiKeyPressed

    private void listKaigoKijyunKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKaigoKijyunKeyPressed
        //list*KeyPressed
        listNumSet(evt);
    }//GEN-LAST:event_listKaigoKijyunKeyPressed

    private void listKaigoHokenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKaigoHokenKeyPressed
        //list*KeyPressed
        listNumSet(evt);
    }//GEN-LAST:event_listKaigoHokenKeyPressed

    private void listKaigoSonotaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKaigoSonotaKeyPressed
        //list*KeyPressed
        listNumSet(evt);
    }//GEN-LAST:event_listKaigoSonotaKeyPressed

    private void listIryoKijyunKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listIryoKijyunKeyPressed
        //list*KeyPressed
        listNumSet(evt);
    }//GEN-LAST:event_listIryoKijyunKeyPressed

    private void listIryouHokenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listIryouHokenKeyPressed
        //list*KeyPressed
        listNumSet(evt);
    }//GEN-LAST:event_listIryouHokenKeyPressed

    private void listIryouSonotaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listIryouSonotaKeyPressed
        //list*KeyPressed
        listNumSet(evt);
    }//GEN-LAST:event_listIryouSonotaKeyPressed

    private void listKyouikuSonotaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKyouikuSonotaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_listKyouikuSonotaKeyPressed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        instSaiseihi();
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * 調書２の内容をテーブルに保存する。
     */
    private void instSaiseihi() {
        //エラーチェック
        //世帯主１人か？
        int nusi = 0;
        for (int i = 1; i < rsKojin.length; i++) {
            if (dbKojin.getValueB(rsKojin, "nushiFlg", i)) {
                nusi = nusi + 1;
            }
        }
        if (nusi <= 0) {
            JOptionPane.showMessageDialog(this, "世帯主が設定されていません。世帯主欄にチェックを入れてください。");
            return;
        }
        if (nusi > 1) {
            JOptionPane.showMessageDialog(this, "世帯主が複数設定されています。ご確認ください。");
            return;
        }
        
        //更新前の確認
        if ((JOptionPane.showConfirmDialog(this, "更新しますか？", "確認", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)) {
            JOptionPane.showMessageDialog(this, "処理を中止しました。");
            return;
        };
        
        //インサート処理
        ArrayList lst = new ArrayList();
        
        //インサート前にデリート(1件ずつ全件：１つのSQLで複数件削除はエラーとなることに注意)
        if (rsSaiseihi != null) {
            for (int i = 1; i < rsSaiseihi.length; i++) {
                String[][] field = {
                    {"caseNo", dbSaiseihi.getValue(rsSaiseihi, "caseNo", i), ""},		//TEXT
                    {"inNo", dbSaiseihi.getValue(rsSaiseihi, "inNo", i), ""},		//INTEGER
                    {"kianYmd", dbSaiseihi.getValue(rsSaiseihi, "kianYmd", i), ""},		//INTEGER
                    {"ninteiYmd", dbSaiseihi.getValue(rsSaiseihi, "ninteiYmd", i), ""},		//INTEGER
                };
                //前レコードが見つかったため削除しておく
                String wk = dbSaiseihi.deleteSQL(field);
                lst.add(wk);
                logDebug(wk);
            }
        }
        
        ArrayList<String[][]> arrFieldSaiseihi = new ArrayList<String[][]>();
        
        String valueBefore = "";
        String valueAfter = "";
        
        for (int i = 0; i < jComboBoxKojin.getItemCount(); i++) {
            int inNo = DbAccessOS.getValueI((String) jComboBoxKojin.getItemAt(i));
            String ichiRuiTotal = "0";
            String niRuiTotal = "0";
            String toukiTotal = "0";
            String kimatuTotal = "0";
            String SeikatuTotal = "0";
            String KyouikuTotal = "0";
            String KaigoTotal = "0";
            String IryouTotal = "0";
            String JyutakuTotal = "0";
            //世帯主か？
            if (dbKojin.getValueB(rsKojin, "nushiFlg", i)) {
                ichiRuiTotal = text1Total.getText();
                niRuiTotal = text2Total.getText();
                toukiTotal = textTouki.getText();
                kimatuTotal = textKimatu.getText();
                SeikatuTotal = textSeikatuKei.getText();
                KyouikuTotal = textKyouiku.getText();
                KaigoTotal = textKaigo.getText();
                IryouTotal = textIryou.getText();
                JyutakuTotal = textJyutaku.getText();
            }
String[][] field = {
    {"caseNo", valueBefore, textCaseNo.getText()},		//TEXT
    {"inNo", valueBefore, "" + inNo},		//INTEGER
    {"kianYmd", valueBefore, textYmdKian.getID()},		//TEXT
    {"ninteiYmd", valueBefore, textYmdNintei.getID()},		//TEXT
    {"ichiRuiIppan", valueBefore, (String)list1Ippan.getModel().getElementAt(i)},		//TEXT
    {"ichiRuiKasan", valueBefore, (String)list1Kasan.getModel().getElementAt(i)},		//TEXT
    {"ichiRuiKasanKbn", valueBefore, (String)list1KasanSbt.getModel().getElementAt(i)},		//TEXT
    {"ichiRuiTeigenRitu", valueBefore, textTeigenRitu.getText()},		//TEXT
    {"ichiRuiTotal", valueBefore, ichiRuiTotal},		//INTEGER           Totalとあるものは世帯主のみに計上
    {"niRuiTotal", valueBefore, niRuiTotal},		//INTEGER
    {"toukiTotal", valueBefore, toukiTotal},		//INTEGER
    {"kimatuTotal", valueBefore, kimatuTotal},		//INTEGER
    {"SeikatuTotal", valueBefore, SeikatuTotal},		//INTEGER
    {"JyutakuTotal", valueBefore, JyutakuTotal},		//INTEGER
    {"KyouikuKijyun", valueBefore, valueAfter},		//TEXT
    {"KyouikuKyozai", valueBefore, valueAfter},		//TEXT
    {"KyouikuKyusyoku", valueBefore, valueAfter},		//TEXT
    {"KyouikuKoutuuHi", valueBefore, valueAfter},		//TEXT
    {"KyouikuSienHi", valueBefore, valueAfter},		//TEXT
    {"KyouikuSonota", valueBefore, valueAfter},		//TEXT
    {"KyouikuTotal", valueBefore, KyouikuTotal},		//INTEGER
    {"KaigoGetugaku", valueBefore, valueAfter},		//TEXT
    {"KaigoHoken", valueBefore, valueAfter},		//TEXT
    {"KaigoSonota", valueBefore, valueAfter},		//TEXT
    {"KaigoTotal", valueBefore, KaigoTotal},		//INTEGER
    {"IryouGetugaku", valueBefore, valueAfter},		//TEXT
    {"IryouHoken", valueBefore, valueAfter},		//TEXT
    {"IryouKouhi", valueBefore, valueAfter},		//TEXT
    {"IryouTotal", valueBefore, IryouTotal},		//INTEGER
    {"Total", valueBefore, valueAfter}		//INTEGER
};
            arrFieldSaiseihi.add(field);
        }        
        
        for (int i = 0; i < arrFieldSaiseihi.size(); i++) {
            //インサート処理
            String wk = dbSaiseihi.insertSQL(arrFieldSaiseihi.get(i));
            lst.add(wk);
            logDebug(wk);
        }
        //更新処理
        String[] SQL=(String[])lst.toArray(new String[0]);
        String msg = dbSaiseihi.execSQLUpdate(SQL);
        if (msg.equals("")) {
            JOptionPane.showMessageDialog(this, "更新しました。");
            //同じキーで再読み込み
            getKojin();
        } else {
            JOptionPane.showMessageDialog(this, msg);
        }
    }
    
    /**
     * 最低生活費の再計算を行う。(ここでは月額を設定する。日割りは別処理)
     * 基本的に前回の認定のコピーを貼り付け、（無ければ基準から初期値を取得）
     * 自動変更可能なタイミングでは、当該部分のみ再設定を行う。（変更部分はユーザに知らせる）
     * 世帯構成等は画面のものは使わず、全てテーブルを取得して行う。（データの整合性確認のため）
     */
    private void calc() {
        //最低生活費認定　再計算
        
        String caseNo = textCaseNo.getText();
        String ninteiYmd = textYmdNintei.getID();
        String kianYmd = textYmdKian.getID();
        int nendo = classYMD.getNendo(ninteiYmd);
        
        //setai, kojin の取得
        //String[][] rsSetaiN = dbSetai.getResultSetTable("WHERE caseNo = '" + caseNo + "'");
        String[][] rsKojinN = dbKojin.getResultSetTable("WHERE caseNo = '" + caseNo + "' AND kianYmd = '" + kianYmd + "' AND ninteiYmd ='" + ninteiYmd + "'");
        String[][] rsKijyun = dbKijyun.getResultSetTable("WHERE nendo = '" + nendo + "'");
        
        //デフォルト級地のインデックス取得
        int kijyunIdx = dbKijyun.getKyutiIdx(rsKijyun, OpenSeihoNintei.DefaultKyuti);
        if (kijyunIdx < 0) {
            JOptionPane.showMessageDialog(this, "基準額データがみつかりません。" + OpenSeihoNintei.DefaultKyuti);
            return;
        }
        //個人の級地のインデックスを全員分取得
        Integer[] kijyunIdxKojin = new Integer[rsKojinN.length];
        for (int i = 0; i < rsKojinN.length; i++) {
            String kojinKyuti = dbKojin.getValue(rsKojinN, "kyuti", i);
            kijyunIdxKojin[i] = dbKijyun.getKyutiIdx(rsKijyun, kojinKyuti);
            if (kijyunIdx < 0) {
                JOptionPane.showMessageDialog(this, "基準額データがみつかりません。" + kojinKyuti);
                return;
            }
        }
        
        //世帯人数
        int ninzu = rsKojinN.length - 1;
        
        //居宅はコード 1-19
        int kyotakuNinzu = 0;
        
        //(1)、(2)の値の退避用
        Double[] kyotaku1ruiKijyun1 = new Double[ninzu + 1];
        Double[] kyotaku1ruiKijyun2 = new Double[ninzu + 1];
        
        //氏名セット（構成員のみ）
        String[] gakunen = {"小学１", "小学２", "小学３", "小学４", "小学５", "小学６", "中学１", "中学２", "中学３"};
        ((DefaultListModel)listSetaiIn.getModel()).clear();
        for (int i = 1; i < rsKojinN.length; i++) {
            String nameKj = dbKojin.getValue(rsKojinN, "nameKj", i);
            ((DefaultListModel)listSetaiIn.getModel()).addElement(nameKj);
            //教育は絞り込み
            int nenrei = dbKojin.getValueI(rsKojinN, "ninteiNenrei", i);
            if ((6 <= nenrei) && (nenrei <= 14)) {
                ((DefaultListModel)listSetaiIn2.getModel()).addElement(nameKj);
                ((DefaultListModel)listGakunen.getModel()).addElement(gakunen[nenrei - 6]);
                //教育
                int kijyunS = dbKijyun.getValueI(rsKijyun, "KyoikuS", kijyunIdxKojin[i]);
                int kijyunC = dbKijyun.getValueI(rsKijyun, "KyoikuC", kijyunIdxKojin[i]);
                int sienhiS = dbKijyun.getValueI(rsKijyun, "KyoikuSienS", kijyunIdxKojin[i]);
                int sienhiC = dbKijyun.getValueI(rsKijyun, "KyoikuSienC", kijyunIdxKojin[i]);
                
                if (nenrei <= 11) {
                    //小学生
                    ((DefaultListModel)listKyouikuKijyun.getModel()).addElement("" + kijyunS);
                    ((DefaultListModel)listKyouikuSienhi.getModel()).addElement("" + sienhiS);
                } else {
                    //中学生
                    ((DefaultListModel)listKyouikuKijyun.getModel()).addElement("" + kijyunC);
                    ((DefaultListModel)listKyouikuSienhi.getModel()).addElement("" + sienhiC);
                }
                //前回のをセット。なければ０
                if (true) {
                    ((DefaultListModel)listKyouikuKoutuu.getModel()).addElement("0");
                    ((DefaultListModel)listKyouikuKyouzai.getModel()).addElement("0");
                    ((DefaultListModel)listKyouikuKyuusyoku.getModel()).addElement("0");
                    ((DefaultListModel)listKyouikuSonota.getModel()).addElement("0");
                } else {
                    
                }
            }
            ((DefaultListModel)listSetaiIn3.getModel()).addElement(nameKj);
            
            ((DefaultListModel)listSetaiIn4.getModel()).addElement(nameKj);
            //前回のをセット。なければ０
            if (true) {
                ((DefaultListModel)listIryoKijyun.getModel()).addElement("0");
                ((DefaultListModel)listIryouHoken.getModel()).addElement("0");
                ((DefaultListModel)listIryouSonota.getModel()).addElement("0");
                ((DefaultListModel)listKaigoHoken.getModel()).addElement("0");
                ((DefaultListModel)listKaigoKijyun.getModel()).addElement("0");
                ((DefaultListModel)listKaigoSonota.getModel()).addElement("0");
            } else {
                
            }
            
            //居宅はコード 1-19
            //kyotakuNinzu
            int seikatukeitai = DbAccessOS.getValueI(dbKojin.getValue(rsKojinN, "seikatuKeitai", i));
            if ((1 <= seikatukeitai) && (seikatukeitai <= 19)) {
                kyotakuNinzu = kyotakuNinzu + 1;
                //一類
                String nenreiKbn;
                if (nenrei <= 2) {
                    nenreiKbn = "1";
                } else if (nenrei <= 5) {
                    nenreiKbn = "2";
                } else if (nenrei <= 11) {
                    nenreiKbn = "3";
                } else if (nenrei <= 19) {
                    nenreiKbn = "4";
                } else if (nenrei <= 40) {
                    nenreiKbn = "5";
                } else if (nenrei <= 59) {
                    nenreiKbn = "6";
                } else if (nenrei <= 69) {
                    nenreiKbn = "7";
                } else {
                    nenreiKbn = "8";
                }
                //一類（１）
                double Kijyun1_1 = dbKijyun.getValueI(rsKijyun, "kyotaku1_1" + nenreiKbn, kijyunIdxKojin[i]);
                //一類（２）
                double Kijyun1_2 = dbKijyun.getValueI(rsKijyun, "kyotaku1_2" + nenreiKbn, kijyunIdxKojin[i]);
                
                //表示
                ((DefaultListModel)list1Ippan.getModel()).addElement("" + (int)Math.round(Kijyun1_1) + "/" + (int)Math.round(Kijyun1_2));
                
                //低減率をかける前の値を退避
                //(1)基準
                kyotaku1ruiKijyun1[i] = Kijyun1_1;
                //(2)基準
                kyotaku1ruiKijyun2[i] = Kijyun1_2;
            }
            
            //加算
            String kasanSbt = "";
            int kasan = 0;
            int kasanSyougai = 0;
            int wk = 0;
//        {"kasanNinpu", "TEXT"},
            wk = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanNinpu", i));
            if (wk == 1) {
                kasan = kasan + dbKijyun.getValueI(rsKijyun, "NinpuS", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",妊";
            }
            if (wk == 2) {
                kasan = kasan + dbKijyun.getValueI(rsKijyun, "NinpuL", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",妊";
            }
//        {"kasanSanpu", "TEXT"},
            wk = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanSanpu", i));
            if (wk == 1) {
                kasan = kasan + dbKijyun.getValueI(rsKijyun, "Sanpu", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",産";
            }
//        {"kasanSyussanYmd", "TEXT"},
//        {"kasanSyougai", "TEXT"},
            wk = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanSyougai", i));
            if (wk == 1) {
                kasanSyougai = kasanSyougai + dbKijyun.getValueI(rsKijyun, "Syougai1", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",障";
            }
            if (wk == 2) {
                kasanSyougai = kasanSyougai + dbKijyun.getValueI(rsKijyun, "Syougai2", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",障";
            }
            if (wk == 3) {
                kasanSyougai = kasanSyougai + dbKijyun.getValueI(rsKijyun, "Syougai3", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",障";
            }
            if (wk == 4) {
                kasanSyougai = kasanSyougai + dbKijyun.getValueI(rsKijyun, "Syougai4", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",障";
            }
            if (wk == 5) {
                kasanSyougai = kasanSyougai + dbKijyun.getValueI(rsKijyun, "Syougai5", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",障";
            }
            if (wk == 6) {
                kasanSyougai = kasanSyougai + dbKijyun.getValueI(rsKijyun, "Syougai6", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",障";
            }
            if (wk == 7) {
                kasanSyougai = kasanSyougai + dbKijyun.getValueI(rsKijyun, "Syougai7", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",障";
            }
//        {"kasanKaigoHiyou", "INTEGER"},
            wk = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanKaigoHiyou", i));
            if (wk > 0) {
                kasanSyougai = kasanSyougai + wk;
                kasanSbt = kasanSbt + ",障";        
            }
//        {"kasanKaigoNyusyo", "INTEGER"},
            wk = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanKaigoNyusyo", i));
            if (wk > 0) {
                kasan = kasan + wk;
                kasanSbt = kasanSbt + ",介施";        
            }
//        {"kasanZaitakuFlg", "INTEGER"},
            wk = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanZaitakuFlg", i));
            if (wk == 1) {
                kasan = kasan + dbKijyun.getValueI(rsKijyun, "ZaitakuKnajya", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",在";        
            }
//        {"kasanHousyasen", "TEXT"},
            wk = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanHousyasen", i));
            if (wk == 1) {
                kasan = kasan + dbKijyun.getValueI(rsKijyun, "Housya1", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",放";
            }
            if (wk == 2) {
                kasan = kasan + dbKijyun.getValueI(rsKijyun, "Housya2", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",放";
            }
//        {"kasanJidouYouiku", "TEXT"},
            wk = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanJidouYouiku", i));
            if (wk == 1) {
                kasan = kasan + dbKijyun.getValueI(rsKijyun, "JIdouyouiku1", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",児";
            }
            if (wk == 2) {
                kasan = kasan + dbKijyun.getValueI(rsKijyun, "JIdouyouiku2", kijyunIdxKojin[i]);
                kasanSbt = kasanSbt + ",児";
            }
//        {"kasanKaigoHokenRyou", "INTEGER"},
            wk = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanKaigoHokenRyou", i));
            if (wk > 0) {
                kasan = kasan + wk;
                kasanSbt = kasanSbt + ",介保";        
            }
//        {"kasanBoshi", "TEXT"},
            wk = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanBoshi", i));
//        {"kasanBoshiNinzu", "INTEGER"},
//        {"kasanBoshiNinzuS", "INTEGER"},
            int boshiNinzuTotal = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanBoshiNinzu", i));      //Total
            int boshiNinzuS = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanBoshiNinzuS", i));     //入所
            int boshiNinzuZ = boshiNinzuTotal - boshiNinzuS; //在宅
            //金額の大きな在宅から順に計上、入所分は額が小さくなるため、後回しのほうが支給額は大きくなる
            int boshiKingaku = 0;
            //何人目まで在宅かに注意
            int kIdx = 1;
            for (int j = 0; j < boshiNinzuTotal; j++) { 
                if (boshiNinzuZ > j) {
                    //在宅
                    boshiKingaku = boshiKingaku + dbKijyun.getValueI(rsKijyun, "Boshi" + kIdx + "Z", kijyunIdxKojin[i]);
                } else {
                    //施設
                    boshiKingaku = boshiKingaku + dbKijyun.getValueI(rsKijyun, "Boshi" + kIdx + "N", kijyunIdxKojin[i]);
                }
                kIdx = kIdx + 1;
                if (kIdx > 3) {
                    kIdx = 3;
                }
            }
            if (boshiKingaku > 0 ) {
                kasanSbt = kasanSbt + ",母";  
            }
//        {"kasanTyohukuFlg", "INTEGER"}
            wk = dbKojin.getValueI(dbKojin.getValue(rsKojinN, "kasanTyohukuFlg", i));
            if (wk == 1) {
                //重複調整あり
                kasanSbt = kasanSbt + "(重複調整)";
                //障害・母子の多い方のみ計上
                if (kasanSyougai < boshiKingaku) {
                    kasan = kasan + boshiKingaku;
                } else {
                    kasan = kasan + kasanSyougai;
                }
            } else {
                //重複調整なし：両方計上
                kasan = kasan + kasanSyougai + boshiKingaku;
            }
            //加算額表示
            ((DefaultListModel)list1Kasan.getModel()).addElement("" + kasan);
            kasanSbt = kasanSbt + "  "; //エラー回避
            ((DefaultListModel)list1KasanSbt.getModel()).addElement(kasanSbt.substring(1)); //最初のカンマを除く
            
        }   //個人ループ終了
        //加算合計
        int kasanTotal = 0;
        for (int i = 0; i < list1Kasan.getModel().getSize(); i++) {
            kasanTotal = kasanTotal + DbAccessOS.getValueI((String) list1Kasan.getModel().getElementAt(i));
        }
        text1Kasan.setText("" + kasanTotal);
        
        //二類
        if (kyotakuNinzu > 0) {
            String ninzuStr = "";
            int kasan1rui = 0;
            if (kyotakuNinzu > 9) {
                //10以上の場合は専用の処理が必要。
                ninzuStr = "09";
                //加算額（１０人以上の場合）
                //kasan1rui = 
            } else {
                //９人まではこの額でOK
                ninzuStr = ("0" + kyotakuNinzu);
            }
            //基準２類(1)
            int kijyun2rui1 = dbKijyun.getValueI(rsKijyun, "kyotaku2_1" + ninzuStr, kijyunIdx);
            //基準２類(2)
            int kijyun2rui2 = dbKijyun.getValueI(rsKijyun, "kyotaku2_2" + ninzuStr, kijyunIdx);
            //率（１）Teigen_101 (1)_(niinzuStr)
            int Ritu_1 = dbKijyun.getValueI(rsKijyun, "Teigen_1" + ninzuStr, kijyunIdx);
            //率（２）
            int Ritu_2 = dbKijyun.getValueI(rsKijyun, "Teigen_2" + ninzuStr, kijyunIdx);
            /*
            A = 1rui(1) * ritu(1) + 2rui(1)
            B1 = 1rui(2) * ritu(2) + 2rui(2)
            B2 = A * 0.9
            B = B1 or B2 (大きい方)
            A * 2/3 + B * 1/3 + touki
            */
            StringBuilder sb = new StringBuilder();
            sb.append("A = 1rui(1) * ritu(1) + 2rui(1)\n");
            sb.append("B1 = 1rui(2) * ritu(2) + 2rui(2)\n");
            sb.append("B2 = A * 0.9\n");
            sb.append("B = B1 or B2 (大きい方)\n");
            sb.append("A * 2/3 + B * 1/3 + touki\n");
            double total1rui1 = 0;
            double total1rui2 = 0;
            for (int i = 1; i < kyotaku1ruiKijyun1.length; i++) {
                total1rui1 = total1rui1 + kyotaku1ruiKijyun1[i];
                total1rui2 = total1rui2 + kyotaku1ruiKijyun2[i];
            }
            sb.append("sikiA:\n");
            sb.append("" + total1rui1);
            sb.append(" * " + (double)Ritu_1 / 10000D);
            sb.append(" + " + kijyun2rui1 + "\n");
            double sikiA = total1rui1 * (double)Ritu_1 / 10000D + kijyun2rui1;
            sb.append("A:" + sikiA);
            sb.append("\n");
            sb.append("sikiB1:\n");
            sb.append("" + total1rui2);
            sb.append(" * " + (double)Ritu_2 / 10000D);
            sb.append(" + " + kijyun2rui2 + "\n");
            double sikiB1 = total1rui2 * (double)Ritu_2 / 10000D + kijyun2rui2;
            sb.append("B1:" + sikiB1);
            sb.append("\n");
            sb.append("sikiB2:\n");
            sb.append("" + sikiA);
            sb.append(" * 0.9\n");
            double sikiB2 = sikiA * 0.9D;
            sb.append("B2:" + sikiB2);
            sb.append("\n");
            double sikiB = 0;
            if (sikiB1 < sikiB2) {
                sikiB = sikiB2;
            } else {
                sikiB = sikiB1;
            }
            sb.append("B:" + sikiB);
            sb.append("\n");
            sb.append("よって、式は A*2/3 + B*1/3 + C(冬季加算)なので\n");
            sb.append("A*2/3 + B*1/3 = ");
            double seikatu1and2Total = sikiA * 2D / 3D + sikiB / 3D;
            sb.append("" + seikatu1and2Total);
            sb.append("\n");
            sb.append("低減率は\n");
            //低減率表示
            DecimalFormat fmt = new DecimalFormat("0.0000");
            String str = "";
            if (sikiB1 > sikiB2) {
                //複雑な方
                str = "(1):" + fmt.format((double)Ritu_1 / 10000D) + "  (2):" + fmt.format((double)Ritu_2 / 10000D);
                textTeigenRitu.setText(str);
                sb.append(str);
                sb.append("\n");
                sb.append("\n");
                sb.append("検算\n");
                sb.append("" + total1rui1);
                sb.append(" * ");
                sb.append(fmt.format((double)Ritu_1 / 10000D));
                sb.append(" * 2/3 + ");
                sb.append("" + total1rui2);
                sb.append(" * ");
                sb.append(fmt.format((double)Ritu_2 / 10000D));
                sb.append(" * 1/3\n");
                sb.append("= ");
                int kyotaku1rui = (int)(Math.round((double)total1rui1 * (double)Ritu_1 / 10000D * 2D / 3D + (double)total1rui2 * (double)Ritu_2 / 10000D / 3D * 0.9D));
                sb.append("" + kyotaku1rui);
                sb.append("\n");
                text1TotalKyotaku.setText("" + (int)Math.round(kyotaku1rui));
            } else {
//                str = "(1):" + fmt.format((double)Ritu_1 / 10000D) + "  (B): A * 0.9";
                str = "(1):" + fmt.format((double)Ritu_1 / 10000D) + "  (1): " + fmt.format((double)Ritu_1 / 10000D) + " * 0.9";
                textTeigenRitu.setText(str);
                sb.append(str);
                sb.append("\n");
                sb.append("\n");
                sb.append("(2)は使わないので、(1)基準のみ表示する。\n");
                for (int i = 0; i < list1Ippan.getModel().getSize(); i++) {
                    String item = (String) ((DefaultListModel)list1Ippan.getModel()).getElementAt(i);
                    String[] items = item.split("/");
                    ((DefaultListModel)list1Ippan.getModel()).setElementAt(items[0], i);
                }
                sb.append("検算\n");
                sb.append("" + total1rui1);
                sb.append(" * ");
                sb.append(fmt.format((double)Ritu_1 / 10000D));
                sb.append(" * 2/3 + ");
                sb.append("" + total1rui1);
                sb.append(" * ");
                sb.append(fmt.format((double)Ritu_1 / 10000D));
                sb.append(" * 1/3 *0.9\n");
                sb.append("= ");
                int kyotaku1rui = (int)(Math.round((double)total1rui1 * (double)Ritu_1 / 10000D * 2D / 3D + (double)total1rui1 * (double)Ritu_1 / 10000D / 3D * 0.9D));
                sb.append("" + kyotaku1rui);
                sb.append("\n");
                text1TotalKyotaku.setText("" + (int)Math.round(kyotaku1rui));
                //ここはあってた。
            }
            
/*
double sikiA = total1rui1 * (double)Ritu_1 / 10000D + kijyun2rui1;
double sikiB1 = total1rui2 * (double)Ritu_2 / 10000D + kijyun2rui2;
double sikiB2 = sikiA * 0.9D;
*/
            //２類は逓減率なし
            double keisanZumi2rui = 0;
            if (sikiB1 > sikiB2) {
                //複雑な方
                keisanZumi2rui = kijyun2rui1 * 2D / 3D + kijyun2rui2 / 3D;
                sb.append("\n");
                sb.append("検算\n");
                sb.append("２類の計算式は\n");
                sb.append(kijyun2rui1);
                sb.append(" * 2/3 + (");
                sb.append(kijyun2rui2);
                sb.append(" * 1/3 )\n");
                sb.append("= " + keisanZumi2rui);
                sb.append("\n");
            } else {
                keisanZumi2rui = kijyun2rui1 * 2D / 3D + kijyun2rui1 / 3D * 0.9D;
                sb.append("\n");
                sb.append("検算\n");
                sb.append("２類の計算式は\n");
                sb.append(kijyun2rui1);
                sb.append(" * 2/3 + (");
                sb.append(kijyun2rui1);
                sb.append(" * 1/3 * 0.9)\n");
                sb.append("= " + keisanZumi2rui);
                sb.append("\n");
            }
            //一類計(按分)
            text1Total.setText("" + Math.round(seikatu1and2Total - keisanZumi2rui + kasanTotal));
            //２類(按分)
            text2Total.setText("" + Math.round(keisanZumi2rui));
            
            int ninteiM = DbAccessOS.getValueI(ninteiYmd.substring(4, 6));
            //冬季加算 11-3
            int touki = 0;
            if ((ninteiM >= 11) || (ninteiM <= 3)) {
                touki = dbKijyun.getValueI(rsKijyun, "kyotaku2_T" + OpenSeihoNintei.DefaultTouki + ninzuStr, kijyunIdx);
                textTouki.setText("" + touki);
            } else {
                textTouki.setText("0");
            }
            //期末一時扶助 12
            int kimatu = 0;
            if (ninteiM == 12) {
                kimatu = dbKijyun.getValueI(rsKijyun, "Kimatu_" + ninzuStr, kijyunIdx);
                textKimatu.setText("" + kimatu);
            } else {
                textKimatu.setText("0");
            }
            
            //生活計(10円切り上げ) ceil
            int totalSeikatu = (int)(Math.ceil((double)(seikatu1and2Total + kasanTotal + touki + kimatu) / 10D)) * 10;
            textSeikatuKei.setText("" + totalSeikatu);
            
            //一般分小計
            int total1Ippan = 0;
            boolean errfrg = false;
            for (int i = 0; i < list1Ippan.getModel().getSize(); i++) {
                int wk = DbAccessOS.getValueI((String)((DefaultListModel)list1Ippan.getModel()).get(i));
                if (wk == DbAccessOS.defaultErrorIntValue) {
                    errfrg = true;
                }
                total1Ippan = total1Ippan + wk;
            }
            if (errfrg) {
                text1Ippan.setText("");
            } else {
                text1Ippan.setText("" + total1Ippan);
            }
            textBikou.setText(sb.toString());
        }
        
        //住宅
        int jyutakuHi = 0;
        if (kyotakuNinzu > 0) {
            jyutakuHi = dbKijyun.getValueI(rsKijyun, "JyutakuJyougen", kijyunIdx);
        }
        textJyutaku.setText("" + jyutakuHi);
        
        //介護
        
        //医療
        
        //合計金額セット
        recalc();
    }
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
    private javax.swing.JCheckBox chkNushi;
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
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButtonKojinCheck;
    private javax.swing.JButton jButtonKojinInst;
    private javax.swing.JButton jButtonSaikeisan;
    private javax.swing.JComboBox jComboBoxKojin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelKasan;
    private javax.swing.JPanel jPanelkojin;
    private javax.swing.JScrollPane jScrollPane1;
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
    private javax.swing.JScrollPane jScrollPane26;
    private javax.swing.JScrollPane jScrollPane27;
    private javax.swing.JScrollPane jScrollPane28;
    private javax.swing.JScrollPane jScrollPane29;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane30;
    private javax.swing.JScrollPane jScrollPane31;
    private javax.swing.JScrollPane jScrollPane32;
    private javax.swing.JScrollPane jScrollPane33;
    private javax.swing.JScrollPane jScrollPane34;
    private javax.swing.JScrollPane jScrollPane35;
    private javax.swing.JScrollPane jScrollPane36;
    private javax.swing.JScrollPane jScrollPane37;
    private javax.swing.JScrollPane jScrollPane38;
    private javax.swing.JScrollPane jScrollPane39;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane40;
    private javax.swing.JScrollPane jScrollPane41;
    private javax.swing.JScrollPane jScrollPane42;
    private javax.swing.JScrollPane jScrollPane43;
    private javax.swing.JScrollPane jScrollPane44;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JScrollPane jScrollPaneGetugaku;
    private javax.swing.JScrollPane jScrollPaneHiwari;
    private javax.swing.JScrollPane jScrollPaneSetai;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList list1Ippan;
    private javax.swing.JList list1Ippan1;
    private javax.swing.JList list1Kasan;
    private javax.swing.JList list1Kasan1;
    private javax.swing.JList list1KasanSbt;
    private javax.swing.JList list1KasanSbt1;
    private javax.swing.JList listGakunen;
    private javax.swing.JList listGakunen1;
    private javax.swing.JList listIryoKijyun;
    private javax.swing.JList listIryoKijyun1;
    private javax.swing.JList listIryouHoken;
    private javax.swing.JList listIryouHoken1;
    private javax.swing.JList listIryouSonota;
    private javax.swing.JList listIryouSonota1;
    private javax.swing.JList listKaigoHoken;
    private javax.swing.JList listKaigoHoken1;
    private javax.swing.JList listKaigoKijyun;
    private javax.swing.JList listKaigoKijyun1;
    private javax.swing.JList listKaigoSonota;
    private javax.swing.JList listKaigoSonota1;
    private javax.swing.JList listKyouikuKijyun;
    private javax.swing.JList listKyouikuKijyun1;
    private javax.swing.JList listKyouikuKoutuu;
    private javax.swing.JList listKyouikuKoutuu1;
    private javax.swing.JList listKyouikuKyouzai;
    private javax.swing.JList listKyouikuKyouzai1;
    private javax.swing.JList listKyouikuKyuusyoku;
    private javax.swing.JList listKyouikuKyuusyoku1;
    private javax.swing.JList listKyouikuSienhi;
    private javax.swing.JList listKyouikuSienhi1;
    private javax.swing.JList listKyouikuSonota;
    private javax.swing.JList listKyouikuSonota1;
    private javax.swing.JList listSetaiIn;
    private javax.swing.JList listSetaiIn1;
    private javax.swing.JList listSetaiIn2;
    private javax.swing.JList listSetaiIn3;
    private javax.swing.JList listSetaiIn4;
    private javax.swing.JList listSetaiIn5;
    private javax.swing.JList listSetaiIn6;
    private javax.swing.JList listSetaiIn7;
    private javax.swing.JList listSetaiIn8;
    private javax.swing.JList listSetaiIn9;
    private openseiho.JyusyoPanel panelJyusyo;
    private javax.swing.JPanel panelSetai;
    private javax.swing.JPanel panelSetaiBase;
    private openseihonintei.SetaiPanel setaiInPanel;
    private javax.swing.JTextField text1Ippan;
    private javax.swing.JTextField text1Ippan1;
    private javax.swing.JTextField text1Kasan;
    private javax.swing.JTextField text1Kasan1;
    private javax.swing.JTextField text1Total;
    private javax.swing.JTextField text1Total1;
    private javax.swing.JTextField text1TotalKyotaku;
    private javax.swing.JTextField text2Total;
    private javax.swing.JTextField text2Total1;
    private javax.swing.JTextArea textBikou;
    private javax.swing.JTextField textCaseNo;
    private javax.swing.JTextField textIryou;
    private javax.swing.JTextField textIryou1;
    private javax.swing.JTextField textJyutaku;
    private javax.swing.JTextField textJyutaku1;
    private javax.swing.JTextField textKaigo;
    private javax.swing.JTextField textKaigo1;
    private javax.swing.JTextField textKaigoHi;
    private javax.swing.JTextField textKaigoSisetu;
    private javax.swing.JTextField textKasanBoshiNinzuu;
    private javax.swing.JTextField textKasanBoshiNinzuuS;
    private javax.swing.JTextField textKasanKaigoHokenRyou;
    private javax.swing.JTextField textKimatu;
    private javax.swing.JTextField textKimatu1;
    private javax.swing.JTextField textKyouiku;
    private javax.swing.JTextField textKyouiku1;
    private javax.swing.JTextField textMyouji;
    private javax.swing.JTextField textMyoujiKana;
    private javax.swing.JTextField textSeikatuKei;
    private javax.swing.JTextField textSeikatuKei1;
    private openseiho.textYmdPanel textSyussanYmd;
    private javax.swing.JTextField textTeigenRitu;
    private javax.swing.JTextField textTeigenRitu1;
    private javax.swing.JTextField textTotal;
    private javax.swing.JTextField textTotal1;
    private javax.swing.JTextField textTouki;
    private javax.swing.JTextField textTouki1;
    private openseiho.textYmdPanel textYmdKian;
    private openseiho.textYmdPanel textYmdNintei;
    // End of variables declaration//GEN-END:variables

}
