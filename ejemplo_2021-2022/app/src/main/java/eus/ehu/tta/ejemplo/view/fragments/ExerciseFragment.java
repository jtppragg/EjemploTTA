package eus.ehu.tta.ejemplo.view.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletionStage;

import eus.ehu.tta.ejemplo.R;
import eus.ehu.tta.ejemplo.view.activities.ActivityFutureLauncher;
import eus.ehu.tta.ejemplo.viewmodel.ExerciseViewModel;

public class ExerciseFragment extends BaseFragment {
    private ExerciseViewModel viewModel;
    private final ActivityFutureLauncher<String,Uri> launcherFile = registerForActivityFuture(
        new ActivityResultContracts.GetContent());
    private final ActivityFutureLauncher<Uri, Boolean> launcherPicture = registerForActivityFuture(
        new ActivityResultContracts.TakePicture());

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        onCreatedViewmodel(viewModel);

        View view = inflater.inflate(R.layout.fragment_exercise, container, false);
        viewModel.getExercise().observe(getViewLifecycleOwner(), exercise -> {
            ((TextView)view.findViewById(R.id.exercise_wording)).setText(exercise.getWording());
        });
        view.findViewById(R.id.button_send_file).setOnClickListener( button ->
            launcherFile.launch("*/*").thenAccept(uri -> sendFile(uri))
        );
        view.findViewById(R.id.button_take_picture).setOnClickListener(button -> sendPicture());
        view.findViewById(R.id.button_record_audio).setOnClickListener(button -> recordAudio());
        view.findViewById(R.id.button_record_video).setOnClickListener(button -> recordVideo());
        return view;
    }

    private void sendFile(Uri result) {
        Toast.makeText(getContext(), result.toString(), Toast.LENGTH_SHORT).show();
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
                Uri pictureUri = FileProvider.getUriForFile(getContext(),
                    getContext().getApplicationContext().getPackageName() + ".fileprovider", file);
                launcherPicture.launch(pictureUri).thenAccept(ok -> {
                   if( ok )
                       sendFile(pictureUri);
                });
            } catch( IOException e ) {
                e.printStackTrace();
            }
        });
    }

    private void recordAudio() {
    }

    private void recordVideo() {
    }
}