package view.beerapp.activities.hardware;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import view.beerapp.R;

// Code reference :
//http://www.devexchanges.info/2016/10/reading-barcodeqr-code-using-mobile.html

/**
 * Represents the camera activity used to scan barcodes. The majority of this code is referenced
 * from the source above.
 */
public class BarcodeCameraActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 2;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private TextView barcodeValue;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkForPermission();
    }

    /**
     * Checks if the camera use is permitted on the device and asks for permission if not.
     */
    private void checkForPermission() {
        int perm1 = PermissionChecker.checkSelfPermission(BarcodeCameraActivity.this, "android.permission.CAMERA");

        if (perm1 == PermissionChecker.PERMISSION_GRANTED) {
            scanPicture();
        } else {
            ActivityCompat.requestPermissions(BarcodeCameraActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        }
    }

    /**
     * Receives and reacts to the camera permission result.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    scanPicture();
                } else {
                    // permission denied
                    setResult(Activity.RESULT_CANCELED, new Intent());
                    finish();
                }
                break;
            }
        }
    }

    /**
     * Scan and detect barcode using the mobile vision google api.
     */
    private void scanPicture() {
        setContentView(R.layout.barcode_camera);
        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        barcodeValue = (TextView) findViewById(R.id.barcode_value);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    //noinspection MissingPermission
                    if (ActivityCompat.checkSelfPermission(BarcodeCameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        Detector.Processor processor = new Detector.Processor() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections detections) {
                final SparseArray barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    barcodeValue.post(new Runnable() {
                        @Override
                        public void run() {
                            //Update barcode value to TextView
                            barcodeValue.setText(((Barcode)barcodes.valueAt(0)).displayValue);
                            Intent intent = new Intent();
                            intent.putExtra("BARCODE", ((Barcode)barcodes.valueAt(0)).displayValue);
                            setResult(RESULT_OK, intent);
                            finish();

                        }
                    });
                }
            }
        };

        barcodeDetector.setProcessor(processor);
    }

    /**
     * Called when activity is destroyed to liberate all resources
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) cameraSource.release();
        if (barcodeDetector != null) barcodeDetector.release();
    }
}