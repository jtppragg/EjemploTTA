package eus.ehu.tta.ejemplo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import eus.ehu.tta.ejemplo.model.Locator;
import eus.ehu.tta.ejemplo.model.backend.Backend;
import eus.ehu.tta.ejemplo.model.beans.Choice;
import eus.ehu.tta.ejemplo.model.beans.Test;

public class TestViewModel extends BaseViewModel {
    private int selection = -1;
    private final Backend backend = Locator.getBackend();
    private final MutableLiveData<Test> liveTest = new MutableLiveData<>();
    private final MutableLiveData<Boolean> liveSent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> liveFinished = new MutableLiveData<>();
    private boolean skipNotification, skipAdvise;

    public TestViewModel() {
        startLoad();
        backend.getTest().handle((test, ex) -> {
            endLoad();
            if( ex != null )
                ex.printStackTrace();
            else
                liveTest.setValue(test);
            return test;
        });
    }

    // Actions

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public void send() {
        Choice choice = getChoice();
        if( choice == null )
            return;
        startLoad();
        backend.uploadChoice(choice.getId()).handle((aVoid, ex) -> {
           endLoad();
           if( ex == null )
               liveSent.setValue(choice.isCorrect());
           else
               ex.printStackTrace();
           return null;
        });
    }

    public void finish() {
        liveFinished.setValue(true);
    }

    // Observation

    public int getSelection() {
        return selection;
    }

    public Choice getChoice() {
        Test test = liveTest.getValue();
        if( test == null || selection == -1 )
            return null;
        return test.getChoices().get(selection);
    }

    public LiveData<Test> getTest() {
        return liveTest;
    }

    public LiveData<Boolean> getSent() {
        return liveSent;
    }

    public LiveData<Boolean> getFinished() {
        return liveFinished;
    }

    public boolean isSkipNotification() {
        return skipNotification;
    }

    public void setSkipNotification(boolean skipNotification) {
        this.skipNotification = skipNotification;
    }

    public boolean isSkipAdvise() {
        return skipAdvise;
    }

    public void setSkipAdvise(boolean skipAdvise) {
        this.skipAdvise = skipAdvise;
    }
}