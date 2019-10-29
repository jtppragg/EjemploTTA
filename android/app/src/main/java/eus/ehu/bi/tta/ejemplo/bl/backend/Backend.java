package eus.ehu.bi.tta.ejemplo.bl.backend;

import java.io.IOException;
import java.io.InputStream;

import eus.ehu.bi.tta.ejemplo.bl.beans.Exercise;
import eus.ehu.bi.tta.ejemplo.bl.beans.Test;
import eus.ehu.bi.tta.ejemplo.bl.beans.UserProfile;

public interface Backend {
    void setCredentials(String login, String passwd);

    UserProfile getUserProfile() throws IOException;
    Test getTest(int id) throws IOException;
    Exercise getExercise(int id) throws IOException;

    void uploadChoice(int userId, int choiceId) throws IOException;
    void uploadSolution(int userId, int exId, InputStream is, String name) throws IOException;
}
