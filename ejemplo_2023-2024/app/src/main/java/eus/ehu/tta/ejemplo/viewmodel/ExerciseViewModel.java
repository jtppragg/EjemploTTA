package eus.ehu.tta.ejemplo.viewmodel;

import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import eus.ehu.tta.ejemplo.EjemploTTA;
import eus.ehu.tta.ejemplo.model.Locator;
import eus.ehu.tta.ejemplo.model.backend.Backend;
import eus.ehu.tta.ejemplo.model.beans.Exercise;

public class ExerciseViewModel extends BaseViewModel {
    private final Backend backend = Locator.getBackend();
    private final MutableLiveData<Exercise> liveExercise = new MutableLiveData<>();
    private final MutableLiveData<Boolean> liveSent = new MutableLiveData<>();
    private boolean skipNotification;
    private Uri uri;

    public ExerciseViewModel() {
        startLoad();
        backend.getExercise().addOnCompleteListener(task -> {
            endLoad();
            liveExercise.setValue(task.getResult());
        });
    }

    public void sendResponse(Uri uri) {
        if( liveExercise.getValue() == null )
            return;
        try {
            InputStream is = EjemploTTA.getContext().getContentResolver().openInputStream(uri);
            String name = resolveName(uri);
            startLoad();
            backend.uploadExercise(is, name).addOnCompleteListener(task -> {
                endLoad();
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                liveSent.setValue(task.isSuccessful());
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            liveSent.setValue(false);
        }
    }

    private String resolveName( Uri uri ) {
        Cursor cursor = EjemploTTA.getContext().getContentResolver().query(uri,null,null,null,null,null);
        if( cursor == null )
            return uri.getLastPathSegment();
        try {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        } finally {
            cursor.close();
        }
    }

    public LiveData<Exercise> getExercise() {
        return liveExercise;
    }

    public LiveData<Boolean> getSent() {
        return liveSent;
    }

    public boolean isSkipNotification() {
        return skipNotification;
    }

    public void setSkipNotification(boolean skipNotification) {
        this.skipNotification = skipNotification;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
