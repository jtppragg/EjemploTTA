package eus.ehu.tta.ejemplo.model.backend;

import java.io.InputStream;

import eus.ehu.tta.ejemplo.model.beans.Exercise;
import eus.ehu.tta.ejemplo.model.beans.Test;
import eus.ehu.tta.ejemplo.model.beans.UserProfile;
import java9.util.concurrent.CompletableFuture;

public interface Backend {
    CompletableFuture<UserProfile> login();
    CompletableFuture<Void> logout();

    UserProfile getUserProfile();

    CompletableFuture<Test> getTest();
    CompletableFuture<Exercise> getExercise();

    CompletableFuture<Void> uploadChoice( int choiceId );
    CompletableFuture<Void> uploadExercise(InputStream is, String name );
}
