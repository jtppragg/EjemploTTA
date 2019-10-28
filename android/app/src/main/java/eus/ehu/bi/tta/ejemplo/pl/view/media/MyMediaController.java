package eus.ehu.bi.tta.ejemplo.pl.view.media;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.MediaController;

public class MyMediaController extends MediaController {
    public MyMediaController(Context context) {
        super(context);
    }

    @Override
    public void hide() {
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if( event.getKeyCode() == KeyEvent.KEYCODE_BACK )
            onBackPressed();
        return super.dispatchKeyEvent(event);
    }

    public void onBackPressed() {
    }
}
