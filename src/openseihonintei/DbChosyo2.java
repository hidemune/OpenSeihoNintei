/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package openseihonintei;

/**
 *
 * @author hdm
 * DBアクセス機能（サブクラス）
 * 
 * テーブル名：ID-Textテーブル
 */
public class DbChosyo2 extends DbAccessOS{
    //個別部分 : テーブル定義
    private static final String tableName = "chosyo2";
    private static final String[] tableUnique = {"caseNo", "inNo", "kianYmd", "ninteiYmd"};
    private static final String[] tablePrimary = {"caseNo", "inNo", "kianYmd", "ninteiYmd"};
    private static final String[][] tableField = {
        //列名, データ型(, 制約)        Total という名前なら世帯主のみに計上
        {"caseNo", "TEXT"}, 
        {"inNo", "INTEGER"},
        {"kianYmd", "TEXT"}, 
        {"ninteiYmd", "TEXT"}, 
        {"ichiRuiIppan", "TEXT"},
        {"ichiRuiKasan", "TEXT"},
        {"ichiRuiKasanKbn", "TEXT"},
        {"ichiRuiTeigenRitu", "TEXT"},
        {"ichiRuiTotal", "INTEGER"},
        {"niRuiTotal", "INTEGER"},
        {"toukiTotal", "INTEGER"},
        {"kimatuTotal", "INTEGER"},
        {"SeikatuTotal", "INTEGER"},
        {"JyutakuTotal", "INTEGER"},
        {"KyouikuKijyun", "TEXT"},
        {"KyouikuKyozai", "TEXT"},
        {"KyouikuKyusyoku", "TEXT"},
        {"KyouikuKoutuuHi", "TEXT"},
        {"KyouikuSienHi", "TEXT"},
        {"KyouikuSonota", "TEXT"},
        {"KyouikuTotal", "INTEGER"},
        {"KaigoGetugaku", "TEXT"},
        {"KaigoHoken", "TEXT"},
        {"KaigoSonota", "TEXT"},
        {"KaigoTotal", "INTEGER"},
        {"IryouGetugaku", "TEXT"},
        {"IryouHoken", "TEXT"},
        {"IryouKouhi", "TEXT"},
        {"IryouTotal", "INTEGER"},
        {"Total", "INTEGER"}
    };
    //共通部分
    public DbChosyo2(){
        setTableName(tableName);
        setTableUnique(tableUnique);
        setTablePrimary(tablePrimary);
        setTableField(tableField);
        logDebug("初期化:" + tableName);
    }
}
