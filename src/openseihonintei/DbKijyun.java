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
public class DbKijyun extends DbAccessOS{
    //個別部分 : テーブル定義
    private static final String tableName = "kijyun";
    private static final String[] tableUnique = {"nendo", "kyuti"};
    private static final String[] tablePrimary = {"nendo", "kyuti"};
    private static final String[][] tableField = {
        //列名, データ型(, 制約)
        {"nendo", "INTEGER"}, 
        {"kyuti", "INTEGER"},
        {"kyotaku1_11", "INTEGER"},
        {"kyotaku1_12", "INTEGER"},
        {"kyotaku1_13", "INTEGER"},
        {"kyotaku1_14", "INTEGER"},
        {"kyotaku1_15", "INTEGER"},
        {"kyotaku1_16", "INTEGER"},
        {"kyotaku1_17", "INTEGER"},
        {"kyotaku1_18", "INTEGER"},
        {"kyotaku1_21", "INTEGER"},
        {"kyotaku1_22", "INTEGER"},
        {"kyotaku1_23", "INTEGER"},
        {"kyotaku1_24", "INTEGER"},
        {"kyotaku1_25", "INTEGER"},
        {"kyotaku1_26", "INTEGER"},
        {"kyotaku1_27", "INTEGER"},
        {"kyotaku1_28", "INTEGER"},
        {"kyotaku2_101", "INTEGER"},
        {"kyotaku2_102", "INTEGER"},
        {"kyotaku2_103", "INTEGER"},
        {"kyotaku2_104", "INTEGER"},
        {"kyotaku2_105", "INTEGER"},
        {"kyotaku2_106", "INTEGER"},
        {"kyotaku2_107", "INTEGER"},
        {"kyotaku2_108", "INTEGER"},
        {"kyotaku2_109", "INTEGER"},
        {"kyotaku2_110", "INTEGER"},
        {"kyotaku2_201", "INTEGER"},
        {"kyotaku2_202", "INTEGER"},
        {"kyotaku2_203", "INTEGER"},
        {"kyotaku2_204", "INTEGER"},
        {"kyotaku2_205", "INTEGER"},
        {"kyotaku2_206", "INTEGER"},
        {"kyotaku2_207", "INTEGER"},
        {"kyotaku2_208", "INTEGER"},
        {"kyotaku2_209", "INTEGER"},
        {"kyotaku2_210", "INTEGER"},
        
        {"kyotaku2_T101", "INTEGER"},
        {"kyotaku2_T102", "INTEGER"},
        {"kyotaku2_T103", "INTEGER"},
        {"kyotaku2_T104", "INTEGER"},
        {"kyotaku2_T105", "INTEGER"},
        {"kyotaku2_T106", "INTEGER"},
        {"kyotaku2_T107", "INTEGER"},
        {"kyotaku2_T108", "INTEGER"},
        {"kyotaku2_T109", "INTEGER"},
        {"kyotaku2_T110", "INTEGER"},
        
        {"kyotaku2_T201", "INTEGER"},
        {"kyotaku2_T202", "INTEGER"},
        {"kyotaku2_T203", "INTEGER"},
        {"kyotaku2_T204", "INTEGER"},
        {"kyotaku2_T205", "INTEGER"},
        {"kyotaku2_T206", "INTEGER"},
        {"kyotaku2_T207", "INTEGER"},
        {"kyotaku2_T208", "INTEGER"},
        {"kyotaku2_T209", "INTEGER"},
        {"kyotaku2_T210", "INTEGER"},
        
        {"kyotaku2_T301", "INTEGER"},
        {"kyotaku2_T302", "INTEGER"},
        {"kyotaku2_T303", "INTEGER"},
        {"kyotaku2_T304", "INTEGER"},
        {"kyotaku2_T305", "INTEGER"},
        {"kyotaku2_T306", "INTEGER"},
        {"kyotaku2_T307", "INTEGER"},
        {"kyotaku2_T308", "INTEGER"},
        {"kyotaku2_T309", "INTEGER"},
        {"kyotaku2_T310", "INTEGER"},
        
        {"kyotaku2_T401", "INTEGER"},
        {"kyotaku2_T402", "INTEGER"},
        {"kyotaku2_T403", "INTEGER"},
        {"kyotaku2_T404", "INTEGER"},
        {"kyotaku2_T405", "INTEGER"},
        {"kyotaku2_T406", "INTEGER"},
        {"kyotaku2_T407", "INTEGER"},
        {"kyotaku2_T408", "INTEGER"},
        {"kyotaku2_T409", "INTEGER"},
        {"kyotaku2_T410", "INTEGER"},
        
        {"kyotaku2_T501", "INTEGER"},
        {"kyotaku2_T502", "INTEGER"},
        {"kyotaku2_T503", "INTEGER"},
        {"kyotaku2_T504", "INTEGER"},
        {"kyotaku2_T505", "INTEGER"},
        {"kyotaku2_T506", "INTEGER"},
        {"kyotaku2_T507", "INTEGER"},
        {"kyotaku2_T508", "INTEGER"},
        {"kyotaku2_T509", "INTEGER"},
        {"kyotaku2_T510", "INTEGER"},
        
        {"kyotaku2_T601", "INTEGER"},
        {"kyotaku2_T602", "INTEGER"},
        {"kyotaku2_T603", "INTEGER"},
        {"kyotaku2_T604", "INTEGER"},
        {"kyotaku2_T605", "INTEGER"},
        {"kyotaku2_T606", "INTEGER"},
        {"kyotaku2_T607", "INTEGER"},
        {"kyotaku2_T608", "INTEGER"},
        {"kyotaku2_T609", "INTEGER"},
        {"kyotaku2_T610", "INTEGER"},
        
        {"Teigen_101", "INTEGER"},
        {"Teigen_102", "INTEGER"},
        {"Teigen_103", "INTEGER"},
        {"Teigen_104", "INTEGER"},
        {"Teigen_105", "INTEGER"},
        {"Teigen_106", "INTEGER"},
        {"Teigen_107", "INTEGER"},
        {"Teigen_108", "INTEGER"},
        {"Teigen_109", "INTEGER"},
        {"Teigen_110", "INTEGER"},
        {"Teigen_201", "INTEGER"},
        {"Teigen_202", "INTEGER"},
        {"Teigen_203", "INTEGER"},
        {"Teigen_204", "INTEGER"},
        {"Teigen_205", "INTEGER"},
        {"Teigen_206", "INTEGER"},
        {"Teigen_207", "INTEGER"},
        {"Teigen_208", "INTEGER"},
        {"Teigen_209", "INTEGER"},
        {"Teigen_210", "INTEGER"},
        {"KyugoSisetu", "INTEGER"},
        {"KouseiSisetu", "INTEGER"},
        
        {"Sisetu_T1", "INTEGER"},
        {"Sisetu_T2", "INTEGER"},
        {"Sisetu_T3", "INTEGER"},
        {"Sisetu_T4", "INTEGER"},
        {"Sisetu_T5", "INTEGER"},
        {"Sisetu_T6", "INTEGER"},

        {"Kimatu_01", "INTEGER"},
        {"Kimatu_02", "INTEGER"},
        {"Kimatu_03", "INTEGER"},
        {"Kimatu_04", "INTEGER"},
        {"Kimatu_05", "INTEGER"},
        {"Kimatu_06", "INTEGER"},
        {"Kimatu_07", "INTEGER"},
        {"Kimatu_08", "INTEGER"},
        {"Kimatu_09", "INTEGER"},
        {"Kimatu_10", "INTEGER"},

        {"Kimatu_KyugoSisetu", "INTEGER"},

        {"NyuinNitiyouHinPi", "INTEGER"},
        {"NyuinNitiyouHinPi_T1", "INTEGER"},
        {"NyuinNitiyouHinPi_T2", "INTEGER"},
        {"NyuinNitiyouHinPi_T3", "INTEGER"},
        {"NyuinNitiyouHinPi_T4", "INTEGER"},
        {"NyuinNitiyouHinPi_T5", "INTEGER"},
        {"NyuinNitiyouHinPi_T6", "INTEGER"},

        {"KaigoSeikatuHi", "INTEGER"},
        {"KaigoSeikatuHi_T1", "INTEGER"},
        {"KaigoSeikatuHi_T2", "INTEGER"},
        {"KaigoSeikatuHi_T3", "INTEGER"},
        {"KaigoSeikatuHi_T4", "INTEGER"},
        {"KaigoSeikatuHi_T5", "INTEGER"},
        {"KaigoSeikatuHi_T6", "INTEGER"},

        {"NinpuS", "INTEGER"},
        {"NinpuL", "INTEGER"},
        {"Sanpu", "INTEGER"},

        {"Syougai1", "INTEGER"},
        {"Syougai2", "INTEGER"},
        {"Syougai3", "INTEGER"},
        {"Syougai4", "INTEGER"},
        {"Syougai5", "INTEGER"},
        {"Syougai6", "INTEGER"},
        {"Syougai7", "INTEGER"},
        {"Syougai8", "INTEGER"},

        {"KaigoNyusyo", "INTEGER"},
        {"ZaitakuKnajya", "INTEGER"},
        {"Housya1", "INTEGER"},
        {"Housya2", "INTEGER"},
        {"JIdouyouiku1", "INTEGER"},
        {"JIdouyouiku2", "INTEGER"},

        {"Boshi1Z", "INTEGER"},
        {"Boshi1N", "INTEGER"},
        {"Boshi2Z", "INTEGER"},
        {"Boshi2N", "INTEGER"},
        {"Boshi3Z", "INTEGER"},
        {"Boshi3N", "INTEGER"},
        
        //教育扶助
        {"KyoikuS", "INTEGER"},
        {"KyoikuC", "INTEGER"},
        {"KyoikuSienS", "INTEGER"},
        {"KyoikuSienC", "INTEGER"},
        {"KyoikuGakkyuuS", "INTEGER"},
        {"KyoikuGakkyuuC", "INTEGER"},
        {"KyoikuSaiSikyuS", "INTEGER"},
        {"KyoikuSaiSikyuC", "INTEGER"},

        {"JyutakuJyougen", "INTEGER"}
    };
    //共通部分
    public DbKijyun(){
        setTableName(tableName);
        setTableUnique(tableUnique);
        setTablePrimary(tablePrimary);
        setTableField(tableField);
        logDebug("初期化:" + tableName);
    }
    
    /**
     * 基準DB専用　リザルトセットの配列の行番号を返します。
     * @param rs
     * @param kyutiID 11 ならば１級地−１の基準を探します
     * @return 
     */
    public int getKyutiIdx(String[][] rs, String kyutiID) {
        for (int i = 0; i < rs.length; i++) {
            String wk = this.getValue(rs, "kyuti", i);
            if (wk.equals(kyutiID)) {
                return i;
            }
        }
        //見つからなかった場合
        return -1;
    }
}
