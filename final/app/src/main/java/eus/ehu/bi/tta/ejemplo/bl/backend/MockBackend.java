package eus.ehu.bi.tta.ejemplo.bl.backend;

import java.io.InputStream;

import eus.ehu.bi.tta.ejemplo.bl.beans.Exercise;
import eus.ehu.bi.tta.ejemplo.bl.beans.Test;
import eus.ehu.bi.tta.ejemplo.bl.beans.UserProfile;

public class MockBackend implements Backend {
    @Override
    public void setCredentials(String login, String passwd) {
    }

    @Override
    public UserProfile getUserProfile() {
        UserProfile user = new UserProfile();
        user.setId(1);
        user.setName("John Doe");
        user.setCurrentLesson(1);
        user.setLessonTitle("Introducción");
        user.setPictureUrl("https://upload.wikimedia.org/wikipedia/commons/a/af/Tux.png");
        return user;
    }

    @Override
    public Test getTest(int id) {
        Test test = new Test();
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
        return test;
    }

    @Override
    public Exercise getExercise(int id) {
        Exercise ex = new Exercise();
        ex.setId(id);
        ex.setWording("Explica cómo aplicarías el patrón de diseño MVVM en el desarrollo de una app para Android");
        return ex;
    }

    @Override
    public void uploadChoice(int userId, int choiceId) {
    }

    @Override
    public void uploadSolution(int userId, int exId, InputStream is, String name) {
    }

    private static void addChoice(Test test, String wording, String mime, String advise) {
        Test.Choice choice = new Test.Choice();
        choice.setWording(wording);
        choice.setAdvise(advise);
        Test.ResourceType type = new Test.ResourceType();
        type.setMime(mime);
        choice.setResourceType(type);
        choice.setCorrect(advise == null);
        test.getChoices().add(choice);
    }
}
