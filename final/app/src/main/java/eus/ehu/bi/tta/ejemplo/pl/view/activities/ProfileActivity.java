package eus.ehu.bi.tta.ejemplo.pl.view.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;
import eus.ehu.bi.tta.ejemplo.R;
import eus.ehu.bi.tta.ejemplo.bl.beans.UserProfile;
import eus.ehu.bi.tta.ejemplo.pl.model.Locator;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        UserProfile user = Locator.getUserModel().getProfile();

        ImageView imageView = findViewById(R.id.profilePicture);
        if( user.getPictureUrl() == null )
            imageView.setImageResource(R.drawable.ic_default_picture);
        else
            Glide.with(this).load(user.getPictureUrl()).into(imageView);

        ((TextView)findViewById(R.id.profileName)).setText(user.getName());

        ((TextView)findViewById(R.id.userStatus)).setText(
            String.format("%s: %d", getString(R.string.lesson), user.getCurrentLesson()));
    }
}
