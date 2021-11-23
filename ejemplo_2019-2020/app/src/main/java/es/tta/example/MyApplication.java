package es.tta.example;

import android.app.Application;
import android.content.Context;

/**
 * Created by gorka on 1/12/17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    private static Context context;
}
