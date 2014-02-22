/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 環境をいじっていて、クラスファイルが作成されなくなった。
 * このファイルを作成することで、紐付けを取り戻せることを発見。
*/

package OpenSeiho;

/**
 *
 * @author hdm
 */
public class TestMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        textYmdPanel textYmd = new textYmdPanel();
        TestFrame frm = new TestFrame();
        frm.add(textYmd);
        frm.setVisible(true);
    }
    
}
