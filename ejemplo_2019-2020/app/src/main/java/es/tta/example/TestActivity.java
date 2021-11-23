package es.tta.example;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;

import java.io.IOException;

import es.tta.example.model.Test;
import es.tta.prof.view.AudioPlayer;
import es.tta.prof.view.ProgressTask;


public class TestActivity extends ModelActivity implements View.OnClickListener {
    private int correct = -1, selected = -1;
    private JSONArray array;
    private LinearLayout layout;
    private AudioPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        layout = ((LinearLayout) findViewById(R.id.test_layout));

        Test test = data.getTest();
        TextView textWording = (TextView)findViewById(R.id.test_wording);
        textWording.setText(test.getWording());
        RadioGroup group = (RadioGroup)findViewById(R.id.test_choices);
        int i = 0;
        for(Test.Choice choice : test.getChoices() ) {
            RadioButton radio = new RadioButton(this);
            radio.setText(choice.getWording());
            radio.setOnClickListener(this);
            group.addView(radio);
            if( choice.isCorrect() )
                correct = i;
            i++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
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

    @Override
    public void onClick(View v) {
        findViewById(R.id.button_send_test)
                .setVisibility(View.VISIBLE);
    }

    public void send( View view ) {
        final RadioGroup group = (RadioGroup)findViewById(R.id.test_choices);
        selected = group.getCheckedRadioButtonId();
        if( selected < 0 )
            return;
        selected = group.indexOfChild(group.findViewById(selected));
        final Test.Choice choice = data.getTest().getChoices().get(selected);
        new ProgressTask<Void>() {
            @Override
            protected Void work() throws Exception {
                server.uploadChoice(data.getUserId(), choice.getId());
                return null;
            }

            @Override
            protected void onFinish(Void result) {
                int choices = group.getChildCount();
                for (int i = 0; i < choices; i++)
                    group.getChildAt(i).setEnabled(false);
                //findViewById(R.id.button_send_test).setEnabled(false);
                layout.removeView(findViewById(R.id.button_send_test));

                group.getChildAt(correct).setBackgroundColor(Color.GREEN);
                if (selected != correct) {
                    group.getChildAt(selected).setBackgroundColor(Color.RED);
                    Toast.makeText(getApplicationContext(), R.string.incorrect, Toast.LENGTH_SHORT).show();
                    if (choice.getAdvise() != null && !choice.getAdvise().isEmpty())
                        findViewById(R.id.button_view_advise).setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(getApplicationContext(), R.string.correct, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    public void advise( View view ) {
        findViewById(R.id.button_view_advise).setEnabled(false);
        //layout.removeView(findViewById(R.id.button_view_advise));
        Test.Choice choice = data.getTest().getChoices().get(selected);
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
            Uri uri = Uri.parse(advise);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            WebView web = new WebView(this);
            //web.loadUrl(advise);
            web.loadData(advise, "text/html", null);
            web.setBackgroundColor(Color.TRANSPARENT);
            web.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            layout.addView(web);
        }
    }

    private void showVideo(String advise) {
        VideoView video = new VideoView(this);
        video.setVideoURI(Uri.parse(advise));
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        video.setLayoutParams(params);

        MediaController controller = new MediaController(this) {
            @Override
            public void hide() {
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if( event.getKeyCode() == KeyEvent.KEYCODE_BACK )
                    finish();
                return super.dispatchKeyEvent(event);
            }
        };
        controller.setAnchorView(video);
        video.setMediaController(controller);

        layout.addView(video);
        video.start();
    }

    private void playAudio(String advise) {
        try {
            player = new AudioPlayer(layout, new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
            player.setAudioUri(Uri.parse(advise));
        } catch (IOException e) {
        }
    }

    @Override
    protected void onStop() {
        if( player != null ) {
            player.release();
            player = null;
        }
        super.onStop();
    }
}