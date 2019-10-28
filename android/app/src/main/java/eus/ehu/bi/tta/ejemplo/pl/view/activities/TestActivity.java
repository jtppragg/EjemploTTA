package eus.ehu.bi.tta.ejemplo.pl.view.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import eus.ehu.bi.tta.ejemplo.R;
import eus.ehu.bi.tta.ejemplo.bl.beans.Test;
import eus.ehu.bi.tta.ejemplo.pl.view.media.AudioPlayer;
import eus.ehu.bi.tta.ejemplo.pl.view.media.MyMediaController;
import eus.ehu.bi.tta.ejemplo.pl.view.tasks.ProgressTask;
import eus.ehu.bi.tta.ejemplo.pl.viewmodel.TestViewModel;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {
    private TestViewModel viewModel;
    private ViewGroup layout;
    private RadioGroup radioGroup;
    private Button buttonSend, buttonAdvise;
    private AudioPlayer audioPlayer;
    private boolean skipNotification = true, skipAdvise = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        layout = findViewById(R.id.test_layout);
        radioGroup = findViewById(R.id.test_choices);
        buttonSend = findViewById(R.id.button_send_test);
        buttonAdvise = findViewById(R.id.button_view_advise);

        viewModel = ViewModelProviders.of(this).get(TestViewModel.class);
        if( viewModel.getTest().getValue() == null )
            new DownloadTask(this).execute();

        viewModel.getTest().observe(this, test -> loadTest());
        viewModel.isSent().observe(this, correct -> {
            showSolution();
            buttonSend.setEnabled(false);
            if( !skipNotification)
                Toast.makeText(this, correct ? R.string.correct : R.string.incorrect, Toast.LENGTH_SHORT).show();
            if( !correct )
                buttonAdvise.setVisibility(View.VISIBLE);

        });
        viewModel.isFinished().observe(this, aBoolean -> {
            loadAdvise();
            buttonAdvise.setEnabled(false);
        });
    }

    @Override
    protected void onStop() {
        if( audioPlayer != null ) {
            audioPlayer.release();
            audioPlayer = null;
        }
        super.onStop();
    }

    private void loadTest() {
        Test test = viewModel.getTest().getValue();
        TextView textWording = findViewById(R.id.test_wording);
        textWording.setText(test.getWording());
        for(Test.Choice choice : test.getChoices() ) {
            RadioButton radio = new RadioButton(this);
            radio.setText(choice.getWording());
            radio.setOnClickListener(this);
            radioGroup.addView(radio);
        }
        if( viewModel.getSelection() != -1 ) {
            radioGroup.check(radioGroup.getChildAt(viewModel.getSelection()).getId());
            buttonSend.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int pos = radioGroup.indexOfChild(view);
        viewModel.setSelection(pos);
        if( pos != -1 )
            buttonSend.setVisibility(View.VISIBLE);
    }

    public void send(View view) {
        skipNotification = false;
        new UploadTask(this).execute();
    }

    private void showSolution() {
        Test test = viewModel.getTest().getValue();
        int i = 0;
        for(Test.Choice choice : test.getChoices() ) {
            RadioButton radio = (RadioButton)radioGroup.getChildAt(i++);
            radio.setEnabled(false);
            if( choice.isCorrect() )
                radio.setBackgroundColor(Color.GREEN);
            else if( radio.isChecked() )
                radio.setBackgroundColor(Color.RED);
        }
    }

    public void advise(View view) {
        skipAdvise = false;
        viewModel.finish();
    }

    private void loadAdvise() {
        Test.Choice choice = viewModel.getChoice();
        String mime = choice.getMime().toLowerCase();
        if( mime.contains("video") )
            showVideo(choice.getAdvise());
        else if( mime.contains("audio") )
            playAudio(choice.getAdvise());
        else
            showHtml(choice.getAdvise());
    }

    private void showHtml( String advise ) {
        if( advise.substring(0,10).contains("://") ) {
            if( !skipAdvise) {
                Uri uri = Uri.parse(advise);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        } else {
            WebView web = new WebView(this);
            //web.loadUrl(advise);
            web.loadData(advise, "text/html", null);
            web.setBackgroundColor(Color.TRANSPARENT);
            web.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            layout.addView(web);
        }
    }

    private void playAudio(String advise) {
        try {
            audioPlayer = new AudioPlayer(layout) {
                @Override
                public void onBackPressed() {
                    finish();
                }
            };
            audioPlayer.setAudioUri(Uri.parse(advise));
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void showVideo(String advise) {
        VideoView video = new VideoView(this);
        video.setVideoURI(Uri.parse(advise));
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        video.setLayoutParams(params);

        MediaController controller = new MyMediaController(this) {
            @Override
            public void onBackPressed() {
                finish();
            }
        };
        controller.setAnchorView(video);
        video.setMediaController(controller);

        layout.addView(video);
        video.start();
    }

    // Background tasks

    private static class DownloadTask extends ProgressTask<TestActivity,Void,Void,Void> {
        DownloadTask(TestActivity activity) {
            super(activity);
        }

        @Override
        protected Void doInBackground(TestActivity activity, Void... voids) {
            activity.viewModel.loadTest();
            return null;
        }
    }

    private static class UploadTask extends ProgressTask<TestActivity,Void,Void,Void> {
        UploadTask(TestActivity activity) {
            super(activity);
        }

        @Override
        protected Void doInBackground(TestActivity activity, Void... voids) {
            activity.viewModel.send();
            return null;
        }
    }
}
