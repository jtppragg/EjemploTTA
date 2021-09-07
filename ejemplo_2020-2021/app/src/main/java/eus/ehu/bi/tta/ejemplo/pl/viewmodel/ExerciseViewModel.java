package eus.ehu.bi.tta.ejemplo.pl.viewmodel;

import java.io.IOException;
import java.io.InputStream;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import eus.ehu.bi.tta.ejemplo.bl.backend.Backend;
import eus.ehu.bi.tta.ejemplo.bl.beans.Exercise;
import eus.ehu.bi.tta.ejemplo.pl.model.Locator;
import eus.ehu.bi.tta.ejemplo.pl.model.UserModel;

public class ExerciseViewModel extends ViewModel {
    private final Backend backend = Locator.getBackend();
    private final UserModel user = Locator.getUserModel();
    private final MutableLiveData<Exercise> liveExercise = new MutableLiveData<>();
    private final MutableLiveData<String> finished = new MutableLiveData<>();

    public void loadExercise() {
        int exId = user.getProfile().getCurrentExercise();
        try {
            Exercise exercise = backend.getExercise(exId);
            liveExercise.postValue(exercise);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LiveData<Exercise> getExercise() {
        return liveExercise;
    }

    public void uploadSolution(InputStream is, String name) {
        int userId = user.getProfile().getId();
        int exId = liveExercise.getValue().getId();
        try {
            backend.uploadSolution(userId, exId, is, name);
            finished.postValue(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LiveData<String> isFinished() {
        return finished;
    }
}
