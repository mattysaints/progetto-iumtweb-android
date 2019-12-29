package com.ium.unito.progetto_ium_tweb1.ui.viewPren;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewPrenViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ViewPrenViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}