/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicenta.pos.sales.restaurant;

import com.unicenta.data.loader.Session;
import com.unicenta.pos.forms.AppView;
import com.unicenta.pos.forms.DataLogicSystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author JDL
 */

public class RestaurantDBUtils {
    private Session s;
    private Connection con;
    private AppView m_App;

    protected DataLogicSystem dlSystem;

    /**
     *
     * @param oApp
     */
    public RestaurantDBUtils(AppView oApp) {
        m_App=oApp;

        try{
            s=m_App.getSession();
            con=s.getConnection();
        } catch (SQLException e){
        }
    }

    /**
     *
     * @param newTable
     * @param ticketID
     */
    public void moveCustomer(String newTable, String ticketID){
      String oldTable=getTableDetails(ticketID);

        if (countTicketIdInTable(ticketID)>1){
            setCustomerNameInTable(getCustomerNameInTable(oldTable),newTable);
            setWaiterNameInTable(getWaiterNameInTable(oldTable),newTable);
            setTicketIdInTable(ticketID,newTable);
            setGuestsInTable(getGuestsInTable(oldTable),newTable);



            oldTable = getTableMovedName(ticketID);
            if ((oldTable != null) && (oldTable != newTable)){
                clearCustomerNameInTable(oldTable);
                clearWaiterNameInTable(oldTable);
                clearTicketIdInTable(oldTable);
                clearTableMovedFlag(oldTable);
            } else {
                oldTable = getTableMovedName(ticketID);
                clearTableMovedFlag(oldTable);
            }
        }
  }

    /**
     *
     * @param custName
     * @param tableName
     */
    public void setCustomerNameInTable(String custName, String tableName){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET CUSTOMER=? WHERE NAME=?")) {
            pstmt.setString(1,custName);
            pstmt.setString(2,tableName);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param custName
     * @param tableID
     */
    public void setCustomerNameInTableById(String custName, String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET CUSTOMER=? WHERE ID=?")) {
            pstmt.setString(1,custName);
            pstmt.setString(2,tableID);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param custName
     * @param ticketID
     */
    public void setCustomerNameInTableByTicketId(String custName, String ticketID){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET CUSTOMER=? WHERE TICKETID=?")) {
            pstmt.setString(1,custName);
            pstmt.setString(2,ticketID);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
}

    /**
     *
     * @param tableName
     * @return
     */
    public String getCustomerNameInTable(String tableName){
        try (PreparedStatement pstmt = con.prepareStatement("SELECT customer FROM places WHERE NAME=?")) {
            pstmt.setString(1, tableName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    return rs.getString("CUSTOMER");
                }
            }
        }catch(SQLException e){
        }

        return "";
  }

    /**
     *
     * @param tableId
     * @return
     */
    public String getCustomerNameInTableById(String tableId){
        try (PreparedStatement pstmt = con.prepareStatement("SELECT customer FROM places WHERE ID=?")) {
            pstmt.setString(1, tableId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    return rs.getString("CUSTOMER");
                }
            }
        }catch(SQLException e){
        }

        return "";
  }

    /**
     *
     * @param tableName
     */
    public void clearCustomerNameInTable(String tableName){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET CUSTOMER=null WHERE NAME=?")) {
            pstmt.setString(1,tableName);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param tableID
     */
    public void clearCustomerNameInTableById(String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET CUSTOMER=null WHERE ID=?")) {
            pstmt.setString(1,tableID);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param waiterName
     * @param tableName
     */
    public void setWaiterNameInTable(String waiterName, String tableName){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET WAITER=? WHERE NAME=?")) {
            pstmt.setString(1,waiterName);
            pstmt.setString(2,tableName);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param waiterName
     * @param tableID
     */
    public void setWaiterNameInTableById(String waiterName, String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET WAITER=? WHERE ID=?")) {
            pstmt.setString(1,waiterName);
            pstmt.setString(2,tableID);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param tableName
     * @return
     */
    public String getWaiterNameInTable(String tableName){
        try (PreparedStatement pstmt = con.prepareStatement("SELECT waiter FROM places WHERE NAME=?")) {
            pstmt.setString(1, tableName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    return rs.getString("WAITER");
                }
            }
        }catch(SQLException e){
        }

       return "";
    }

    /**
     *
     * @param tableID
     * @return
     */
    public String getWaiterNameInTableById(String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("SELECT waiter FROM places WHERE ID=?")) {
            pstmt.setString(1, tableID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    return rs.getString("WAITER");
                }
            }
        }catch(SQLException e){
        }

        return "";
    }

    /**
     *
     * @param tableName
     */
    public void clearWaiterNameInTable(String tableName){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET WAITER=null WHERE NAME=?")) {
            pstmt.setString(1,tableName);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param tableID
     */
    public void clearWaiterNameInTableById(String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET WAITER=null WHERE ID=?")) {
            pstmt.setString(1,tableID);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param ID
     * @return
     */
    public String getTicketIdInTable(String ID){
        try (PreparedStatement pstmt = con.prepareStatement("SELECT TICKETID FROM places WHERE ID=?")) {
            pstmt.setString(1, ID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    return rs.getString("TICKETID");
                }
            }
        }catch(SQLException e){
        }

        return "";
    }

    /**
     *
     * @param TicketID
     * @param tableName
     */
    public void setTicketIdInTable(String TicketID, String tableName){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET TICKETID=? WHERE NAME=?")) {
            pstmt.setString(1,TicketID);
            pstmt.setString(2,tableName);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param tableName
     */
    public void clearTicketIdInTable(String tableName){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET TICKETID=null, OCCUPIED=null WHERE NAME=?")) {
            pstmt.setString(1,tableName);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }

        clearGuestsInTable(tableName);

    }

    /**
     *
     * @param tableID
     */
    public void clearTicketIdInTableById(String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET TICKETID=null WHERE ID=?")) {
            pstmt.setString(1,tableID);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param tableID
     * @return
     */
    public Integer getGuestsInTable(String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("SELECT guests FROM places WHERE ID=?")) {
            pstmt.setString(1, tableID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    return rs.getInt("GUESTS");
                }
            }
        }catch(SQLException e){
        }

        return 0;
    }

    /**
     *
     * @param guests
     * @param tableID
     */
    public void setGuestsInTable(Integer guests, String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET GUESTS=? WHERE ID=?")) {
            pstmt.setInt(1,guests);
            pstmt.setString(2,tableID);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param tableID
     * @return
     */
    public Integer updateGuestsInTable(String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("SELECT guests FROM places WHERE ID=?")) {
            pstmt.setString(1, tableID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    return rs.getInt("GUESTS");
                }
            }
        }catch(SQLException e){
        }

        return 0;
    }

