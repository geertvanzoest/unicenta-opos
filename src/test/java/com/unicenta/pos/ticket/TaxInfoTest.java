package com.unicenta.pos.ticket;

import org.junit.Test;
import static org.junit.Assert.*;

public class TaxInfoTest {

    private TaxInfo createDefault() {
        return new TaxInfo("id1", "BTW 21%", "cat1", "custcat1", "parent1", 0.21, false, 1);
    }

    // --- constructor / getters ---

    @Test
    public void constructorSetsId() {
        assertEquals("id1", createDefault().getId());
    }

    @Test
    public void constructorSetsName() {
        assertEquals("BTW 21%", createDefault().getName());
    }

    @Test
    public void constructorSetsTaxCategoryId() {
        assertEquals("cat1", createDefault().getTaxCategoryID());
    }

    @Test
    public void constructorSetsTaxCustCategoryId() {
        assertEquals("custcat1", createDefault().getTaxCustCategoryID());
    }

    @Test
    public void constructorSetsParentId() {
        assertEquals("parent1", createDefault().getParentID());
    }

    @Test
    public void constructorSetsRate() {
        assertEquals(0.21, createDefault().getRate(), 1e-9);
    }

    @Test
    public void constructorSetsCascade() {
        assertFalse(createDefault().isCascade());
    }

    @Test
    public void constructorSetsOrder() {
        assertEquals(Integer.valueOf(1), createDefault().getOrder());
    }

    // --- getKey ---

    @Test
    public void getKeyReturnsId() {
        TaxInfo tax = createDefault();
        assertEquals("id1", tax.getKey());
    }

    // --- setters ---

    @Test
    public void setIdUpdatesId() {
        TaxInfo tax = createDefault();
        tax.setID("newId");
        assertEquals("newId", tax.getId());
    }

    @Test
    public void setNameUpdatesName() {
        TaxInfo tax = createDefault();
        tax.setName("BTW 9%");
        assertEquals("BTW 9%", tax.getName());
    }

    @Test
    public void setTaxCategoryIdUpdates() {
        TaxInfo tax = createDefault();
        tax.setTaxCategoryID("cat2");
        assertEquals("cat2", tax.getTaxCategoryID());
    }

    @Test
    public void setTaxCustCategoryIdUpdates() {
        TaxInfo tax = createDefault();
        tax.setTaxCustCategoryID("custcat2");
        assertEquals("custcat2", tax.getTaxCustCategoryID());
    }

    @Test
    public void setParentIdUpdates() {
        TaxInfo tax = createDefault();
        tax.setParentID("parent2");
        assertEquals("parent2", tax.getParentID());
    }

    @Test
    public void setRateUpdates() {
        TaxInfo tax = createDefault();
        tax.setRate(0.09);
        assertEquals(0.09, tax.getRate(), 1e-9);
    }

    @Test
    public void setCascadeUpdates() {
        TaxInfo tax = createDefault();
        tax.setCascade(true);
        assertTrue(tax.isCascade());
    }

    @Test
    public void setOrderUpdates() {
        TaxInfo tax = createDefault();
        tax.setOrder(5);
        assertEquals(Integer.valueOf(5), tax.getOrder());
    }

    // --- toString ---

    @Test
    public void toStringReturnsName() {
        assertEquals("BTW 21%", createDefault().toString());
    }

    // --- getApplicationOrder ---

    @Test
    public void getApplicationOrderReturnsOrderWhenSet() {
        assertEquals(1, createDefault().getApplicationOrder().intValue());
    }

    @Test
    public void getApplicationOrderReturnsMaxValueWhenNull() {
        TaxInfo tax = new TaxInfo("id1", "BTW 21%", "cat1", "custcat1", "parent1", 0.21, false, null);
        assertEquals(Integer.MAX_VALUE, tax.getApplicationOrder().intValue());
    }

    // --- zero rate ---

    @Test
    public void zeroRateConstructor() {
        TaxInfo tax = new TaxInfo("id0", "Vrijgesteld", "cat0", "custcat0", "parent0", 0.0, false, 0);
        assertEquals(0.0, tax.getRate(), 1e-9);
    }
}
