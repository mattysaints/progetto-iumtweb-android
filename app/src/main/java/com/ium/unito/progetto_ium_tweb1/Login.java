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
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Login extends AppCompatActivity {
    private static final Gson gson = new Gson();


    private EditText username;
    private EditText password;
    private TextView result;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username_et);
        password = findViewById(R.id.password_et);
        result = findViewById(R.id.result);
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
        AsyncTask<AsyncHttp.Ajax, Void, String> task = new AsyncHttp();
        task.execute(new AsyncHttp.Ajax("http://10.0.2.2:8080/progetto_ium_tweb2/Login","POST", params));
        Map<String, String> response = null;
        try {
            response = gson.fromJson(task.get(), new TypeToken<Map<String, String>>() {
            }.getType());

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (response != null && response.get("result") != null && Boolean.parseBoolean(response.get("result"))) {
            // result.setText("Autenticazione avvenuta con successo");
            pref = getSharedPreferences("user_information", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("username", username.getText().toString());
            String ad = response.get("admin");
            assert ad!=null;
            editor.putBoolean("admin", ad.equals("true"));
            String osp = response.get("ospite");
            assert osp!=null;
            editor.putBoolean("ospite", osp.equals("true"));
            editor.commit();

            Intent homepageIntent = new Intent(this, Homepage.class);
            homepageIntent.putExtra("username", usr);
            startActivity(homepageIntent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Credenziali inserite non corrette!", Toast.LENGTH_LONG);
            toast.show();
            password.setText("");
        }
    }
}
