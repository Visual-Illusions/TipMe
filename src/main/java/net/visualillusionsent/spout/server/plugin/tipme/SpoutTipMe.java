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
                Spout.getCommandManager().getCommand("tipme").setExecutor(new TipMeCommandExecutor(this));
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
