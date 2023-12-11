package eus.ehu.tta.ejemplo.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import eus.ehu.tta.ejemplo.R;
import eus.ehu.tta.ejemplo.model.Locator;
import eus.ehu.tta.ejemplo.model.beans.UserProfile;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        UserProfile user = Locator.getBackend().getUserProfile();
        TextView textLogin = view.findViewById(R.id.menu_login);
        textLogin.setText(String.format("%s %s",  getString(R.string.welcome), user.getName()));
        TextView textLesson = view.findViewById(R.id.menu_lesson);
        textLesson.setText(String.format("%s %d: %s",
            getString(R.string.lesson),
            user.getCurrentLesson(),
            user.getLessonTitle())
        );
        return view;
    }
}