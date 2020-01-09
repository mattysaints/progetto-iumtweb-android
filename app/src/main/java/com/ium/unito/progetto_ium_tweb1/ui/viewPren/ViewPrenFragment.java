package com.ium.unito.progetto_ium_tweb1.ui.viewPren;

import com.ium.unito.progetto_ium_tweb1.entities.Utente;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.ui.prenRip.RecyclerViewAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ViewPrenFragment extends Fragment {

    private static List<Prenotazione> prenotazioni;
    private static RecyclerView recyclerView;
    private static RecyclerViewAdapter adapter;
    private static final Gson gson = new Gson();
    public static Context baseContext;
    public static View viewLayout;
    private SharedPreferences pref;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_viewpren, container, false);
        String url = "http://10.0.2.2:8080/progetto_ium_tweb2/StoricoPrenotazioni";
        pref = this.getContext().getSharedPreferences("user_information", Context.MODE_PRIVATE);
        String user = pref.getString("username","");
        if(!user.isEmpty()) {
            Map<String, String> par = new HashMap<>();
            Utente u = new Utente(user, null, null);
            par.put("utente", gson.toJson(u, Utente.class));
            try {
                String p = new AsyncHttp().execute(new AsyncHttp.Ajax(url, "POST", par)).get();
                prenotazioni = gson.fromJson(p, new TypeToken<List<Prenotazione>>() {
                }.getType());
                System.out.println("le prenotazioni: " + prenotazioni);

                viewLayout = root.findViewById(R.id.viewLayout);
                recyclerView = root.findViewById(R.id.recyclerView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new RecyclerViewAdapter(prenotazioni, root.getContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setHasFixedSize(true);
                adapter.notifyDataSetChanged();

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast t = new Toast(this.getContext());
            t.setText("Richiesta al server fallita");
            t.show();
        }
        return root;
    }
}