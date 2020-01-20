package com.ium.unito.progetto_ium_tweb1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private static final Gson gson = new Gson();


    private EditText username;
    private EditText password;
    private TextView result;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username_et);
        password = findViewById(R.id.password_et);
        result = findViewById(R.id.result);

        preferences = getSharedPreferences("user_information", MODE_PRIVATE);
        AsyncHttpRequest.initSessionManagement();
    }

    public void checkAutentication(View view) {
        String usr = username.getText().toString();
        String psw = password.getText().toString();
        String errorString = getResources().getString(R.string.form_error);
        boolean error = false;

        if (TextUtils.isEmpty(usr)) {
            username.setError(errorString);
            error = true;
        }
        if (TextUtils.isEmpty(psw)) {
            password.setError(errorString);
            error = true;
        }
        if (error)
            return;

        Map<String, String> params = new HashMap<>();
        params.put("username", usr);
        params.put("password", psw);
        AsyncTask<AsyncHttpRequest.Ajax, Void, String> task = new AsyncHttpRequest();
        task.execute(new AsyncHttpRequest.Ajax("http://10.0.2.2:8080/progetto_ium_tweb2/Login", "POST", params));
        Map<String, String> response = null;
        try {
            response = gson.fromJson(task.get(), new TypeToken<Map<String, String>>() {
            }.getType());

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (response != null && response.get("result") != null && Boolean.parseBoolean(response.get("result"))) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("admin", Boolean.parseBoolean(response.get("admin")));
            editor.putBoolean("ospite", false);
            editor.putString("username", username.getText().toString());
            editor.apply();

            Intent homepageIntent = new Intent(this, HomepageActivity.class);
            startActivity(homepageIntent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Credenziali inserite non corrette!", Toast.LENGTH_LONG);
            toast.show();
            password.setText("");
        }
    }

    public void autenticationOspite(View view) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("admin", false);
        editor.putBoolean("ospite", true);
        editor.putString("username", "Ospite");
        editor.apply();

        Intent homepageIntent = new Intent(this, HomepageActivity.class);
        startActivity(homepageIntent);
    }
}
