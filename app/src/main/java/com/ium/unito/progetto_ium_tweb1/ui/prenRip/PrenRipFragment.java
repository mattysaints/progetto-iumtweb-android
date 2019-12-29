package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ium.unito.progetto_ium_tweb1.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

public class PrenRipFragment extends Fragment {

    private PrenRipViewModel prenRipViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        prenRipViewModel =
                ViewModelProviders.of(this).get(PrenRipViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prenrip, container, false);
        /*final TextView textView = root.findViewById(R.id.text_gallery);
        prenRipViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        new taskJson().execute();
        return root;
    }

    private class taskJson extends AsyncTask<Void,Void,String>{

        private String result;

        @Override
        protected String doInBackground(Void... voids) {
            result = downloadJsonFile();

            if(result == null){
                Log.d("Download","Download non riuscito");
            }
            return result;
        }

        private String downloadJsonFile(){

            StringBuilder stringBuilder = new StringBuilder(); //stringa mutabile

            try{
                URL url = new URL("http://10.0.2.2:8080/progetto_ium_tweb2/RipetizioniDisponibili");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int response = connection.getResponseCode(); //ERROR 404 ...
                Log.d("response code","The response code is " + response);

                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8")); //codice binario da decifrare e trasformare in String
                BufferedReader reader = new BufferedReader(isr);

                String line = reader.readLine();
                while (line != null){
                    stringBuilder.append(line).append("\n");
                    line = reader.readLine();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("result",stringBuilder.toString());
            return stringBuilder.toString();
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }



}