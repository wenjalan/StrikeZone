package wenjalan.strikezone.hardware;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

// Manages the Camera functionality through Android
// Supports both Video and Photos
@SuppressWarnings("ALL")
public class CameraManager {

    // debug tag
    protected static final String TAG = "SZ-CameraManager";

    // the default FPS the camera records at
    public static final int FPS = 60;

    // filepath to store the video
    public static final String CAPTURE_FILEPATH = "capture/capture.mpg";

    // the context this CameraManager belongs to
    protected final Context context;

    // the Camera object
    protected Camera camera;

    // the MediaRecorder object
    protected MediaRecorder mediaRecorder;

    // the CameraPreview object
    protected CameraPreview cameraPreview;

    // the FrameLayout for the ViewFinder
    protected FrameLayout frameLayout;

    // the picture callback
    protected Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // pass to the CameraManager's method
            pictureTaken(data, camera);
        }
    };

    // constructor
    public CameraManager(Context context, FrameLayout frameLayout) {
        this.context = context;
        this.frameLayout = frameLayout;
        init();
    }

    // initialization
    protected void init() {
        initCamera();
        initMediaRecorder(); // media recorder relies in Camera, so init Camera first
    }

    // initialization of MediaRecorder
    protected void initMediaRecorder() {
        mediaRecorder = new MediaRecorder();

//        mediaRecorder.setOutputFile(CAPTURE_FILEPATH);
//        mediaRecorder.setCamera(this.camera);
//
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
//
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//
//        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File outputFile = new File(path, "capture.mpg");

        mediaRecorder.setCamera(this.camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mediaRecorder.setProfile(profile);
        mediaRecorder.setOutputFile(outputFile);
//        mediaRecorder.setMaxDuration(5_000_000); // 5 seconds
//        mediaRecorder.setMaxFileSize(5_000_000); // 5 Mb

        try {
            mediaRecorder.prepare();
            Log.d(TAG, "Output file location: " + path.getPath());
        } catch (IOException e) {
            Log.d(TAG, "Failed to initialize MediaRecorder!");
            e.printStackTrace();
        }
    }

    // initialization
    protected void initCamera() {
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
        this.frameLayout.addView(this.cameraPreview);

        // start the camera
        startCamera();
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

    // adds a capture button to this camera
    public void addCaptureButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
            }
        });
    }

    // starts the camera
    public void startCamera() {
        if (camera != null) {
            try {
                camera.startPreview();
            } catch (Exception e) {
                Log.d(TAG, "Failed to start camera!");
                e.printStackTrace();
            }
        }
    }

    // releases the camera and media recorder
    public void release() {
        releaseCamera();
        releaseMediaRecorder();
    }

    // releases the camera
    protected void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
            Log.d(TAG, "Released Camera!");
        }
    }

    // releases the media recorder
    protected void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
            Log.d(TAG, "Released MediaRecorder!");
        }
    }

    // takes a picture
    public void takePicture() {
        camera.takePicture(null, null, pictureCallback);
    }

    // takes a video
    public void startVideo() {
        this.camera.stopPreview();
        this.camera.unlock();
        mediaRecorder.start();
    }

    // stops a video recording
    public void stopVideo() {
        mediaRecorder.stop();
    }

    // called by pictureCallback onPictureTaken
    protected void pictureTaken(byte[] data, Camera camera) {
        // log
        Log.d(TAG, "Picture taken");
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
