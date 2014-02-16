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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * TipMe MySQL data source
 *
 * @author Jason (darkdiplomat)
 */
final class TipMeMySQL implements TipMeDatasource {

    private final TipMe tipme;
    private final TipMeData data;

    TipMeMySQL(TipMe tipme, TipMeData data) {
        this.tipme = tipme;
        this.data = data;
    }

    @Override
    public final boolean loadTips() {
        boolean toRet = true;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = data.getConnection();
        }
        catch (SQLException sqlex) {
            tipme.getPluginLogger().log(Level.SEVERE, "Failed to get SQL Connection...", sqlex);
            toRet = false;
        }
        if (conn != null) {
            try {
                int num = 0;
                ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `TipMeTips` (`id` INT(255) NOT NULL AUTO_INCREMENT, `Tip` TEXT NOT NULL, PRIMARY KEY (`id`))");
                ps.execute();
                ps = conn.prepareStatement("SELECT * FROM TipMeTips");
                rs = ps.executeQuery();
                while (rs.next()) {
                    data.addFromSource(rs.getString("Tip"));
                    num++;
                }
                tipme.getPluginLogger().info(String.format("Loaded %d tips.", num));
            }
            catch (SQLException sqlex) {
                tipme.getPluginLogger().log(Level.SEVERE, "Failed to load tips from TipMeTips table...", sqlex);
                toRet = false;
            }
            finally {
                try {
                    if (rs != null && !rs.isClosed()) {
                        rs.close();
                    }
                    if (ps != null && !ps.isClosed()) {
                        ps.close();
                    }
                    if (!conn.isClosed()) {
                        conn.close();
                    }
                }
                catch (SQLException sqlex) {
                    tipme.getPluginLogger().warning("Failed to close SQL Connection...");
                }
            }
        }
        return toRet;
    }

    @Override
    public final boolean saveTip(String tip) {
        boolean toRet = true;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = data.getConnection();
        }
        catch (SQLException sqlex) {
            tipme.getPluginLogger().log(Level.SEVERE, "Failed to get SQL Connection...", sqlex);
            toRet = false;
        }
        if (conn != null) {
            try {
                ps = conn.prepareStatement("INSERT INTO TipMeTips (Tip) VALUES(?)");
                ps.setString(1, tip);
                ps.execute();
            }
            catch (SQLException sqlex) {
                tipme.getPluginLogger().log(Level.SEVERE, "Failed to insert tip into TipMeTips table...", sqlex);
                toRet = false;
            }
            finally {
                try {
                    if (ps != null && !ps.isClosed()) {
                        ps.close();
                    }
                    if (!conn.isClosed()) {
                        conn.close();
                    }
                }
                catch (SQLException sqlex) {
                    tipme.getPluginLogger().warning("Failed to close SQL Connection...");
                }
            }
        }
        return toRet;
    }

    @Override
    public final boolean removeTip(String tip) {
        boolean toRet = true;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = data.getConnection();
        }
        catch (SQLException sqlex) {
            tipme.getPluginLogger().log(Level.SEVERE, "Failed to get SQL Connection...", sqlex);
            toRet = false;
        }
        if (conn != null) {
            try {
                ps = conn.prepareStatement("DELETE FROM TipMeTips WHERE Tip = ?");
                ps.setString(1, tip);
                ps.execute();
            }
            catch (SQLException sqlex) {
                tipme.getPluginLogger().log(Level.SEVERE, "Failed to delete tip from TipMeTips table...", sqlex);
                toRet = false;
            }
            finally {
                try {
                    if (ps != null && !ps.isClosed()) {
                        ps.close();
                    }
                    if (!conn.isClosed()) {
                        conn.close();
                    }
                }
                catch (SQLException sqlex) {
                    tipme.getPluginLogger().warning("Failed to close SQL Connection...");
                }
            }
        }
        return toRet;
    }
}