    /**
     *
     * @param tableID
    */
    public void clearGuestsInTable(String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET guests=0 WHERE ID=?")) {
            pstmt.setString(1,tableID);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }
    /**
     *
     * @param table
    */
    public void clearGuestsTable(String table){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET guests=0 WHERE NAME=?")) {
            pstmt.setString(1,table);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param tableID
    */
    public void clearOccupied(String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET occupied=null WHERE ID=?")) {
            pstmt.setString(1,tableID);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }
    /**
     *
     * @param table
    */
    public void clearOccupiedTable(String table){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET occupied=null WHERE NAME=?")) {
            pstmt.setString(1,table);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }
    /**
     *
     * @param tableID
     * @return
     */
    public Timestamp getOccupied(String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("SELECT occupied FROM places WHERE ID=?")) {
            pstmt.setString(1, tableID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    return rs.getTimestamp("OCCUPIED");
                }
            }
        }catch(SQLException e){
        }

        return null;
    }

    /**
     *
     * @param ticketID
    */
    public void setOccupied(String ticketID){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET occupied=NOW() WHERE TICKETID=?")) {
            pstmt.setString(1,ticketID);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param ticketID
     * @return
    */
    public Integer countTicketIdInTable(String ticketID){
        try (PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(*) AS RECORDCOUNT FROM places WHERE TICKETID=?")) {
            pstmt.setString(1, ticketID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    return rs.getInt("RECORDCOUNT");
                }
            }
        }catch(SQLException e){
        }

        return 0;
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public String getTableDetails (String ticketID){
        try (PreparedStatement pstmt = con.prepareStatement("SELECT NAME FROM places WHERE TICKETID=?")) {
            pstmt.setString(1, ticketID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    return rs.getString("NAME");
                }
            }
        }catch(SQLException e){
        }

        return "";
    }

    /**
     *
     * @param tableID
     */
    public void setTableMovedFlag (String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET TABLEMOVED='true' WHERE ID=?")) {
            pstmt.setString(1,tableID);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public String getTableMovedName (String ticketID){
        try (PreparedStatement pstmt = con.prepareStatement("SELECT NAME FROM places WHERE TICKETID=? AND TABLEMOVED ='true'")) {
            pstmt.setString(1, ticketID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    return rs.getString("NAME");
                }
            }
        }catch(SQLException e){
        }

        return null;
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public Boolean getTableMovedFlag (String ticketID){
        try (PreparedStatement pstmt = con.prepareStatement("SELECT TABLEMOVED FROM places WHERE TICKETID=?")) {
            pstmt.setString(1, ticketID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    return rs.getBoolean("TABLEMOVED");
                }
            }
        }catch(SQLException e){
        }

        return (false);
    }

    /**
     *
     * @param tableID
     */
    public void clearTableMovedFlag (String tableID){
        try (PreparedStatement pstmt = con.prepareStatement("UPDATE places SET TABLEMOVED='false' WHERE NAME=?")) {
            pstmt.setString(1,tableID);
            pstmt.executeUpdate();
        }catch(SQLException e){
        }
    }
}
