package com.example.rootine;

import java.util.Calendar;

//singleton class that keeps track of the app activity
public class AppManager {

    private static AppManager instance = null;

    private int goal = 0;

    private AppManager(){

    }

    public static AppManager getInstance(){
        if (instance == null){
            instance = new AppManager();
        }
        return instance;
    }

    public void setGoal(int goal){
        this.goal = goal;
    }

    public int getGoal(){
        return this.goal;
    }
}
