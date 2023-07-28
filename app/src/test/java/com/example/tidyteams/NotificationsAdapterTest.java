package com.example.tidyteams;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationsAdapterTest {

    @Mock
    private Context context;

    @Mock
    private NotificationsAdapter.MyViewHolder myViewHolder;

    @Mock
    private NotificationsActivity notificationsActivity;

    @Mock
    private View view;

    @Mock
    private TextView eventTitle, eventDate, eventTime, eventLocation;

    @Mock
    private ImageView eventImageView;

    private NotificationsAdapter notificationsAdapter;
    private ArrayList<Events> eventList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        eventList = new ArrayList<>();
        eventList.add(new Events("Title1", "Date1", "Time1", "Image1", "Country1", "Region1",
                "Postcode1", "Street1", "Number1"));
        eventList.add(new Events("Title2", "Date2", "Time2", "Image2", "Country2", "Region2",
                "Postcode2", "Street2", "Number2"));

        notificationsAdapter = new NotificationsAdapter(context, eventList, notificationsActivity);
    }

    @Test
    public void getItemCount_returnsCorrectSize() {
        assertEquals(2, notificationsAdapter.getItemCount());
    }

    @Test
    public void onBindViewHolder_bindsDataCorrectly() {
        when(myViewHolder.eventTitle).thenReturn(eventTitle);
        when(myViewHolder.eventDate).thenReturn(eventDate);
        when(myViewHolder.eventTime).thenReturn(eventTime);
        when(myViewHolder.eventLocation).thenReturn(eventLocation);
        when(myViewHolder.eventImageView).thenReturn(eventImageView);

        notificationsAdapter.onBindViewHolder(myViewHolder, 0);

        verify(eventTitle).setText(eventList.get(0).getTitle());
        verify(eventDate).setText(eventList.get(0).getDate());
        verify(eventTime).setText(eventList.get(0).getTime());
        String location = eventList.get(0).getCountry() + ", " + eventList.get(0).getRegion() +
                ", " + eventList.get(0).getPostcode() + ", " + eventList.get(0).getStreet() + ", "
                + eventList.get(0).getNumber();
        verify(eventLocation).setText(location);
    }
}
