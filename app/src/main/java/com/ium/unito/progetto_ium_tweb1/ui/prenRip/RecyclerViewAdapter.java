package com.ium.unito.progetto_ium_tweb1.ui.prenRip;


import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ium.unito.progetto_ium_tweb1.Homepage;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.*;

import java.io.Serializable;
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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Prenotazione prenotazione = prenotazioni.get(position);
        holder.docente.setText(prenotazione.getDocente().toString());
        holder.corso.setText(prenotazione.getCorso().getTitolo());
        holder.giorno.setText(prenotazione.getGiorno().toString());
        holder.ora.setText(prenotazione.getSlot().toString());

        holder.touch_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyItemChanged(holder.getAdapterPosition());
                Intent dettailsIntent = new Intent(context, DettailsActivity.class);
                dettailsIntent.putExtra("prenotazione", getItem(position));
                context.startActivity(dettailsIntent);
            }
        });
    }

    public Prenotazione getItem(int position) {
        return prenotazioni.get(position);
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
        private RelativeLayout touch_layout;

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
