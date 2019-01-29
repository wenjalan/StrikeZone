package wenjalan.strikezone.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import wenjalan.strikezone.R;
import wenjalan.strikezone.hardware.CameraManager;

// Home Activity
public class Home extends AppCompatActivity {

    // tag
    protected static final String TAG = "SZ-Home";

    // the CameraManager
    protected CameraManager cameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraManager.startCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager.releaseCamera();
    }

    // initialization
    protected void init() {
        // find the viewfinder
        FrameLayout viewfinder = findViewById(R.id.viewfinder);

        // initialize CameraManager
        this.cameraManager = new CameraManager(this, viewfinder);
    }

}
