package wenjalan.strikezone.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
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
        cameraManager.release();
    }

    // initialization
    protected void init() {
        // find the viewfinder
        FrameLayout viewfinder = findViewById(R.id.ViewFinder);

        // initialize CameraManager
        this.cameraManager = new CameraManager(this, viewfinder);

        // initialize the strike zone box
        initStrikeZoneBox();

        // initialize recording button
        Button captureButton = findViewById(R.id.CaptureButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureCallback(v);
            }
        });
    }

    // initializes the size of the strike zone based on the height of the screen
    // TODO: Write about this in my docs
    protected void initStrikeZoneBox() {
        // find the view to resize
        final View strikeZoneBox = findViewById(R.id.StrikeZoneBox);

        // once the initial layout has been completed
        strikeZoneBox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // get the size of the constraint layout this view resides in
                View container = (View) strikeZoneBox.getParent();
                int containerWidth = container.getWidth();
                int containerHeight = container.getHeight();

                // set the width and height of the box according to the ratio specified
                ViewGroup.LayoutParams params = strikeZoneBox.getLayoutParams();
                params.height = containerHeight / 2;
                params.width = containerWidth / 2;

                // request layout
                strikeZoneBox.requestLayout();
            }
        });

        // find the height of the screen

        // request layout
        strikeZoneBox.requestLayout();
    }

    // the callback for the recording button
    protected void captureCallback(View v) {
        Log.d(TAG, "Starting capture...");
        cameraManager.startVideo();
    }

}
