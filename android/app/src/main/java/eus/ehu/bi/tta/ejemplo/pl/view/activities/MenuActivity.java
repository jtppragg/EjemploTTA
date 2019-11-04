package eus.ehu.bi.tta.ejemplo.pl.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import eus.ehu.bi.tta.ejemplo.R;
import eus.ehu.bi.tta.ejemplo.pl.model.Locator;
import eus.ehu.bi.tta.ejemplo.pl.model.UserModel;

public class MenuActivity extends AppCompatActivity {
    private final UserModel user = Locator.getUserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView textLogin = findViewById(R.id.menu_login);
        textLogin.setText(String.format("%s %s",
            getString(R.string.welcome),
            user.getProfile().getName())
        );
        TextView textLesson = findViewById(R.id.menu_lesson);
        textLesson.setText(String.format("%s %d: %s",
            getString(R.string.lesson),
            user.getProfile().getCurrentLesson(),
            user.getProfile().getLessonTitle())
        );
    }

    public void test(View view) {
        startActivity(new Intent(this, TestActivity.class));
    }

    public void exercise(View view) {
        startActivity(new Intent(this, ExerciseActivity.class));
    }

    public void profile(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}
