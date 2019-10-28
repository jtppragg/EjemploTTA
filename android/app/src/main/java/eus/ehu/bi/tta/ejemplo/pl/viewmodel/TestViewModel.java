package eus.ehu.bi.tta.ejemplo.pl.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import eus.ehu.bi.tta.ejemplo.bl.backend.Backend;
import eus.ehu.bi.tta.ejemplo.bl.beans.Test;
import eus.ehu.bi.tta.ejemplo.bl.beans.UserProfile;
import eus.ehu.bi.tta.ejemplo.pl.model.Locator;

public class TestViewModel extends ViewModel {
    private int selection = -1;
    private final Backend backend = Locator.getBackend();
    private final MutableLiveData<Test> liveTest = new MutableLiveData<>();
    private final MutableLiveData<Boolean> liveSent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> liveFinished = new MutableLiveData<>();

    // Actions

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public void loadTest() {
        int testId = Locator.getUserModel().getProfile().getCurrentTest();
        Test test = backend.getTest(testId);
        liveTest.postValue(test);
    }

    public void send() {
        UserProfile user = Locator.getUserModel().getProfile();
        Test.Choice choice = getChoice();
        backend.uploadChoice(user.getId(), choice.getId());
        liveSent.postValue(choice.isCorrect());
    }

    public void finish() {
        liveFinished.setValue(true);
    }

    // Observation

    public int getSelection() {
        return selection;
    }

    public Test.Choice getChoice() {
        Test test = liveTest.getValue();
        if( test == null || selection == -1 )
            return null;
        return test.getChoices().get(selection);
    }

    public LiveData<Test> getTest() {
        return liveTest;
    }

    public LiveData<Boolean> isSent() {
        return liveSent;
    }

    public LiveData<Boolean> isFinished() {
        return liveFinished;
    }
}
