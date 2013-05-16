package net.visualillusionsent.minecraft.server.mod.canary.plugin.tipme;

import java.util.logging.Logger;
import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.plugin.Plugin;
import net.visualillusionsent.minecraft.server.mod.plugin.tipme.ITipMe;
import net.visualillusionsent.minecraft.server.mod.plugin.tipme.TipMeData;

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
public class TipMe extends Plugin implements ITipMe{
    TipMeData tmd;

    @Override
    public boolean enable(){
        try {
            if (tmd == null) {
                tmd = new TipMeData(this);
                new TipMeCommandListener(this);
            }
        }
        catch (Exception ex) {
            getLogman().logStacktrace("TipMe failed to start...", ex);
            return false;
        }
        return true;
    }

    @Override
    public void disable(){
        tmd.killTimer();
    }

    @Override
    public Logger getLog(){
        return getLogman();
    }

    @Override
    public void broadcastTip(String tip){
        Canary.getServer().broadcastMessage(tip);
    }

    @Override
    public void sendPlayerMessage(Object player, String tip){
        if (player instanceof Player) {
            ((Player) player).sendMessage(tip);
        }
    }
}
