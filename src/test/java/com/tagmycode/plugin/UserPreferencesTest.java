package com.tagmycode.plugin;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class UserPreferencesTest {

    @Test
    public void booleanValue() {
        UserPreferences userPreferences = new UserPreferences(new File(""));
        assertTrue(userPreferences.getBoolean("not_existing_key", true));
        assertFalse(userPreferences.getBoolean("not_existing_key", false));

        userPreferences.setBoolean("key", true);
        assertTrue(userPreferences.getBoolean("key", false));
    }

    @Test
    public void intValue() {
        UserPreferences userPreferences = new UserPreferences(new File(""));
        assertEquals(Integer.valueOf(5), userPreferences.getInteger("not_existing_key", 5));
        assertNull(userPreferences.getInteger("not_existing_key", null));

        userPreferences.setInteger("key", 5);
        assertEquals(Integer.valueOf(5), userPreferences.getInteger("key", 1));
    }

}