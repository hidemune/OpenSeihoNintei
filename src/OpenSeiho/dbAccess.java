/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OpenSeiho;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

/**
 *
 * @author hdm
 * DBアクセス機能（スーパークラス）
 * 
 */
public class dbAccess {
    String host = "localhost";
    String port = "5432";
    String dbname = "OpenSeiho";
    String rolename = "postgres";
    String password = "xxxxxxxx";
    String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;
    
    //共通部分
    public static boolean DebugMode = false;
    public static void logDebug(String str) {
        if (DebugMode) {
            System.out.println(str);
        }
    }
    //テーブル定義
    private String tableNameSup;
    private String[] tableUniqueSup;
    private String[] tablePrimarySup;
    private String[][] tableFieldSup ;
    
    public void setTableName(String str){
        tableNameSup = str;
        logDebug("テーブル名:" + tableNameSup);
    }
    public void setTableUnique(String[] str){
        tableUniqueSup = str;
        logDebug("Uniq:" + tableUniqueSup);
    }
    public void setTablePrimary(String[] str){
        tablePrimarySup = str;
        logDebug("Primary:" + tablePrimarySup);
    }
    public void setTableField(String[][] str){
        tableFieldSup = str;
        logDebug("Field:" + tableFieldSup);
    }
    
    public String getTableName(){
        return tableNameSup;
    }

    //Edit by Sheet
    public void editTable(dbAccess dbA, String where) {
        dbSheetFrame frm = new dbSheetFrame(dbA);
        frm.setVisible(true);
        //テーブルのレザルトセットを取得
        String[][] str = getResultSetTable(where);
        //結果を一覧にセット
        frm.setResultSet(str, where);
    }
    
