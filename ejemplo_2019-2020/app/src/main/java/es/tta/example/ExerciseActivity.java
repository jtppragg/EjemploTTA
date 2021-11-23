package es.tta.example;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import es.tta.prof.view.ProgressTask;


public class ExerciseActivity extends ModelActivity {
    private static final int READ_REQUEST_CODE = 1;
    private static final int VIDEO_REQUEST_CODE = 2;
    private static final int AUDIO_REQUEST_CODE = 3;
    private static final int PICTURE_REQUEST_CODE = 4;
    private Uri pictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        TextView textWording = (TextView)findViewById(R.id.exercise_wording);
        textWording.setText(data.getExercise().getWording());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exercise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendFile( View view ) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode != Activity.RESULT_OK )
            return;
        switch( requestCode ) {
            case READ_REQUEST_CODE:
            case VIDEO_REQUEST_CODE:
            case AUDIO_REQUEST_CODE:
                sendFile(data.getData());
                break;
            case PICTURE_REQUEST_CODE:
                sendFile(pictureUri);
                break;
        }
    }

    private void sendFile( final Uri uri ) {
        if( uri == null )
            return;
        new ProgressTask<Void>() {
            @Override
            protected Void work() throws Exception {
                try(InputStream is = getContentResolver().openInputStream(uri)) {
                    server.uploadSolution(data.getUserId(), data.getExercise().getId(), is, resolveName(uri));
                }
                return null;
            }

            @Override
            protected void onFinish(Void result) {
                Toast.makeText(getApplicationContext(), R.string.send_ok, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    public void sendPicture( View view ) {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            Toast.makeText(this, R.string.no_camera, Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                try {
                    File file = File.createTempFile("tta", ".jpg", dir);
                    pictureUri = Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
                    startActivityForResult(intent, PICTURE_REQUEST_CODE);
                } catch( IOException e ) {
                }
            }
            else
                Toast.makeText(this,R.string.no_app,Toast.LENGTH_SHORT).show();
        }
    }

    public void recordAudio( View view ) {
        if( !getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE) )
            Toast.makeText(this,R.string.no_micro,Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(intent, AUDIO_REQUEST_CODE);
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
                startActivityForResult(intent, VIDEO_REQUEST_CODE);
            else
                Toast.makeText(this,R.string.no_app,Toast.LENGTH_SHORT).show();
        }
    }
}