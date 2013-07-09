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
final class TipMeMySQL implements TipMeDatasource{

    private final TipMe tipme;
    private final TipMeData data;

    private final String tip_tablecreate = "CREATE TABLE IF NOT EXISTS `TipMeTips` (`id` INT(255) NOT NULL AUTO_INCREMENT, `Tip` TEXT NOT NULL, PRIMARY KEY (`id`))";
    private final String tip_insert = "INSERT INTO TipMeTips (Tip) VALUES(?)";
    private final String tip_delete = "DELETE FROM TipMeTips WHERE Tip = ?";
    private final String tip_select = "SELECT * FROM TipMeTips";

    TipMeMySQL(TipMe tipme, TipMeData data){
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