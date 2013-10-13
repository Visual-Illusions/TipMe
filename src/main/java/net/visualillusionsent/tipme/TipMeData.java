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
package net.visualillusionsent.tipme;

import java.io.*;
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
    private Properties tipsprops;
    private TipMeDatasource tmds;
    private int currenttip;
    private ArrayList<String> tips = new ArrayList<String>();
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

    private final boolean load() {
        if (useMySQL) {
            tmds = new TipMeMySQL(tipme, this);
        } else {
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
            tips = new ArrayList<String>(backup);
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
                    String[] parse = tip.split(colorPre + "[Zz]");
                    receiver.send("\u00A76#" + index + ":\u00A7r " + parse[0]);
                    for (int lnindex = 1; lnindex < parse.length; lnindex++) {
                        receiver.send("  ".concat(parse[lnindex]));
                    }
                    index++;
                }
            }
        } else {
            receiver.send("\u00A7CNo tips to display...");
        }
    }

    public final void sendTip() {
        if (tips.size() > 0) {
            String tippy = null;
            if (currenttip >= tips.size()) {
                if (randomize) {
                    Collections.shuffle(tips);
                }
                currenttip = 0;
            }
            tippy = tips.get(currenttip);
            currenttip++;
            if (tippy != null) {
                String[] parse = tippy.split(colorPre.concat("[Zz]"));
                tipme.broadcastTip(new StringBuilder().append(prefix).append("\u00A7r ").append(parse[0]).toString());
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

    private final boolean makeFiles() {
        File checkDir = new File(tipsFile.replace("Tips.txt", ""));
        File checkTipsFile = new File(tipsFile);
        File checkTipsProps = new File(tipspropsFile);
        if (!checkDir.exists()) {
            if (!checkDir.mkdirs()) {
                tipme.getLog().severe("Unable to create TipMe configuration directory...");
                return false;
            }
        }
        if (!checkTipsFile.exists()) {
            try {
                checkTipsFile.createNewFile();
            } catch (IOException e) {
                tipme.getLog().severe("Failed to make Tips.txt");
                return false;
            }
        }
        if (!checkTipsProps.exists()) {
            try {
                BufferedWriter write = new BufferedWriter(new FileWriter(checkTipsProps));
                write.write("#Sets the String to convert into \u00A7 (Default: @)");
                write.newLine();
                write.write("color.prefix=@");
                write.newLine();
                write.write("#The String to use to prefix Tips (Default: @2ProTip:)");
                write.newLine();
                write.write("tip.prefix=@2ProTip:");
                write.newLine();
                write.write("#Specifies if the Tips should be displayed using the internal scheduler");
                write.newLine();
                write.write("use.internal.schedule=true");
                write.newLine();
                write.write("#The time in minutes between displaying tips (Default: 5)");
                write.newLine();
                write.write("internal.schedule.delay=5");
                write.newLine();
                write.write("#Specifies whether to randomize the tips or not (Default: true)");
                write.newLine();
                write.write("randomize=false");
                write.newLine();
                write.write("#Specifies whether to store Tips in a MySQL Table (Default: false)");
                write.newLine();
                write.write("use.mysql=false");
                write.newLine();
                write.write("#Specifies the user name to connect to the MySQL Database (Default: root)");
                write.newLine();
                write.write("mysql.username=root");
                write.newLine();
                write.write("#Specifies the password to use to connect to the MySQL Database (Default: password)");
                write.newLine();
                write.write("mysql.password=password");
                write.newLine();
                write.write("#Specifies the URL of the MySQL Database in \"url:port/database\" format");
                write.newLine();
                write.write("mysql.db.url=url:port/database");
                write.newLine();
                write.flush();
                write.close();
            } catch (IOException e) {
                return false;
            }
        }
        tipsprops = new Properties();
        try {
            tipsprops.load(new FileInputStream(tipspropsFile));
            return true;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return false;
    }

    private final String parseTip(String tip) {
        return tip.replaceAll(colorPre + "([0-9A-FK-ORa-fk-or])", "\u00A7$1");
    }
}
