package eus.ehu.bi.tta.ejemplo.pl.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import eus.ehu.bi.tta.ejemplo.R;
import eus.ehu.bi.tta.ejemplo.bl.backend.Backend;
import eus.ehu.bi.tta.ejemplo.bl.beans.UserProfile;
import eus.ehu.bi.tta.ejemplo.pl.model.Locator;
import eus.ehu.bi.tta.ejemplo.pl.view.tasks.WeakTask;

public class LoginActivity extends AppCompatActivity {
    private final Backend backend = Locator.getBackend();
    private EditText loginView, passwdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginView = findViewById(R.id.login);
        passwdView = findViewById(R.id.passwd);
    }

    public void login( View view ) {
        new LoginTask(this).execute();
    }

    private void onLoginOk(UserProfile userProfile) {
        Locator.getUserModel().setProfile(userProfile);
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
            return activity.backend.getUserProfile();
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
