package com.ium.unito.progetto_ium_tweb1.utils;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Task che esegue una generica post e restituise il risultato
 */
public class AsyncHttpRequest extends AsyncTask<AsyncHttpRequest.Ajax, Void, String> {
    private static boolean initialized = false;

    public static void initSessionManagement() {
        if (initialized)
            return;

        initialized = true;
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    /**
     * Ottiene il risultato dal server
     *
     * @param ajaxs: contiene url, method e params
     * @return true se il login è avvenuto con successo
     */
    @Override
    protected String doInBackground(Ajax... ajaxs) {
        HttpURLConnection connection = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        String response = null;
        Ajax ajax = ajaxs[0];
        try {
            connection = (HttpURLConnection) ajax.url.openConnection();
            connection.setRequestMethod(ajax.method);
            connection.setDoOutput(true);

            String params;
            if (ajax.params==null)
                params="";
            else
                params = encodeParameters(ajax.params);
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
     * @param params
     */
    private String encodeParameters(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
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
            builder.append(line);
            line = in.readLine();
        }

        return builder.toString();
    }

    public static class Ajax {
        URL url;
        String method;
        public Map<String,String> params;

        public Ajax(@NonNull String url) {
            try {
                this.url = new URL(url) ;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            method="POST";
        }

        public Ajax(@NonNull String url, String method, Map<String, String> params) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if(method==null)
                this.method="POST";
            else
                this.method = method;
            this.params = params;
        }
    }
}