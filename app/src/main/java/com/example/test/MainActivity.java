package com.example.test;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.os.Build;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////// Setting up READ BUTTON listener ////////////////////////////////////////////////////
        Button readFileButton = findViewById(R.id.readFileButton);
        readFileButton.setOnClickListener(v -> readCsvFileFromAssets());

        ////// Setting up ANONYMIZE BUTTON listener //////////////////////////////////////////////
        Button anonymizeButton = findViewById(R.id.anonymizeButton);
//        findViewById(R.id.anonymizeButton).setOnClickListener(v -> checkAndRequestPermissions());
//        anonymizeButton.setOnClickListener(v -> {
//            Log.d(TAG, "Anonymize button clicked");
//            if (checkPermission()) {
//                Log.d(TAG, "Permission already granted, executing anonymization");
//                executeAnonymization();
//            } else {
//                Log.d(TAG, "Permission not granted, requesting permission");
//                requestPermission();
//            }
//        });
        findViewById(R.id.anonymizeButton).setOnClickListener(v -> checkAndRequestPermissions());
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO,
                                Manifest.permission.READ_MEDIA_AUDIO
                        }, PERMISSION_REQUEST_CODE);
            } else {
                executeAnonymization();
            }
        } else {
            // For Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                executeAnonymization();
                Log.d(TAG, "(checkAndRequestPermissions) Permission granted, executing anonymization");
            }
        }
    }



    ///////////// CHECK PERMISSION: If permission is already granted, proceed with the task //////////////////////////
//    private boolean checkPermission() {
//        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        Log.d("checkPermission", "Permission check result: " + result);
//        return result == PackageManager.PERMISSION_GRANTED;
//    }
//
//    ///////////// REQUEST PERMISSION: If permission is not granted, request it from the user. /////////////////////
//    private void requestPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//        Log.d("requestPermission", "Request Permission");
//    }


    ///////////// Handle Result: Based on the user's response, either proceed with the task or inform the user that the task cannot be performed due to lack of permission//////////////

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "Permission granted, executing anonymization");
//                executeAnonymization();
//                Toast.makeText(this, "Permission Granted. Perform anonymization in the backend.", Toast.LENGTH_SHORT).show();
//            } else {
//                Log.d(TAG, "Permission denied, Cannot perform anonymization");
//                Toast.makeText(this, "Permission Denied. Cannot perform anonymization.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted, executing anonymization");
                executeAnonymization();
                Toast.makeText(this, "Permission Granted. Performing anonymization.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Permission denied, Cannot perform anonymization");
                Toast.makeText(this, "Permission Denied. Cannot perform anonymization.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    ///////////// READ FILE BUTTON - ACTIVATE ////////////////////////////////////////////////////////////
    private void readCsvFileFromAssets() {
        AssetManager assetManager = getAssets();
        try (InputStream inputStream = assetManager.open("dataset.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null && lineCount < 5) {
                // Process each line
                Log.d("CSV_Content", line);
                lineCount++;
            }
            Toast.makeText(this, "CSV file read successfully in the backend", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("CSV_Error", "Error reading CSV file", e);
            Toast.makeText(this, "Error reading CSV file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    //////////// ANONYMIZE BUTTON - ACTIVATE /////////////////////////////////////////////////////////////
    private void executeAnonymization() {
        Log.d("Anonymization", "Beginning of the execution class");
        List<String> quasiIdentifiers = Arrays.asList("sex", "age", "race", "marital-status");
        List<String> sensitiveAttributes = Arrays.asList("salary-class");

        String dataFileName = "dataset.csv";
        String hierarchyFolderName = "hierarchy";
        String anonymizedFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/";

        int k = 5;

        new Thread(() -> {
            try {
                Log.d("Anonymization", "Starting anonymization process");
                String anonymizedFilePath = AnonymizationHelper.runAnonymization(
                        quasiIdentifiers,
                        sensitiveAttributes,
                        dataFileName,
                        hierarchyFolderName,
                        anonymizedFileDir,
                        k,
                        this
                );

                File file = new File(anonymizedFilePath);
                if (file.exists()) {
                    Log.d("Anonymization", "File created successfully at: " + anonymizedFilePath);
                    Log.d("Anonymization", "File size: " + file.length() + " bytes");
                } else {
                    Log.e("Anonymization", "File not found at : " + anonymizedFilePath);
                }

                runOnUiThread(() -> {
                    String message = file.exists() ?
                            "Anonymized data saved at: " + anonymizedFilePath :
                            "Failed to create file at: " + anonymizedFilePath;
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                Log.e("Anonymization", "Error during anonymization", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

}


