package com.ium.unito.progetto_ium_tweb1.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Task che esegue una generica post e restituise il risultato
 */
public class AsyncHttpPost extends AsyncTask<Void, Void, String> {
    private String url;
    private Map<String, String> parameters;

    /**
     * Costruttore che si vuole nascondere
     */
    private AsyncHttpPost() {
    }

    /**
     * Costruttore
     *
     * @param url: url della post
     */
    public AsyncHttpPost(String url, Map<String, String> parameters) {
        this.url = url;
        this.parameters = parameters;
    }

    /**
     * Ottiene il risultato dal server
     *
     * @param voids: non usati
     * @return true se il login è avvenuto con successo
     */
    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection connection = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        String response = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String params = encodeParameters();
            out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write(params);
            out.flush();

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            response = decodeResponse(in);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null)
                connection.disconnect();
        }
        return response;
    }

    /**
     * Converte la mappa di parametri in una stringa
     *
     * @return stringa di parametri codificati
     * @throws UnsupportedEncodingException: URLEncoder.encode()
     */
    private String encodeParameters() throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    /**
     * Legge dal BufferedReader della connessione http e restituisce il risultato sotto forma di stringa
     *
     * @param in: stream di input della connessione
     * @return stringa di risultato dal server
     * @throws IOException: BufferedReader.readLine()
     */
    private String decodeResponse(BufferedReader in) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line = in.readLine();

        while (line != null) {
            builder.append(line).append("\n");
            line = in.readLine();
        }

        return builder.toString();
    }
}