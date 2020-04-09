package io.github.jaccastonguay.notecardapp;

/**
 * Created by jgc00 on 4/7/2020.
 */

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseACL;


public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // tN2gQJnMZ7Ev

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("b34c7eaa607b48bf6bfe150c51f13dc750a5f3ea")
                .clientKey("f0b7e7077ee2ce817cb3f6bd379b02bb65721bd7")
                .server("http://13.58.241.167:80/parse/")
                .build()
        );



        //ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}