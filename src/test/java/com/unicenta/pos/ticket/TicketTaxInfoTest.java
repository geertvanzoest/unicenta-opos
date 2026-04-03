package com.unicenta.pos.ticket;

import org.junit.Test;
import static org.junit.Assert.*;

public class TicketTaxInfoTest {

    private static final double RATE = 0.21;

    private TaxInfo tax21() {
        return new TaxInfo("t1", "BTW 21%", "cat1", "custcat1", null, RATE, false, 1);
    }

    private TaxInfo tax0() {
        return new TaxInfo("t0", "Vrijgesteld", "cat0", "custcat0", null, 0.0, false, 0);
    }

    // --- initial state ---

    @Test
    public void initialSubTotalIsZero() {
        assertEquals(0.0, new TicketTaxInfo(tax21()).getSubTotal(), 1e-9);
    }

    @Test
    public void initialTaxIsZero() {
        assertEquals(0.0, new TicketTaxInfo(tax21()).getTax(), 1e-9);
    }

    @Test
    public void initialTotalIsZero() {
        assertEquals(0.0, new TicketTaxInfo(tax21()).getTotal(), 1e-9);
    }

    // --- single add: 100 @ 21% ---

    @Test
    public void addSingleValueSubTotal() {
        TicketTaxInfo info = new TicketTaxInfo(tax21());
        info.add(100.0);
        assertEquals(100.0, info.getSubTotal(), 1e-9);
    }

    @Test
    public void addSingleValueTax() {
        TicketTaxInfo info = new TicketTaxInfo(tax21());
        info.add(100.0);
        assertEquals(21.0, info.getTax(), 1e-9);
    }

    @Test
    public void addSingleValueTotal() {
        TicketTaxInfo info = new TicketTaxInfo(tax21());
        info.add(100.0);
        assertEquals(121.0, info.getTotal(), 1e-9);
    }

    // --- multiple adds accumulate subtotal; tax recalculates on accumulated base ---

    @Test
    public void addMultipleAccumulatesSubTotal() {
        TicketTaxInfo info = new TicketTaxInfo(tax21());
        info.add(100.0);
        info.add(50.0);
        assertEquals(150.0, info.getSubTotal(), 1e-9);
    }

    @Test
    public void addMultipleTaxOnAccumulatedBase() {
        TicketTaxInfo info = new TicketTaxInfo(tax21());
        info.add(100.0);
        info.add(50.0);
        // taxtotal = subtotal * rate = 150 * 0.21
        assertEquals(150.0 * RATE, info.getTax(), 1e-9);
    }

    @Test
    public void addMultipleTotalCorrect() {
        TicketTaxInfo info = new TicketTaxInfo(tax21());
        info.add(100.0);
        info.add(50.0);
        assertEquals(150.0 + 150.0 * RATE, info.getTotal(), 1e-9);
    }

    // --- negative add ---

    @Test
    public void addNegativeReducesSubTotal() {
        TicketTaxInfo info = new TicketTaxInfo(tax21());
        info.add(100.0);
        info.add(-40.0);
        assertEquals(60.0, info.getSubTotal(), 1e-9);
    }

    @Test
    public void addNegativeTaxOnReducedBase() {
        TicketTaxInfo info = new TicketTaxInfo(tax21());
        info.add(100.0);
        info.add(-40.0);
        assertEquals(60.0 * RATE, info.getTax(), 1e-9);
    }

    // --- getTaxInfo ---

    @Test
    public void getTaxInfoReturnsOriginalObject() {
        TaxInfo tax = tax21();
        TicketTaxInfo info = new TicketTaxInfo(tax);
        assertSame(tax, info.getTaxInfo());
    }

    // --- print methods return non-null ---

    @Test
    public void printSubTotalNonNull() {
        TicketTaxInfo info = new TicketTaxInfo(tax21());
        info.add(100.0);
        assertNotNull(info.printSubTotal());
    }

    @Test
    public void printTaxNonNull() {
        TicketTaxInfo info = new TicketTaxInfo(tax21());
        info.add(100.0);
        assertNotNull(info.printTax());
    }

    @Test
    public void printTotalNonNull() {
        TicketTaxInfo info = new TicketTaxInfo(tax21());
        info.add(100.0);
        assertNotNull(info.printTotal());
    }

    // --- zero tax rate ---

    @Test
    public void zeroRateSubTotalEqualsTotal() {
        TicketTaxInfo info = new TicketTaxInfo(tax0());
        info.add(100.0);
        assertEquals(100.0, info.getSubTotal(), 1e-9);
        assertEquals(0.0, info.getTax(), 1e-9);
        assertEquals(100.0, info.getTotal(), 1e-9);
    }
}
