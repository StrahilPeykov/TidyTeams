package com.example.tidyteams;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.TimePickerDialog;
import android.widget.TimePicker;

import com.squareup.picasso.Picasso;

import com.google.protobuf.StringValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.StringValue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import android.widget.EditText;
import android.app.DatePickerDialog;
import android.widget.DatePicker;


public class EventDetailsFragment extends Fragment {
    private static final String ARG_IS_CREATE_MODE = "is_create_mode";
    private static final String ARG_IS_EDIT_MODE = "is_edit_mode";

    private static final String ARG_EVENT_ID = "event_id";
    private String eventId;
    private boolean isCreateMode;


    private static final int PICK_IMAGE_REQUEST = 1;
    EditText eventDateEditText;
    EditText eventTimeEditText;

    EditText eventCountryEditText, eventRegionEditText, eventStreetEditText, eventNumberEditText;
    EditText eventTitleEditText, eventCatchPhraseEditText, eventPostCodeEditText;
    EditText eventDescriptionEditText, eventAtendeeLimitEditText;
    private ImageView eventImageView, cancelButton;
    private ProgressDialog loadingBar;
    private Button saveChangesButton;

    private boolean isEditMode;

    private Uri imageUri;

    private StorageReference PostImagesReference;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id;

    public EventDetailsFragment() {
    }

