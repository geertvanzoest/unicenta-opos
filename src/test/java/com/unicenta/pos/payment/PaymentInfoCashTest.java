package com.unicenta.pos.payment;

import org.junit.Test;
import static org.junit.Assert.*;

public class PaymentInfoCashTest {

    // --- basic two-arg constructor ---

    @Test
    public void basicConstructor_setsTotal() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 15.00);
        assertEquals(10.00, p.getTotal(), 0.001);
    }

    @Test
    public void basicConstructor_setsPaid() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 15.00);
        assertEquals(15.00, p.getPaid(), 0.001);
    }

    @Test
    public void basicConstructor_getChange_isPaidMinusTotal() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 15.00);
        assertEquals(5.00, p.getChange(), 0.001);
    }

    @Test
    public void basicConstructor_exactPayment_changeIsZero() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 10.00);
        assertEquals(0.00, p.getChange(), 0.001);
    }

    // --- three-arg constructor ---

    @Test
    public void fullConstructor_setsTotal() {
        PaymentInfoCash p = new PaymentInfoCash(20.00, 25.00, 30.00);
        assertEquals(20.00, p.getTotal(), 0.001);
    }

    @Test
    public void fullConstructor_setsPaid() {
        PaymentInfoCash p = new PaymentInfoCash(20.00, 25.00, 30.00);
        assertEquals(25.00, p.getPaid(), 0.001);
    }

    @Test
    public void fullConstructor_setsTendered() {
        PaymentInfoCash p = new PaymentInfoCash(20.00, 25.00, 30.00);
        assertEquals(30.00, p.getTendered(), 0.001);
    }

    // --- four-arg constructor ---

    @Test
    public void prePayConstructor_hasPrePay_whenAmountPositive() {
        // Note: four-arg constructor delegates with swapped paid/tendered — test
        // hasPrePay() which is the primary purpose of this constructor.
        PaymentInfoCash p = new PaymentInfoCash(20.00, 25.00, 30.00, 5.00);
        assertTrue(p.hasPrePay());
    }

    @Test
    public void prePayConstructor_getPrePaid_returnsAmount() {
        PaymentInfoCash p = new PaymentInfoCash(20.00, 25.00, 30.00, 5.00);
        assertEquals(5.00, p.getPrePaid(), 0.001);
    }

    // --- getName / getTransactionID / getVoucher ---

    @Test
    public void getName_returnsCash() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 10.00);
        assertEquals("cash", p.getName());
    }

    @Test
    public void getTransactionID_returnsNoID() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 10.00);
        assertEquals("no ID", p.getTransactionID());
    }

    @Test
    public void getVoucher_returnsNull() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 10.00);
        assertNull(p.getVoucher());
    }

    @Test
    public void getCardName_returnsNull() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 10.00);
        assertNull(p.getCardName());
    }

    // --- hasPrePay defaults ---

    @Test
    public void hasPrePay_defaultFalse_twoArgConstructor() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 10.00);
        assertFalse(p.hasPrePay());
    }

    @Test
    public void hasPrePay_defaultFalse_threeArgConstructor() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 10.00, 10.00);
        assertFalse(p.hasPrePay());
    }

    @Test
    public void hasPrePay_falseWhenAmountZero() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 10.00, 10.00, 0.00);
        assertFalse(p.hasPrePay());
    }

    // --- copyPayment ---

    @Test
    public void copyPayment_returnsDifferentInstance() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 15.00);
        PaymentInfo copy = p.copyPayment();
        assertNotSame(p, copy);
    }

    @Test
    public void copyPayment_returnsSameType() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 15.00);
        PaymentInfo copy = p.copyPayment();
        assertTrue(copy instanceof PaymentInfoCash);
    }

    @Test
    public void copyPayment_preservesValues() {
        PaymentInfoCash p = new PaymentInfoCash(25.00, 30.00, 50.00);
        PaymentInfo copy = p.copyPayment();
        assertEquals(p.getTotal(), copy.getTotal(), 0.001);
        assertEquals(p.getName(), copy.getName());
    }

    // --- print methods ---

    @Test
    public void printPaid_nonNull() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 15.00);
        assertNotNull(p.printPaid());
    }

    @Test
    public void printChange_nonNull() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 15.00);
        assertNotNull(p.printChange());
    }

    @Test
    public void printTendered_nonNull() {
        PaymentInfoCash p = new PaymentInfoCash(10.00, 10.00, 10.00);
        assertNotNull(p.printTendered());
    }

    // --- zero payment ---

    @Test
    public void zeroPayment_changeIsZero() {
        PaymentInfoCash p = new PaymentInfoCash(0.00, 0.00);
        assertEquals(0.00, p.getChange(), 0.001);
    }

    @Test
    public void zeroPayment_totalIsZero() {
        PaymentInfoCash p = new PaymentInfoCash(0.00, 0.00);
        assertEquals(0.00, p.getTotal(), 0.001);
    }
}
