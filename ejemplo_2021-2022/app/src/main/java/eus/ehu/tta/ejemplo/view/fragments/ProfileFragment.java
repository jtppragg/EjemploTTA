package eus.ehu.tta.ejemplo.view.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import eus.ehu.tta.ejemplo.R;
import eus.ehu.tta.ejemplo.model.Locator;
import eus.ehu.tta.ejemplo.model.beans.UserProfile;

public class ProfileFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        UserProfile user = Locator.getBackend().getUserProfile();
        ((TextView)view.findViewById(R.id.userName)).setText(user.getName());
        if( user.getPictureUrl() != null ) {
            Uri uri = Uri.parse(user.getPictureUrl());
            Glide.with(this).load(uri).into((ImageView)view.findViewById(R.id.avatar));
        }
        ((TextView)view.findViewById(R.id.userStatus)).setText(getString(R.string.lesson) + ": " + user.getCurrentLesson());
        return view;
    }
}