package eus.ehu.tta.ejemplo;

import android.app.Application;
import android.content.Context;

public class EjemploTTA extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
