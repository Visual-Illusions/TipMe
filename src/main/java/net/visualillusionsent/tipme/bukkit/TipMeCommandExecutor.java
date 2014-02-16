/*
 * This file is part of TipMe.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
 *
 * TipMe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.tipme.bukkit;

import net.visualillusionsent.minecraft.plugin.bukkit.VisualIllusionsBukkitPluginInformationCommand;
import net.visualillusionsent.tipme.TipMeData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.logging.Level;

/**
 * TipMe Command Executor for Bukkit
 *
 * @author Jason (darkdiplomat)
 */
public class TipMeCommandExecutor extends VisualIllusionsBukkitPluginInformationCommand implements CommandExecutor {
    private final TipMeData tmd;

    TipMeCommandExecutor(BukkitTipMe tipme) {
        super(tipme);
        this.tmd = tipme.tmd;
        tipme.getCommand("tipme").setExecutor(this);
        tipme.getCommand("tip").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equals("tip")) {
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
                        tmd.sendAll(new BukkitTipReceiver(sender));
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
                    else if (args[0].equalsIgnoreCase("server")) {
                        tmd.sendTip();
                        return true;
                    }
                }
                sender.sendMessage("\u00A7CUsage: /tip <add|remove|getall|reload|server> [index|message] (index needed for removal, message need for adding)");
            }
            catch (Exception ex) {
                sender.sendMessage("\u00A7CAn unhandled exception has occurred in TipMe! Error has been logged!");
                plugin.getPluginLogger().log(Level.SEVERE, "An unhandled exception has occurred in TipMe! Report this to DarkDiplomat on GitHub!", ex);
            }
            return true;
        }
        else if (label.equals("tipme")) {
            this.sendInformation(sender);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
