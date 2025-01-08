package com.example.test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Input parameters
        List<String> quasiIdentifiers = Arrays.asList("sex", "age", "race", "marital-status");
        List<String> sensitiveAttributes = Arrays.asList("salary-class");

        String dataFileName = "dataset.csv";
        String hierarchyFolderName = "hierarchy";
        String anonymizedFileDir = getExternalFilesDir(null) + "/anonymized/";

        int k = 5;

        // Run the anonymization process in a background thread
        new Thread(() -> {
            try {
                String anonymizedFilePath = AnonymizationHelper.runAnonymization(
                        quasiIdentifiers,
                        sensitiveAttributes,
                        dataFileName,
                        hierarchyFolderName,
                        anonymizedFileDir,
                        k,
                        this
                );

                runOnUiThread(() -> {
                    Toast.makeText(this, "Anonymized data saved at: " + anonymizedFilePath, Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
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
