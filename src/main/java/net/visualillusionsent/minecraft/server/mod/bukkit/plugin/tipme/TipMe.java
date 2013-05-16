package net.visualillusionsent.minecraft.server.mod.bukkit.plugin.tipme;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.visualillusionsent.minecraft.server.mod.plugin.tipme.ITipMe;
import net.visualillusionsent.minecraft.server.mod.plugin.tipme.TipMeData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
public final class TipMe extends JavaPlugin implements ITipMe{
    private TipMeData tmd;

    @Override
    public final void onEnable(){
        if (tmd == null) {
            tmd = new TipMeData(this);
        }
    }

    @Override
    public final void onDisable(){
        tmd.killTimer();
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command cmd, String mainCmd, String[] args){
        if (mainCmd.equals("tipme")) {
            try {
                if (args.length > 0) {
                    if (args.length > 1) {
                        if (args[0].equalsIgnoreCase("add")) {
                            String tip = StringUtils.join(args, " ", 1, args.length);
                            if (tmd.createTip(tip)) {
                                sender.sendMessage("\u00A72Tip Added!");
                            }
                            else {
                                sender.sendMessage("\u00A7CFailed to add tip... Error has been logged.");
                            }
                            return true;
                        }
                    }
                    else if (args[0].equalsIgnoreCase("remove")) {
                        if (args.length > 2) {
                            try {
                                int index = Integer.parseInt(args[1]);
                                boolean removed = tmd.removeTip(index);
                                sender.sendMessage(removed ? "\u00A74Tip Removed!" : "\u00A7cFailed to remove tip... Error has been logged.");
                            }
                            catch (NumberFormatException nfe) {
                                sender.sendMessage("\u00A7CInvaild Tip #");
                            }
                            return true;
                        }
                    }
                    else if (args[0].equalsIgnoreCase("getall")) {
                        if (sender instanceof Player) {
                            tmd.sendAll((Player) sender);
                        }
                        else {
                            tmd.sendAllConsole();
                        }
                        return true;
                    }
                    else if (args[0].equalsIgnoreCase("reload")) {
                        if (!tmd.reload()) {
                            sender.sendMessage("\u00A7CFailed to reload tips... Error has been logged.");
                        }
                        else {
                            sender.sendMessage("\u00A72Tips reloaded!");
                        }
                        return true;
                    }
                    else if (args[0].equalsIgnoreCase("tipserver")) {
                        tmd.sendTip();
                        return true;
                    }
                }
                sender.sendMessage("\u00A7CUsage: /tipme <add|remove|getall|reload|tipserver> [index|tipmessage] (index needed for removal, message need for adding)");
            }
            catch (Exception ex) {
                sender.sendMessage("\u00A7CAn unhandled exception has occurred in TipMe! Error has been logged!");
                this.getLogger().log(Level.SEVERE, "An unhandled exception has occurred in TipMe! Report this to DarkDiplomat on GitHub!", ex);
            }
            return true;
        }
        return false;
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
