package es.tta.example;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;

import es.tta.example.model.Business;
import es.tta.example.presentation.Data;
import es.tta.example.presentation.Preferences;
import es.tta.prof.comms.RestClient;

/**
 * Created by gorka on 29/09/15.
 */
public abstract class ModelActivity extends AppCompatActivity {
    public static final String URL = "http://server:8080/ServidorTta/rest/tta";
    protected RestClient rest;
    protected Business server;
    protected Preferences prefs;
    protected Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new Data(getIntent().getExtras());
        rest = new RestClient(URL);
        String auth = data.getAuthToken();
        if( auth != null )
            rest.setAuthorization(auth);
        server = new Business(rest);
        prefs = new Preferences(this);
    }

    protected <T> void startModelActivity(Class<T> cls) {
        Intent intent = newIntent(cls);
        startActivity(intent);
    }

    protected <T> void startModelActivityForResult(Class<T> cls, int requestCode) {
        Intent intent = newIntent(cls);
        startActivityForResult(intent, requestCode);
    }

    private <T> Intent newIntent(Class<T> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtras(data.getBundle());
        return intent;
    }

    protected String resolveName( Uri uri ) {
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
}