    //ExecSQL
    private String execSQLUpdate(String SQL){
        System.out.println("execSQLUpdate" + tableNameSup);
        Properties props = new Properties();
        props.setProperty("user", rolename);
        props.setProperty("password", password);
        Connection con = null;
        String msg = "";
        
        try {
            Class.forName("org.postgresql.Driver");
            
            con = DriverManager.getConnection(url, props);
            System.out.println("データベースに接続しました。");
            
            //自動コミットを無効にする
            con.setAutoCommit(false);
            
            //ステートメント作成
            Statement stmt = con.createStatement();
            
            //SQLの実行
            int rows = stmt.executeUpdate(SQL);
            System.out.println("Update:" + rows);
            
            //ステートメントのクローズ
            stmt.close();
            
            //コミットする
            con.commit();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBCドライバが見つかりませんでした。");
            msg = msg + "JDBCドライバが見つかりませんでした。\n";
        } catch (SQLException e) {
            System.err.println("エラーコード　　: " + e.getSQLState());
            System.err.println("エラーメッセージ: " + e.getMessage());
            msg = msg + "エラーコード　　: " + e.getSQLState() + "\n";
            msg = msg + "エラーメッセージ: " + e.getMessage() + "\n";
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                    System.out.println("データベースとの接続を切断しました。");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /* メッセージは画面側に任せる
        if (msg.equals("")) {
            msg = "正常に更新しました。";
        }
        */
        return msg;
    }
    
    /**
     * デフォルトソート版：強制的に、先頭カラムから順にソートされます。
     * @param where
     * @return 
     */
    public String[][] getResultSetTable(String where) {
        System.out.println("getResultSetTable" + tableNameSup);
        String[][] ret = new String[tableFieldSup.length][1]; //col,rowの順
        Properties props = new Properties();
        props.setProperty("user", rolename);
        props.setProperty("password", password);
        Connection con = null;
        
        try {
            Class.forName("org.postgresql.Driver");
            
            con = DriverManager.getConnection(url, props);
            System.out.println("データベースに接続しました。");
            
            //自動コミットを無効にする
            //con.setAutoCommit(false);
            
            //ステートメント作成
            Statement stmt = con.createStatement();
            
            //列数取得
//            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableNameSup);
 //           rs.next();
 //           int rows = rs.getInt("COUNT");
//            System.out.println(rows);
            
            //行数取得
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableNameSup + " " + where);
            rs.next();
            int rows = rs.getInt("COUNT");
            System.out.println("rows:" + rows);
            rs.close();
            
            //SQLの実行
            rs = stmt.executeQuery("SELECT * FROM " + tableNameSup + " " + where);
            
            int cols = tableFieldSup.length;
            System.out.println("cols:" + cols);
            //配列の枠を作成
            ret = new String[cols][rows + 1]; //タイトル分１行多い
            
            //Title
            for (int i = 0; i < cols; i++) {
                logDebug("Title" + i + ":" + tableFieldSup[i][0]);
                ret[i][0] = tableFieldSup[i][0];
            }
            
            //Data
            int idx = 0;
            while (rs.next()) {
                idx = idx + 1;
                for (int j = 0; j < cols; j++) {
                    ret[j][idx] = rs.getString(j + 1);
                    logDebug("Data" + idx + "," + j + ":" + rs.getString(j + 1));
                    logDebug("ret[][]" + idx + "," + j + ":" + ret[j][idx]);
                }
            }
            
            rs.close();
            
            //ステートメントのクローズ
            stmt.close();
            
            //コミットする
            //con.commit();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBCドライバが見つかりませんでした。");
        } catch (SQLException e) {
            System.err.println("エラーコード　　: " + e.getSQLState());
            System.err.println("エラーメッセージ: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                    System.out.println("データベースとの接続を切断しました。");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ret = sortArray(ret);
        return ret;
    }
    /**
     * ２次元配列をソートして返します
     * 　:Resultset用なので、最初の一行はカラム名とみなし、ソートしません。
     * @param src
     * @return 
     */
    private String[][] sortArray(String[][] src) {
        int cols = src.length;
        int rows = src[0].length;
        String[][] desc = new String[cols][rows];
        
        DecimalFormat exFormat10 = new DecimalFormat("0000000000");
        TreeSet<String> arrayKeys = new TreeSet<String>();
        //１行目はカラム名：
        arrayKeys.add(exFormat10.format(0));            //強制的に０を指定
        //Sort
        //Integer[] idx = new Integer[rows];
        for (int i = 1; i < rows; i++) {                //２行目から始まっていることに注意
            StringBuilder key = new StringBuilder();
            for (int j = 0; j < cols; j++) {
                //ソートキー作成
                try {
                    long wk = Long.parseLong(src[j][i]);
                    key.append(exFormat10.format(wk));
                } catch (Exception e) {
                    key.append(src[j][i]);
                }
            }
            //最後の１０桁はインデックス
            key.append(exFormat10.format(i));
            arrayKeys.add(key.toString());
        }
        //新しい配列に紐付け
        Iterator<String> itr = arrayKeys.iterator();
        for (int i = 0; i < rows; i++) {
            String key = itr.next();
            int idx = 0;
            try {
                idx = Integer.parseInt(key.substring(key.length() - 10));
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int j = 0; j < cols; j++) {
                desc[j][i] = src[j][idx];
            }
        }
        
        return desc;
    }

    //CREATE TABLE
    public void createTable(){
        logDebug("Create/テーブル名:" + tableNameSup);
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(tableNameSup);
        sb.append(" (");
        String separator = "";
        for (int i = 0; i < tableFieldSup.length; i++) {
            sb.append(separator);
            for (int j = 0; j < tableFieldSup[i].length; j++) {
                sb.append(tableFieldSup[i][j]);
                sb.append(" ");
                logDebug(tableFieldSup[i][j]);
            }
            separator = ",";
        }
        sb.append(");");
        
        sb.append("ALTER TABLE ");
        sb.append(tableNameSup);
        sb.append(" ADD PRIMARY KEY(");
        separator = "";
        for (int i = 0; i < tablePrimarySup.length; i++) {
            sb.append(separator);
            sb.append(tablePrimarySup[i]);
            sb.append(" ");
            separator = ",";
        }
        sb.append(");");
        
        sb.append("ALTER TABLE ");
        sb.append(tableNameSup);
        sb.append(" ADD UNIQUE (");
        separator = "";
        for (int i = 0; i < tableUniqueSup.length; i++) {
            sb.append(separator);
            sb.append(tableUniqueSup[i]);
            sb.append(" ");
            separator = ",";
        }
        sb.append(");");
        
        logDebug(sb.toString());
        //DB Access
        execSQLUpdate(sb.toString());
    }
    //INSERT
    /**
    *   fieldは、2次元配列でフィールド名・値の順とする。
    *       id0     value
    *       id1     value
    *       text    value
    */
    public String insert(ArrayList field[]) {
        /*設計方針：
            ・名前との対応を必ず行い、列番号に頼らない
            　（パッケージのバージョンアップに伴い、データが壊れなければOK）
            ・エラー情報を画面で確認可能とする
        */
        String msg ="";
        
        //tableFieldSup[][] スーパークラスで管理するカラム名    チェック可能
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(tableNameSup);
        sb.append("(");
        String sep = "";
        for (int i = 0; i < field[0].size(); i++) {
            sb.append(sep);
            sb.append(field[0].get(i));
            sep = ",";
        }
        sb.append(") VALUES (");
        sep = "";
        for (int i = 0; i < field[1].size(); i++) {
            sb.append(sep);
            sb.append("'");
            sb.append(field[2].get(i));
            sb.append("'");
            sep = ",";
        }
        
        sb.append(")");
        
        //Exec
        msg = execSQLUpdate(sb.toString());
        
        return msg;
    }
    
    //UPDATE
    /**
    *   fieldは、2次元配列でフィールド名・値の順とする。
    *       id0     value
    *       id1     value
    *       text    value
    */
    public String update(ArrayList field[]) {
        /*設計方針：
            ・名前との対応を必ず行い、列番号に頼らない
            　（パッケージのバージョンアップに伴い、データが壊れなければOK）
            ・エラー情報を画面で確認可能とする
        */
        String msg ="";
        
        //tableFieldSup[][] スーパークラスで管理するカラム名    チェック可能
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(tableNameSup);
        sb.append(" SET ");
        String sep = "";
        for (int i = 0; i < field[0].size(); i++) {
            sb.append(sep);
            sb.append(field[0].get(i));
            sb.append(" = ");
            sb.append("'");
            sb.append(field[2].get(i));
            sb.append("'");
            sep = ",";
        }
        
        sb.append(" WHERE ");
        sep = "";
        for (int i = 0; i < tableUniqueSup.length; i++) {
            sb.append(sep);
            sb.append(tableUniqueSup[i]);
            sb.append(" = ");
            //キーの値を取得
            for (int j = 0; j < field[0].size(); j++) {
                if (field[0].get(i).equals(tableUniqueSup[i])) {
                    //同じの！
                    sb.append("'");
                    sb.append(field[1].get(i));
                    sb.append("'");
                    
                    sep = " AND ";
                    break;
                }
            }
        }

        
        //Exec
        msg = execSQLUpdate(sb.toString());
        
        return msg;
    }
    
    //DELETE
    /**
    *   fieldは、2次元配列でフィールド名・値の順とする。
    *       id0     value
    *       id1     value
    *       text    value
    *   UNIQ のみをWHERE句に設定
    */
    public String delete(ArrayList field[]) {
        String msg = "";
        
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(tableNameSup);
        sb.append(" WHERE ");
        String sep = "";
        for (int i = 0; i < tableUniqueSup.length; i++) {
            sb.append(sep);
            sb.append(tableUniqueSup[i]);
            sb.append(" = ");
            //キーの値を取得
            for (int j = 0; j < field[0].size(); j++) {
                if (field[0].get(i).equals(tableUniqueSup[i])) {
                    //同じの！
                    sb.append("'");
                    sb.append(field[1].get(i));
                    sb.append("'");
                    
                    sep = " AND ";
                    break;
                }
            }
        }
        
        logDebug(sb.toString());
        msg = execSQLUpdate(sb.toString());
        
        return msg;
    }
}
