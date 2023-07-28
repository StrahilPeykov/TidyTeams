package com.example.tidyteams;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AttendeeListItemTest {

    private AttendeeListItem attendeeListItem;

    @Before
    public void setUp() {
        attendeeListItem = new AttendeeListItem("testUser");
    }

    @Test
    public void getUsername_returnsCorrectUsername() {
        String expectedUsername = "testUser";
        assertEquals(expectedUsername, attendeeListItem.getUsername());
    }
}
