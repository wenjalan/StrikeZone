package wenjalan.strikezone.hardware;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.Log;
import android.view.View;
import android.widget.Button;

// Manages the Camera functionality through Android
@SuppressWarnings("ALL")
public class CameraManager {

    // debug tag
    protected static final String TAG = "SZ-CameraManager";

    // the context this CameraManager belongs to
    protected final Context context;

    // the Camera object
    protected Camera camera;

    // the CameraPreview object
    protected CameraPreview cameraPreview;

    // the picture callback
    protected Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // pass to the CameraManager's method
            pictureTaken(data, camera);
        }
    };

    // constructor
    public CameraManager(Context context) {
        this.context = context;
        init();
    }

    // initialization
    protected void init() {
        // log
        Log.d(TAG, "Initializing Camera...");

        // open camera
        this.camera = getCameraInstance();

        // set orientation to portrait
        setCameraOrientation(90);

        // enable auto focus
        Camera.Parameters par = this.camera.getParameters();
        par.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        this.camera.setParameters(par);

        // create camera preview
        this.cameraPreview = new CameraPreview(this.context, this.camera);
    }

    // sets the orientation of the camera
    protected void setCameraOrientation(int degrees) {
        camera.setDisplayOrientation(degrees);
    }

    // returns the Android Camera instance, or null if an error is encountered
    protected Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            Log.d(TAG, "Failed to open camera!");
            e.printStackTrace();
        }
        return c;
    }

    // called by pictureCallback onPictureTaken
    protected void pictureTaken(byte[] data, Camera camera) {
        // log
        Log.d(TAG, "Picture taken");
    }

    // adds a capture button to this camera
    public void addCaptureButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
            }
        });
    }

    // releases the camera
    public void release() {
        if (camera != null) {
            camera.release();
            camera = null;
            Log.d(TAG, "Released camera!");
        }
    }

    // returns the cameraPreview
    public CameraPreview getCameraPreview() {
        return this.cameraPreview;
    }

    // util method for rotating a bitmap
    protected Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix mat = new Matrix();
        mat.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
    }

}
