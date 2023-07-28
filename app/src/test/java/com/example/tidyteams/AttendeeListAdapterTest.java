package com.example.tidyteams;

import android.content.Context;
import android.view.View;
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

public class AttendeeListAdapterTest {

    @Mock
    private Context context;

    @Mock
    private AttendeeListAdapter.MyViewHolder myViewHolder;

    @Mock
    private View view;

    @Mock
    private TextView username;

    private AttendeeListAdapter attendeeListAdapter;
    private ArrayList<AttendeeListItem> attendeeList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        attendeeList = new ArrayList<>();
        attendeeList.add(new AttendeeListItem("User1"));
        attendeeList.add(new AttendeeListItem("User2"));

        attendeeListAdapter = new AttendeeListAdapter(context, attendeeList);
    }

    @Test
    public void getItemCount_returnsCorrectSize() {
        assertEquals(2, attendeeListAdapter.getItemCount());
    }

    @Test
    public void onBindViewHolder_bindsDataCorrectly() {
        when(myViewHolder.username).thenReturn(username);

        attendeeListAdapter.onBindViewHolder(myViewHolder, 0);

        verify(username).setText(attendeeList.get(0).getUsername());
    }
}
