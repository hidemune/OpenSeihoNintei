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

import javax.swing.UIManager;

/**
 *
 * @author hdm
 */
public class OpenSeihoNintei {
public static final String version = "0.01";
public static int MaxSetaiIn = 20;
public static String DefaultKyuti = "11";
public static String DefaultTouki = "6";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e) {
          e.printStackTrace();
        }
        
        SplashFrame sp = new SplashFrame();
        sp.setVisible(true);
        MainFrame frm = new MainFrame();
        frm.setVisible(true);
        sp.setVisible(false);
        //frm.init();
    }
    
}
