package eus.ehu.tta.ejemplo.model.backend;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.io.InputStream;

import eus.ehu.tta.ejemplo.model.beans.Exercise;
import eus.ehu.tta.ejemplo.model.beans.Test;
import eus.ehu.tta.ejemplo.model.beans.UserProfile;
import java9.util.concurrent.CompletableFuture;
import java9.util.concurrent.CompletionException;

public class FirebaseBackend extends FirebaseHelper implements Backend {
    private UserProfile user;

    @Override
    public CompletableFuture<UserProfile> login() {
        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
        if( fireUser == null )
            return CompletableFuture.failedFuture(new Exception("Not logged"));
        user = new UserProfile();
        user.setId(fireUser.getUid());
        user.setName(fireUser.getDisplayName());
        user.setPictureUrl(fireUser.getPhotoUrl().toString());
        return getFuture(dbRef("users", fireUser.getUid())).thenApply(data -> {
            user.setCurrentLesson((Integer)data.child("currentLesson").getValue());
            user.setLessonTitle(data.child("lessonTitle").toString());
            user.setCurrentTest((Integer)data.child("currentTest").getValue());
            user.setCurrentExercise((Integer)data.child("currentExercise").getValue());
            return user;
        }).exceptionallyCompose(throwable -> getFuture(dbRef("lessons", 1)).thenApply(lesson -> {
            user.setCurrentLesson(1);
            user.setLessonTitle(lesson.child("title").getValue().toString());
            user.setCurrentTest(1);
            user.setCurrentExercise(1);
            return null;
        }).thenCompose(o -> setFuture(dbRef("users", fireUser.getUid()), user)));
    }

    @Override
    public CompletableFuture<Void> logout() {
        user = null;
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public UserProfile getUserProfile() {
        return user;
    }

    @Override
    public CompletableFuture<Test> getTest() {
        return getFuture(dbRef("tests", user.getCurrentLesson(), user.getCurrentTest())).thenApply(data -> {
            Test test = data.getValue(Test.class);
            test.setId(user.getCurrentTest());
            while( test.getChoices().remove(null) );
            int i = 0;
            for(DataSnapshot choice : data.child("choices").getChildren())
                test.getChoices().get(i++).setId(Integer.parseInt(choice.getKey()));
            return test;
        });
    }

    @Override
    public CompletableFuture<Exercise> getExercise() {
        return getFuture(dbRef("exercises", user.getCurrentLesson(), user.getCurrentExercise())).thenApply(data -> {
            Exercise ex = data.getValue(Exercise.class);
            ex.setId(user.getCurrentExercise());
            return ex;
        });
    }

    @Override
    public CompletableFuture<Void> uploadChoice(int choiceId) {
        return setFuture(dbRef("testAnswers", user.getId(), user.getCurrentLesson(), user.getCurrentTest()), choiceId)
            .thenCompose(data -> getFuture(dbRef("tests", user.getCurrentLesson(), user.getCurrentTest(), "choices", choiceId, "correct")))
            .thenCompose(data -> {
                if( !data.getValue(Boolean.class) ) // Incorrect
                    gotoFuture();
                return getFuture(dbRef("tests", user.getCurrentLesson()).limitToLast(1));
            }).thenCompose(data -> {
                int lastTest = Integer.parseInt(data.getChildren().iterator().next().getKey());
                int nextTest = user.getCurrentTest() < lastTest ? user.getCurrentTest()+1 : 1;
                user.setCurrentTest(nextTest);
                return setFuture(dbRef("users", user.getId(), "currentTest"), nextTest);
            }).handle((data, ex) -> {
                if( ex == null || ex.getCause() instanceof JumpFutureException)
                    return null;
                throw new CompletionException(ex);
            });
    }

    @Override
    public CompletableFuture<Void> uploadExercise(InputStream is, String name) {
        return uploadFuture(storageRef("exerciseAnswers", user.getId(), user.getCurrentLesson(), user.getCurrentExercise(), name), is)
            .thenCompose(data -> getFuture(dbRef("exercises", user.getCurrentLesson()).limitToLast(1)))
            .thenCompose(data -> {
                int lastExercise = Integer.parseInt(data.getChildren().iterator().next().getKey());
                int nextExercise = user.getCurrentExercise() < lastExercise ? user.getCurrentExercise()+1 : 1;
                user.setCurrentExercise(nextExercise);
                return setFuture(dbRef("users", user.getId(), "currentExercise"), nextExercise);
            }).thenRun(() -> {});
    }
}
