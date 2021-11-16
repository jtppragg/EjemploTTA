package eus.ehu.tta.ejemplo.view.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;

import eus.ehu.tta.ejemplo.R;
import eus.ehu.tta.ejemplo.model.Locator;
import eus.ehu.tta.ejemplo.model.beans.UserProfile;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_test, R.id.nav_exercise, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        UserProfile user = Locator.getBackend().getUserProfile();
        View headerView = navigationView.getHeaderView(0);
        ((TextView)headerView.findViewById(R.id.userName)).setText(user.getName());
        if( user.getPictureUrl() != null ) {
            Uri uri = Uri.parse(user.getPictureUrl());
            Glide.with(this).load(uri).into((ImageView) headerView.findViewById(R.id.avatar));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout(View view) {
        Locator.getBackend().logout();
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener(task -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
    }

    public void test(View view) {
        Navigation.findNavController(view).navigate(R.id.nav_test);
    }

    public void exercise(View view) {
        Navigation.findNavController(view).navigate(R.id.nav_exercise);
    }

    public void profile(View view) {
        Navigation.findNavController(view).navigate(R.id.nav_profile);
    }
}