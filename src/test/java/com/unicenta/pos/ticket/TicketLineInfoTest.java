package com.unicenta.pos.ticket;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Properties;

public class TicketLineInfoTest {

    private static final double RATE = 0.21;
    private static final double DELTA = 1e-9;

    private TaxInfo tax21() {
        return new TaxInfo("t1", "BTW 21%", "cat1", "custcat1", null, RATE, false, 1);
    }

    /** 4-arg constructor: productid, multiply, price, tax */
    private TicketLineInfo line(double multiply, double price) {
        return new TicketLineInfo("prod1", multiply, price, tax21());
    }

    // --- getPrice / getMultiply ---

    @Test
    public void getPrice() {
        assertEquals(10.0, line(2.0, 10.0).getPrice(), DELTA);
    }

    @Test
    public void getMultiply() {
        assertEquals(2.0, line(2.0, 10.0).getMultiply(), DELTA);
    }

    // --- getPriceTax = price * (1 + rate) ---

    @Test
    public void getPriceTax() {
        // 10 * 1.21 = 12.10
        assertEquals(10.0 * (1.0 + RATE), line(1.0, 10.0).getPriceTax(), DELTA);
    }

    // --- getTaxRate ---

    @Test
    public void getTaxRateWith21Percent() {
        assertEquals(RATE, line(1.0, 10.0).getTaxRate(), DELTA);
    }

    @Test
    public void getTaxRateIsZeroWhenTaxIsNull() {
        TicketLineInfo l = new TicketLineInfo("prod0", 1.0, 10.0, (TaxInfo) null);
        assertEquals(0.0, l.getTaxRate(), DELTA);
    }

    // --- getValue = price * multiply * (1 + rate) ---

    @Test
    public void getValue() {
        // 3 * 10 * 1.21 = 36.30
        assertEquals(3.0 * 10.0 * (1.0 + RATE), line(3.0, 10.0).getValue(), DELTA);
    }

    // --- getSubValue = price * multiply ---

    @Test
    public void getSubValue() {
        assertEquals(3.0 * 10.0, line(3.0, 10.0).getSubValue(), DELTA);
    }

    // --- getTax = price * multiply * rate ---

    @Test
    public void getTax() {
        // 3 * 10 * 0.21 = 6.30
        assertEquals(3.0 * 10.0 * RATE, line(3.0, 10.0).getTax(), DELTA);
    }

    // --- setPrice ---

    @Test
    public void setPriceUpdatesPrice() {
        TicketLineInfo l = line(1.0, 10.0);
        l.setPrice(20.0);
        assertEquals(20.0, l.getPrice(), DELTA);
    }

    @Test
    public void setPriceAffectsGetValue() {
        TicketLineInfo l = line(2.0, 10.0);
        l.setPrice(15.0);
        assertEquals(2.0 * 15.0 * (1.0 + RATE), l.getValue(), DELTA);
    }

    // --- setPriceTax: sets net price from gross ---

    @Test
    public void setPriceTaxCalculatesNetPrice() {
        TicketLineInfo l = line(1.0, 0.0);
        // gross = 12.10  →  net = 12.10 / 1.21 = 10.0
        l.setPriceTax(12.1);
        assertEquals(12.1 / (1.0 + RATE), l.getPrice(), 1e-6);
    }

    @Test
    public void setPriceTaxRoundTrip() {
        TicketLineInfo l = line(1.0, 10.0);
        double gross = l.getPriceTax();   // 12.10
        l.setPriceTax(gross);
        assertEquals(10.0, l.getPrice(), 1e-6);
    }

    // --- setMultiply ---

    @Test
    public void setMultiplyUpdatesMultiply() {
        TicketLineInfo l = line(1.0, 10.0);
        l.setMultiply(5.0);
        assertEquals(5.0, l.getMultiply(), DELTA);
    }

    @Test
    public void setMultiplyAffectsGetSubValue() {
        TicketLineInfo l = line(1.0, 10.0);
        l.setMultiply(4.0);
        assertEquals(40.0, l.getSubValue(), DELTA);
    }

    // --- properties (setProperty / getProperty) ---

    @Test
    public void setAndGetProperty() {
        TicketLineInfo l = line(1.0, 10.0);
        l.setProperty("custom.key", "hello");
        assertEquals("hello", l.getProperty("custom.key"));
    }

    @Test
    public void getPropertyWithDefaultReturnsDefaultWhenAbsent() {
        TicketLineInfo l = line(1.0, 10.0);
        assertEquals("fallback", l.getProperty("no.such.key", "fallback"));
    }

    @Test
    public void getPropertiesReturnsNonNull() {
        assertNotNull(line(1.0, 10.0).getProperties());
    }

    // --- copyTicketLine produces independent copy ---

    @Test
    public void copyTicketLineHasSamePrice() {
        TicketLineInfo original = line(2.0, 10.0);
        TicketLineInfo copy = original.copyTicketLine();
        assertEquals(original.getPrice(), copy.getPrice(), DELTA);
    }

    @Test
    public void copyTicketLineHasSameMultiply() {
        TicketLineInfo original = line(2.0, 10.0);
        TicketLineInfo copy = original.copyTicketLine();
        assertEquals(original.getMultiply(), copy.getMultiply(), DELTA);
    }

    @Test
    public void copyTicketLineIsIndependent() {
        TicketLineInfo original = line(2.0, 10.0);
        TicketLineInfo copy = original.copyTicketLine();
        copy.setPrice(99.0);
        // original must be unchanged
        assertEquals(10.0, original.getPrice(), DELTA);
    }

    @Test
    public void copyTicketLinePropertyIsIndependent() {
        TicketLineInfo original = line(1.0, 10.0);
        original.setProperty("k", "v1");
        TicketLineInfo copy = original.copyTicketLine();
        copy.setProperty("k", "v2");
        assertEquals("v1", original.getProperty("k"));
    }

    // --- 7-arg constructor sets product.name ---

    @Test
    public void sevenArgConstructorSetsProductName() {
        TicketLineInfo l = new TicketLineInfo(
                "prod2", "Cola 33cl", "cat1", "printer1",
                1.0, 2.50, tax21());
        assertEquals("Cola 33cl", l.getProductName());
    }

    @Test
    public void sevenArgConstructorSetsProductId() {
        TicketLineInfo l = new TicketLineInfo(
                "prod2", "Cola 33cl", "cat1", "printer1",
                1.0, 2.50, tax21());
        assertEquals("prod2", l.getProductID());
    }

    @Test
    public void sevenArgConstructorPrice() {
        TicketLineInfo l = new TicketLineInfo(
                "prod2", "Cola 33cl", "cat1", "printer1",
                3.0, 2.50, tax21());
        assertEquals(2.50, l.getPrice(), DELTA);
        assertEquals(3.0, l.getMultiply(), DELTA);
    }
}
