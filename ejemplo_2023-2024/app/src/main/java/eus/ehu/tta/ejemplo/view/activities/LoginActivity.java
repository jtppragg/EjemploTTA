package eus.ehu.tta.ejemplo.view.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import java.util.Arrays;
import java.util.List;

import eus.ehu.tta.ejemplo.R;
import eus.ehu.tta.ejemplo.model.Locator;
import eus.ehu.tta.ejemplo.model.backend.FirebaseBackend;

public class LoginActivity extends AppCompatActivity {
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );

    private void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.Theme_EjemploTTA)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if( result.getResultCode() != RESULT_OK )
            finish();
        setProgressBarIndeterminateVisibility(true);
        Locator.getBackend().login()
            .addOnCompleteListener(task -> {
                if( task.isSuccessful() )
                    startActivity(new Intent(this, MainActivity.class));
                finish();
            });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if( Locator.getBackend() instanceof FirebaseBackend )
            createSignInIntent();
        else {
            Locator.getBackend().login();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}