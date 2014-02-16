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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

    TipMeFlatfile(TipMe tipme, TipMeData data) {
        this.tipme = tipme;
        this.data = data;
    }

    @Override
    public boolean loadTips() {
        BufferedReader in = null;
        boolean toRet = true;
        try {
            in = new BufferedReader(new FileReader(data.tipsFile));
            String str;
            int num = 0;
            while ((str = in.readLine()) != null) {
                if (!str.startsWith("#") && !str.startsWith(";") && !str.trim().isEmpty()) {
                    data.addFromSource(str);
                    num++;
                }
            }
            tipme.getLog().info(String.format("Loaded %d tips.", num));
        }
        catch (IOException ioex) {
            tipme.getLog().log(Level.SEVERE, "Unable to load Tips.txt", ioex);
            toRet = false;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ioex) {
                    tipme.getLog().warning("Failed to close Tips.txt");
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
            out = new PrintWriter(new FileWriter(data.tipsFile, true));
            out.println(tip);
        }
        catch (IOException ioex) {
            tipme.getLog().log(Level.SEVERE, "Unable to save tip to Tips.txt", ioex);
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
        boolean toRet = true;
        BufferedReader br = null;
        PrintWriter pw = null;
        File tips = new File(data.tipsFile);
        File tempFile = new File(data.tipsFile + ".tmp");
        try {
            br = new BufferedReader(new FileReader(tips));
            pw = new PrintWriter(new FileWriter(tempFile));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals(tip)) {
                    pw.println(line);
                    pw.flush();
                }
            }
        }
        catch (FileNotFoundException fnfex) {
            tipme.getLog().log(Level.SEVERE, "Unable to find Tips.txt", fnfex);
            toRet = false;
        }
        catch (IOException ioex) {
            tipme.getLog().log(Level.SEVERE, "Unable to save Tips.txt", ioex);
            toRet = false;
        }
        finally {
            if (pw != null) {
                pw.close();
            }
            try {
                if (br != null) {
                    br.close();
                }
            }
            catch (IOException e) {
                tipme.getLog().warning("Failed to close Tips.txt");
            }
            if (!tips.delete()) {
                tipme.getLog().severe("Could not delete old tips file...");
                toRet = false;
            }
            else if (!tempFile.renameTo(tips)) {
                tipme.getLog().severe("Could not rename tips tempfile...");
                toRet = false;
            }
        }
        return toRet;
    }
}
