package com.ium.unito.progetto_ium_tweb1.ui.viewPren;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.entities.Utente;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RecyclerViewAdapterStorico extends RecyclerView.Adapter<RecyclerViewAdapterStorico.MyViewHolder> {

   private List<Prenotazione> prenotazioni;
   private Context context;
   private SharedPreferences pref;
   private static final Gson gson = new Gson();



   public RecyclerViewAdapterStorico(Context context) {
      String url = "http://10.0.2.2:8080/progetto_ium_tweb2/StoricoPrenotazioni";
      pref = context.getSharedPreferences("user_information", Context.MODE_PRIVATE);
      String user = pref.getString("username","");
      if(!user.isEmpty()) {
         Map<String, String> par = new HashMap<>();
         Utente u = new Utente(user, null, null);
         par.put("utente", gson.toJson(u, Utente.class));
         try {
            String p = new AsyncHttpRequest().execute(new AsyncHttpRequest.Ajax(url, "POST", par)).get();
            prenotazioni = gson.fromJson(p, new TypeToken<List<Prenotazione>>() {
            }.getType());
            System.out.println("le prenotazioni: " + prenotazioni);

         } catch (ExecutionException e) {
            e.printStackTrace();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      else {
         Toast t = new Toast(context);
         t.setText("Richiesta al server fallita");
         t.show();
      }
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
      if(prenotazione.getDocente()!=null)
         holder.docente.setText(prenotazione.getDocente().toString());
      else
         holder.docente.setText(R.string.docente_eliminato);
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
          docente = itemView.findViewById(R.id.docente_text_view);
          corso = itemView.findViewById(R.id.corso_text_view);
          giorno = itemView.findViewById(R.id.giorno_text_view);
          ora = itemView.findViewById(R.id.ora_text_view);
         touch_layout = itemView.findViewById(R.id.touch_layout);
         card = itemView.findViewById(R.id.card_row);
      }
   }

}
