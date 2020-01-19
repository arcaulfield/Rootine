package com.example.rootine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Category;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    private TextView daysLeftText;
    private MaterialCalendarView calendarView;

    private AppManager manager = AppManager.getInstance();

    private VisionServiceClient client;

    private Uri mImageUri;

    // The image selected to detect.
    private Bitmap mBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Calendar");

        if (client==null){
            client = new VisionServiceRestClient(getString(R.string.subscription_key), getString(R.string.subscription_apiroot));
        }

        daysLeftText = findViewById(R.id.daysLeftText);

        updateTextBox();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);

                if (manager.getLoggedToday()) {
                    displayMessage(view, "You already logged your entry today! You have to wait till tomorrow");
                    return;
                }

                // Alert the manager that no meat was eaten today
                manager.noMeatToday();

                if (manager.getNoMeatDaysThisWeek() == manager.getGoal()) {
                    displayMessage(view, "You reached your goal! Go find your new animal in your wildlife reserve!");
                }

                // Display how many noMeatDays left
                updateTextBox();

                Calendar managerDate = manager.getCurrentDate();

                Calendar today = Calendar.getInstance();
                today.set(managerDate.get(Calendar.YEAR), managerDate.get(Calendar.MONTH), managerDate.get(Calendar.DATE));

                // Add today as a no meat day
                manager.getNoMeatDays().add(today);

                refreshDecorators();
            }
        });

        FloatingActionButton next_day = findViewById(R.id.next_day);
        next_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.incrementDate();
                updateTextBox();

                refreshDecorators();
            }
        });

        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);

        refreshDecorators();
    }

    public void displayMessage(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void updateTextBox() {
        String message = "Meatless days this week: " + manager.getNoMeatDaysThisWeek() + "\n";

        int goal = manager.getGoal();
        int noMeatThisWeek = manager.getNoMeatDaysThisWeek();

        if (goal == noMeatThisWeek) {
            message += "You reached yor goal this week!";
        }
        else if (goal > noMeatThisWeek) {
            message += (goal - noMeatThisWeek) + " meatless day" + ((goal - noMeatThisWeek == 1) ? "" : "s") + " left this week!";
        }
        else {
            message += "Wow! You went over your goal by " + (noMeatThisWeek - goal) + " day" + ((noMeatThisWeek - goal == 1) ? "" : "s") + " this week!";
        }

        daysLeftText.setText(message);
    }

    private void refreshDecorators() {
        calendarView.setCurrentDate(AppManager.getInstance().getCurrentDate());
        calendarView.removeDecorators();
        calendarView.addDecorator(new CurrentDayDecorator());
        calendarView.addDecorator(new CarrotDecorator(this));
        calendarView.addDecorator(new CarrotTodayDecorator(this));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        new doRequest().execute();
                    }
                    break;
                }
        }
    }

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();
        String[] features = {"ImageType", "Categories"};
        String[] details = {};

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        AnalysisResult v = this.client.analyzeImage(inputStream, features, details);

        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
    }


    private class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence

            if (e != null) {
                displayMessage(calendarView, "Something went wrong...");
                this.e = null;
            } else {
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);

                for (Category category: result.categories) {
                    Log.d("", "Category: " + category.name + ", score: " + category.score + "\n");
                }

            }

        }


    }


}
