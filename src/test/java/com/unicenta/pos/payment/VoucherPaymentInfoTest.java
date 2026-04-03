package com.unicenta.pos.payment;

import org.junit.Test;
import static org.junit.Assert.*;

public class VoucherPaymentInfoTest {

    // --- three-arg constructor ---

    @Test
    public void constructor_setsTotal() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        assertEquals(15.00, p.getTotal(), 0.001);
    }

    @Test
    public void constructor_setsName() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        assertEquals("voucher", p.getName());
    }

    @Test
    public void constructor_setsVoucher() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        assertEquals("V-001", p.getVoucher());
    }

    // --- default constructor ---

    @Test
    public void defaultConstructor_totalIsZero() {
        VoucherPaymentInfo p = new VoucherPaymentInfo();
        assertEquals(0.00, p.getTotal(), 0.001);
    }

    @Test
    public void defaultConstructor_nameIsNull() {
        VoucherPaymentInfo p = new VoucherPaymentInfo();
        assertNull(p.getName());
    }

    @Test
    public void defaultConstructor_voucherIsNull() {
        VoucherPaymentInfo p = new VoucherPaymentInfo();
        assertNull(p.getVoucher());
    }

    // --- getPaid / getChange ---

    @Test
    public void getPaid_equalsTotal() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        assertEquals(p.getTotal(), p.getPaid(), 0.001);
    }

    @Test
    public void getChange_returnsZero() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        assertEquals(0.00, p.getChange(), 0.001);
    }

    // --- getTransactionID ---

    @Test
    public void getTransactionID_returnsNull() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        assertNull(p.getTransactionID());
    }

    // --- copyPayment ---

    @Test
    public void copyPayment_returnsDifferentInstance() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        PaymentInfo copy = p.copyPayment();
        assertNotSame(p, copy);
    }

    @Test
    public void copyPayment_returnsSameType() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        PaymentInfo copy = p.copyPayment();
        assertTrue(copy instanceof VoucherPaymentInfo);
    }

    @Test
    public void copyPayment_preservesTotal() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        PaymentInfo copy = p.copyPayment();
        assertEquals(p.getTotal(), copy.getTotal(), 0.001);
    }

    // --- print methods ---

    @Test
    public void printPaid_nonNull() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        assertNotNull(p.printPaid());
    }

    @Test
    public void printVoucherTotal_nonNull() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        assertNotNull(p.printVoucherTotal());
    }

    // --- getCardType ---

    @Test
    public void getCardType_returnsNull() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        assertNull(p.getCardType());
    }

    // --- unsupported operations ---

    @Test(expected = UnsupportedOperationException.class)
    public void getTendered_throwsUnsupportedOperationException() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        p.getTendered();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getCardName_throwsUnsupportedOperationException() {
        VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
        p.getCardName();
    }
}
