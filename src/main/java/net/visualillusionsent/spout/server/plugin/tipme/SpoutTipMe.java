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

import java.util.logging.Logger;
import net.visualillusionsent.tipme.TipMe;
import net.visualillusionsent.tipme.TipMeData;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.plugin.Plugin;

public final class SpoutTipMe extends Plugin implements TipMe {

    TipMeData tmd;

    public void onEnable() {
        try {
            if (tmd == null) {
                tmd = new TipMeData(this);

            }
        }
        catch (Throwable thrown) {
            getLogger().severe("TipMe failed to start...");
        }
    }

    public void onDisable() {}

    @Override
    public Logger getLog() {
        return this.getLogger();
    }

    @Override
    public void broadcastTip(String tip) {
        ((Server) Spout.getEngine()).broadcastMessage(tip);
    }

    @Override
    public void sendPlayerMessage(Object player, String tip) {
        ((Player) player).sendMessage(tip);
    }
}
