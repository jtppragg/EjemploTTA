package eus.ehu.bi.tta.ejemplo.pl.view.tasks;

import android.app.ProgressDialog;

import eus.ehu.bi.tta.ejemplo.R;

public abstract class ProgressTask<Context extends android.content.Context,Params,Progress,Result>
        extends WeakTask<Context,Params,Progress,Result> {

    private final ProgressDialog dialog;

    public ProgressTask(Context context) {
        super(context);
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.loading));
    }

    @Override
    protected void onPreExecute(Context context) {
        dialog.show();
    }

    @Override
    protected void onPostExecute(Context context, Result result) {
        if( dialog.isShowing() )
            dialog.dismiss();
    }

    @Override
    protected void onCancelled(Context context, Result result) {
        if( dialog.isShowing() )
            dialog.dismiss();
    }
}
