package eus.ehu.tta.ejemplo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class BaseViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private int loadCount = 0;

    protected void startLoad() {
        loadCount++;
        if( loadCount == 1 )
            loading.setValue(true);
    }

    protected void endLoad() {
        loadCount--;
        if( loadCount == 0 )
            loading.setValue(false);
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }
}
