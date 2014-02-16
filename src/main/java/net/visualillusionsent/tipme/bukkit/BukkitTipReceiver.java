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

import net.visualillusionsent.tipme.TipReceiver;
import org.bukkit.command.CommandSender;

/**
 * Bukkit Tip Receiver
 *
 * @author Jason (darkdiplomat)
 */
public class BukkitTipReceiver implements TipReceiver {

    private final CommandSender receiver;

    BukkitTipReceiver(CommandSender receiver) {
        this.receiver = receiver;
    }


    @Override
    public void send(String tip) {
        receiver.sendMessage(tip);
    }
}
