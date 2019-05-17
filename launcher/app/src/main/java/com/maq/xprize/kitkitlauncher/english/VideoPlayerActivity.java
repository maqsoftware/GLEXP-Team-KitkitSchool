package com.maq.xprize.kitkitlauncher.english;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.maq.kitkitlogger.KitKitLoggerActivity;

/**
 * Created by yshong on 2018. 4. 13..
 */

public class VideoPlayerActivity extends KitKitLoggerActivity implements SurfaceHolder.Callback{
    private MediaPlayer mMediaPlayer;
    private SurfaceView mVideoView;
    private int mCurrentPosition;
    private boolean mbPause;
    private String mVideoFileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Util.hideSystemUI(this);
        setContentView(R.layout.activity_video);

        final ImageButton imageButton = findViewById(R.id.videoCloseButton);
        final TextView textView = findViewById(R.id.installAppButton);

        imageButton.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);

        mVideoView = (SurfaceView)findViewById(R.id.videoSurface);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return false;
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                finish();
                imageButton.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                redirectToPlayStore();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                imageButton.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                imageButton.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                redirectToPlayStore();
            }
        });

        mCurrentPosition = 0;
        mbPause = false;

        mVideoView.getHolder().addCallback(this);


        String video = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            video = bundle.getString("packageName");
        }

        if(video.equals("com.maq.xprize.kitkitschool.english")){
            mVideoFileName = "main_app_demo_eng.mp4";
        }
        else if(video.equals("com.maq.xprize.kitkitlibrary.english")){
            mVideoFileName = "library_demo_eng_.mp4";
        }

        playVideo(mVideoFileName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mbPause = true;
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
            mMediaPlayer.pause();
            if (mCurrentPosition > 10) {
                mCurrentPosition -= 10;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mbPause == true) {
            playVideo(mVideoFileName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void redirectToPlayStore(){
        String packageName = getIntent().getStringExtra("packageName");
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Util.hideSystemUI(this);
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(surfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    private void playVideo(String fileName) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            AssetFileDescriptor fd = getAssets().openFd(fileName);
            mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mMediaPlayer.prepare();

            if (mbPause) {
                mMediaPlayer.seekTo(mCurrentPosition);
            }

            mMediaPlayer.start();
        } catch (Exception e) {
            Log.e("myLog", "" + e);
            e.printStackTrace();
            finish();
        }
    }
}
