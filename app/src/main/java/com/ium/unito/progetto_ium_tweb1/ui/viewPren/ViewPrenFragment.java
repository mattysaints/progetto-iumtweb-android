package com.ium.unito.progetto_ium_tweb1.ui.viewPren;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ium.unito.progetto_ium_tweb1.R;

public class ViewPrenFragment extends Fragment {

    private ViewPrenViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(ViewPrenViewModel.class);
        View root = inflater.inflate(R.layout.fragment_viewpren, container, false);
        return root;
    }
}