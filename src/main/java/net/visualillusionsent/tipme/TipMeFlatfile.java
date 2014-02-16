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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

/**
 * TipMe Flatfile data source
 *
 * @author Jason (darkdiplomat)
 */
public class TipMeFlatfile implements TipMeDatasource {

    private final TipMe tipme;
    private final TipMeData data;
    private final File tipsFile = new File("config/TipMe/Tips.txt");

    TipMeFlatfile(TipMe tipme, TipMeData data) {
        this.tipme = tipme;
        this.data = data;
    }

    @Override
    public boolean loadTips() {
        BufferedReader in = null;
        boolean toRet = true;
        try {
            in = new BufferedReader(new FileReader(tipsFile));
            String str;
            int num = 0;
            while ((str = in.readLine()) != null) {
                if (!str.startsWith("#") && !str.startsWith(";") && !str.trim().isEmpty()) {
                    data.addFromSource(str);
                    num++;
                }
            }
            tipme.getPluginLogger().info(String.format("Loaded %d tips.", num));
        }
        catch (IOException ioex) {
            tipme.getPluginLogger().log(Level.SEVERE, "Unable to load Tips.txt", ioex);
            toRet = false;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ioex) {
                    tipme.getPluginLogger().warning("Failed to close Tips.txt");
                }
            }
        }
        return toRet;
    }

    @Override
    public boolean saveTip(String tip) {
        boolean toRet = true;
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(tipsFile, true));
            out.println(tip);
            out.flush();
        }
        catch (IOException ioex) {
            tipme.getPluginLogger().log(Level.SEVERE, "Unable to save tip to Tips.txt", ioex);
            toRet = false;
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
        return toRet;
    }

    @Override
    public boolean removeTip(String tip) {
        try {
            FileUtils.removeLine(tipsFile, tip);
        }
        catch (Exception ex) {
            return false;
        }
        return true;
    }
}
