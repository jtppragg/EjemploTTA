package eus.ehu.tta.ejemplo.view.media;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.MediaController;

import java.io.IOException;

public class AudioPlayer implements MediaController.MediaPlayerControl, MediaPlayer.OnPreparedListener {
    private View view;
    private MediaPlayer player;
    private MediaController controller;

    public AudioPlayer(View view) {
        this.view = view;
        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        AudioPlayer self = this;
        controller = new MyMediaController(view.getContext()) {
            @Override
            public void onBackPressed() {
                self.onBackPressed();
            }
        };
    }

    public void setAudioUri( Uri uri ) throws IOException {
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDataSource(view.getContext(), uri);
        player.prepareAsync();
    }

    public void release() {
        if( player != null ) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
        controller.setMediaPlayer(this);
        controller.setAnchorView(view);
        controller.show(0);
    }

    @Override
    public void start() {
        if( player != null )
            player.start();
    }

    @Override
    public void pause() {
        if( player != null )
            player.pause();
    }

    @Override
    public int getDuration() {
        return player == null ? 0 : player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return player == null ? 0 : player.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        if( player != null )
            player.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return player == null ? false : player.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public void onBackPressed() {
    }
}
