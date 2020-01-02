package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

import android.widget.TextView;
import android.widget.Toolbar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PrenRipViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PrenRipViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }


}