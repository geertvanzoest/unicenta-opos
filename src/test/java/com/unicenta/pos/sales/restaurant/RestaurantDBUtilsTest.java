package com.unicenta.pos.sales.restaurant;

import com.unicenta.data.loader.Session;
import com.unicenta.pos.forms.AppView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Verifies that RestaurantDBUtils uses parameterized queries (PreparedStatement)
 * instead of string concatenation, preventing SQL injection.
 */
public class RestaurantDBUtilsTest {

    private Connection mockCon;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;
    private RestaurantDBUtils dbUtils;

    @Before
    public void setUp() throws SQLException {
        mockCon = mock(Connection.class);
        mockPstmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);

        when(mockCon.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockRs);
        when(mockPstmt.executeUpdate()).thenReturn(1);

        Session mockSession = mock(Session.class);
        when(mockSession.getConnection()).thenReturn(mockCon);

        AppView mockApp = mock(AppView.class);
        when(mockApp.getSession()).thenReturn(mockSession);

        dbUtils = new RestaurantDBUtils(mockApp);
    }

    @Test
    public void getCustomerNameInTable_usesParameterizedQuery() throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("CUSTOMER")).thenReturn("Jan");

        String result = dbUtils.getCustomerNameInTable("Table 1");

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockCon).prepareStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("?"));
        assertFalse(sqlCaptor.getValue().contains("Table 1"));
        verify(mockPstmt).setString(1, "Table 1");
        assertEquals("Jan", result);
    }

    @Test
    public void getCustomerNameInTableById_usesParameterizedQuery() throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("CUSTOMER")).thenReturn("Piet");

        String result = dbUtils.getCustomerNameInTableById("42");

        verify(mockPstmt).setString(1, "42");
        assertEquals("Piet", result);
    }

    @Test
    public void getWaiterNameInTable_usesParameterizedQuery() throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("WAITER")).thenReturn("Kees");

        String result = dbUtils.getWaiterNameInTable("Table 5");

        verify(mockPstmt).setString(1, "Table 5");
        assertEquals("Kees", result);
    }

    @Test
    public void getWaiterNameInTableById_usesParameterizedQuery() throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("WAITER")).thenReturn("Marie");

        String result = dbUtils.getWaiterNameInTableById("7");

        verify(mockPstmt).setString(1, "7");
        assertEquals("Marie", result);
    }

    @Test
    public void getTicketIdInTable_usesParameterizedQuery() throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("TICKETID")).thenReturn("T-100");

        String result = dbUtils.getTicketIdInTable("3");

        verify(mockPstmt).setString(1, "3");
        assertEquals("T-100", result);
    }

    @Test
    public void getGuestsInTable_usesParameterizedQuery() throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("GUESTS")).thenReturn(4);

        Integer result = dbUtils.getGuestsInTable("5");

        verify(mockPstmt).setString(1, "5");
        assertEquals(Integer.valueOf(4), result);
    }

    @Test
    public void getOccupied_usesParameterizedQuery() throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getTimestamp("OCCUPIED")).thenReturn(null);

        dbUtils.getOccupied("10");

        verify(mockPstmt).setString(1, "10");
    }

    @Test
    public void countTicketIdInTable_usesParameterizedQuery() throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("RECORDCOUNT")).thenReturn(2);

        Integer result = dbUtils.countTicketIdInTable("T-200");

        verify(mockPstmt).setString(1, "T-200");
        assertEquals(Integer.valueOf(2), result);
    }

    @Test
    public void getTableDetails_usesParameterizedQuery() throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("NAME")).thenReturn("Table 3");

        String result = dbUtils.getTableDetails("T-300");

        verify(mockPstmt).setString(1, "T-300");
        assertEquals("Table 3", result);
    }

    @Test
    public void getTableMovedName_usesParameterizedQuery() throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("NAME")).thenReturn("Table 9");

        String result = dbUtils.getTableMovedName("T-400");

        verify(mockPstmt).setString(1, "T-400");
        assertEquals("Table 9", result);
    }

    @Test
    public void getTableMovedFlag_usesParameterizedQuery() throws SQLException {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getBoolean("TABLEMOVED")).thenReturn(true);

        Boolean result = dbUtils.getTableMovedFlag("T-500");

        verify(mockPstmt).setString(1, "T-500");
        assertTrue(result);
    }

    @Test
    public void setCustomerNameInTable_usesParameterizedQuery() throws SQLException {
        dbUtils.setCustomerNameInTable("Jan", "Table 1");

        verify(mockPstmt).setString(1, "Jan");
        verify(mockPstmt).setString(2, "Table 1");
        verify(mockPstmt).executeUpdate();
    }

    @Test
    public void sqlInjectionAttempt_isSafelyParameterized() throws SQLException {
        when(mockRs.next()).thenReturn(false);

        String malicious = "'; DROP TABLE places; --";
        dbUtils.getCustomerNameInTable(malicious);

        // Value passed as parameter, not concatenated into SQL
        verify(mockPstmt).setString(1, malicious);
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockCon).prepareStatement(sqlCaptor.capture());
        assertFalse(sqlCaptor.getValue().contains("DROP"));
    }

    @Test
    public void getCustomerNameInTable_returnsEmptyOnNoResult() throws SQLException {
        when(mockRs.next()).thenReturn(false);

        String result = dbUtils.getCustomerNameInTable("Nonexistent");

        assertEquals("", result);
    }

    @Test
    public void countTicketIdInTable_returnsZeroOnNoResult() throws SQLException {
        when(mockRs.next()).thenReturn(false);

        Integer result = dbUtils.countTicketIdInTable("T-999");

        assertEquals(Integer.valueOf(0), result);
    }
}
