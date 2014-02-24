/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package openseihonintei;

/**
 *
 * @author hdm
 */
public class OpenSeihoNintei {
public static final String version = "0.01";
public static int MaxSetaiIn = 14;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SplashFrame sp = new SplashFrame();
        sp.setVisible(true);
        MainFrame frm = new MainFrame();
        frm.setVisible(true);
        sp.setVisible(false);
        //frm.init();
    }
    
}
