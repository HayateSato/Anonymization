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

        Button readFileButton = findViewById(R.id.readFileButton);
        readFileButton.setOnClickListener(v -> readCsvFileFromAssets());

        Button anonymizeButton = findViewById(R.id.anonymizeButton);
        anonymizeButton.setOnClickListener(v -> {
            Log.d(TAG, "Anonymize button clicked");
            if (checkPermission()) {
                Log.d(TAG, "Permission already granted, executing anonymization");
                executeAnonymization();
            } else {
                Log.d(TAG, "Permission not granted, requesting permission");
                requestPermission();
            }
        });
    }




    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                executeAnonymization();
//            } else {
//                Toast.makeText(this, "Permission Denied. Cannot perform anonymization.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted, executing anonymization");
                executeAnonymization();
            } else {
                Log.d(TAG, "Permission denied");
                Toast.makeText(this, "Permission Denied. Cannot perform anonymization.", Toast.LENGTH_SHORT).show();
            }
        }
    }




//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        /////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////

//        Button readFileButton = findViewById(R.id.readFileButton);
//        readFileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                readCsvFileFromAssets();
//            }
//        });

        /////////////////////////////////////////////////////////////////////////

//        Button calculateButton = findViewById(R.id.calculateButton);
//        calculateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                calculateCsvFileFromAssets();
//            }
//        });

        /////////////////////////////////////////////////////////////////////////

//        Button anonymizeButton = findViewById(R.id.anonymizeButton);
//        anonymizeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                executeAnonymization();
//            }
//        });

        /////////////////////////////////////////////////////////////////////////
//    }


//        // Input parameters
//        List<String> quasiIdentifiers = Arrays.asList("sex", "age", "race", "marital-status");
//        List<String> sensitiveAttributes = Arrays.asList("salary-class");
//
//        String dataFileName = "dataset.csv";
//        String hierarchyFolderName = "hierarchy";
//        String anonymizedFileDir = getExternalFilesDir(null) + "/anonymized/";
//
//        int k = 5;
//
//        // Run the anonymization process in a background thread
//        new Thread(() -> {
//            try {
//                String anonymizedFilePath = AnonymizationHelper.runAnonymization(
//                        quasiIdentifiers,
//                        sensitiveAttributes,
//                        dataFileName,
//                        hierarchyFolderName,
//                        anonymizedFileDir,
//                        k,
//                        this
//                );
//
//                runOnUiThread(() -> {
//                    Toast.makeText(this, "Anonymized data saved at: " + anonymizedFilePath, Toast.LENGTH_LONG).show();
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//                runOnUiThread(() -> {
//                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//            }
//        }).start();
//    }

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


    /////////////////////////////////////////////////////////////////////////

    private void calculateCsvFileFromAssets() {
        AssetManager assetManager = getAssets();



    }


    /////////////////////////////////////////////////////////////////////////
    private void executeAnonymization() {
        Log.d("Anonymization", "Beginning og the execution class");
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
                    Log.e("Anonymization", "File not found at: " + anonymizedFilePath);
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

   ////////////////////////////////////////////////////////////////////////

}




//package com.example.test;
//
//import android.content.res.AssetManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import androidx.appcompat.app.AppCompatActivity;
//import tech.tablesaw.api.Table;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.Arrays;
//import java.util.List;
//
//import android.os.Bundle;
//import android.widget.Toast;
//
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Button readFileButton = findViewById(R.id.readFileButton);
//        readFileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                readCsvFileFromAssets();
//            }
//        });
////        Button calculateButton = findViewById(R.id.calculateButton);
////        calculateButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                calculateCsvFileFromAssets();
////            }
////        });
//
//    }
//
//    private void readCsvFileFromAssets() {
//        AssetManager assetManager = getAssets();
//        try (InputStream inputStream = assetManager.open("dataset.csv");
//             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // Process each line
//                Log.d("FileContent", line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//
//
//
//
////    private void calculateCsvFileFromAssets() {
////        AssetManager assetManager = getAssets();
////
////        try (InputStream inputStream = assetManager.open("dataset.csv");
////             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
////
////            String line;
////            while ((line = reader.readLine()) != null) {
////                String[] values = line.split(","); // Split the line into values
////
////                // Assuming numerical data starts from the second column (index 1)
////                for (int i = 1; i < values.length; i++) {
////                    try {
////                        int num = Integer.parseInt(values[i]);
////                        values[i] = String.valueOf(num * 2); // Multiply by 2
////                    } catch (NumberFormatException e) {
////                        // Handle non-numerical values (e.g., headers)
////                    }
////                }
////
////                // Reconstruct the line with modified values
////                line = String.join(",", values);
////                Log.d("FileContent", line); // Log the modified line
////            }
////
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////    }
//
//
//
//}
//
//
//
