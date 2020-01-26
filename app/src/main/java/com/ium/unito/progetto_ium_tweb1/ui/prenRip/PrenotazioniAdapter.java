package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

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

import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.model.Giorno;
import com.ium.unito.progetto_ium_tweb1.model.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.model.Slot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrenotazioniAdapter extends RecyclerView.Adapter<PrenotazioniAdapter.MyViewHolder> implements Filterable, Serializable {
    private Fragment fragment;
    private PrenotazioniViewModel prenotazioniViewModel;
    private List<Prenotazione> prenotazioniVisibili;

    public PrenotazioniAdapter(PrenotazioniFragment fragment, PrenotazioniViewModel prenotazioniViewModel) {
        this.fragment = fragment;
        this.prenotazioniViewModel = prenotazioniViewModel;
        initPrenotazioniVisibili();
    }

    private void initPrenotazioniVisibili() {
        List<Prenotazione> prenotazioni = prenotazioniViewModel.getPrenotazioni().getValue();
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
        holder.docente.setText(prenotazione.getDocente().toString());
        holder.corso.setText(prenotazione.getCorso().getTitolo());
        holder.giorno.setText(prenotazione.getGiorno().toString());
        holder.ora.setText(prenotazione.getSlot().toString());
        holder.touchLayout.setOnClickListener(view -> {
            Intent detailsIntent = new Intent(fragment.getContext(), DetailsActivity.class);
            detailsIntent.putExtra(DetailsActivity.PRENOTAZIONE_EXTRA, getItem(position));
            fragment.startActivityForResult(detailsIntent, DetailsActivity.CODE_PRENOTA);
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
                initPrenotazioniVisibili();

                // formato: <ricerca_docente>_<ricerca_corso>_<indice_ora>_<indice_giorno>
                String[] filterString = ((String) charSequence).split("_");

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
        private RelativeLayout touchLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            docente = itemView.findViewById(R.id.docente_text_view);
            corso = itemView.findViewById(R.id.corso_text_view);
            giorno = itemView.findViewById(R.id.giorno_text_view);
            ora = itemView.findViewById(R.id.ora_text_view);
            touchLayout = itemView.findViewById(R.id.touch_layout);
        }
    }
}
