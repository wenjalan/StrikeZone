package wenjalan.strikezone.hardware;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    // tag
    protected static final String TAG = "SZ-CameraPreview";

    // surface holder
    protected SurfaceHolder surfaceHolder;

    // the camera
    protected Camera camera;

    // constructor
    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
        this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    // on surface created
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Failed to start preview!");
            e.printStackTrace();
        }
    }

    // on surface destroyed
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty
    }

    // on surface changed
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (this.surfaceHolder.getSurface() == null) {
            return;
        }

        // stop preview
        try {
            this.camera.stopPreview();
        } catch (Exception e) {
            Log.d(TAG, "Failed to stop preview!");
            e.printStackTrace();
        }

        // start again
        try {
            this.camera.setPreviewDisplay(this.surfaceHolder);
            this.camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error restarting camera preview!");
            e.printStackTrace();
        }
    }

}
