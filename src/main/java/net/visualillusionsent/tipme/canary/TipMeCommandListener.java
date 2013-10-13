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
package net.visualillusionsent.tipme.canary;

import net.canarymod.Canary;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.visualillusionsent.tipme.TipMeData;
import net.visualillusionsent.utils.StringUtils;

/**
 * TipMe Command Listener for CanaryMod
 *
 * @author Jason (darkdiplomat)
 */
public final class TipMeCommandListener implements CommandListener {
    private final TipMeData data;

    TipMeCommandListener(CanaryTipMe tipme) throws CommandDependencyException {
        this.data = tipme.tmd;
        Canary.commands().registerCommands(this, tipme, false);
    }

    @Command(aliases = {"tip"},
            description = "TipMe command",
            permissions = {"tipme.admin"},
            toolTip = "/tip <add|remove|getall|reload|server> [index|message]")
    public final void main(MessageReceiver msgrec, String[] args) {
        msgrec.notice("Usage: /tip <add|remove|getall|reload|server> [index|message] (index needed for removal, message need for adding)");
    }

    @Command(aliases = {"add"},
            description = "TipMe add tip command",
            permissions = {"tipme.admin"},
            toolTip = "/tip add <message>",
            parent = "tip")
    public final void add(MessageReceiver msgrec, String[] args) {
        if (args.length < 1) {
            msgrec.notice("/tip add <message>");
            return;
        }
        String tip = StringUtils.joinString(args, " ", 1);
        if (data.createTip(tip)) {
            msgrec.message("\u00A72Tip Added!");
        } else {
            msgrec.notice("Failed to add tip... Error has been logged.");
        }
    }

    @Command(aliases = {"remove"},
            description = "TipMe remove tip command",
            permissions = {"tipme.admin"},
            toolTip = "/tip remove <index>",
            parent = "tip")
    public final void remove(MessageReceiver msgrec, String[] args) {
        if (args.length > 1) {
            try {
                int index = Integer.parseInt(args[1]);
                boolean removed = data.removeTip(index);
                msgrec.message(removed ? "\u00A74Tip Removed!" : "\u00A7cFailed to remove tip... Error has been logged.");
            } catch (NumberFormatException nfe) {
                msgrec.notice("Invalid Tip #");
            }
        }
    }

    @Command(aliases = {"getall"},
            description = "TipMe getall command",
            permissions = {"tipme.admin"},
            toolTip = "/tip getall",
            parent = "tip")
    public final void getall(MessageReceiver msgrec, String[] args) {
        data.sendAll(new CanaryTipReceiver(msgrec));
    }

    @Command(aliases = {"reload"},
            description = "TipMe reload command",
            permissions = {"tipme.admin"},
            toolTip = "/tipme reload",
            parent = "tipme")
    public final void reload(MessageReceiver msgrec, String[] args) {
        if (!data.reload()) {
            msgrec.notice("Failed to reload tips... Error has been logged.");
        } else {
            msgrec.message("\u00A72Tips reloaded!");
        }
    }

    @Command(aliases = {"server"},
            description = "TipMe tip server command",
            permissions = {"tipme.admin"},
            toolTip = "/tip server",
            parent = "tip")
    public final void tipserver(MessageReceiver msgrec, String[] args) {
        data.sendTip();
    }
}
