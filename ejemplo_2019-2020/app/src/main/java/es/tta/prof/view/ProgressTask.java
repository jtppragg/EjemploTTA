package es.tta.prof.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import es.tta.example.MyApplication;
import es.tta.example.R;

/**
 * Created by gorka on 8/10/15.
 */
public abstract class ProgressTask<T> extends AsyncTask<Void, Void, T> {

    protected final Context context;
    private final ProgressDialog dialog;
    private Exception e;

    public ProgressTask() {
        this.context = MyApplication.getContext();
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.downloading));
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected T doInBackground(Void... params) {
        T result = null;
        try {
            result = work();
        } catch( Exception e ) {
            this.e = e;
        }
        return result;
    }

    @Override
    protected void onPostExecute(T result) {
        if( dialog.isShowing() )
            dialog.dismiss();
        if( e != null )
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        else
            onFinish(result);
    }

    protected abstract T work() throws Exception;
    protected abstract void onFinish(T result);
}