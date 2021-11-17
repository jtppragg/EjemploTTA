package eus.ehu.tta.ejemplo.model.backend;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.InputStream;

import eus.ehu.tta.ejemplo.model.beans.Choice;
import eus.ehu.tta.ejemplo.model.beans.Exercise;
import eus.ehu.tta.ejemplo.model.beans.Test;
import eus.ehu.tta.ejemplo.model.beans.UserProfile;

public class MockBackend implements Backend {
    private UserProfile user;

    @Override
    public Task<UserProfile> login() {
        user = new UserProfile();
        user.setId("1");
        user.setName("John Doe");
        user.setCurrentLesson(1);
        user.setLessonTitle("Introducción");
        user.setCurrentTest(1);
        user.setCurrentExercise(1);
        user.setPictureUrl("https://upload.wikimedia.org/wikipedia/commons/a/af/Tux.png");
        return Tasks.forResult(user);
    }

    @Override
    public Task<Void> logout() {
        user = null;
        return Tasks.forResult(null);
    }

    @Override
    public UserProfile getUserProfile() {
        return user;
    }

    @Override
    public Task<Test> getTest() {
        Test test = new Test();
        test.setId(user.getCurrentTest());
        if( test.getId() == 1 ) {
            test.setWording("¿Cuál de las siguientes opciones NO se indica en el fichero de manifiesto de la app?");
            addChoice(test, "Permisos de la aplicación", "text/html",
                "http://developer.android.com/guide/topics/manifest/manifest-intro.html");
            addChoice(test, "Listado de componentes de la aplicación", "text/html",
                "The manifest describes the <b>components of the application</b>: the activities, services, broadcast receivers, and content providers that the application is composed of. It names the classes that implement each of the components and publishes their capabilities (for example, which Intent messages they can handle). These declarations let the Android system know what the components are and under what conditions they can be launched.");
            addChoice(test, "Dependencias de otras librerías", null, null);
            addChoice(test, "Icono de la aplicación", "audio/mpeg",
                "https://labtel.ehu.eus/tta/AndroidManifest.mp4");
            addChoice(test, "Identificador único de la aplicación", "video/mp4",
                "https://labtel.ehu.eus/tta/AndroidManifest.mp4");
        } else {
            test.setWording("¿Cuál de las siguientes opciones NO se corresponde con un mecanismo de llamadas asíncronas?");
            addChoice(test, "Callbacks", "text/html", "Los callbacks son el mecanismo más básico de llamdas asíncronas");
            addChoice(test, "CompetableFuture", "text/html", "Las promesas permiten encadenar llamadas asíncronas");
            addChoice(test, "LiveData", "text/html", "LiveData implementa el patrón observer en MVVM");
            addChoice(test, "Llamadas bloqueantes", null, null);
        }
        return Tasks.forResult(test);
    }

    @Override
    public Task<Exercise> getExercise() {
        Exercise ex = new Exercise();
        ex.setId(user.getCurrentExercise());
        if( ex.getId() == 1 )
            ex.setWording("Explica cómo aplicarías el patrón de diseño MVVM en el desarrollo de una app para Android");
        else
            ex.setWording("Explica una opción para gestionar llamadas asíncronas en Android");
        return Tasks.forResult(ex);
    }

    @Override
    public Task<Void> uploadChoice(int choiceId) {
        int nextTest = user.getCurrentTest() + 1;
        if( nextTest > 2 ) {
            nextTest = 1;
            user.setCurrentLesson(user.getCurrentLesson()+1);
        }
        user.setCurrentTest(nextTest);
        return Tasks.forResult(null);
    }

    @Override
    public Task<Void> uploadExercise(InputStream is, String name) {
        int nextExercise = user.getCurrentExercise() + 1;
        if( nextExercise > 2 ) {
            nextExercise = 1;
            user.setCurrentLesson(user.getCurrentLesson()+1);
        }
        user.setCurrentExercise(nextExercise);
        return Tasks.forResult(null);
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
