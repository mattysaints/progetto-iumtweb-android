package com.ium.unito.progetto_ium_tweb1.ui.storico;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
    private Fragment fragment;
    private StoricoViewModel storicoViewModel;
    private List<Prenotazione> prenotazioniVisibili;


    public StoricoAdapter(Fragment fragment, StoricoViewModel storicoViewModel) {
        this.fragment = fragment;
        this.storicoViewModel = storicoViewModel;
        initPrenotazioniVisibili();

        this.storicoViewModel.getPrenotazioni().observe(fragment,
                prenotazioni -> notifyDataSetChanged());
    }

    private void initPrenotazioniVisibili() {
        List<Prenotazione> prenotazioni = storicoViewModel.getPrenotazioni().getValue();
        if (prenotazioni != null)
            prenotazioniVisibili = new ArrayList<>(prenotazioni);
        else
            prenotazioniVisibili = new ArrayList<>();
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
                holder.card.setStrokeColor(fragment.getContext().getColor(R.color.stato_prenotazione_effettuata));
                break;
            case ATTIVA:
                holder.card.setStrokeColor(fragment.getContext().getColor(R.color.stato_prenotazione_attiva));
                break;
            case DISDETTA:
                holder.card.setStrokeColor(fragment.getContext().getColor(R.color.stato_prenotazione_disdetta));
                break;
        }
        holder.touchLayout.setOnClickListener(view -> {
            Intent detailsIntent = new Intent(fragment.getContext(), DetailsActivity.class);
            detailsIntent.putExtra(DetailsActivity.PRENOTAZIONE_EXTRA, getItem(position));
            fragment.startActivityForResult(detailsIntent, DetailsActivity.CODE_STORICO);
        });
    }

    private Prenotazione getItem(int position) {
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
                List<Prenotazione> prenotazioni = storicoViewModel.getPrenotazioni().getValue();
                if (prenotazioni == null)
                    return null;
                prenotazioniVisibili = new ArrayList<>(prenotazioni);

                if (!filterString[0].isEmpty()) {
                    String[] docente = filterString[0].split("\\+");
                    prenotazioniVisibili.removeIf(prenotazione ->
                            Arrays.stream(docente).noneMatch(elem ->
                                    prenotazione.getDocente().toString().toLowerCase().contains(elem.toLowerCase())));
                }

                if (!filterString[1].isEmpty())
                    prenotazioniVisibili.removeIf(prenotazione ->
                            !prenotazione.getCorso().getTitolo().toLowerCase().contains(filterString[1].toLowerCase()));

                int slotIndex = Integer.parseInt(filterString[2]);
                if (slotIndex != 0) {
                    Slot[] slots = Slot.values();
                    prenotazioniVisibili.removeIf(prenotazione ->
                            prenotazione.getSlot() != slots[slotIndex - 1]);
                }

                int giornoIndex = Integer.parseInt(filterString[3]);
                if (giornoIndex != 0) {
                    Giorno[] giorni = Giorno.values();
                    prenotazioniVisibili.removeIf(prenotazione ->
                            prenotazione.getGiorno() != giorni[giornoIndex - 1]);
                }

                int statoIndex = Integer.parseInt(filterString[4]);
                if (statoIndex != 0) {
                    Stato[] stati = Stato.values();
                    prenotazioniVisibili.removeIf(prenotazione ->
                            prenotazione.getStato() != stati[statoIndex - 1]);
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
        RelativeLayout touchLayout;
        MaterialCardView card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            docente = itemView.findViewById(R.id.docente_text_view);
            corso = itemView.findViewById(R.id.corso_text_view);
            giorno = itemView.findViewById(R.id.giorno_text_view);
            ora = itemView.findViewById(R.id.ora_text_view);
            touchLayout = itemView.findViewById(R.id.touch_layout);
            card = itemView.findViewById(R.id.card_row);
        }
    }
}
