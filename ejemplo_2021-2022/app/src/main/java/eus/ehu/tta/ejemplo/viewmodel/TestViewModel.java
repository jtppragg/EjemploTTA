package eus.ehu.tta.ejemplo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import eus.ehu.tta.ejemplo.model.Locator;
import eus.ehu.tta.ejemplo.model.backend.Backend;
import eus.ehu.tta.ejemplo.model.beans.Choice;
import eus.ehu.tta.ejemplo.model.beans.Test;

public class TestViewModel extends BaseViewModel {
    private int selection = -1;
    private final Backend backend = Locator.getBackend();
    private final LiveData<Test> liveTest;
    private final MutableLiveData<Boolean> liveSent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> liveFinished = new MutableLiveData<>();

    public TestViewModel() {
        liveTest = Transformations.switchMap(
            backend.getUserProfile(),
            user -> {
                MutableLiveData<Test> result = new MutableLiveData<>();
                startLoad();
                backend.getTest().handle((test, ex) -> {
                    endLoad();
                    if( ex != null )
                        ex.printStackTrace();
                    else
                        result.setValue(test);
                    return test;
                });
                return result;
            }
        );
    }

    // Actions

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public void send() {
        Choice choice = getChoice();
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
}