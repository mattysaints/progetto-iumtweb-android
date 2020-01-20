package com.ium.unito.progetto_ium_tweb1.ui.viewPren;


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

import com.google.android.material.card.MaterialCardView;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.model.Giorno;
import com.ium.unito.progetto_ium_tweb1.model.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.model.Slot;
import com.ium.unito.progetto_ium_tweb1.model.Stato;
import com.ium.unito.progetto_ium_tweb1.ui.home.StoricoViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoricoAdapter extends RecyclerView.Adapter<StoricoAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private StoricoViewModel storicoViewModel;
    private List<Prenotazione> prenotazioniVisibili;
    private List<Prenotazione> prenotazioniNonVisibili;


    public StoricoAdapter(Context context, StoricoViewModel storicoViewModel) {
        this.context = context;
        this.storicoViewModel = storicoViewModel;

        prenotazioniVisibili = storicoViewModel.getPrenotazioni().getValue();
        prenotazioniNonVisibili = new ArrayList<>();
        // System.out.println("le prenotazioniVisibili: " + prenotazioniVisibili);
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
        if (prenotazione.getDocente().getCognome() != null && prenotazione.getDocente().getNome() != null)
            holder.docente.setText(prenotazione.getDocente().toString());
        else
            holder.docente.setText(R.string.docente_eliminato);
        holder.corso.setText(prenotazione.getCorso().getTitolo());
        holder.giorno.setText(prenotazione.getGiorno().toString());
        holder.ora.setText(prenotazione.getSlot().toString());
        switch (prenotazione.getStato()) {
            case EFFETTUATA:
                holder.card.setStrokeColor(context.getColor(R.color.stato_prenotazione_effettuata));
                break;
            case ATTIVA:
                holder.card.setStrokeColor(context.getColor(R.color.stato_prenotazione_attiva));
                break;
            case DISDETTA:
                holder.card.setStrokeColor(context.getColor(R.color.stato_prenotazione_disdetta));
                break;
        }
        holder.touch_layout.setOnClickListener(view -> {
            notifyItemChanged(holder.getAdapterPosition());
            Intent detailsIntent = new Intent(context, DetailsActivity.class);
            detailsIntent.putExtra("prenotazione", getItem(position));
            context.startActivity(detailsIntent);
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
                        if (!prenotazione.getCorso().getTitolo().toLowerCase().contains(filterString[1].toLowerCase()))
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView docente;
        TextView corso;
        TextView giorno;
        TextView ora;
        RelativeLayout touch_layout;
        MaterialCardView card;

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
