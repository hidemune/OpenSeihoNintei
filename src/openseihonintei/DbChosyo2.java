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
