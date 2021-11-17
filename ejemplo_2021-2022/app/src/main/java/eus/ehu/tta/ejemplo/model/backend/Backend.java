package eus.ehu.tta.ejemplo.model.backend;

import com.google.android.gms.tasks.Task;

import java.io.InputStream;

import eus.ehu.tta.ejemplo.model.beans.Exercise;
import eus.ehu.tta.ejemplo.model.beans.Test;
import eus.ehu.tta.ejemplo.model.beans.UserProfile;

public interface Backend {
    Task<UserProfile> login();
    Task<Void> logout();

    UserProfile getUserProfile();

    Task<Test> getTest();
    Task<Exercise> getExercise();

    Task<Void> uploadChoice( int choiceId );
    Task<Void> uploadExercise(InputStream is, String name );
}
