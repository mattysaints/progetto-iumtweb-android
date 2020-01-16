package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Giorno;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.entities.Slot;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements Filterable, Serializable {
   private List<Prenotazione> prenotazioniVisibili;
   private List<Prenotazione> prenotazioniNonVisibili;
   private Context context;
   private final Gson gson = new Gson();

   public RecyclerViewAdapter(Context context) {
      this.context = context;
      String url = "http://10.0.2.2:8080/progetto_ium_tweb2/RipetizioniDisponibili"; //testato e funziona anche la class taskjson

      try {
         String p = new AsyncHttpRequest().execute(new AsyncHttpRequest.Ajax(url, "POST", null)).get();
         prenotazioniVisibili = gson.fromJson(p, new TypeToken<List<Prenotazione>>() {
         }.getType());
         //System.out.println("le prenotazioniNonVisibili: " + prenotazioniNonVisibili);
      } catch (ExecutionException | InterruptedException e) {
         e.printStackTrace();
      }

      prenotazioniNonVisibili = new ArrayList<>(0);
   }

   @NonNull
   @Override
   public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row, parent, false);
      return new MyViewHolder(v);
   }

   @Override
   public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
      final Prenotazione prenotazione = prenotazioniVisibili.get(position);
      holder.docente.setText(prenotazione.getDocente().toString());
      holder.corso.setText(prenotazione.getCorso().getTitolo());
      holder.giorno.setText(prenotazione.getGiorno().toString());
      holder.ora.setText(prenotazione.getSlot().toString());
      holder.touch_layout.setOnClickListener(view -> {
         notifyItemChanged(holder.getAdapterPosition());
         Intent detailsIntent = new Intent(context, DetailsActivity.class);
         detailsIntent.putExtra("prenotazione", getItem(position));
         ((Activity) context).startActivityForResult(detailsIntent, DetailsActivity.PASS_DELETED_ITEM);
      });

   }

   public Prenotazione getItem(int position) {
      return prenotazioniVisibili.get(position);
   }

   @Override
   public int getItemCount() {
      return prenotazioniVisibili.size();
   }

   public void removeItem(int position) {
      prenotazioniVisibili.remove(position);
      notifyItemRemoved(position);
   }

   @Override
   public Filter getFilter() {
      return new Filter() {
         @Override
         protected FilterResults performFiltering(CharSequence charSequence) {
            // formato: <ricerca_docente>_<ricerca_corso>_<indice_ora>_<indice_giorno>
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
            return null;
         }

         @Override
         protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            notifyDataSetChanged();
         }
      };
   }

   public static class MyViewHolder extends RecyclerView.ViewHolder {
      private TextView docente;
      private TextView corso;
      private TextView giorno;
      private TextView ora;
      private RelativeLayout touch_layout;

      public MyViewHolder(@NonNull View itemView) {
         super(itemView);
         docente = itemView.findViewById(R.id.docente_text_view);
         corso = itemView.findViewById(R.id.corso_text_view);
         giorno = itemView.findViewById(R.id.giorno_text_view);
         ora = itemView.findViewById(R.id.ora_text_view);
         touch_layout = itemView.findViewById(R.id.touch_layout);
      }
   }
}
