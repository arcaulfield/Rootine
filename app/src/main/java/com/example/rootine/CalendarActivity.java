package com.example.rootine;

import android.os.Bundle;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private List<EventDay> events;

    private AppManager manager = AppManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.events = manager.getNoMeatDays();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (manager.getLoggedToday()) {
                    displayMessage(view, "You already logged your entry today! You have to wait till tomorrow");
                    return;
                }

                // Alert the manager that no meat was eaten today
                manager.noMeatToday();

                int goal = manager.getGoal();
                int noMeatThisWeek = manager.getNoMeatDaysThisWeek();
                String message;
                if (goal == noMeatThisWeek) {
                    message = "You reached yor goal! You got a new animal!";
                }
                else if (goal > noMeatThisWeek) {
                    message = (goal - noMeatThisWeek) + " meatless day" + ((goal - noMeatThisWeek == 1) ? "" : "s") + " left!";
                }
                else {
                    message = "Wow! You went over your goal by " + (noMeatThisWeek - goal) + " day" + ((noMeatThisWeek - goal == 1) ? "" : "s") + " this week!";
                }

                displayMessage(view, message);

                Calendar managerDate = manager.getCurrentDate();

                Calendar today = Calendar.getInstance();
                today.set(managerDate.get(Calendar.YEAR), managerDate.get(Calendar.MONTH), managerDate.get(Calendar.DATE));
                events.add(new EventDay(today, R.mipmap.carrot_transparant));
                calendarView.setEvents(events);
            }
        });

        FloatingActionButton next_day = findViewById(R.id.next_day);
        next_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.incrementDate();
            }
        });

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setEvents(events);
    }

    private void noMeatToday() {
        events.add(new EventDay(AppManager.getInstance().getCurrentDate(), R.mipmap.carrot));
    }

    public void displayMessage(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
