package com.ium.unito.progetto_ium_tweb1.ui.viewPren;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.ui.home.StoricoViewModel;

public class StoricoFragment extends Fragment {

   private static StoricoAdapter adapter;

   private static RecyclerView recyclerView;
   private StoricoViewModel storicoViewModel;
   private Context baseContext;
   private CharSequence lastFilterDocente;
   private CharSequence lastFilterCorso;
   private int lastFilterOra;
   private int lastFilterGiorno;
   private int lastFilterStato;

   public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      FragmentActivity activity = getActivity();
      if (activity != null)
         storicoViewModel = ViewModelProviders.of(activity).get(StoricoViewModel.class);

      View root = inflater.inflate(R.layout.fragment_list_prenotazioni, container, false);
      setHasOptionsMenu(true);
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
      linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
      recyclerView = root.findViewById(R.id.recyclerView);
      recyclerView.setLayoutManager(linearLayoutManager);
      baseContext = root.getContext();
      adapter = new StoricoAdapter(baseContext, storicoViewModel);
      recyclerView.setAdapter(adapter);
      recyclerView.setHasFixedSize(true);
      adapter.notifyDataSetChanged();

      return root;
   }

   @Override
   public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
      inflater.inflate(R.menu.prenrip, menu);
      super.onCreateOptionsMenu(menu, inflater);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == R.id.filtra) {
         AlertDialog.Builder builder = new AlertDialog.Builder(baseContext);
         LayoutInflater inflater = this.getLayoutInflater();
         View dialogView = inflater.inflate(R.layout.dialog_layout, null);
         builder.setView(dialogView);

         final EditText docente = dialogView.findViewById(R.id.docente_et);
         docente.setText(lastFilterDocente);
         final EditText corso = dialogView.findViewById(R.id.corso_et);
         corso.setText(lastFilterCorso);
         final Spinner ora = dialogView.findViewById(R.id.ora_spinner);
         ora.setSelection(lastFilterOra);
         final Spinner giorno = dialogView.findViewById(R.id.giorno_spinner);
         giorno.setSelection(lastFilterGiorno);
         final Spinner stato = dialogView.findViewById(R.id.filter_stato);
         stato.setSelection(lastFilterStato);

         builder.setMessage("Filtra Ripetizioni");
         builder.setPositiveButton("Cerca", (dialogInterface, i) -> {
            lastFilterDocente = docente.getText();
            lastFilterCorso = corso.getText();
            lastFilterOra = (int) ora.getSelectedItemId();
            lastFilterGiorno = (int) giorno.getSelectedItemId();
            lastFilterStato = (int) stato.getSelectedItemId();
            filterPrenotazioni();
            dialogInterface.dismiss();
         });
         builder.setNegativeButton("Cancella", (dialogInterface, i) -> {
            lastFilterDocente = "";
            lastFilterCorso = "";
            lastFilterOra = 0;
            lastFilterGiorno = 0;
            lastFilterStato = 0;
            filterPrenotazioni();
            dialogInterface.dismiss();
         });
         AlertDialog alertDialog = builder.create();
         alertDialog.show();
      }

      return false;
   }

   private void filterPrenotazioni() {
      StringBuilder filterBuilder = new StringBuilder();
      filterBuilder
            .append(lastFilterDocente.toString().replace(" ", "+")).append("_")
            .append(lastFilterCorso).append("_")
            .append(lastFilterOra).append("_")
            .append(lastFilterGiorno).append("_")
            .append(lastFilterStato);
      adapter.getFilter().filter(filterBuilder);
   }
}