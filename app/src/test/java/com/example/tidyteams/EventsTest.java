package com.example.tidyteams;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventsTest {

    private Events events;

    @Before
    public void setUp() {
        events = new Events("Catchphrase", "2023-04-08", "Description", "Place", "PostImage", "12" +
                ":00", "Title", "User123", "Post123", 40.7128, -74.0060, "USA", "New York",
                "10001", "123", "5th Ave");
    }

    @Test
    public void gettersAndSetters() {
        assertEquals("Catchphrase", events.getCatchphrase());
        assertEquals("2023-04-08", events.getDate());
        assertEquals("Description", events.getDescription());
        assertEquals("PostImage", events.getPostimage());
        assertEquals("12:00", events.getTime());
        assertEquals("Title", events.getTitle());
        assertEquals("User123", events.getUid());
        assertEquals("Post123", events.getPostID());
        assertEquals(40.7128, events.getLatitude(), 0.001);
        assertEquals(-74.0060, events.getLongitude(), 0.001);
        assertEquals("USA", events.getCountry());
        assertEquals("New York", events.getRegion());
        assertEquals("10001", events.getPostcode());
        assertEquals("123", events.getNumber());
        assertEquals("5th Ave", events.getStreet());

        events.setCatchphrase("New Catchphrase");
        events.setDate("2023-04-09");
        events.setDescription("New Description");
        events.setPostimage("NewPostImage");
        events.setTime("13:00");
        events.setTitle("New Title");
        events.setUid("User456");
        events.setPostID("Post456");
        events.setCountry("Canada");
        events.setRegion("Ontario");
        events.setPostcode("M5V 2T6");
        events.setNumber("789");
        events.setStreet("Queen St W");

        assertEquals("New Catchphrase", events.getCatchphrase());
        assertEquals("2023-04-09", events.getDate());
        assertEquals("New Description", events.getDescription());
        assertEquals("NewPostImage", events.getPostimage());
        assertEquals("13:00", events.getTime());
        assertEquals("New Title", events.getTitle());
        assertEquals("User456", events.getUid());
        assertEquals("Post456", events.getPostID());
        assertEquals("Canada", events.getCountry());
        assertEquals("Ontario", events.getRegion());
        assertEquals("M5V 2T6", events.getPostcode());
        assertEquals("789", events.getNumber());
        assertEquals("Queen St W", events.getStreet());
    }
}
