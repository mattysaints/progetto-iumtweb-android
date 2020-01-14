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

import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Giorno;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.entities.Slot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements Filterable, Serializable {
    private List<Prenotazione> prenotazioniFiltered;
    private List<Prenotazione> prenotazioni;
    private Context context;

    public RecyclerViewAdapter(List<Prenotazione> prenotazioniFiltered, Context context) {
        this.prenotazioniFiltered = prenotazioniFiltered;
        this.context = context;
        prenotazioni = new ArrayList<>(prenotazioniFiltered);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Prenotazione prenotazione = prenotazioniFiltered.get(position);
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
        return prenotazioniFiltered.get(position);
    }

    @Override
    public int getItemCount() {
        return prenotazioniFiltered.size();
    }

    public void removeItem(int position) {
        prenotazioni.remove(position);
        prenotazioniFiltered.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                // formato: <ricerca_docente>_<ricerca_corso>_<indice_ora>_<indice_giorno>
                String[] filterString = ((String) charSequence).split("_");
                List<Prenotazione> prenotazioniFiltered = new ArrayList<>(prenotazioni);

                if (!filterString[0].isEmpty()) {
                    String[] docente = filterString[0].split("\\+");
                    prenotazioniFiltered.removeIf(prenotazione ->
                            Arrays.stream(docente).noneMatch(elem ->
                                    prenotazione.getDocente().toString().contains(elem)));
                }

                if (!filterString[1].isEmpty())
                    prenotazioniFiltered.removeIf(prenotazione -> !prenotazione.getCorso().getTitolo().contains(filterString[1]));

                int slotIndex = Integer.parseInt(filterString[2]);
                if (slotIndex != 0) {
                    Slot[] slots = Slot.values();
                    prenotazioniFiltered.removeIf(prenotazione -> prenotazione.getSlot() != slots[slotIndex - 1]);
                }

                int giornoIndex = Integer.parseInt(filterString[3]);
                if (giornoIndex != 0) {
                    Giorno[] giorni = Giorno.values();
                    prenotazioniFiltered.removeIf(prenotazione -> prenotazione.getGiorno() != giorni[giornoIndex - 1]);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = prenotazioniFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                prenotazioniFiltered.clear();
                prenotazioniFiltered.addAll((Collection<? extends Prenotazione>) filterResults.values);
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
