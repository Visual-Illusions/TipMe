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
package net.visualillusionsent.minecraft.server.mod.bukkit.plugin.tipme;

import java.util.logging.Logger;
import net.visualillusionsent.minecraft.server.mod.plugin.tipme.TipMe;
import net.visualillusionsent.minecraft.server.mod.plugin.tipme.TipMeData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * TipMe main plugin class for Bukkit
 * 
 * @author Jason (darkdiplomat)
 */
public final class TipMe_Bukkit extends JavaPlugin implements TipMe{
    TipMeData tmd;

    @Override
    public final void onEnable(){
        try {
            if (tmd == null) {
                tmd = new TipMeData(this);
                getCommand("tipme").setExecutor(new TipMeCommandExecutor(this));
            }
        }
        catch (Throwable thrown) {
            getLogger().severe("TipMe failed to start...");
        }
    }

    @Override
    public final void onDisable(){
        if (tmd != null) {
            tmd.killTimer();
        }
    }

    @Override
    public Logger getLog(){
        return this.getLogger();
    }

    @Override
    public void broadcastTip(String tip){
        Bukkit.getServer().broadcastMessage(tip);
    }

    @Override
    public void sendPlayerMessage(Object player, String tip){
        if (player instanceof Player) {
            ((Player) player).sendMessage(tip);
        }
    }
}
