package eus.ehu.bi.tta.ejemplo.bl.backend;

import java.io.InputStream;

import eus.ehu.bi.tta.ejemplo.bl.beans.Exercise;
import eus.ehu.bi.tta.ejemplo.bl.beans.Test;
import eus.ehu.bi.tta.ejemplo.bl.beans.UserProfile;

public class EhuBackend implements Backend {
    private final String url;

    public EhuBackend(String url) {
        this.url = url;
    }

    @Override
    public void setCredentials(String login, String passwd) {
    }

    @Override
    public UserProfile getUserProfile() {
        return null;
    }

    @Override
    public Test getTest(int id) {
        return null;
    }

    @Override
    public Exercise getExercise(int id) {
        return null;
    }

    @Override
    public void uploadChoice(int userId, int choiceId) {
    }

    @Override
    public void uploadSolution(int userId, int exId, InputStream is, String name) {
    }
}
