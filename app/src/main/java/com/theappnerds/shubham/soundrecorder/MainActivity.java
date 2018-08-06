package com.theappnerds.shubham.soundrecorder;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private AVLoadingIndicatorView mAVLoadingIndicatorView;
    private Chronometer chronometer;
    private ImageView startRecord;
    private ImageView stopRecord;
    private ImageView playLastRecordedAudio;
    private ImageView about_menu;
    private SeekBar seekBar;
    private LinearLayout linearLayout_main;
    private TextView tap_on_mic_instruction;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String fileName = null;
    private int lastProgress = 0;
    private Handler mHandler = new Handler();
    private int REQUEST_CODE = 123;
    private boolean isPlaying = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //call to permission handler
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ;
        {
            getPermissionHandler();
        }

        mAVLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicatorView);
        CardView cardViewStartRecording = (CardView) findViewById(R.id.cardViewStartRecording);
        linearLayout_main = (LinearLayout) findViewById(R.id.linearLayout_main);

        chronometer = (Chronometer) findViewById(R.id.chronometerTimer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        ImageView listrecord_menu = (ImageView) findViewById(R.id.listrecord_menu);
        ImageView about_menu = (ImageView) findViewById(R.id.about_menu);

        startRecord = (ImageView) findViewById(R.id.start_record);
        stopRecord = (ImageView) findViewById(R.id.stop_record);
        playLastRecordedAudio = (ImageView) findViewById(R.id.playLastRecordedAudio);

        tap_on_mic_instruction = (TextView) findViewById(R.id.tap_on_mic_instruction);


        listrecord_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordingListActivity.class);
                startActivity(intent);
            }
        });

        about_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
            }
        });

        cardViewStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        playLastRecordedAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying && fileName != null) {
                    isPlaying = true;
                    startPlaying();
                } else {
                    isPlaying = false;
                    stopPlaying();
                }
            }
        });


    }


    private void startRecording() {

        //UI Prepare to start recording
        tap_on_mic_instruction.setVisibility(View.GONE);
        playLastRecordedAudio.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);
        mAVLoadingIndicatorView.setVisibility(View.VISIBLE);
        startRecord.setVisibility(View.GONE);
        stopRecord.setVisibility(View.VISIBLE);


        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/SoundRecorder");
        if (!file.exists()) {
            file.mkdirs();
        }

        fileName = root.getAbsolutePath() + "/SoundRecorder/" + "Rec" + "_" + String.valueOf(System.currentTimeMillis()) + ".mp3";
        //String.valueOf(System.currentTimeMillis()
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastProgress = 0;
        seekBar.setProgress(0);
        stopPlaying();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle("Recording in process...")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("simply go to app for more action."))
                .setAutoCancel(false);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

    }

    private void stopRecording() {

        //UI prepare to stop recording
        tap_on_mic_instruction.setVisibility(View.GONE);
        playLastRecordedAudio.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.VISIBLE);
        mAVLoadingIndicatorView.setVisibility(View.GONE);
        startRecord.setVisibility(View.VISIBLE);
        stopRecord.setVisibility(View.GONE);


        try {
            mRecorder.stop();
            mRecorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRecorder = null;
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        Snackbar.make(linearLayout_main, "Recording saved.", Snackbar.LENGTH_SHORT).show();

    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
        playLastRecordedAudio.setImageResource(R.drawable.icon_pause);

        seekBar.setProgress(lastProgress);
        mPlayer.seekTo(lastProgress);
        seekBar.setMax(mPlayer.getDuration());
        seekUpdation();
        chronometer.start();


        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playLastRecordedAudio.setImageResource(R.drawable.icon_play);
                isPlaying = false;
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mPlayer != null && fromUser) {
                    mPlayer.seekTo(progress);
                    chronometer.setBase(SystemClock.elapsedRealtime() - mPlayer.getCurrentPosition());
                    lastProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void stopPlaying() {
        try {
            mPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlayer = null;
        playLastRecordedAudio.setImageResource(R.drawable.icon_play);
        chronometer.stop();

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    private void seekUpdation() {
        if (mPlayer != null) {
            int mCurrentPosition = mPlayer.getCurrentPosition();
            seekBar.setProgress(mCurrentPosition);
            lastProgress = mCurrentPosition;
        } else {

        }
        mHandler.postDelayed(runnable, 100);
    }


    //Permission Handler
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermissionHandler() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    //handle callback from requestPermission
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length == 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

            } else {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                Snackbar.make(linearLayout_main, "You must give permissions to use this app.", Snackbar.LENGTH_LONG).show();

            }
        }
    }
}

