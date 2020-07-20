package com.example.musicplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class SmartPlayerActivity extends AppCompatActivity
{
    private RelativeLayout relativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerintent;
    private String keeper = "";
    private TextView textView_songname;
    private ImageView pouse,next,previous;
    private Button voicecontrolButton;
    private String mode = "ON";
    private MediaPlayer mediaPlayer;
    private int position;
    private ArrayList<File>mySongs;
    private String song_name;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_player);
        init();
        checkVoiceCommandPermission();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SmartPlayerActivity.this);
        speechRecognizerintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        vaildateReceiveValueaeAndStartPlaying();

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results)
            {
                //has this parameter bundle from this bundle class ,get the voice from the user and convert it into text is return a lot of results but we want the first result
                ArrayList<String> matchesfound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matchesfound != null)
                {
                    // mean we have a lots of command
                    if(mode.equals("ON"))
                    {
                        keeper = matchesfound.get(0);
                        if(keeper.equals("pause the song"))
                        {
                            playAndPauseSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command= " + keeper, Toast.LENGTH_SHORT).show();
                        }
                        else if(keeper.equals("play the song"))
                        {
                            playAndPauseSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command= " + keeper, Toast.LENGTH_SHORT).show();
                        }
                        else if(keeper.equals("play next song"))
                        {
                            playingNextSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command= " + keeper, Toast.LENGTH_SHORT).show();
                        }
                        else if(keeper.equals("play previous song"))
                        {
                            playPreviousSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command= " + keeper, Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(SmartPlayerActivity.this, "Result = " + keeper, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        relativeLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                // action down press on the screen
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerintent);
                        keeper = "";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }
        });
        voicecontrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (mode.equals("ON"))
                {
                    mode = "OFF";
                    voicecontrolButton.setText("Voice Enbled Mode- OFF");
                    relativeLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    mode = "ON";
                    voicecontrolButton.setText("Voice Enbled Mode- ON");
                }
            }
        });
        pouse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                playAndPauseSong();
            }
        });
        previous.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mediaPlayer.getCurrentPosition()>0)
                {
                    playPreviousSong();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mediaPlayer.getCurrentPosition()>0)
                {
                    playingNextSong();
                }
            }
        });
    }

    private void init()
    {
        relativeLayout = findViewById(R.id.ParentRelativeLayout);
        textView_songname = findViewById(R.id.song_name);
        pouse = findViewById(R.id.pause_btn);
        next = findViewById(R.id.next_btn);
        previous = findViewById(R.id.previous_btn);
        voicecontrolButton = findViewById(R.id.control_btn);
    }

    private void vaildateReceiveValueaeAndStartPlaying()
    {
        // receive the data from main activity
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mySongs =(ArrayList) bundle.getParcelableArrayList("song");
        song_name = mySongs.get(position).getName();
        String songName = intent.getStringExtra("songname");
        textView_songname.setText(songName);
        textView_songname.setSelected(true);
        position = bundle.getInt("position",0);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(SmartPlayerActivity.this,uri);
        mediaPlayer.start();
    }

    private void checkVoiceCommandPermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //M=33, marshmallow android version
        {
            if (!(ContextCompat.checkSelfPermission(SmartPlayerActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))// if the permission is granted what we gonna do
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }
    private void playAndPauseSong()
    {
        if (mediaPlayer.isPlaying())
        {
            pouse.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
        }
        else
        {
            pouse.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
    }

    private void playingNextSong()
    {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();
        position = ((position+1)% mySongs.size());
        Uri uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(SmartPlayerActivity.this,uri);
        song_name = mySongs.get(position).toString();
        textView_songname.setText(song_name);
        mediaPlayer.start();
    }
    private void playPreviousSong()
    {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();
        position = ((position-1)<0 ? (mySongs.size()-1):(position-1));
        Uri uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(SmartPlayerActivity.this,uri);
        song_name = mySongs.get(position).toString();
        textView_songname.setText(song_name);
        mediaPlayer.start();
        if (mediaPlayer.isPlaying())
        {
            pouse.setImageResource(R.drawable.ic_pause);
        }
        else
        {
            pouse.setImageResource(R.drawable.ic_play);
        }
    }
}