package eus.ehu.tta.ejemplo.view.media;

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
        if( event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
            onBackPressed();
        return super.dispatchKeyEvent(event);
    }

    public void onBackPressed() {
        super.hide();
    }
}
