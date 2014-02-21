/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OpenSeiho;

/**
 *
 * @author hdm
 * DBアクセス機能（サブクラス）
 * 
 * テーブル名：ID-Textテーブル
 */
public class dbIdText extends dbAccess{
    //個別部分 : テーブル定義
    private static final String tableName = "id_text";
    private static final String[] tableUnique = {"id0", "id1"};
    private static final String[] tablePrimary = {"id0", "id1"};
    private static final String[][] tableField = {
        //列名, データ型(, 制約)
        {"id0", "INTEGER"},
        {"id1", "INTEGER"},
        {"text", "TEXT"}
    };
    //共通部分
    public dbIdText(){
        setTableName(tableName);
        setTableUnique(tableUnique);
        setTablePrimary(tablePrimary);
        setTableField(tableField);
        logDebug("初期化:" + tableName);
    }
}
