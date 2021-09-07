package eus.ehu.tta.ejemplo.model.backend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.InputStream;

import eus.ehu.tta.ejemplo.model.beans.Choice;
import eus.ehu.tta.ejemplo.model.beans.Exercise;
import eus.ehu.tta.ejemplo.model.beans.Test;
import eus.ehu.tta.ejemplo.model.beans.UserProfile;
import java9.util.concurrent.CompletableFuture;

public class MockBackend implements Backend {
    private final MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();

    @Override
    public CompletableFuture<UserProfile> login() {
        UserProfile user = new UserProfile();
        user.setId(1);
        user.setName("John Doe");
        user.setCurrentLesson(1);
        user.setLessonTitle("Introducción");
        user.setCurrentTest(1);
        user.setCurrentExercise(1);
        user.setPictureUrl("https://upload.wikimedia.org/wikipedia/commons/a/af/Tux.png");
        userProfile.setValue(user);
        return CompletableFuture.completedFuture(user);
    }

    @Override
    public CompletableFuture<Void> logout() {
        userProfile.setValue(null);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    @Override
    public CompletableFuture<Test> getTest() {
        Test test = new Test();
        test.setId(userProfile.getValue().getCurrentTest());
        test.setWording(
                "¿Cuál de las siguientes opciones NO se indica en el fichero de manifiesto de la app?");
        addChoice(test, "Permisos de la aplicación", "text/html",
                "http://developer.android.com/guide/topics/manifest/manifest-intro.html");
        addChoice(test, "Listado de componentes de la aplicación", "text/html",
                "<html><body>The manifest describes the <b>components of the application</b>: the activities, services, broadcast receivers, and content providers that the application is composed of. It names the classes that implement each of the components and publishes their capabilities (for example, which Intent messages they can handle). These declarations let the Android system know what the components are and under what conditions they can be launched.</body></html>");
        addChoice(test, "Dependencias de otras librerías", null, null);
        addChoice(test, "Icono de la aplicación", "audio/mpeg",
                "http://u017633.ehu.eus:28080/static/ServidorTta/AndroidManifest.mp4");
        addChoice(test, "Identificador único de la aplicación", "video/mp4",
                "http://u017633.ehu.eus:28080/static/ServidorTta/AndroidManifest.mp4");
        return CompletableFuture.completedFuture(test);
    }

    @Override
    public CompletableFuture<Exercise> getExercise() {
        Exercise ex = new Exercise();
        ex.setId(userProfile.getValue().getCurrentExercise());
        ex.setWording("Explica cómo aplicarías el patrón de diseño MVVM en el desarrollo de una app para Android");
        return CompletableFuture.completedFuture(ex);
    }

    @Override
    public CompletableFuture<Void> uploadChoice(int choiceId) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> uploadExercise(InputStream is, String name) {
        return CompletableFuture.completedFuture(null);
    }

    private static void addChoice(Test test, String wording, String mime, String advise) {
        Choice choice = new Choice();
        choice.setAnswer(wording);
        choice.setAdvise(advise);
        choice.setAdviseType(mime);
        choice.setCorrect(advise == null);
        test.getChoices().add(choice);
    }
}
