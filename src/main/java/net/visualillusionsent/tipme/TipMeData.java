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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Timer;

/**
 * TipMe data handler
 *
 * @author Jason (darkdiplomat)
 */
public final class TipMeData {

    private final TipMe tipme;
    private final boolean randomize;
    private final boolean useMySQL;
    private final boolean useScheduler;
    private final long tipDelay;
    private final String colorPre;
    private final String prefix;
    private final String tipspropsFile = "config/TipMe/TipMe.cfg";
    private final String sqlDriveURL = "jdbc:mysql://";
    private final ArrayList<String> tips = new ArrayList<String>();
    private Properties tipsprops;
    private TipMeDatasource tmds;
    private int currenttip;
    private String sqlUsername, sqlPassword, sqlDatabase;
    private Timer tipTime;

    final String tipsFile = "config/TipMe/Tips.txt";

    public TipMeData(TipMe tipme) {
        this.tipme = tipme;
        if (!makeFiles()) {
            throw new InternalError();
        }

        colorPre = tipsprops.getProperty("color.prefix", "@");
        prefix = parseTip(tipsprops.getProperty("tip.prefix", "@2ProTip:"));
        useScheduler = Boolean.parseBoolean(tipsprops.getProperty("use.internal.schedule", "true"));
        tipDelay = Long.parseLong(tipsprops.getProperty("internal.schedule.delay", "5")) * 60000;
        randomize = Boolean.parseBoolean(tipsprops.getProperty("randomize", "false"));
        useMySQL = Boolean.parseBoolean(tipsprops.getProperty("use.mysql", "false"));
        sqlUsername = tipsprops.getProperty("mysql.username", "root");
        sqlPassword = tipsprops.getProperty("mysql.password", "password");
        sqlDatabase = tipsprops.getProperty("mysql.db.url", "url:port/database");

        if (!load()) {
            throw new InternalError();
        }
    }

    private boolean load() {
        if (useMySQL) {
            tmds = new TipMeMySQL(tipme, this);
        }
        else {
            tmds = new TipMeFlatfile(tipme, this);
        }

        if (!tmds.loadTips()) {
            return false;
        }

        if (useScheduler) {
            tipTime = new Timer();
            tipTime.scheduleAtFixedRate(new SendTipTask(this), tipDelay, tipDelay);
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
        return true;
    }

    public final Connection getConnection() throws SQLException {
        return DriverManager.getConnection(sqlDriveURL.concat(sqlDatabase), sqlUsername, sqlPassword);
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
        if (!tips.isEmpty()) {
            synchronized (tips) {
                int index = 0;
                for (String tip : tips) {
                    String[] parse = tip.split(colorPre.concat("[Zz]"));
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
        if (tips.size() > 0) {
            if (currenttip >= tips.size()) {
                if (randomize) {
                    Collections.shuffle(tips);
                }
                currenttip = 0;
            }
            String tippy = tips.get(currenttip++);
            if (tippy != null) {
                String[] parse = tippy.split(colorPre.concat("[Zz]"));
                tipme.broadcastTip(prefix.concat("\u00A7r ").concat(parse[0]));
                for (int index = 1; index < parse.length; index++) {
                    tipme.broadcastTip("  ".concat(parse[index]));
                }
            }
        }
    }

    public final void killTimer() {
        if (tipTime != null) {
            tipTime.cancel();
        }
    }

    final void addFromSource(String tip) {
        tips.add(parseTip(tip));
    }

    private boolean makeFiles() {
        File checkDir = new File(tipsFile.replace("Tips.txt", ""));
        File checkTipsFile = new File(tipsFile);
        File checkTipsProps = new File(tipspropsFile);
        if (!checkDir.exists()) {
            if (!checkDir.mkdirs()) {
                tipme.getPluginLogger().severe("Unable to create TipMe configuration directory...");
                return false;
            }
        }
        if (!checkTipsFile.exists()) {
            try {
                checkTipsFile.createNewFile();
            }
            catch (IOException e) {
                tipme.getPluginLogger().severe("Failed to make Tips.txt");
                return false;
            }
        }
        if (!checkTipsProps.exists()) {
            try {
                PrintWriter printer = new PrintWriter(new FileWriter(checkTipsProps));
                printer.println("#Sets the String to convert into \u00A7 (Default: @)");
                printer.println("color.prefix=@");
                printer.println("#The String to use to prefix Tips (Default: @2ProTip:)");
                printer.println("tip.prefix=@2ProTip:");
                printer.println("#Specifies if the Tips should be displayed using the internal scheduler");
                printer.println("use.internal.schedule=true");
                printer.println("#The time in minutes between displaying tips (Default: 5)");
                printer.println("internal.schedule.delay=5");
                printer.println("#Specifies whether to randomize the tips or not (Default: true)");
                printer.println("randomize=false");
                printer.println("#Specifies whether to store Tips in a MySQL Table (Default: false)");
                printer.println("use.mysql=false");
                printer.println("#Specifies the user name to connect to the MySQL Database (Default: root)");
                printer.println("mysql.username=root");
                printer.println("#Specifies the password to use to connect to the MySQL Database (Default: password)");
                printer.println("mysql.password=password");
                printer.println("#Specifies the URL of the MySQL Database in \"url:port/database\" format");
                printer.println("mysql.db.url=url:port/database");
                printer.flush();
                printer.close();
            }
            catch (IOException e) {
                return false;
            }
        }
        tipsprops = new Properties();
        try {
            tipsprops.load(new FileInputStream(tipspropsFile));
            return true;
        }
        catch (FileNotFoundException e) {
            // IGNORED
        }
        catch (IOException e) {
            // IGNORED
        }
        return false;
    }

    private final String parseTip(String tip) {
        return tip.replaceAll(colorPre + "([0-9A-FK-ORa-fk-or])", "\u00A7$1");
    }
}
