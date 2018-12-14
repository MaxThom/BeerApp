package view.beerapp.activities.hardware;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import view.beerapp.R;

/**
 * Default camera activity used when not scanning barcodes.
 */
public class DefaultCameraActivity extends AppCompatActivity {
    private static final int CAMERA_MEMORY_GROUP_PERMISSION_REQUEST = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String locId;
    String mCurrentPhotoPath;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_camera);

        Bundle data = getIntent().getExtras();
        locId = "";
        if (data != null)
            locId = data.getString("LOCATION");

        checkForPermission();
    }

    /**
     * Checks if the camera use is permitted on the device and asks for permission if not.
     */
    private void checkForPermission() {
        int perm1 = PermissionChecker.checkSelfPermission(DefaultCameraActivity.this, "android.permission.CAMERA");
        int perm2 = PermissionChecker.checkSelfPermission(DefaultCameraActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE");

        if (perm1 == PermissionChecker.PERMISSION_GRANTED && perm2 == PermissionChecker.PERMISSION_GRANTED) {
            if (!locId.equals(""))
                takePicture();
            else {
                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
            }

        } else {
            ActivityCompat.requestPermissions(DefaultCameraActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_MEMORY_GROUP_PERMISSION_REQUEST);
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
            case CAMERA_MEMORY_GROUP_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PermissionChecker.PERMISSION_GRANTED
                        && grantResults[1] == PermissionChecker.PERMISSION_GRANTED) {
                    if (!locId.equals(""))
                        takePicture();
                    else {
                        setResult(Activity.RESULT_CANCELED, new Intent());
                        finish();
                    }
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
     * Creating the activity and imagefile to properly launch the device's camera
     */
    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "view.beerapp.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Result of a successfully taken picture with the camera.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Intent returnIntent = new Intent();
            returnIntent.putExtra("PICTURE", mCurrentPhotoPath);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        }
    }

    /**
     * Created the image file to save on the device and then show on the location/drink/user.
     *
     * @return the image that will be saved on the device.
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String title = locId;
        String imageFileName = "JPEG_" + title + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
