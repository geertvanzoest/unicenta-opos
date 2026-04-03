package com.unicenta.pos.ticket;

import org.junit.Test;
import java.awt.image.BufferedImage;
import static org.junit.Assert.*;

public class CategoryInfoTest {

    private CategoryInfo createDefault() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        return new CategoryInfo("cat-1", "Dranken", img, "Keuze dranken", Boolean.TRUE);
    }

    // --- constructor ---

    @Test
    public void constructorSetsId() {
        assertEquals("cat-1", createDefault().getID());
    }

    @Test
    public void constructorSetsName() {
        assertEquals("Dranken", createDefault().getName());
    }

    @Test
    public void constructorSetsTextTip() {
        assertEquals("Keuze dranken", createDefault().getTextTip());
    }

    @Test
    public void constructorSetsCatShowName() {
        assertTrue(createDefault().getCatShowName());
    }

    @Test
    public void constructorSetsImage() {
        assertNotNull(createDefault().getImage());
    }

    // --- getKey returns ID ---

    @Test
    public void getKeyReturnsId() {
        CategoryInfo cat = createDefault();
        assertEquals("cat-1", cat.getKey());
    }

    // --- setters ---

    @Test
    public void setIdUpdatesId() {
        CategoryInfo cat = createDefault();
        cat.setID("cat-99");
        assertEquals("cat-99", cat.getID());
    }

    @Test
    public void setNameUpdatesName() {
        CategoryInfo cat = createDefault();
        cat.setName("Snacks");
        assertEquals("Snacks", cat.getName());
    }

    @Test
    public void setTextTipUpdatesTextTip() {
        CategoryInfo cat = createDefault();
        cat.setTextTip("Keuze snacks");
        assertEquals("Keuze snacks", cat.getTextTip());
    }

    @Test
    public void setCatShowNameUpdates() {
        CategoryInfo cat = createDefault();
        cat.setCatShowName(Boolean.FALSE);
        assertFalse(cat.getCatShowName());
    }

    // --- toString returns name ---

    @Test
    public void toStringReturnsName() {
        assertEquals("Dranken", createDefault().toString());
    }

    // --- null texttip and catshowname ---

    @Test
    public void nullTextTipIsAllowed() {
        CategoryInfo cat = new CategoryInfo("id1", "Test", null, null, null);
        assertNull(cat.getTextTip());
    }

    @Test
    public void nullCatShowNameIsAllowed() {
        CategoryInfo cat = new CategoryInfo("id1", "Test", null, null, null);
        assertNull(cat.getCatShowName());
    }

    @Test
    public void nullImageIsAllowed() {
        CategoryInfo cat = new CategoryInfo("id1", "Test", null, "tip", Boolean.FALSE);
        assertNull(cat.getImage());
    }
}
