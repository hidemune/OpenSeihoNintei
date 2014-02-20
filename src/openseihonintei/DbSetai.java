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
public class DbSetai extends DbAccessOS{
    //個別部分 : テーブル定義
    private static final String tableName = "setai";
    private static final String[] tableUnique = {"caseNo", "inNo"};
    private static final String[] tablePrimary = {"caseNo", "inNo"};
    private static final String[][] tableField = {
        //列名, データ型(, 制約)
        {"caseNo", "TEXT"}, 
        {"inNo", "INTEGER"},
        {"syokkenFlg", "INTEGER"},
        {"yubinNo", "TEXT"},
        {"Address1", "TEXT"},
        {"Address2", "TEXT"},
        {"kouseiIn", "INTEGER"},
        {"nameKj", "TEXT"},
        {"nameKn", "TEXT"},
        {"seibetu", "INTEGER"},
        {"zokuCd", "INTEGER"},
        {"birthYmd", "TEXT"}
    };
    //共通部分
    public DbSetai(){
        setTableName(tableName);
        setTableUnique(tableUnique);
        setTablePrimary(tablePrimary);
        setTableField(tableField);
        logDebug("初期化:" + tableName);
    }
}
