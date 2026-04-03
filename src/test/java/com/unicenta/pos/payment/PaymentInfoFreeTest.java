package com.unicenta.pos.payment;

import org.junit.Test;
import static org.junit.Assert.*;

public class PaymentInfoFreeTest {

    @Test
    public void constructor_setsTotal() {
        PaymentInfoFree p = new PaymentInfoFree(12.50);
        assertEquals(12.50, p.getTotal(), 0.001);
    }

    @Test
    public void getPaid_returnsZero() {
        PaymentInfoFree p = new PaymentInfoFree(12.50);
        assertEquals(0.00, p.getPaid(), 0.001);
    }

    @Test
    public void getChange_returnsZero() {
        PaymentInfoFree p = new PaymentInfoFree(12.50);
        assertEquals(0.00, p.getChange(), 0.001);
    }

    @Test
    public void getName_returnsFree() {
        PaymentInfoFree p = new PaymentInfoFree(12.50);
        assertEquals("free", p.getName());
    }

    @Test
    public void getTransactionID_returnsNoID() {
        PaymentInfoFree p = new PaymentInfoFree(12.50);
        assertEquals("no ID", p.getTransactionID());
    }

    @Test
    public void getVoucher_returnsNull() {
        PaymentInfoFree p = new PaymentInfoFree(12.50);
        assertNull(p.getVoucher());
    }

    @Test
    public void getCardName_returnsNull() {
        PaymentInfoFree p = new PaymentInfoFree(12.50);
        assertNull(p.getCardName());
    }

    @Test
    public void copyPayment_returnsDifferentInstance() {
        PaymentInfoFree p = new PaymentInfoFree(12.50);
        PaymentInfo copy = p.copyPayment();
        assertNotSame(p, copy);
    }

    @Test
    public void copyPayment_returnsSameType() {
        PaymentInfoFree p = new PaymentInfoFree(12.50);
        PaymentInfo copy = p.copyPayment();
        assertTrue(copy instanceof PaymentInfoFree);
    }

    @Test
    public void copyPayment_preservesTotal() {
        PaymentInfoFree p = new PaymentInfoFree(12.50);
        PaymentInfo copy = p.copyPayment();
        assertEquals(p.getTotal(), copy.getTotal(), 0.001);
    }

    @Test
    public void printTotal_nonNull() {
        PaymentInfoFree p = new PaymentInfoFree(12.50);
        assertNotNull(p.printTotal());
    }

    @Test
    public void zeroTotal_totalIsZero() {
        PaymentInfoFree p = new PaymentInfoFree(0.00);
        assertEquals(0.00, p.getTotal(), 0.001);
    }

    @Test
    public void zeroTotal_changeIsZero() {
        PaymentInfoFree p = new PaymentInfoFree(0.00);
        assertEquals(0.00, p.getChange(), 0.001);
    }
}
