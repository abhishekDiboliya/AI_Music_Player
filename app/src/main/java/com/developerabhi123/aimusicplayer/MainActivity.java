package com.developerabhi123.aimusicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {



    /* created by Abhishek Diboliya */


    private RelativeLayout parent_relative_layout,lowerLayout,upperLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";

    private ImageView logo,pause_btn,next_btn,previous_btn;
    private TextView songName;
    private Button voiceEnabledbtn;
    private String mode ="ON";

    private MediaPlayer mymediaplayer;
    private int position;
    private ArrayList<File> mysongs;
    private String msongname;
    private TextView instructions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_bg));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }


        checkVoiceCommandPermission();


        logo = findViewById(R.id.logo);
        next_btn = findViewById(R.id.next_btn);
        pause_btn = findViewById(R.id.pause_btn);
        previous_btn = findViewById(R.id.previous_btn);
        voiceEnabledbtn = findViewById(R.id.voiceEnabledbtn);
        songName = findViewById(R.id.songName);
        upperLayout = findViewById(R.id.upperLayout);
        lowerLayout = findViewById(R.id.lowerLyout);
        instructions= findViewById(R.id.instructions);



        parent_relative_layout = findViewById(R.id.parent_relative_layout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());



        validateData();
        logo.setBackgroundResource(R.drawable.logo);





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
                speechRecognizer.startListening(speechRecognizerIntent);
                keeper="";

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(matchesFound != null)
                {
                    if(mode.equals("ON"))
                    {
                        keeper = matchesFound.get(0);

                        if(keeper.equals("pause the song"))
                        {
                            playPauseSong();
                            Toast.makeText(MainActivity.this,"Command = "+ keeper,Toast.LENGTH_LONG).show();

                        }
                        else if(keeper.equals("play the song"))
                        {
                            playPauseSong();
                            Toast.makeText(MainActivity.this,"Command = "+ keeper,Toast.LENGTH_LONG).show();

                        }
                        else if(keeper.equals("play next song"))
                        {
                            playNextSong();
                            Toast.makeText(MainActivity.this,"Command = "+ keeper,Toast.LENGTH_LONG).show();

                        }
                        else if(keeper.equals("play previous song"))
                        {
                            playPreviousSong();
                            Toast.makeText(MainActivity.this,"Command = "+ keeper,Toast.LENGTH_LONG).show();

                        }
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



        parent_relative_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper="";
                        break;


                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }
        });



        voiceEnabledbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode.equals("ON")){
                    mode ="OFF";
                    voiceEnabledbtn.setText("Voice Enabled Mode - OFF");
                    lowerLayout.setVisibility(View.VISIBLE);
                    instructions.setVisibility(View.GONE);
                }
                else{
                    mode ="ON";
                    voiceEnabledbtn.setText("Voice Enabled Mode - ON");
                    lowerLayout.setVisibility(View.GONE);
                    instructions.setVisibility(View.VISIBLE);
                }
            }
        });


        pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseSong();
            }
        });




        previous_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mymediaplayer.getCurrentPosition()>0){
                    playPreviousSong();
                }
            }
        });



        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mymediaplayer.getCurrentPosition()>0){
                    playNextSong();
                }
            }
        });

        onsongcomplete();


    }



    private void validateData()
    {
        if( mymediaplayer != null)
        {
            mymediaplayer.stop();
            mymediaplayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mysongs = (ArrayList) bundle.getParcelableArrayList("song");
        msongname = mysongs.get(position).getName();
        String songNames = intent.getStringExtra("name");

        songName.setText(songNames);
        songName.setSelected(true);

        position = bundle.getInt("position",0);
        Uri uri = Uri.parse(mysongs.get(position).toString());


        mymediaplayer = MediaPlayer.create(MainActivity.this, uri);
        mymediaplayer.start();

    }




    private void checkVoiceCommandPermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }


    private  void playPauseSong(){
        logo.setBackgroundResource(R.drawable.four);

        if(mymediaplayer.isPlaying()){
            pause_btn.setImageResource(R.drawable.play);
            mymediaplayer.pause();
        }
        else
        {
            pause_btn.setImageResource(R.drawable.pause);
            mymediaplayer.start();

            logo.setBackgroundResource(R.drawable.five);
        }


    }





    private void playNextSong(){
        mymediaplayer.stop();
        mymediaplayer.pause();
        mymediaplayer.release();


        position = ((position + 1 ) % mysongs.size());

        Uri uri = Uri.parse(mysongs.get(position).toString());

        mymediaplayer = MediaPlayer.create(MainActivity.this,uri);

        msongname = mysongs.get(position).toString();
        songName.setText(msongname);
        mymediaplayer.start();

        logo.setBackgroundResource(R.drawable.three);

        if(mymediaplayer.isPlaying()){
            pause_btn.setImageResource(R.drawable.pause);
        }
        else
        {
            pause_btn.setImageResource(R.drawable.play);

            logo.setBackgroundResource(R.drawable.five);
        }
    }




    private void playPreviousSong()
    {

        mymediaplayer.stop();
        mymediaplayer.pause();
        mymediaplayer.release();

        position = ((position-1)<0 ? (mysongs.size()-1) : (position-1));

        Uri uri = Uri.parse(mysongs.get(position).toString());

        mymediaplayer = MediaPlayer.create(MainActivity.this,uri);

        msongname = mysongs.get(position).toString();
        songName.setText(msongname);
        mymediaplayer.start();

        logo.setBackgroundResource(R.drawable.two);

        if(mymediaplayer.isPlaying()){
            pause_btn.setImageResource(R.drawable.pause);
        }
        else
        {
            pause_btn.setImageResource(R.drawable.play);

            logo.setBackgroundResource(R.drawable.five);
        }

    }


    private void onsongcomplete()
    {
        mymediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mymediaplayer.getCurrentPosition()>0)
                {
                    playNextSong();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateFade(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
/* created by Abhishek Diboliya */
