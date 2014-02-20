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
public class DbKojin extends DbAccessOS{
    //個別部分 : テーブル定義
    private static final String tableName = "kojin";
    private static final String[] tableUnique = {"kianYmd", "ninteiYmd", "caseNo", "inNo"};
    private static final String[] tablePrimary = {"kianYmd", "ninteiYmd", "caseNo", "inNo"};
    private static final String[][] tableField = {
        //列名, データ型(, 制約)
        {"kianYmd", "TEXT"}, 
        {"ninteiYmd", "TEXT"}, 
        {"caseNo", "TEXT"}, 
        {"inNo", "INTEGER"},
        {"seikatuKeitai", "TEXT"},
        {"kyuti", "TEXT"},
        {"touki", "TEXT"},
        {"nameKj", "TEXT"},
        {"nameKn", "TEXT"},
        {"zokuCd", "TEXT"},
        {"ninteiNenrei", "TEXT"},
        {"kasanNinpu", "TEXT"},
        {"kasanSanpu", "TEXT"},
        {"kasanSyussanYmd", "TEXT"},
        {"kasanSyougai", "TEXT"},
        {"kasanKaigoHiyou", "INTEGER"},
        {"kasanKaigoNyusyo", "INTEGER"},
        {"kasanZaitakuFlg", "INTEGER"},
        {"kasanHousyasen", "TEXT"},
        {"kasanJidouYouiku", "TEXT"},
        {"kasanKaigoHokenRyou", "INTEGER"},
        {"kasanBoshi", "TEXT"},
        {"kasanBoshiNinzu", "INTEGER"},
        {"kasanTyohukuFlg", "INTEGER"}
    };
    //共通部分
    public DbKojin(){
        setTableName(tableName);
        setTableUnique(tableUnique);
        setTablePrimary(tablePrimary);
        setTableField(tableField);
        logDebug("初期化:" + tableName);
    }
}
