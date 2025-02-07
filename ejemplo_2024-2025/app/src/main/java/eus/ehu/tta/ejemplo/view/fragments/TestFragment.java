package eus.ehu.tta.ejemplo.view.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.io.IOException;

import eus.ehu.tta.ejemplo.R;
import eus.ehu.tta.ejemplo.model.beans.Choice;
import eus.ehu.tta.ejemplo.model.beans.Test;
import eus.ehu.tta.ejemplo.view.media.AudioPlayer;
import eus.ehu.tta.ejemplo.viewmodel.TestViewModel;

public class TestFragment extends BaseFragment implements View.OnClickListener {

    private TestViewModel viewModel;
    private ViewGroup layout;
    private RadioGroup radioGroup;
    private Button buttonSend, buttonAdvise;
    private AudioPlayer audioPlayer;
    private VideoView videoView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(TestViewModel.class);
        onCreatedViewmodel(viewModel);

        View view = inflater.inflate(R.layout.fragment_test, container, false);
        layout = view.findViewById(R.id.test_layout);
        radioGroup = view.findViewById(R.id.test_choices);
        buttonSend = view.findViewById(R.id.button_send_test);
        buttonAdvise = view.findViewById(R.id.button_view_advise);

        buttonSend.setOnClickListener(button -> send());
        buttonAdvise.setOnClickListener(button -> advise());
        
        viewModel.getTest().observe(getViewLifecycleOwner(), test ->
            loadTest(test));
        viewModel.getSent().observe(getViewLifecycleOwner(), correct -> {
            showSolution();
            buttonSend.setEnabled(false);
            if( !viewModel.isSkipNotification()) {
                Toast.makeText(getContext(), correct ? R.string.correct : R.string.incorrect, Toast.LENGTH_SHORT).show();
                viewModel.setSkipNotification(true);
            } if( !correct )
                buttonAdvise.setVisibility(View.VISIBLE);
        });
        viewModel.getFinished().observe(getViewLifecycleOwner(), aBoolean -> {
            loadAdvise();
            buttonAdvise.setEnabled(false);
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if( audioPlayer != null ) {
            viewModel.setPlayerPosition(audioPlayer.getCurrentPosition());
            audioPlayer.release();
            audioPlayer = null;
        } else if( videoView != null ) {
            viewModel.setPlayerPosition(videoView.getCurrentPosition());
            videoView.stopPlayback();
            videoView.setMediaController(null);
            videoView = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if( viewModel.getPlayerPosition() != 0 && audioPlayer == null && videoView == null )
            loadAdvise();
    }

    private void loadTest(Test test) {
        TextView textWording = getView().findViewById(R.id.test_wording);
        textWording.setText(test.getWording());
        for(Choice choice : test.getChoices() ) {
            RadioButton radio = new RadioButton(getContext());
            radio.setText(choice.getAnswer());
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

    private void showSolution() {
        Test test = viewModel.getTest().getValue();
        int i = 0;
        for(Choice choice : test.getChoices() ) {
            RadioButton radio = (RadioButton)radioGroup.getChildAt(i++);
            radio.setEnabled(false);
            if( choice.isCorrect() )
                radio.setBackgroundColor(Color.GREEN);
            else if( radio.isChecked() )
                radio.setBackgroundColor(Color.RED);
        }
    }

    private void advise() {
        viewModel.setSkipAdvise(false);
        viewModel.finish();
    }

    private void loadAdvise() {
        Choice choice = viewModel.getChoice();
        String mime = choice.getAdviseType().toLowerCase();
        if( mime.contains("video") )
            showVideo(choice.getAdvise());
        else if( mime.contains("audio") ) {
            playAudio(choice.getAdvise());
        } else
            showHtml(choice.getAdvise());
    }

    private void send() {
        viewModel.setSkipNotification(false);
        viewModel.send();
    }

    private void showHtml( String advise ) {
        if( advise.substring(0,10).contains("://") ) {
            if( !viewModel.isSkipAdvise()) {
                Uri uri = Uri.parse(advise);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        } else {
            WebView web = new WebView(getContext());
            //web.loadUrl(advise);
            web.loadData(advise, "text/html", null);
            web.setBackgroundColor(Color.TRANSPARENT);
            web.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            layout.addView(web);
        }
    }

    private void playAudio(String advise) {
        TestFragment self = this;
        try {
            audioPlayer = new AudioPlayer(getView()) {
                @Override
                public void onBackPressed() {
                    if( isAdded() )
                        NavHostFragment.findNavController(self).popBackStack();
                }

                @Override
                public void onPrepared(MediaPlayer mp) {
                    super.onPrepared(mp);
                    seekTo(viewModel.getPlayerPosition());
                }
            };
            audioPlayer.setAudioUri(Uri.parse(advise));
        } catch (IOException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void showVideo(String advise) {
        videoView = new VideoView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        videoView.setLayoutParams(params);
        layout.addView(videoView);
        videoView.setVideoURI(Uri.parse(advise));
        videoView.seekTo(viewModel.getPlayerPosition());
        videoView.start();
    }
}