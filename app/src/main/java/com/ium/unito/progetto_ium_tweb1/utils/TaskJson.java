package com.ium.unito.progetto_ium_tweb1.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class TaskJson extends AsyncTask<String,Void,String>{

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
