package eus.ehu.bi.tta.ejemplo.pl.view.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import eus.ehu.bi.tta.ejemplo.R;
import eus.ehu.bi.tta.ejemplo.bl.backend.Backend;
import eus.ehu.bi.tta.ejemplo.bl.beans.UserProfile;
import eus.ehu.bi.tta.ejemplo.pl.model.Locator;
import eus.ehu.bi.tta.ejemplo.pl.view.tasks.WeakTask;

public class LoginActivity extends AppCompatActivity {
    private final Backend backend = Locator.getBackend();
    private SharedPreferences prefs;
    private static final String PREF_LOGIN = "login";
    private EditText loginView, passwdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = getPreferences(Context.MODE_PRIVATE);

        loginView = findViewById(R.id.login);
        passwdView = findViewById(R.id.passwd);

        String login = prefs.getString(PREF_LOGIN,null);
        if( login != null ) {
            loginView.setText(login);
            passwdView.requestFocus();
        }
    }

    public void login( View view ) {
        new LoginTask(this).execute();
    }

    private void onLoginOk(UserProfile userProfile) {
        Locator.getUserModel().setProfile(userProfile);
        prefs.edit().putString(PREF_LOGIN, loginView.getText().toString()).apply();
        Intent menuIntent = new Intent(this, MenuActivity.class);
        startActivity(menuIntent);
    }

    private void onLoginFailed() {
        passwdView.getText().clear();
        Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
    }

    //
    // Background tasks
    //

    private static class LoginTask extends WeakTask<LoginActivity,Void,Void, UserProfile> {
        LoginTask(LoginActivity activity) {
            super(activity);
        }

        @Override
        protected UserProfile doInBackground(LoginActivity activity, Void... voids) {
            activity.backend.setCredentials(
                activity.loginView.getText().toString(),
                activity.passwdView.getText().toString()
            );
            try {
                return activity.backend.getUserProfile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(LoginActivity activity, UserProfile userProfile) {
            if( userProfile != null )
                activity.onLoginOk(userProfile);
            else
                activity.onLoginFailed();
        }
    }
}
