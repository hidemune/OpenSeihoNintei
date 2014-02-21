/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OpenSeiho;

import com.ibm.icu.text.Transliterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;


/**
 *
 * @author hdm<auau@kne.biglobe.ne.jp>
 * 
 */

/*
* 日付に関しては、和暦は全てプロパティファイルに従うものとする。
# Meiji since 1868-01-01 00:00:00 local time (Gregorian)
# Taisho since 1912-07-30 00:00:00 local time (Gregorian)
# Showa since 1926-12-25 00:00:00 local time (Gregorian)
# Heisei since 1989-01-08 00:00:00 local time (Gregorian)
*/
public class classYMD {
    //共通部分
    public static boolean DebugMode = false;
    public static void logDebug(String str) {
        if (DebugMode) {
            System.out.println(str);
        }
    }
    //個別部分
    public static String StrToYmdId(String str) {
        logDebug("StrToYmdId:" + str);
        DecimalFormat exFormat4 = new DecimalFormat("0000");
        DecimalFormat exFormat2 = new DecimalFormat("00");
        
        //エラー時のデフォルト値
        String def = "00000000";
        
        //空文字チェック
        if (str.equals("")) {
            return def;
        }
        
        String gengo[] = new String[100];
        String gengoJ[] = new String[100];
        int sinceY[] = new int[100];
        //int sinceM[] = new int[100];
        //int sinceD[] = new int[100];
        //プロパティファイルの読み込み
        Properties config = new Properties();
        String msg = "";
        try {
            File file= new File("jpYmd.properties");
            msg = file.getAbsolutePath();
            config.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            //config.load(new FileInputStream(classYMD.class.getResource("/jpYmd.properties")));
        }catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "プロパティファイルがありません。環境をご確認下さい。\njpYmd.properties\n" + msg, "エラー", JOptionPane.ERROR_MESSAGE);
        }
        //元号は99個まで
        for (int i = 0; i < 100; i++) {
            String wk = config.getProperty("name" + exFormat2.format(i), "");
            if (wk.equals("")) {
                break;
            }
            logDebug(wk);
            String[] split = wk.split(",");
            gengo[i] = split[0];
            gengoJ[i] = split[1];
            sinceY[i] = Integer.parseInt(split[2]);
            //sinceM[i] = Integer.parseInt(split[3]);
            //sinceD[i] = Integer.parseInt(split[4]);
        }
        
        //全角を半角にする
        Transliterator tr = Transliterator.getInstance("Fullwidth-Halfwidth");
        str = tr.transliterate(str);
        logDebug("半角へ" + str);
        
        //セパレータを統一、等　･
        str = str.replaceAll("-", "/");
        str = str.replaceAll("･", "/");
        str = str.replaceAll("\\.", "/");
        str = str.replaceAll("年", "/");
        str = str.replaceAll("月", "/");
        str = str.replaceAll("日", "");
        str = str.replaceAll("元", "1");
        
        //「平成」を「H」に変換
        for (int i = 0; i < 100; i++) {
            if (gengo[i] == null) {
                break;
            }
            logDebug(gengoJ[i] + "to" + gengo[i]);
            str = str.replaceAll(gengoJ[i], gengo[i]);
        }
        logDebug("日付形式へ" + str);
        
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();	//現在日時でDateを作成
        cal.set(1,1,1);
        Date date = cal.getTime();
        //yyyy/MM/DD を作成
        //桁数チェック
        int keta = str.length();
        
        //数字かどうか
        chk:try {
            Long.parseLong(str);
            //数字の場合、８桁必須
            str = str.substring(0, 4) + "/" + str.substring(4, 6) + "/" + str.substring(6, 8);
        } catch (NumberFormatException e) {
            //数字でない場合
            int plus = 1;
            String ymd[] = str.split("/");
            //西暦表記もありうる　2014/1/1
            try {
                Long.parseLong(ymd[0]);
                break chk;
            } catch (NumberFormatException ex) {
                //1桁目は元号とみなす
                for (int i = 0; i < 100; i++) {
                    if (gengo[i] == null) {
                        System.err.print("元号の誤り:" + str);
                        break;
                    }
                    if (gengo[i].equals("")) {
                        System.err.print("元号の誤り:" + str);
                        break chk;
                    }
                    if (str.length() <= 1) {
                        System.err.print("短い文字列:" + str);
                        break chk;
                    }
                    if (gengo[i].equals(str.substring(0, 1).toUpperCase())) {
                        logDebug(gengoJ[i]);
                        plus = sinceY[i];
                        break;
                    }
                }
            }
            String[] split = str.split("/");
            //１年ズレるので引いておく
            plus = plus - 1;
            int yyyy = 1;
            int MM = 1;
            int dd = 1;
            try {
                yyyy = plus + Integer.parseInt(split[0].substring(1));
                MM = Integer.parseInt(split[1]);
                dd = Integer.parseInt(split[2]);
            } catch (Exception ex1) {
                JOptionPane.showMessageDialog(null, "日付の書式に誤りがあります。\n" + str, "エラー", JOptionPane.ERROR_MESSAGE);
                return def;
            }
            str = exFormat4.format(yyyy) + "/" + exFormat2.format(MM) + "/" + exFormat2.format(dd);
        }
        try {
            //yyyy/MM/dd を、yyyymmdd 型で返す
            df = new SimpleDateFormat("yyyyMMdd");
            date = DateFormat.getDateInstance().parse(str);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
            JOptionPane.showMessageDialog(null, "日付の書式に誤りがあります。\n" + str, "エラー", JOptionPane.ERROR_MESSAGE);
            return def;
        }
        return df.format(date);
    }
    public static String YmdIdToStr(String str) {
        logDebug("YmdIdToStr:" + str);
        DecimalFormat exFormat4 = new DecimalFormat("0000");
        DecimalFormat exFormat2 = new DecimalFormat("00");
        //初期値
        int y = 0;
        int m = 0;
        int d = 0;
        //数字かどうか
        try {
            Long.parseLong(str);
            //IDは数字かつ、８桁必須
            y = Integer.parseInt(str.substring(0, 4));
            m = Integer.parseInt(str.substring(4, 6));
            d = Integer.parseInt(str.substring(6, 8));
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            return "";
        }
        
        String gengo[] = new String[100];
        String gengoJ[] = new String[100];
        int sinceY[] = new int[100];
        int sinceM[] = new int[100];
        int sinceD[] = new int[100];
        //プロパティファイルの読み込み
        Properties config = new Properties();
        try {
            config.load(new InputStreamReader(new FileInputStream("jpYmd.properties"), "UTF-8"));
            //config.load(new FileInputStream(classYMD.class.getResource("/jpYmd.properties")));
        }catch (Exception e){
            e.printStackTrace();
        }
        //元号は99個まで
        for (int i = 0; i < 100; i++) {
            String wk = config.getProperty("name" + exFormat2.format(i), "");
            if (wk.equals("")) {
                break;
            }
            logDebug(wk);
            String[] split = wk.split(",");
            gengo[i] = split[0];
            gengoJ[i] = split[1];
            sinceY[i] = Integer.parseInt(split[2]);
            sinceM[i] = Integer.parseInt(split[3]);
            sinceD[i] = Integer.parseInt(split[4]);
        }
        //元号表記をプロパティファイルを元に行う。
        int idx = 0;
        //比較はyyyymmdd形式での数値の大小で決める
        int date1 = Integer.parseInt(exFormat4.format(y) + exFormat2.format(m) + exFormat2.format(d));
        logDebug(Integer.toString(date1));
        for (int i = 99; i > 0; i--) {
            int date2 = Integer.parseInt(exFormat4.format(sinceY[i]) + exFormat2.format(sinceM[i]) + exFormat2.format(sinceD[i]));
            if ((0 < date2) && (date2 <= date1)) {
                logDebug("Since:" + Integer.toString(date2));
                idx = i;
                break;
            }
        }
        int genY = y - sinceY[idx] + 1;
        if (genY == 1) {
            str = gengoJ[idx] + "元年" + Integer.toString(m) + "月" + Integer.toString(d) + "日";
        } else {
            str = gengoJ[idx] + Integer.toString(genY) + "年" + Integer.toString(m) + "月" + Integer.toString(d) + "日";
        }
        return str;
    }
    public static int getNendo(String strYmdID) {
        int ret = 0;
        
        try {
            ret = Integer.parseInt(strYmdID.substring(0, 4));
            int md = Integer.parseInt(strYmdID.substring(4, 8));
            if (md < 401) {
                ret = ret - 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        
        return ret;
    }
    public static boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
