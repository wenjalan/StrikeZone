package wenjalan.strikezone.android;

// Manages the Camera functionality through Android
// Singleton class
public class CameraManager {

    // the instance of CameraManager
    private static CameraManager cameraManager = null;

    // returns the instance of CameraManager, creating one if none exists
    public static CameraManager getCameraManager() {
        return cameraManager == null ? (cameraManager = new CameraManager()) : cameraManager;
    }

    // private constructor
    private CameraManager() {
        // check if a CameraManager already exists
        if (cameraManager != null) {
            throw new AssertionError("An instance of CameraManager already exists");
        }

    }

}
