package eus.ehu.tta.ejemplo.view.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eus.ehu.tta.ejemplo.R;
import eus.ehu.tta.ejemplo.view.activities.ActivityFutureLauncher;
import eus.ehu.tta.ejemplo.viewmodel.ExerciseViewModel;

public class ExerciseFragment extends BaseFragment {
    private ExerciseViewModel viewModel;
    private final ActivityFutureLauncher<String,Uri> launcherFile = registerForActivityFuture(
        new ActivityResultContracts.GetContent());
    private final ActivityFutureLauncher<Uri, Boolean> launcherPicture = registerForActivityFuture(
        new ActivityResultContracts.TakePicture());
    private final ActivityFutureLauncher<Intent, ActivityResult> launcherAudio = registerForActivityFuture(
        new ActivityResultContracts.StartActivityForResult());
    private final ActivityFutureLauncher<Uri, Bitmap> launcherVideo = registerForActivityFuture(
        new ActivityResultContracts.TakeVideo());

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        onCreatedViewmodel(viewModel);

        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        Button buttonFile = view.findViewById(R.id.button_send_file);
        Button buttonPicture = view.findViewById(R.id.button_take_picture);
        Button buttonAudio = view.findViewById(R.id.button_record_audio);
        Button buttonVideo = view.findViewById(R.id.button_record_video);
        List<Button> buttons = new ArrayList<>();
        buttons.add(buttonFile);
        buttons.add(buttonPicture);
        buttons.add(buttonAudio);
        buttons.add(buttonVideo);

        viewModel.getExercise().observe(getViewLifecycleOwner(), exercise -> {
            ((TextView)view.findViewById(R.id.exercise_wording)).setText(exercise.getWording());
            enableButtons(buttons, true);
        });

        buttonFile.setOnClickListener( button ->
            launcherFile.launch("*/*").thenAccept(uri -> sendFile(uri))
        );
        buttonPicture.setOnClickListener(button -> sendPicture());
        buttonAudio.setOnClickListener(button -> recordAudio());
        buttonVideo.setOnClickListener(button -> recordVideo());

        viewModel.getSent().observe(getViewLifecycleOwner(), ok -> {
            enableButtons(buttons, !ok);
            if( viewModel.isSkipNotification() )
                return;
            Toast.makeText(getContext(), getString(ok ? R.string.uploaded_ok : R.string.uploaded_ko), Toast.LENGTH_SHORT).show();
            viewModel.setSkipNotification(true);
        });

        return view;
    }

    private void enableButtons(List<Button> buttons, boolean enabled) {
        for( Button b : buttons )
            b.setEnabled(enabled);
    }

    private void sendFile(Uri uri) {
        viewModel.setSkipNotification(false);
        viewModel.sendResponse(uri);
    }

    private void sendPicture() {
        if( !getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) ) {
            Toast.makeText(getContext(), R.string.no_camera, Toast.LENGTH_SHORT).show();
            return;
        }
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.storage_rationale)).thenAccept(granted -> {
            if( !granted )
                return;
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            try {
                File file = File.createTempFile("tta", ".jpg", dir);
                //pictureUri = Uri.fromFile(file);
                Uri uri = FileProvider.getUriForFile(getContext(),getContext().getApplicationContext().getPackageName() + ".fileprovider", file);
                launcherPicture.launch(uri).thenAccept(ok -> {
                   if( ok )
                       sendFile(uri);
                });
            } catch( IOException e ) {
                e.printStackTrace();
            }
        });
    }

    private void recordAudio() {
        if( !getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE) ) {
            Toast.makeText(getContext(), R.string.no_micro, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if( intent.resolveActivity(getActivity().getPackageManager()) == null ) {
            Toast.makeText(getContext(), R.string.no_app, Toast.LENGTH_SHORT).show();
            return;
        }
        launcherAudio.launch(intent).thenAccept(result -> sendFile(result.getData().getData()));
    }

    private void recordVideo() {
        if( !getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) ) {
            Toast.makeText(getContext(), R.string.no_camera, Toast.LENGTH_SHORT).show();
            return;
        }
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.storage_rationale)).thenAccept(granted -> {
            if( !granted )
                return;
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            try {
                File file = File.createTempFile("tta", ".mp4", dir);
                Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".fileprovider", file);
                launcherVideo.launch(uri).thenAccept(bitmap -> sendFile(uri));
            } catch( IOException e ) {
                e.printStackTrace();
            }
        });
    }
}