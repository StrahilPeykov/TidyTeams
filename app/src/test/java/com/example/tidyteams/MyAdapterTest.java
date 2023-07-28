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

public class MyAdapterTest {

    @Mock
    private Context context;

    @Mock
    private RecyclerViewEventInterface recyclerViewEventInterface;

    @Mock
    private MyAdapter.MyViewHolder myViewHolder;

    @Mock
    private View view;

    @Mock
    private TextView eventTitle;

    @Mock
    private TextView eventDate;

    @Mock
    private TextView eventTime;

    @Mock
    private TextView eventLocation;

    @Mock
    private ImageView eventImageView;

    private MyAdapter myAdapter;
    private ArrayList<Events> eventsList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        eventsList = new ArrayList<>();
        eventsList.add(new Events("id1", "2023-04-08", "18:00", "Title 1", "Desc 1", "Catchphrase" +
                " 1", "ImageURL1", "2023-04-08", "User1", 40.7128, -74.0060, "USA", "New York",
                "Wall St", "15", "10005"));
        eventsList.add(new Events("id2", "2023-04-09", "19:00", "Title 2", "Desc 2", "Catchphrase" +
                " 2", "ImageURL2", "2023-04-09", "User2", 51.5074, -0.1278, "UK", "London",
                "Baker St", "221B", "NW1 6XE"));

        myAdapter = new MyAdapter(context, eventsList, recyclerViewEventInterface);
    }

    @Test
    public void getItemCount_returnsCorrectSize() {
        assertEquals(2, myAdapter.getItemCount());
    }

    @Test
    public void onBindViewHolder_bindsDataCorrectly() {
        when(myViewHolder.eventTitle).thenReturn(eventTitle);
        when(myViewHolder.eventDate).thenReturn(eventDate);
        when(myViewHolder.eventTime).thenReturn(eventTime);
        when(myViewHolder.eventLocation).thenReturn(eventLocation);
        when(myViewHolder.eventImageView).thenReturn(eventImageView);

        myAdapter.onBindViewHolder(myViewHolder, 0);

        verify(eventTitle).setText(eventsList.get(0).getTitle());
        verify(eventDate).setText(eventsList.get(0).getDate());
        verify(eventTime).setText(eventsList.get(0).getTime());
        String location =
                eventsList.get(0).getCountry() + ", " + eventsList.get(0).getRegion() + ", " + eventsList.get(0).getPostcode() + ", " + eventsList.get(0).getStreet() + ", " + eventsList.get(0).getNumber();
        verify(eventLocation).setText(location);
    }
}
