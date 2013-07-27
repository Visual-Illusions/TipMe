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
package net.visualillusionsent.spout.server.plugin.tipme;

import net.visualillusionsent.tipme.TipMeData;
import org.spout.api.command.Command;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.Executor;
import org.spout.api.exception.CommandException;

/**
 * TipMe Command Executor for Spout
 * 
 * @author Jason (darkdiplomat)
 */
public class TipMeCommandHandler implements Executor {

    private final TipMeData tmd;
    private final SpoutTipMe tipme;

    TipMeCommandHandler(SpoutTipMe tipme) {
        this.tmd = tipme.tmd;
        this.tipme = tipme;
    }

    @Override
    public void execute(CommandSource source, Command command, CommandArguments args) throws CommandException {
        //        if (command.getName().equals("tipme")) {
        //            try {
        //                if (args.length() > 0) {
        //                    if (args.length() > 1) {
        //                        if (args.getString(0).equalsIgnoreCase("add")) {
        //                            String tip = args.getJoinedString(1);
        //                            if (tmd.createTip(tip)) {
        //                                source.sendMessage("\u00A72Tip Added!");
        //                            }
        //                            else {
        //                                source.sendMessage("\u00A7CFailed to add tip... Error has been logged.");
        //                            }
        //                            return;
        //                        }
        //                    }
        //                    else if (args.getString(0).equalsIgnoreCase("remove")) {
        //                        if (args.length() > 2) {
        //                            try {
        //                                int index = args.getInteger(1);
        //                                boolean removed = tmd.removeTip(index);
        //                                source.sendMessage(removed ? "\u00A74Tip Removed!" : "\u00A7cFailed to remove tip... Error has been logged.");
        //                            }
        //                            catch (CommandException cex) {
        //                                source.sendMessage("\u00A7CInvaild Tip #");
        //                            }
        //                            return;
        //                        }
        //                    }
        //                    else if (args.getString(0).equalsIgnoreCase("getall")) {
        //                        if (source instanceof Player) {
        //                            tmd.sendAll((Player) source);
        //                        }
        //                        else {
        //                            tmd.sendAllConsole();
        //                        }
        //                        return;
        //                    }
        //                    else if (args.getString(0).equalsIgnoreCase("reload")) {
        //                        if (!tmd.reload()) {
        //                            source.sendMessage("\u00A7CFailed to reload tips... Error has been logged.");
        //                        }
        //                        else {
        //                            source.sendMessage("\u00A72Tips reloaded!");
        //                        }
        //                        return;
        //                    }
        //                    else if (args.getString(0).equalsIgnoreCase("tipserver")) {
        //                        tmd.sendTip();
        //                        return;
        //                    }
        //                }
        //                source.sendMessage("\u00A7CUsage: /tipme <add|remove|getall|reload|tipserver> [index|tipmessage] (index needed for removal, message need for adding)");
        //            }
        //            catch (Exception ex) {
        //                source.sendMessage("\u00A7CAn unhandled exception has occurred in TipMe! Error has been logged!");
        //                tipme.getLogger().log(Level.SEVERE, "An unhandled exception has occurred in TipMe! Report this to DarkDiplomat on GitHub!", ex);
        //            }
        //        }
    }
}
