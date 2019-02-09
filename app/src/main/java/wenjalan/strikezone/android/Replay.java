package wenjalan.strikezone.android;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.VideoView;

import java.io.IOException;

import wenjalan.strikezone.R;
import wenjalan.strikezone.hardware.CameraManager;

public class Replay extends AppCompatActivity {

    protected static final String TAG = "SZ-Replay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // initializes the activity
    protected void init() {
        // init VideoView
        initVideoView();
    }

    // initializes VideoView
    protected void initVideoView() {
        final TextureView view = findViewById(R.id.TextureView);
        final MediaPlayer mediaPlayer = new MediaPlayer();
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

                    mediaPlayer.start();
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

}