package com.example.weatherappjava;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button mainBtn, voiceBtn;
    private TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text);
        mainBtn = findViewById(R.id.main_btn);
        voiceBtn = findViewById(R.id.voice_btn);
        textResult = findViewById(R.id.text_result);


        mainBtn.setOnClickListener(v -> {
            api();
        });

        voiceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start speaking");
            startActivityForResult(intent, 10);
            Log.d("work", "onClick");
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            editText.setText(text.get(0));
            api();
            Log.d("work", "data is null");
        } else {
            Toast.makeText(this, "Please say the city name", Toast.LENGTH_SHORT).show();
            Log.d("work", "switch else");
        }
    }

    private void api() {
        if (!editText.getText().toString().trim().isEmpty()) {
            String city = editText.getText().toString();
            String key = "9a64353cf5dbd3468a125199cb3ffd2d";
            String url = "https://api.openweathermap.org/data/2.5/weather?q="
                    + city + "&appid="
                    + key + "&lang=ru&units=metric";
            new GetUrlData().execute(url);

        } else {
            Toast.makeText(this, R.string.no_input_text, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetUrlData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textResult.setText("Загрузка...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                textResult.setText(
                        //"Погода: " + jsonObject.getJSONObject("weather").getString("description")
                        "Температура: " + jsonObject.getJSONObject("main").getDouble("temp")
                                + "\n Ветер: " + jsonObject.getJSONObject("wind").getDouble("speed"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}