    public static EventDetailsFragment newInstance(boolean isCreateMode, String eventId,
                                                   boolean isEditMode) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_CREATE_MODE, isCreateMode);
        args.putString(ARG_EVENT_ID, eventId);
        args.putBoolean(ARG_IS_EDIT_MODE, isEditMode);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isCreateMode = getArguments().getBoolean(ARG_IS_CREATE_MODE);
            eventId = getArguments().getString(ARG_EVENT_ID);
            isEditMode = getArguments().getBoolean(ARG_IS_EDIT_MODE);
        }
    }


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        //Firebase Variables
        mAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        PostImagesReference = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance("https://tidyteams-dbl15-default-rtdb" +
                ".europe-west1.firebasedatabase.app").getReference().child("Posts");

        if (getArguments() != null) {
            isCreateMode = getArguments().getBoolean(ARG_IS_CREATE_MODE);
            if (!isCreateMode) {
                eventId = getArguments().getString(ARG_EVENT_ID);
                loadEventDetails(eventId);
            }
        }


        eventDateEditText = view.findViewById(R.id.event_date_edittext);
        //eventPlaceEditText = view.findViewById(R.id.event_place_edittext);

        eventCountryEditText = view.findViewById(R.id.event_country_edittext);
        eventRegionEditText = view.findViewById(R.id.event_region_edittext);
        eventStreetEditText = view.findViewById(R.id.event_street_edittext);
        eventNumberEditText = view.findViewById(R.id.event_number_edittext);
        eventPostCodeEditText = view.findViewById(R.id.event_postcode_edittext);


        eventTitleEditText = view.findViewById(R.id.event_title_edittext);
        eventCatchPhraseEditText = view.findViewById(R.id.event_catch_phrase_edittext);

        eventImageView = view.findViewById(R.id.event_image_view);
        eventDescriptionEditText = view.findViewById(R.id.event_description_edittext);
        eventAtendeeLimitEditText = view.findViewById(R.id.event_attendee_limit_edittext);

        saveChangesButton = view.findViewById(R.id.save_changes_button);

        eventTimeEditText = view.findViewById(R.id.event_time_edittext);


        cancelButton = view.findViewById(R.id.cancelButton);
        loadingBar = new ProgressDialog(getActivity());

        // Hide the attendeeListButton if it's not in edit mode

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HomePageActivity.class);
                startActivity(intent);
            }
        });
        eventDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        eventImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePostInfo();
            }
        });

        eventTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        return view;


        //method that button "cancelButton" is pressed take the user to activity
        // "activity_home_page"


    }

    private void loadEventDetails(String eventId) {
        PostsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Events event = dataSnapshot.getValue(Events.class);
                    if (event != null) {
                        eventTitleEditText.setText(event.getTitle());
                        eventCatchPhraseEditText.setText(event.getCatchphrase());
                        eventDescriptionEditText.setText(event.getDescription());
                        eventDateEditText.setText(event.getDate());
                        eventTimeEditText.setText(event.getTime());
                        eventCountryEditText.setText(event.getCountry());
                        eventRegionEditText.setText(event.getRegion());
                        eventStreetEditText.setText(event.getStreet());
                        eventNumberEditText.setText(event.getNumber());
                        eventPostCodeEditText.setText(event.getPostcode());
                        //String value of getAttendeeLimit() from int
                        //String value = String.valueOf(event.getAttendeeLimit());
                        //eventAtendeeLimitEditText.setText(value);

                        String eventCountry = dataSnapshot.child("country").getValue().toString();
                        String eventRegion = dataSnapshot.child("region").getValue().toString();
                        String eventStreet = dataSnapshot.child("street").getValue().toString();
                        String eventNumber = dataSnapshot.child("number").getValue().toString();
                        String eventPostCode = dataSnapshot.child("postcode").getValue().toString();

                        eventCountryEditText.setText(eventCountry);
                        eventRegionEditText.setText(eventRegion);
                        eventStreetEditText.setText(eventStreet);
                        eventNumberEditText.setText(eventNumber);
                        eventPostCodeEditText.setText(eventPostCode);


                        if (event.getPostimage() != null && !event.getPostimage().isEmpty()) {
                            Picasso.get().load(event.getPostimage()).into(eventImageView);
                        }

                        // Make fields editable
                        eventTitleEditText.setEnabled(true);
                        eventCatchPhraseEditText.setEnabled(true);
                        eventDescriptionEditText.setEnabled(true);
                        eventDateEditText.setEnabled(true);
                        eventTimeEditText.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),
                        "Error loading event details: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void validatePostInfo() {
        String description = eventDescriptionEditText.getText().toString();
        String title = eventTitleEditText.getText().toString();
        String catchPhrase = eventCatchPhraseEditText.getText().toString();
        String date = eventDateEditText.getText().toString();
        String country = eventCountryEditText.getText().toString();
        String region = eventRegionEditText.getText().toString();
        String street = eventStreetEditText.getText().toString();
        String number = eventNumberEditText.getText().toString();
        String postcode = eventPostCodeEditText.getText().toString();
        String AttendeeLimit = eventAtendeeLimitEditText.getText().toString();
        String time = eventTimeEditText.getText().toString();


        if (TextUtils.isEmpty(description)) {
            Toast.makeText(getActivity(), "Please write event description...",
                    Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(title)) {
            Toast.makeText(getActivity(), "Please write event title...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(catchPhrase)) {
            Toast.makeText(getActivity(), "Please write event catch phrase...",
                    Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(date)) {
            Toast.makeText(getActivity(), "Please write event date...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(country)) {
            Toast.makeText(getActivity(), "Please write event country...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(region)) {
            Toast.makeText(getActivity(), "Please write event region...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(street)) {
            Toast.makeText(getActivity(), "Please write event street...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(number)) {
            Toast.makeText(getActivity(), "Please write event number...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(postcode)) {
            Toast.makeText(getActivity(), "Please write event postcode...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(AttendeeLimit)) {
            Toast.makeText(getActivity(), "Please write event Attendee Limit...",
                    Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(time)) {
            Toast.makeText(getActivity(), "Please write event time...", Toast.LENGTH_SHORT).show();
        } else if (imageUri == null) {
            Toast.makeText(getActivity(), "Please select event image...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Add New Post");
            loadingBar.setMessage("Please wait, while we are updating your new post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            storePostInformation();
            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();

        }
    }

    private void storePostInformation() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath =
                PostImagesReference.child("Post Images").child(imageUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(getActivity(), "Image uploaded successfully to Storage...",
                            Toast.LENGTH_SHORT).show();

                    downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                    SavingPostInformationToDatabase();

                    Toast.makeText(getActivity(), "got the Post image Url Successfully...",
                            Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(getActivity(), "Error Occured: " + message,
                            Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void SavingPostInformationToDatabase() {
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    //String userProfileImage = dataSnapshot.child("profileimage").getValue()
                    // .toString();

                    String eventCountry = eventCountryEditText.getText().toString();
                    String eventRegion = eventRegionEditText.getText().toString();
                    String eventStreet = eventStreetEditText.getText().toString();
                    String eventNumber = eventNumberEditText.getText().toString();
                    String eventPostCode = eventPostCodeEditText.getText().toString();
                    Geocoder geocoder = new Geocoder(getActivity());
                    List<Address> addresses;
                    double latitude = 0;
                    double longitude = 0;
                    try {
                        String fullLocation =
                                eventCountry + ", " + eventRegion + ", " + eventStreet + ", " + eventNumber + ", " + eventPostCode;
                        addresses = geocoder.getFromLocationName(fullLocation, 1);
                    } catch (IOException e) {

                        throw new RuntimeException(e);
                    }

                    if (addresses.size() > 0) {
                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                    }
                    Toast.makeText(getActivity(), "L :%d" + longitude + "la:%d",
                            Toast.LENGTH_SHORT).show();
                    //make new hash map that has integers and strings called hashee


                    HashMap postMap = new HashMap<String, Object>();
                    postMap.put("uid", current_user_id);
                    postMap.put("date", saveCurrentDate);
                    postMap.put("time", saveCurrentTime);
                    postMap.put("description", eventDescriptionEditText.getText().toString());
                    postMap.put("title", eventTitleEditText.getText().toString());
                    postMap.put("catchphrase", eventCatchPhraseEditText.getText().toString());
                    postMap.put("date", eventDateEditText.getText().toString());
                    postMap.put("postimage", String.valueOf(imageUri));
                    postMap.put("eventtime", eventTimeEditText.getText().toString());
                    postMap.put("postID", current_user_id + postRandomName);
                    postMap.put("latitude", latitude);
                    postMap.put("longitude", longitude);
                    postMap.put("country", eventCountryEditText.getText().toString());
                    postMap.put("region", eventRegionEditText.getText().toString());
                    postMap.put("street", eventStreetEditText.getText().toString());
                    postMap.put("number", eventNumberEditText.getText().toString());
                    postMap.put("postcode", eventPostCodeEditText.getText().toString());

                    int eventAtendeeLimit =
                            Integer.parseInt(eventAtendeeLimitEditText.getText().toString());
                    postMap.put("attendeeLimit", eventAtendeeLimit);


                    //postMap.put("profileimage", userProfileImage);
                    //postMap.put("fullname", userFullName);

                    DatabaseReference eventRef;
                    if (isEditMode) {
                        eventRef = PostsRef.child(eventId);
                    } else {
                        eventRef = PostsRef.child(current_user_id + postRandomName);
                    }
                    eventRef.updateChildren(postMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        SendUserToMainActivity();
                                        Toast.makeText(getActivity(), "New Event is updated " +
                                                "successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Error Occured while " +
                                                "updating your post.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(getActivity(), HomePageActivity.class);
        startActivity(mainIntent);
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                // Get the bitmap from the image URI
                Bitmap bitmap =
                        MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                                imageUri);
                // Set the image view with the uploaded image
                eventImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String dateString = String.format("%04d-%02d-%02d", year, month + 1,
                                dayOfMonth);
                        eventDateEditText.setText(dateString);
                    }
                },
                year,
                month,
                day
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Set
        // the minimum date to the current date
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeString = String.format("%02d:%02d", hourOfDay, minute);
                        eventTimeEditText.setText(timeString);
                    }
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }

}