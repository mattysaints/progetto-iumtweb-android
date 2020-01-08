package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Corso;
import com.ium.unito.progetto_ium_tweb1.entities.Docente;
import com.ium.unito.progetto_ium_tweb1.entities.Giorno;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.entities.Slot;
import com.ium.unito.progetto_ium_tweb1.entities.Stato;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PrenRipFragment extends Fragment {

    private static List<Prenotazione> prenotazioni;
    private static RecyclerView recyclerView;
    private static RecyclerViewAdapter adapter;
    private static final Gson gson = new Gson();
    public static Context baseContext;
    public static View viewLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_prenrip, container, false);

        String url = "http://10.0.2.2:8080/progetto_ium_tweb2/RipetizioniDisponibili"; //testato e funziona anche la class taskjson

        try {
            String p = new taskJson().execute(url).get();
            prenotazioni = gson.fromJson(p, new TypeToken<List<Prenotazione>>() {
            }.getType());
            System.out.println("le prenotazioni: " + prenotazioni);

            viewLayout = root.findViewById(R.id.viewLayout);
            recyclerView = root.findViewById(R.id.recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new RecyclerViewAdapter(prenotazioni,root.getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            adapter.notifyDataSetChanged();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return root;
    }

    private class taskJson extends AsyncTask<String,Void,String>{

        private String result;

        @Override
        protected String doInBackground(String... strings) {
            result = downloadJsonFile(strings[0]);

            if(result == null){
                Log.d("Download","Download non riuscito");
            }
            return result;
        }

        private String downloadJsonFile(String urlPath){

            StringBuilder stringBuilder = new StringBuilder(); //stringa mutabile

            try{
                URL url = new URL(urlPath);
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

        public String getResult() {
            return result;
        }
    }

}