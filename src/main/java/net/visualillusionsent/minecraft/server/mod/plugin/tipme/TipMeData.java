package net.visualillusionsent.minecraft.server.mod.plugin.tipme;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Timer;

/**
 * TipMe
 * <p>
 * Copyright (C) 2013 Visual Illusions Entertainment.
 * <p>
 * This program is free software: you can redistribute it and/or modify it<br/>
 * under the terms of the GNU General Public License as published by the Free Software Foundation,<br/>
 * either version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;<br/>
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.<br/>
 * See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program.<br/>
 * If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * @version 2.0.0
 * @author Jason (darkdiplomat)
 */
public final class TipMeData{

    private final ITipMe tipme;
    private final boolean randomize;
    private final boolean useMySQL;
    private final boolean useScheduler;
    private final long tipDelay;
    private final char colorPre;
    private final String prefix;
    private final String tipspropsFile = "plugins/TipMe/TipMe.cfg";
    private final String sqlDriveURL = "jdbc:mysql://";
    private Properties tipsprops;
    private TipMeDatasource tmds;
    private int currenttip;
    private ArrayList<String> tips = new ArrayList<String>();
    private String sqlUsername, sqlPassword, sqlDatabase;
    private Timer tipTime;

    final String tipsFile = "plugins/TipMe/Tips.txt";

    public TipMeData(ITipMe tipme){
        this.tipme = tipme;
        if (!makeFiles()) {
            throw new InternalError();
        }

        colorPre = tipsprops.getProperty("color.prefix", "@").trim().charAt(0);
        prefix = parseTip(tipsprops.getProperty("tip.prefix", "@2Tip"));
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

    private final boolean load(){
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

    public final boolean reload(){
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

    public final Connection getConnection() throws SQLException{
        return DriverManager.getConnection(sqlDriveURL + sqlDatabase, sqlUsername, sqlPassword);
    }

    public final boolean createTip(String tip){
        if (tmds.saveTip(tip)) {
            tips.add(tip);
            return true;
        }
        return false;
    }

    public final boolean removeTip(int index){
        if (index < tips.size() && index > -1) {
            if (tmds.removeTip(tips.get(index))) {
                tips.remove(index);
                return true;
            }
        }
        return false;
    }

    public final void sendAll(Object player){
        if (!tips.isEmpty()) {
            synchronized (tips) {
                int index = 0;
                for (String tip : tips) {
                    String[] parse = parseTip(tip).split(colorPre + "[Zz]");
                    tipme.sendPlayerMessage(player, "\u00A76#" + index + ":\u00A7r " + parse[0]);
                    for (int lnindex = 1; lnindex < parse.length; lnindex++) {
                        tipme.sendPlayerMessage(player, "  ".concat(parse[lnindex]));
                    }
                    index++;
                }
            }
        }
        else {
            tipme.sendPlayerMessage(player, "\u00A7CNo tips to display...");
        }
    }

    public void sendAllConsole(){
        if (!tips.isEmpty()) {
            synchronized (tips) {
                int index = 0;
                for (String tip : tips) {
                    String[] parse = parseTip(tip).split(colorPre + "[Zz]");
                    tipme.getLog().info("\u00A76#" + index + ":\u00A7r " + parse[0]);
                    for (int lnindex = 1; lnindex < parse.length; lnindex++) {
                        tipme.getLog().info("\t".concat(parse[lnindex]));
                    }
                    index++;
                }
            }
        }
        else {
            tipme.getLog().info("\u00A7CNo tips to display...");
        }
    }

    public final void sendTip(){
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
                String[] parse = parseTip(tippy).split(colorPre + "[Zz]");
                tipme.broadcastTip(prefix + "\u00A7r " + parse[0]);
                for (int index = 1; index < parse.length; index++) {
                    tipme.broadcastTip("  ".concat(parse[index]));
                }
            }
        }
    }

    public final void killTimer(){
        if (tipTime != null) {
            tipTime.cancel();
        }
    }

    final void addFromSource(String tip){
        tips.add(tip);
    }

    private final boolean makeFiles(){
        File checkDir = new File(tipsFile.replace("Tips.txt", ""));
        File checkTipsFile = new File(tipsFile);
        File checkTipsProps = new File(tipspropsFile);
        if (!checkDir.exists()) {
            if (!checkDir.mkdirs()) {
                tipme.getLog().severe("[TipMe] Unable to create TipMe configuration directory...");
                return false;
            }
        }
        if (!checkTipsFile.exists()) {
            try {
                checkTipsFile.createNewFile();
            }
            catch (IOException e) {
                tipme.getLog().severe("[TipMe] Failed to make Tips.txt");
                return false;
            }
        }
        if (!checkTipsProps.exists()) {
            try {
                BufferedWriter write = new BufferedWriter(new FileWriter(checkTipsProps));
                write.write("color.prefix=@");
                write.newLine();
                write.write("tip.prefix=@2Tip");
                write.newLine();
                write.write("use.internal.schedule=true");
                write.newLine();
                write.write("internal.schedule.delay=5");
                write.newLine();
                write.write("randomize=false");
                write.newLine();
                write.write("use.mysql=false");
                write.newLine();
                write.write("mysql.username=root");
                write.newLine();
                write.write("mysql.password=password");
                write.newLine();
                write.write("mysql.db.url=url:port/database");
                write.newLine();
                write.flush();
                write.close();
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
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        return false;
    }

    private final String parseTip(String tip){
        tip = tip.replace(colorPre + "0", "\u00A70");
        tip = tip.replace(colorPre + "1", "\u00A71");
        tip = tip.replace(colorPre + "2", "\u00A72");
        tip = tip.replace(colorPre + "3", "\u00A73");
        tip = tip.replace(colorPre + "4", "\u00A74");
        tip = tip.replace(colorPre + "5", "\u00A75");
        tip = tip.replace(colorPre + "6", "\u00A76");
        tip = tip.replace(colorPre + "7", "\u00A77");
        tip = tip.replace(colorPre + "8", "\u00A78");
        tip = tip.replace(colorPre + "9", "\u00A79");
        tip = tip.replaceAll(colorPre + "[Aa]", "\u00A7A");
        tip = tip.replaceAll(colorPre + "[Bb]", "\u00A7B");
        tip = tip.replaceAll(colorPre + "[Cc]", "\u00A7C");
        tip = tip.replaceAll(colorPre + "[Dd]", "\u00A7D");
        tip = tip.replaceAll(colorPre + "[Ee]", "\u00A7E");
        tip = tip.replaceAll(colorPre + "[Ff]", "\u00A7F");
        tip = tip.replaceAll(colorPre + "[Kk]", "\u00A7K");
        tip = tip.replaceAll(colorPre + "[Ll]", "\u00A7L");
        tip = tip.replaceAll(colorPre + "[Mm]", "\u00A7M");
        tip = tip.replaceAll(colorPre + "[Nn]", "\u00A7N");
        tip = tip.replaceAll(colorPre + "[Oo]", "\u00A7O");
        tip = tip.replaceAll(colorPre + "[Rr]", "\u00A7R");
        return tip;
    }
}
