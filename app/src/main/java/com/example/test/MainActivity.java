package com.example.test;

//import android.content.res.AssetManager;
//import android.os.Bundle;
//import android.util.Log;
import android.view.View;
//import android.widget.Button;
//import androidx.appcompat.app.AppCompatActivity;
//import tech.tablesaw.api.Table;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.Arrays;
//import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button readFileButton = findViewById(R.id.readFileButton);
        readFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readCsvFileFromAssets();
            }
        });
        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateCsvFileFromAssets();
            }
        });

    }

    private void readCsvFileFromAssets() {
        AssetManager assetManager = getAssets();
        try (InputStream inputStream = assetManager.open("dataset.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Process each line
                Log.d("FileContent", line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void calculateCsvFileFromAssets() {
        try {
            InputStream inputStream = getAssets().open("dataset.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
            List<String[]> allData = reader.readAll();

            // Process the CSV data
            for (String[] row : allData) {
                // Do something with each row
                Log.d("CSV_DATA", String.join(", ", row));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}





//    private void CalculateCsvFileFromAssets() {
//        AssetManager assetManager = getAssets();
//
//        try (InputStream inputStream = assetManager.open("sample_data.csv");
//             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] values = line.split(","); // Split the line into values
//
//                // Assuming numerical data starts from the second column (index 1)
//                for (int i = 1; i < values.length; i++) {
//                    try {
//                        int num = Integer.parseInt(values[i]);
//                        values[i] = String.valueOf(num * 2); // Multiply by 2
//                    } catch (NumberFormatException e) {
//                        // Handle non-numerical values (e.g., headers)
//                    }
//                }
//
//                // Reconstruct the line with modified values
//                line = String.join(",", values);
//                Log.d("FileContent", line); // Log the modified line
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }




//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import androidx.appcompat.app.AppCompatActivity;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
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
////            @Override
////            public void onClick(View v) {
////                runMondrianAlgorithm();
////            }
//            AssetManager assetManager = getAssets();
//            try (InputStream inputStream = assetManager.open("sample_data.csv")) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Process each line
//                    Log.d("FileContent", line);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//
//    }
//}

//    private void runMondrianAlgorithm() {
//        try {
//            List<String> quasiIdentifiers = Arrays.asList(
//                    "sex", "age", "race", "marital-status", "education", "native-country", "workclass", "occupation"
//            );
//            String dataFilePath = "sample_data.csv";
//            String hierarchyFileDirPath = "path/to/your/hierarchy/";
//            int k = 5;
//
//            Table anonymizedDf = Mondrian.runAnonymize(quasiIdentifiers, dataFilePath, hierarchyFileDirPath, k);
//            // Handle the anonymized data as needed
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}




//import android.content.res.AssetManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;

//
//
//public class MainActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Button readFileButton = findViewById(R.id.readFileButton);
//        readFileButton.setOnClickListener(v -> readCsvFileFromAssets());
//
//        // Call a method to read the file
//        readCsvFileFromAssets();
//    }
//    private void readCsvFileFromAssets() {
////        AssetManager assetManager = getAssets();
////        try (InputStream inputStream = assetManager.open("sample_data.csv")) {
////            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
////            String line;
////            while ((line = reader.readLine()) != null) {
////                // Process each line
////                Log.d("FileContent", line);
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////    }
//
//}
//





