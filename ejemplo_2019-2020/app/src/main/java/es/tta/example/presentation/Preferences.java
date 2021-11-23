package es.tta.example.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gorka on 19/10/15.
 */
public class Preferences {
    private final static String PREF_LOGIN = "login";
    private final SharedPreferences prefs;

    public Preferences( Activity activity ) {
        prefs = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public String loadLogin() {
        return prefs.getString(PREF_LOGIN,null);
    }

    public boolean saveLogin( String login ) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_LOGIN,login);
        return editor.commit();
    }
}
