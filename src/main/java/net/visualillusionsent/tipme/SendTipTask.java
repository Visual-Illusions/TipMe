/*
 * This file is part of TipMe.
 *
 * Copyright © 2012-2013 Visual Illusions Entertainment
 *
 * TipMe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * TipMe is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with TipMe.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.tipme;

import java.util.TimerTask;

/**
 * TipMe Send tip task
 * 
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
