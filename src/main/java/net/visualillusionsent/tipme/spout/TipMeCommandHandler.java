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
package net.visualillusionsent.tipme.spout;

import net.visualillusionsent.minecraft.plugin.spout.VisualIllusionsSpoutPluginInformationCommand;
import net.visualillusionsent.tipme.TipMeData;
import net.visualillusionsent.utils.VersionChecker;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.CommandDescription;
import org.spout.api.command.annotated.Permissible;
import org.spout.api.exception.CommandException;
import org.spout.vanilla.ChatStyle;

import java.util.logging.Level;

/**
 * TipMe Command Executor for Spout
 *
 * @author Jason (darkdiplomat)
 */
public class TipMeCommandHandler extends VisualIllusionsSpoutPluginInformationCommand {

    private final TipMeData tmd;

    TipMeCommandHandler(SpoutTipMe tipme) {
        super(tipme);
        this.tmd = tipme.tmd;
    }

    @CommandDescription(aliases = {"tip"}, usage = "<add|remove|getall|reload|server> [index|message]", desc = "TipMe command")
    @Permissible("tipme.admin")
    public void tip(CommandSource source, CommandArguments args) throws CommandException {
        String[] adjArgs = args.toArray();
        try {
            if (adjArgs.length > 0) {
                if (adjArgs.length > 1) {
                    if (adjArgs[0].equalsIgnoreCase("add")) {
                        String tip = args.toString().substring(4); //remove add
                        if (tmd.createTip(tip)) {
                            source.sendMessage("\u00A72Tip Added!");
                        } else {
                            source.sendMessage("\u00A7CFailed to add tip... Error has been logged.");
                        }
                        return;
                    }
                } else if (adjArgs[0].equalsIgnoreCase("remove")) {
                    if (args.length() > 2) {
                        try {
                            int index = Integer.valueOf(adjArgs[1]);
                            boolean removed = tmd.removeTip(index);
                            source.sendMessage(removed ? "\u00A74Tip Removed!" : "\u00A7cFailed to remove tip... Error has been logged.");
                        } catch (NumberFormatException nfex) {
                            source.sendMessage("\u00A7CInvaild Tip #");
                        }
                        return;
                    }
                } else if (adjArgs[0].equalsIgnoreCase("getall")) {
                    tmd.sendAll(new SpoutTipReceiver(source));
                    return;
                } else if (adjArgs[0].equalsIgnoreCase("reload")) {
                    if (!tmd.reload()) {
                        source.sendMessage("\u00A7CFailed to reload tips... Error has been logged.");
                    } else {
                        source.sendMessage("\u00A72Tips reloaded!");
                    }
                    return;
                } else if (adjArgs[0].equalsIgnoreCase("server")) {
                    tmd.sendTip();
                    return;
                }
            }
            source.sendMessage("\u00A7CUsage: /tip <add|remove|getall|reload|server> [index|message] (index needed for removal, message need for adding)");
        } catch (Exception ex) {
            source.sendMessage("\u00A7CAn unhandled exception has occurred in TipMe! Error has been logged!");
            plugin.getLogger().log(Level.SEVERE, "An unhandled exception has occurred in TipMe! Report this to DarkDiplomat on GitHub!", ex);
        }
    }

    @CommandDescription(aliases = {"tipme"}, usage = "", desc = "TipMe Information Command")
    public void tipme(CommandSource source, CommandArguments args) throws CommandException {
        for (String msg : about) {
            if (msg.equals("$VERSION_CHECK$")) {
                VersionChecker vc = plugin.getVersionChecker();
                Boolean isLatest = vc.isLatest();
                if (isLatest == null) {
                    source.sendMessage(center(ChatStyle.DARK_GRAY.toString().concat("VersionCheckerError: ").concat(vc.getErrorMessage())));
                } else if (!isLatest) {
                    source.sendMessage(center(ChatStyle.DARK_GRAY.toString().concat(vc.getUpdateAvailibleMessage())));
                } else {
                    source.sendMessage(center(ChatStyle.GREEN.toString().concat("Latest Version Installed")));
                }
            } else {
                source.sendMessage(msg);
            }
        }
    }
}
