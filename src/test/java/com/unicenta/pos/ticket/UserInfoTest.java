package com.unicenta.pos.ticket;

import org.junit.Test;
import static org.junit.Assert.*;

public class UserInfoTest {

    // --- constructor sets fields ---

    @Test
    public void constructorSetsId() {
        UserInfo user = new UserInfo("u-001", "Jan Jansen");
        assertEquals("u-001", user.getId());
    }

    @Test
    public void constructorSetsName() {
        UserInfo user = new UserInfo("u-001", "Jan Jansen");
        assertEquals("Jan Jansen", user.getName());
    }

    // --- getId ---

    @Test
    public void getIdReturnsId() {
        UserInfo user = new UserInfo("u-042", "Piet de Vries");
        assertEquals("u-042", user.getId());
    }

    // --- getName ---

    @Test
    public void getNameReturnsName() {
        UserInfo user = new UserInfo("u-042", "Piet de Vries");
        assertEquals("Piet de Vries", user.getName());
    }

    // --- null values ---

    @Test
    public void nullIdIsAllowed() {
        UserInfo user = new UserInfo(null, "Naam");
        assertNull(user.getId());
    }

    @Test
    public void nullNameIsAllowed() {
        UserInfo user = new UserInfo("u-000", null);
        assertNull(user.getName());
    }

    @Test
    public void bothNullIsAllowed() {
        UserInfo user = new UserInfo(null, null);
        assertNull(user.getId());
        assertNull(user.getName());
    }
}
