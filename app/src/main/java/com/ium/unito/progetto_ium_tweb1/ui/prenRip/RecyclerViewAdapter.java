package com.ium.unito.progetto_ium_tweb1.ui.prenRip;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.*;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private List<Prenotazione> prenotazioni;
    private Context context;

    public RecyclerViewAdapter(List<Prenotazione> prenotazioni, Context context) {
        this.prenotazioni = prenotazioni;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Prenotazione prenotazione = prenotazioni.get(position);
        StringBuilder docente = new StringBuilder(prenotazione.getDocente().getNome());
        docente.append(prenotazione.getDocente().getCognome());
        holder.docente.setText(docente.toString());
        holder.corso.setText(prenotazione.getCorso().getTitolo());
        switch (prenotazione.getGiorno().toString()) {
            case "lun":
                holder.giorno.setText("Lunedì");
            case "mar":
                holder.giorno.setText("Martedì");
            case "mer":
                holder.giorno.setText("Mercoledì");
            case "gio":
                holder.giorno.setText("Giovedì");
            case "ven":
                holder.giorno.setText("Venerdì");
        }
        switch (prenotazione.getSlot().toString()) {
            case "15":
                holder.ora.setText("15-16");
            case "16":
                holder.ora.setText("16-17");
            case "17":
                holder.ora.setText("17-18");
            case "18":
                holder.ora.setText("18-19");
        }
        holder.touch_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrenRipFragment.sendObject(context,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return prenotazioni.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView docente;
        private TextView corso;
        private TextView giorno;
        private TextView ora;
        private RecyclerView touch_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            docente = itemView.findViewById(R.id.DocenteText);
            corso = itemView.findViewById(R.id.CorsoText);
            giorno = itemView.findViewById(R.id.GiornoText);
            ora = itemView.findViewById(R.id.OraText);
            touch_layout = itemView.findViewById(R.id.touch_layout);
        }
    }

}