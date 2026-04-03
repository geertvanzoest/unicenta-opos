package com.unicenta.pos.customers;

import org.junit.Test;
import static org.junit.Assert.*;

public class CustomerInfoTest {

    // --- constructor sets id, others null ---

    @Test
    public void constructorSetsId() {
        CustomerInfo c = new CustomerInfo("cust-001");
        assertEquals("cust-001", c.getId());
    }

    @Test
    public void constructorSetsNameToNull() {
        assertNull(new CustomerInfo("cust-001").getName());
    }

    @Test
    public void constructorSetsSearchkeyToNull() {
        assertNull(new CustomerInfo("cust-001").getSearchkey());
    }

    @Test
    public void constructorSetsTaxidToNull() {
        assertNull(new CustomerInfo("cust-001").getTaxid());
    }

    @Test
    public void constructorSetsPcodeToNull() {
        assertNull(new CustomerInfo("cust-001").getPcode());
    }

    @Test
    public void constructorSetsPhone1ToNull() {
        assertNull(new CustomerInfo("cust-001").getPhone1());
    }

    @Test
    public void constructorSetsCemailToNull() {
        assertNull(new CustomerInfo("cust-001").getCemail());
    }

    // --- setName / getName ---

    @Test
    public void setNameThenGetName() {
        CustomerInfo c = new CustomerInfo("cust-001");
        c.setName("Maria van Dam");
        assertEquals("Maria van Dam", c.getName());
    }

    // --- setSearchkey / getSearchkey ---

    @Test
    public void setSearchkeyThenGet() {
        CustomerInfo c = new CustomerInfo("cust-001");
        c.setSearchkey("VANDAM");
        assertEquals("VANDAM", c.getSearchkey());
    }

    // --- setTaxid / getTaxid ---

    @Test
    public void setTaxidThenGet() {
        CustomerInfo c = new CustomerInfo("cust-001");
        c.setTaxid("NL123456789B01");
        assertEquals("NL123456789B01", c.getTaxid());
    }

    // --- setPcode / getPcode ---

    @Test
    public void setPcodeThenGet() {
        CustomerInfo c = new CustomerInfo("cust-001");
        c.setPcode("4811 AA");
        assertEquals("4811 AA", c.getPcode());
    }

    // --- setPhone1 / getPhone1 ---

    @Test
    public void setPhone1ThenGet() {
        CustomerInfo c = new CustomerInfo("cust-001");
        c.setPhone1("076-1234567");
        assertEquals("076-1234567", c.getPhone1());
    }

    // --- setCemail / getCemail ---

    @Test
    public void setCemailThenGet() {
        CustomerInfo c = new CustomerInfo("cust-001");
        c.setCemail("maria@example.com");
        assertEquals("maria@example.com", c.getCemail());
    }

    // --- getCurDebt default null ---

    @Test
    public void curDebtDefaultIsNull() {
        assertNull(new CustomerInfo("cust-001").getCurDebt());
    }

    @Test
    public void setCurDebtThenGet() {
        CustomerInfo c = new CustomerInfo("cust-001");
        c.setCurDebt(25.50);
        assertEquals(25.50, c.getCurDebt(), 1e-9);
    }

    // --- printName encodes XML ---

    @Test
    public void printNameEncodesAmpersand() {
        CustomerInfo c = new CustomerInfo("cust-002");
        c.setName("Jansen & Zn");
        assertEquals("Jansen &amp; Zn", c.printName());
    }

    @Test
    public void printNameEncodesLessThan() {
        CustomerInfo c = new CustomerInfo("cust-002");
        c.setName("A < B");
        assertEquals("A &lt; B", c.printName());
    }

    @Test
    public void printNameEncodesGreaterThan() {
        CustomerInfo c = new CustomerInfo("cust-002");
        c.setName("B > A");
        assertEquals("B &gt; A", c.printName());
    }

    @Test
    public void printNameNoSpecialCharsUnchanged() {
        CustomerInfo c = new CustomerInfo("cust-002");
        c.setName("Gewone Naam");
        assertEquals("Gewone Naam", c.printName());
    }

    // --- printName with null name ---

    @Test
    public void printNameNullReturnsNull() {
        CustomerInfo c = new CustomerInfo("cust-001");
        assertNull(c.printName());
    }

    // --- toString returns name ---

    @Test
    public void toStringReturnsName() {
        CustomerInfo c = new CustomerInfo("cust-001");
        c.setName("Jan Bakker");
        assertEquals("Jan Bakker", c.toString());
    }

    @Test
    public void toStringReturnsNullWhenNameIsNull() {
        CustomerInfo c = new CustomerInfo("cust-001");
        assertNull(c.toString());
    }

    // --- image default null ---

    @Test
    public void imageDefaultIsNull() {
        assertNull(new CustomerInfo("cust-001").getImage());
    }
}
