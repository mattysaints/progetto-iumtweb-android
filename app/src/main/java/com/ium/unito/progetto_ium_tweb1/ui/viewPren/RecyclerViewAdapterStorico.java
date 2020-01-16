package com.ium.unito.progetto_ium_tweb1.ui.viewPren;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Giorno;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.entities.Slot;
import com.ium.unito.progetto_ium_tweb1.entities.Stato;
import com.ium.unito.progetto_ium_tweb1.entities.Utente;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RecyclerViewAdapterStorico extends RecyclerView.Adapter<RecyclerViewAdapterStorico.MyViewHolder> implements Filterable {

   private List<Prenotazione> prenotazioniVisibili;
   private List<Prenotazione> prenotazioniNonVisibili;
   private Context context;
   private static final Gson gson = new Gson();



   public RecyclerViewAdapterStorico(Context context) {
      String url = "http://10.0.2.2:8080/progetto_ium_tweb2/StoricoPrenotazioni";
      SharedPreferences pref = context.getSharedPreferences("user_information", Context.MODE_PRIVATE);
      String user = pref.getString("username","");
      if(!user.isEmpty()) {
         Map<String, String> par = new HashMap<>();
         Utente u = new Utente(user, null, null);
         par.put("utente", gson.toJson(u, Utente.class));
         try {
            String p = new AsyncHttpRequest().execute(new AsyncHttpRequest.Ajax(url, "POST", par)).get();
            prenotazioniVisibili = gson.fromJson(p, new TypeToken<List<Prenotazione>>() {
            }.getType());
            prenotazioniNonVisibili = new ArrayList<>(0);
            System.out.println("le prenotazioniVisibili: " + prenotazioniVisibili);

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
      final Prenotazione prenotazione = prenotazioniVisibili.get(position);
      if(prenotazione.getDocente().getCognome()!=null && prenotazione.getDocente().getNome()!=null)
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
      return prenotazioniVisibili.get(position);
   }

   @Override
   public int getItemCount() {
      return prenotazioniVisibili.size();
   }
   @Override
   public Filter getFilter() {
      return new Filter() {
         @Override
         protected FilterResults performFiltering(CharSequence charSequence) {
            // formato: <ricerca_docente>_<ricerca_corso>_<indice_ora>_<indice_giorno>_<indice_stato>
            String[] filterString = ((String) charSequence).split("_");
            prenotazioniVisibili.addAll(prenotazioniNonVisibili);
            if (!filterString[0].isEmpty()) {
               String[] docente = filterString[0].split("\\+");
               prenotazioniVisibili.removeIf(prenotazione -> {
                  if (Arrays.stream(docente).noneMatch(elem -> prenotazione.getDocente().toString().toLowerCase().contains(elem.toLowerCase())))
                     return prenotazioniNonVisibili.add(prenotazione);
                  else
                     return false;
               });
            }

            if (!filterString[1].isEmpty())
               prenotazioniVisibili.removeIf(prenotazione -> {
                  if(!prenotazione.getCorso().getTitolo().toLowerCase().contains(filterString[1].toLowerCase()))
                     return prenotazioniNonVisibili.add(prenotazione);
                  else
                     return false;
               });

            int slotIndex = Integer.parseInt(filterString[2]);
            if (slotIndex != 0) {
               Slot[] slots = Slot.values();
               prenotazioniVisibili.removeIf(prenotazione -> {
                  if (prenotazione.getSlot() != slots[slotIndex - 1])
                     return prenotazioniNonVisibili.add(prenotazione);
                  else
                     return false;
               });
            }

            int giornoIndex = Integer.parseInt(filterString[3]);
            if (giornoIndex != 0) {
               Giorno[] giorni = Giorno.values();
               prenotazioniVisibili.removeIf(prenotazione -> {
                  if (prenotazione.getGiorno() != giorni[giornoIndex - 1])
                     return prenotazioniNonVisibili.add(prenotazione);
                  else
                     return false;
               });
            }

            int statoIndex = Integer.parseInt(filterString[4]);
            if (statoIndex != 0) {
               Stato[] stati = Stato.values();
               prenotazioniVisibili.removeIf(prenotazione -> {
                  if (prenotazione.getStato() != stati[statoIndex - 1])
                     return prenotazioniNonVisibili.add(prenotazione);
                  else
                     return false;
               });
            }
            return null;
         }

         @Override
         protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            notifyDataSetChanged();
         }
      };
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
