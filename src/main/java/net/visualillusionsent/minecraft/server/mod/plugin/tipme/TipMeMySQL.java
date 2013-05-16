package net.visualillusionsent.minecraft.server.mod.plugin.tipme;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

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
final class TipMeMySQL implements TipMeDatasource{

    private final ITipMe tipme;
    private final TipMeData data;

    private final String tip_tablecreate = "CREATE TABLE IF NOT EXISTS `TipMeTips` (`id` INT(255) NOT NULL AUTO_INCREMENT, `Tip` TEXT NOT NULL, PRIMARY KEY (`id`))";
    private final String tip_insert = "INSERT INTO TipMeTips (Tip) VALUES(?)";
    private final String tip_delete = "DELETE FROM TipMeTips WHERE Tip = ?";
    private final String tip_select = "SELECT * FROM TipMeTips";

    TipMeMySQL(ITipMe tipme, TipMeData data){
        this.tipme = tipme;
        this.data = data;
    }

    @Override
    public final boolean loadTips(){
        boolean toRet = true;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = data.getConnection();
        }
        catch (SQLException sqlex) {
            tipme.getLog().log(Level.SEVERE, "Failed to get SQL Connection...", sqlex);
            toRet = false;
        }
        if (conn != null) {
            try {
                int num = 0;
                ps = conn.prepareStatement(tip_tablecreate);
                ps.execute();
                ps = conn.prepareStatement(tip_select);
                rs = ps.executeQuery();
                while (rs.next()) {
                    data.addFromSource(rs.getString("Tip"));
                    num++;
                }
                String.format("Loaded %d tips.", num);
            }
            catch (SQLException sqlex) {
                tipme.getLog().log(Level.SEVERE, "Failed to load tips from TipMeTips table...", sqlex);
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
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                }
                catch (SQLException sqlex) {
                    tipme.getLog().warning("Failed to close SQL Connection...");
                }
            }
        }
        return toRet;
    }

    @Override
    public final boolean saveTip(String tip){
        boolean toRet = true;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = data.getConnection();
        }
        catch (SQLException sqlex) {
            tipme.getLog().log(Level.SEVERE, "Failed to get SQL Connection...", sqlex);
            toRet = false;
        }
        if (conn != null) {
            try {
                ps = conn.prepareStatement(tip_insert);
                ps.setString(1, tip);
                ps.execute();
            }
            catch (SQLException sqlex) {
                tipme.getLog().log(Level.SEVERE, "Failed to insert tip into TipMeTips table...", sqlex);
                toRet = false;
            }
            finally {
                try {
                    if (ps != null && !ps.isClosed()) {
                        ps.close();
                    }
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                }
                catch (SQLException sqlex) {
                    tipme.getLog().warning("Failed to close SQL Connection...");
                }
            }
        }
        return toRet;
    }

    @Override
    public final boolean removeTip(String tip){
        boolean toRet = true;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = data.getConnection();
        }
        catch (SQLException sqlex) {
            tipme.getLog().log(Level.SEVERE, "Failed to get SQL Connection...", sqlex);
            toRet = false;
        }
        if (conn != null) {
            try {
                ps = conn.prepareStatement(tip_delete);
                ps.setString(1, tip);
                ps.execute();
            }
            catch (SQLException sqlex) {
                tipme.getLog().log(Level.SEVERE, "Failed to delete tip from TipMeTips table...", sqlex);
                toRet = false;
            }
            finally {
                try {
                    if (ps != null && !ps.isClosed()) {
                        ps.close();
                    }
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                }
                catch (SQLException sqlex) {
                    tipme.getLog().warning("Failed to close SQL Connection...");
                }
            }
        }
        return toRet;
    }
}
