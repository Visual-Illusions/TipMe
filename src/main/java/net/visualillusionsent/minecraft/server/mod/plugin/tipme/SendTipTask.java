package net.visualillusionsent.minecraft.server.mod.plugin.tipme;

import java.util.TimerTask;

/**
 * TipMe
 * <p>
 * Copyright (C) 2013 Visual Illusions Entertainment.
 * <p>
 * This program is free software: you can redistribute it and/or modify it<br/>
 * under the terms of the GNU General Public License as published by the Free Software Foundation,<br/>
 * either version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;<br/>
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.<br/>
 * See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program.<br/>
 * If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * @version 2.0.0
 * @author Jason (darkdiplomat)
 */
final class SendTipTask extends TimerTask{
    private final TipMeData tmd;

    SendTipTask(TipMeData tmd){
        this.tmd = tmd;
    }

    public final void run(){
        tmd.sendTip();
    }
}
