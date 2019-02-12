package wenjalan.strikezone.android;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.IOException;

import wenjalan.strikezone.R;
import wenjalan.strikezone.hardware.CameraManager;

public class Replay extends AppCompatActivity implements MediaController.MediaPlayerControl, MediaPlayer.OnPreparedListener {

    protected static final String TAG = "SZ-Replay";

    protected MediaPlayer mediaPlayer;

    protected MediaController mediaController;

    // the scale of the strikezone box, in relation to the screen
    public static final int SCREEN2BOXH = 2;
    public static final int SCREEN2BOXW = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mediaController.show();
        return false;
    }

    // initializes the activity
    protected void init() {
        // init VideoView
        initVideoView();

        // init box
        initStrikeZone();
    }

    // initializes the Strike Zone Box
    protected void initStrikeZone() {
        // get the box
        final View strikeZoneBox = findViewById(R.id.ReplayStrikeZoneBox);

        // resize it according to the dimensions of the screen
        // once the initial layout has been completed
        strikeZoneBox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // get the size of the constraint layout this view resides in
                View container = (View) strikeZoneBox.getParent();
                int containerWidth = container.getWidth();
                int containerHeight = container.getHeight();

                // log
                // Log.d(TAG, "Got screen height/width of " + containerHeight + "/" + containerWidth);

                // set the width and height of the box according to the ratio specified
                ViewGroup.LayoutParams params = strikeZoneBox.getLayoutParams();
                params.height = (int) (containerHeight / SCREEN2BOXH);
                params.width = (int) (containerWidth / SCREEN2BOXW);

                // request layout
                strikeZoneBox.requestLayout();
            }
        });

        // request layout
        strikeZoneBox.requestLayout();
    }

    // initializes VideoView
    protected void initVideoView() {
        final TextureView view = findViewById(R.id.TextureView);
        this.mediaPlayer = new MediaPlayer();

        // init MediaController
        mediaController = new MediaController(this);

        mediaPlayer.setOnPreparedListener(this);
        final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/capture.mpg";

        view.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                try {
                    // set up media player
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setSurface(new Surface(surfaceTexture));
                    mediaPlayer.prepare();

                    // resize the view
                    resizeView(view, mediaPlayer);
                } catch (IOException e) {
                    Log.d(TAG, "Failed to load video!");
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });

    }

    // TODO: Change it up a little more?
    protected void resizeView(TextureView textureView, MediaPlayer mediaPlayer) {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) textureView.getLayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        // get dimensions
        final int screenHeight = dm.heightPixels;
        final int screenWidth = dm.widthPixels;
        int videoHeight = mediaPlayer.getVideoHeight();
        int videoWidth = mediaPlayer.getVideoWidth();
        double hRatio = 1;

        // calculate ratio
        hRatio = ((double) screenHeight / videoHeight) / ((double) screenWidth / videoWidth);

        // set dimensions
        params.width = (int) (hRatio <= 1 ? 0 : Math.round((-(hRatio - 1) / 2) * screenWidth));
        params.height = (int) (hRatio >= 1 ? 0 : Math.round((((-1 / hRatio) + 1) / 2) * screenHeight));
        params.width = screenWidth - params.width - params.width;
        params.height = screenHeight - params.height - params.height;

        // request layout
        textureView.setScaleX(1.00001f);
        textureView.requestLayout();
        textureView.invalidate();
    }

    // on MediaPlayer prepared
    @Override
    public void onPrepared(final MediaPlayer mediaPlayer) {
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.ReplayLayout));
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });
        start();
    }

    // MediaController implements
    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
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
        return mediaPlayer.getAudioSessionId();
    }

}
