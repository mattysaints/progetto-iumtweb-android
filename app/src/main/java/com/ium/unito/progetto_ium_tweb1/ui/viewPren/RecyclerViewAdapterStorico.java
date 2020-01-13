package com.ium.unito.progetto_ium_tweb1.ui.viewPren;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;

import java.util.List;

public class RecyclerViewAdapterStorico extends RecyclerView.Adapter<RecyclerViewAdapterStorico.MyViewHolder> {

   private List<Prenotazione> prenotazioni;
   private Context context;

   public RecyclerViewAdapterStorico(List<Prenotazione> prenotazioni, Context context) {
      this.prenotazioni = prenotazioni;
      this.context = context;
   }

   @NonNull
   @Override
   public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row,parent,false);
      System.out.println(parent.toString()); //test
      return new MyViewHolder(v);
   }

   @Override
   public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
      final Prenotazione prenotazione = prenotazioni.get(position);
      StringBuilder docente = new StringBuilder(prenotazione.getDocente().getNome());
      docente.append(" ");
      docente.append(prenotazione.getDocente().getCognome());
      holder.docente.setText(docente.toString());
      holder.corso.setText(prenotazione.getCorso().getTitolo());
      holder.giorno.setText(prenotazione.getGiorno().toString());
      holder.ora.setText(prenotazione.getSlot().toString());
      switch(prenotazione.getStato()) {
         case effettuata:
            holder.card.setCardBackgroundColor(context.getColor(R.color.stato_prenotazione_effettuata));
            break;
         case attiva:
            holder.card.setBackgroundColor(context.getColor(R.color.stato_prenotazione_attiva));
            break;
         case disdetta:
            holder.card.setBackgroundColor(context.getColor(R.color.stato_prenotazione_disdetta));
            break;
      }
      holder.touch_layout.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            notifyItemChanged(holder.getAdapterPosition());
            Intent detailsIntent = new Intent(context, PopUpDetails.class);
            detailsIntent.putExtra("prenotazione", getItem(position));
            context.startActivity(detailsIntent);
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

      TextView docente;
      TextView corso;
      TextView giorno;
      TextView ora;
      RelativeLayout touch_layout;
      CardView card;

      public MyViewHolder(@NonNull View itemView) {
         super(itemView);
         docente = itemView.findViewById(R.id.DocenteText);
         corso = itemView.findViewById(R.id.CorsoText);
         giorno = itemView.findViewById(R.id.GiornoText);
         ora = itemView.findViewById(R.id.OraText);
         touch_layout = itemView.findViewById(R.id.touch_layout);
         card = itemView.findViewById(R.id.card_row);
      }
   }

}
