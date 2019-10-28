package eus.ehu.bi.tta.ejemplo.bl.backend;

import java.io.InputStream;

import eus.ehu.bi.tta.ejemplo.bl.beans.Exercise;
import eus.ehu.bi.tta.ejemplo.bl.beans.Test;
import eus.ehu.bi.tta.ejemplo.bl.beans.UserProfile;

public interface Backend {
    void setCredentials(String login, String passwd);

    UserProfile getUserProfile();
    Test getTest(int id);
    Exercise getExercise(int id);

    void uploadChoice(int userId, int choiceId);
    void uploadSolution(int userId, int exId, InputStream is, String name);
}
