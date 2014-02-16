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
package net.visualillusionsent.tipme.spout;

import net.visualillusionsent.minecraft.plugin.spout.VisualIllusionsSpoutPlugin;
import net.visualillusionsent.tipme.TipMe;
import net.visualillusionsent.tipme.TipMeData;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.command.annotated.AnnotatedCommandExecutorFactory;

import java.util.logging.Logger;

public final class SpoutTipMe extends VisualIllusionsSpoutPlugin implements TipMe {

    TipMeData tmd;

    public void onEnable() {
        try {
            if (tmd == null) {
                tmd = new TipMeData(this);
                AnnotatedCommandExecutorFactory.create(new TipMeCommandHandler(this));
            }
        }
        catch (Throwable thrown) {
            getLogger().severe("TipMe failed to start...");
        }
    }

    public void onDisable() {
    }

    @Override
    public Logger getLog() {
        return this.getLogger();
    }

    @Override
    public void broadcastTip(String tip) {
        ((Server) Spout.getEngine()).broadcastMessage(tip);
    }
}
