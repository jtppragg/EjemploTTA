package eus.ehu.bi.tta.ejemplo.pl.view.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import eus.ehu.bi.tta.ejemplo.R;
import eus.ehu.bi.tta.ejemplo.pl.view.tasks.ProgressTask;
import eus.ehu.bi.tta.ejemplo.pl.viewmodel.ExerciseViewModel;

public class ExerciseActivity extends AppCompatActivity {
    private static final int READ_REQUEST = 1;
    private static final int PICTURE_REQUEST = 2;
    private static final int AUDIO_REQUEST = 3;
    private static final int VIDEO_REQUEST = 4;
    private static final int PICTURE_PERM = 1;
    private ExerciseViewModel viewModel;
    private Uri pictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        viewModel = ViewModelProviders.of(this).get(ExerciseViewModel.class);

        if( viewModel.getExercise().getValue() == null )
            new DownloadTask(this).execute();
        viewModel.getExercise().observe(this, exercise -> {
            TextView view = findViewById(R.id.exercise_wording);
            view.setText(exercise.getWording());
        });
        viewModel.isFinished().observe(this, name -> {
            findViewById(R.id.button_send_file).setEnabled(false);
            findViewById(R.id.button_take_picture).setEnabled(false);
            findViewById(R.id.button_record_audio).setEnabled(false);
            findViewById(R.id.button_record_video).setEnabled(false);
            Toast.makeText(this,
                String.format("Enviado: %s", getString(R.string.sent), name),
                Toast.LENGTH_SHORT).show();
        });
    }

    public void sendFile( View view ) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,READ_REQUEST);
    }

    public void requestPicture(View view) {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            Toast.makeText(this, R.string.no_camera, Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if( intent.resolveActivity(getPackageManager()) == null )
                Toast.makeText(this,R.string.no_app,Toast.LENGTH_SHORT).show();
            else {
                String perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                if( ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                    if( ActivityCompat.shouldShowRequestPermissionRationale(this, perm) ) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(this);
                        alert.setTitle(R.string.permission_neccesary);
                        alert.setMessage(R.string.storage_rationale);
                        alert.setIcon(android.R.drawable.ic_dialog_info);
                        alert.setPositiveButton(R.string.ok, (dialogInterface, i) ->
                            ActivityCompat.requestPermissions(this, new String[]{perm}, PICTURE_PERM));
                        alert.show();
                    } else
                        ActivityCompat.requestPermissions(this, new String[]{perm}, PICTURE_PERM);
                } else
                    sendPicture();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for( int grantResult : grantResults )
            if( grantResult != PackageManager.PERMISSION_GRANTED )
                return;
        switch (requestCode) {
            case PICTURE_PERM:
                sendPicture();
                break;
        }
    }

    private void sendPicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        try {
            File file = File.createTempFile("tta", ".jpg", dir);
            //pictureUri = Uri.fromFile(file);
            pictureUri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".fileprovider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
            startActivityForResult(intent, PICTURE_REQUEST);
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void recordAudio( View view ) {
        if( !getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE) )
            Toast.makeText(this,R.string.no_micro,Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(intent, AUDIO_REQUEST);
            else
                Toast.makeText(this,R.string.no_app,Toast.LENGTH_SHORT).show();
        }
    }

    public void recordVideo( View view ) {
        if( !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) )
            Toast.makeText(this,R.string.no_camera,Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(intent, VIDEO_REQUEST);
            else
                Toast.makeText(this,R.string.no_app,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case READ_REQUEST:
            case VIDEO_REQUEST:
            case AUDIO_REQUEST:
                sendFile(data.getData());
                break;
            case PICTURE_REQUEST:
                sendFile(pictureUri);
                break;
        }
    }

    private void sendFile( Uri uri ) {
        if( uri == null )
            return;
        new UploadTask(this).execute(uri);
    }

    private String resolveName( Uri uri ) {
        Cursor cursor = getContentResolver().query(uri,null,null,null,null,null);
        if( cursor == null )
            return uri.getLastPathSegment();
        try {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        } finally {
            cursor.close();
        }
    }

    // Background tasks

    private static class DownloadTask extends ProgressTask<ExerciseActivity,Void,Void,Void> {
        DownloadTask(ExerciseActivity activity) {
            super(activity);
        }

        @Override
        protected Void doInBackground(ExerciseActivity activity, Void... voids) {
            activity.viewModel.loadExercise();
            return null;
        }
    }

    private static class UploadTask extends ProgressTask<ExerciseActivity,Uri,Void,Void> {
        UploadTask(ExerciseActivity activity) {
            super(activity);
        }

        @Override
        protected Void doInBackground(ExerciseActivity activity, Uri... uris) {
            Uri uri = uris[0];
            try(InputStream is = activity.getContentResolver().openInputStream(uri)) {
                activity.viewModel.uploadSolution(is, activity.resolveName(uri));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
