/*
 * Copyright (C) 2014 TANAKA_Hidemune
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

import java.util.ArrayList;

/**
 * ArrayListの拡張
 * 　取得時に例外を出したくない場合に使用する。
 * 　Listには、同じインスタンスが続けて入っているものとみなす。
 * @author TANAKA_Hidemune
 */
public class ArrayListOS<E extends Object> extends ArrayList<E>{
private ClassSetaiIn defaultSetaiIn;

    public ArrayListOS() {
        this.defaultSetaiIn = new ClassSetaiIn();
    }
    /**
     * 0番目に何も入っていない状況で呼び出さないでください。
     * @param idx
     * @return 
     */
    public E getSafety(int idx) {
        E ret = null;
        try {
            ret = this.get(idx);
        } catch (Exception e) {
//            if (this.get(0) instanceof ClassSetaiIn) {
                ret = (E) defaultSetaiIn;
//            }
        }
        return ret;
    }
}
