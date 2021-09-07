package eus.ehu.tta.ejemplo.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import eus.ehu.tta.ejemplo.R;
import eus.ehu.tta.ejemplo.model.Locator;
import eus.ehu.tta.ejemplo.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Locator.getBackend().getUserProfile().observe(getViewLifecycleOwner(), user -> {
            TextView textLogin = view.findViewById(R.id.menu_login);
            textLogin.setText(String.format("%s %s",
                    getString(R.string.welcome),
                    user.getName())
            );
            TextView textLesson = view.findViewById(R.id.menu_lesson);
            textLesson.setText(String.format("%s %d: %s",
                    getString(R.string.lesson),
                    user.getCurrentLesson(),
                    user.getLessonTitle())
            );

        });
        return view;
    }
}