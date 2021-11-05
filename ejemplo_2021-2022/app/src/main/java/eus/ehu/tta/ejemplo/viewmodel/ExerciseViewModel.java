package eus.ehu.tta.ejemplo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import eus.ehu.tta.ejemplo.model.Locator;
import eus.ehu.tta.ejemplo.model.backend.Backend;
import eus.ehu.tta.ejemplo.model.beans.Exercise;

public class ExerciseViewModel extends BaseViewModel {
    private final Backend backend = Locator.getBackend();
    private final LiveData<Exercise> liveExercise;

    public ExerciseViewModel() {
        liveExercise = Transformations.switchMap(
            backend.getUserProfile(),
            user -> {
                MutableLiveData<Exercise> result = new MutableLiveData<>();
                startLoad();
                backend.getExercise().handle((exer, excep) -> {
                    endLoad();
                    if( excep != null )
                        excep.printStackTrace();
                    else
                        result.setValue(exer);
                    return exer;
                });
                return result;
            }
        );
    }

    public LiveData<Exercise> getExercise() {
        return liveExercise;
    }
}
