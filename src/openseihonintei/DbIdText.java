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

/**
 *
 * @author hdm
 * DBアクセス機能（サブクラス）
 * 
 * テーブル名：ID-Textテーブル
 */
public class DbIdText extends DbAccessOS{
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
    public DbIdText(){
        setTableName(tableName);
        setTableUnique(tableUnique);
        setTablePrimary(tablePrimary);
        setTableField(tableField);
        logDebug("初期化:" + tableName);
    }
    
}
