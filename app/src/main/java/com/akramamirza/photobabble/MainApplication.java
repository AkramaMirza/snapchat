package com.akramamirza.photobabble;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;

/**
 * Created by CR7 on 10/4/2015.
 */
public class MainApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "4wdaUcGWnZGJx1burBJ8filyHEUeTb3n8raSqszz",
                "DdunEdSNPewzsRoRfVZ8NrQexnAjc0q1kggmmULD");
        context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MainApplication.context;
    }
}
