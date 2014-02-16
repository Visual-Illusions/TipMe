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
package net.visualillusionsent.tipme;

import net.visualillusionsent.utils.FileUtils;
import net.visualillusionsent.utils.JarUtils;
import net.visualillusionsent.utils.PropertiesFile;
import net.visualillusionsent.utils.UtilityException;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * TipMe data handler
 *
 * @author Jason (darkdiplomat)
 */
public final class TipMeData {

    private final TipMe tipme;
    private final ArrayList<String> tips = new ArrayList<String>();
    private final PropertiesFile cfg;
    private TipMeDatasource tmds;
    private Timer tipTimer;
    private int cIndex;

    public TipMeData(TipMe tipme) {
        this.tipme = tipme;
        if (!makeFiles()) {
            throw new InternalError();
        }
        cfg = new PropertiesFile("config/TipMe/TipMe.cfg");
        testProperties();
        if (!load()) {
            throw new InternalError();
        }
    }

    private boolean load() {
        if (cfg.getBoolean("use.mysql")) {
            tmds = new TipMeMySQL(tipme, this);
        }
        else {
            tmds = new TipMeFlatfile(tipme, this);
        }
        if (!tmds.loadTips()) {
            return false;
        }
        if (cfg.getBoolean("use.internal.schedule")) {
            long delay = TimeUnit.MINUTES.toMillis(cfg.getLong("internal.schedule.delay"));
            tipTimer = new Timer();
            tipTimer.scheduleAtFixedRate(new SendTipTask(this), delay, delay);
        }
        return true;
    }

    public final boolean reload() {
        ArrayList<String> backup = new ArrayList<String>(tips);
        Collections.copy(backup, tips);
        tips.clear();
        if (!tmds.loadTips()) {
            Collections.copy(tips, backup);
            return false;
        }
        cfg.reload();
        return true;
    }

    public final Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://".concat(cfg.getString("mysql.db.url")), cfg.getString("mysql.username"), cfg.getString("mysql.password"));
    }

    public final boolean createTip(String tip) {
        if (tmds.saveTip(tip)) {
            tips.add(parseTip(tip));
            return true;
        }
        return false;
    }

    public final boolean removeTip(int index) {
        if (index < tips.size() && index > -1) {
            if (tmds.removeTip(tips.get(index))) {
                tips.remove(index);
                return true;
            }
        }
        return false;
    }

    public final void sendAll(TipReceiver receiver) {
        char cChar = cfg.getCharacter("color.prefix");
        if (!tips.isEmpty()) {
            synchronized (tips) {
                int index = 0;
                for (String tip : tips) {
                    String[] parse = tip.split(cChar + "[Zz]");
                    receiver.send("\u00A76#" + index + ":\u00A7r " + parse[0]);
                    for (int lnindex = 1; lnindex < parse.length; lnindex++) {
                        receiver.send("  ".concat(parse[lnindex]));
                    }
                    index++;
                }
            }
        }
        else {
            receiver.send("\u00A7CNo tips to display...");
        }
    }

    public final void sendTip() {
        char cChar = cfg.getCharacter("color.prefix");
        String prefix = cfg.getString("tip.prefix");
        if (tips.size() > 0) {
            if (cIndex >= tips.size()) {
                if (cfg.getBoolean("randomize")) {
                    Collections.shuffle(tips);
                }
                cIndex = 0;
            }
            String tippy = tips.get(cIndex++);
            if (tippy != null) {
                String[] parse = tippy.split(cChar + "[Zz]");
                tipme.broadcastTip(prefix.concat(parse[0]));
                for (int index = 1; index < parse.length; index++) {
                    tipme.broadcastTip("  ".concat(parse[index]));
                }
            }
        }
    }

    public final void killTimer() {
        if (tipTimer != null) {
            tipTimer.cancel();
        }
    }

    final void addFromSource(String tip) {
        tips.add(parseTip(tip));
    }

    private String parseTip(String tip) {
        return tip.replaceAll(cfg.getCharacter("color.prefix") + "([0-9A-FK-ORa-fk-or])", "\u00A7$1");
    }

    private boolean makeFiles() {
        File checkDir = new File("config/TipMe/");
        if (!checkDir.exists()) {
            if (!checkDir.mkdirs()) {
                tipme.getPluginLogger().severe("Unable to create TipMe configuration directory...");
                return false;
            }
        }
        File checkTipsFile = new File(checkDir, "Tips.txt");
        try {
            if (!checkTipsFile.exists() && !checkTipsFile.createNewFile()) {
                return false;
            }
        }
        catch (IOException ioex) {
            tipme.getPluginLogger().severe("Failed to make Tips.txt");
            return false;
        }
        File checkTipsProps = new File(checkDir, "TipMe.cfg");
        try {
            if (!checkTipsProps.exists()) {
                FileUtils.cloneFileFromJar(JarUtils.getJarPath(getClass()), "resources/default.cfg", checkTipsProps.getAbsolutePath());
            }
        }
        catch (UtilityException uex) {
            tipme.getPluginLogger().severe("Failed to make TipMe.cfg");
            return false;
        }
        return true;
    }

    private void testProperties() {
        cfg.getCharacter("color.prefix", '@');
        cfg.getString("tip.prefix", "@2ProTip:");
        cfg.getBoolean("use.internal.schedule", true);
        cfg.getLong("internal.schedule.delay", 5L);
        cfg.getBoolean("randomize", false);
        cfg.getBoolean("use.mysql", false);
        cfg.getString("mysql.username", "root");
        cfg.getString("mysql.password", "password");
        cfg.getString("mysql.db.url", "url:port/database");
    }

}
