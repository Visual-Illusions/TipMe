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
package net.visualillusionsent.minecraft.server.mod.canary.plugin.tipme;

import java.util.logging.Logger;
import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.plugin.Plugin;
import net.visualillusionsent.tipme.TipMe;
import net.visualillusionsent.tipme.TipMeData;

/**
 * TipMe main plugin class for CanaryMod
 * 
 * @author Jason (darkdiplomat)
 */
public class CanaryTipMe extends Plugin implements TipMe{
    TipMeData tmd;

    @Override
    public boolean enable(){
        try {
            if (tmd == null) {
                tmd = new TipMeData(this);
                new TipMeCommandListener(this);
            }
        }
        catch (Throwable thrown) {
            getLogman().severe("TipMe failed to start...");
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
            ((Player) player).message(tip);
        }
    }
}
