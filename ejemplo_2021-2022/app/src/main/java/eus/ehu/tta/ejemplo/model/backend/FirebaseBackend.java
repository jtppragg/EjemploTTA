package eus.ehu.tta.ejemplo.model.backend;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.io.InputStream;

import eus.ehu.tta.ejemplo.model.beans.Exercise;
import eus.ehu.tta.ejemplo.model.beans.Test;
import eus.ehu.tta.ejemplo.model.beans.UserProfile;

public class FirebaseBackend extends FirebaseHelper implements Backend {
    private UserProfile user;

    @Override
    public Task<UserProfile> login() {
        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
        if( fireUser == null )
            return Tasks.forException(new Exception("Not logged"));
        user = new UserProfile();
        user.setId(fireUser.getUid());
        user.setName(fireUser.getDisplayName());
        if( fireUser.getPhotoUrl() != null )
            user.setPictureUrl(fireUser.getPhotoUrl().toString());
        return dbRef("users", fireUser.getUid()).get()
            .continueWithTask( task -> {
                DataSnapshot data = task.getResult();
                if( data.hasChildren() ) {
                    user.setCurrentLesson(data.child("currentLesson").getValue(Integer.class));
                    user.setLessonTitle(data.child("lessonTitle").getValue(String.class));
                    user.setCurrentTest(data.child("currentTest").getValue(Integer.class));
                    user.setCurrentExercise(data.child("currentExercise").getValue(Integer.class));
                    return task;
                } else return dbRef("lessons", 1).get().continueWith(subtask -> {
                    DataSnapshot lesson = subtask.getResult();
                    user.setCurrentLesson(1);
                    user.setLessonTitle(lesson.child("title").getValue(String.class));
                    user.setCurrentTest(1);
                    user.setCurrentExercise(1);
                    return null;
                });
            }).continueWithTask(task -> {
                task.getResult();   // To propagate possible error
                return dbRef("users").child(fireUser.getUid()).setValue(user);
            }).continueWith(task -> {
                task.getResult();   // To propagate possible error
                return user;
            });
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
        return dbRef("tests", user.getCurrentLesson(), user.getCurrentTest()).get()
            .continueWith(task -> {
                DataSnapshot data = task.getResult();
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
    public Task<Exercise> getExercise() {
        return dbRef("exercises", user.getCurrentLesson(), user.getCurrentExercise()).get()
            .continueWith(task -> {
                Exercise ex = task.getResult().getValue(Exercise.class);
                ex.setId(user.getCurrentExercise());
                return ex;
            });
    }

    @Override
    public Task<Void> uploadChoice(int choiceId) {
        return dbRef("testAnswers", user.getId(), user.getCurrentLesson(), user.getCurrentTest()).setValue(choiceId)
            .continueWithTask(task -> dbRef("tests", user.getCurrentLesson(), user.getCurrentTest(), "choices", choiceId, "correct").get())
            .continueWithTask(task -> {
                if( !task.getResult().getValue(Boolean.class) ) // Incorrect
                    return Tasks.forException(new JumpFutureException());
                return dbRef("tests", user.getCurrentLesson()).limitToLast(1).get();
            }).continueWithTask(task -> {
                int lastTest = Integer.parseInt(task.getResult().getChildren().iterator().next().getKey());
                int nextTest = user.getCurrentTest() < lastTest ? user.getCurrentTest()+1 : 1;
                user.setCurrentTest(nextTest);
                return dbRef("users", user.getId(), "currentTest").setValue(nextTest);
            }).continueWith(task -> {
                if( task.isSuccessful() || task.getException() instanceof JumpFutureException )
                    return null;
                return task.getResult();
            });
    }

    @Override
    public Task<Void> uploadExercise(InputStream is, String name) {
        return storageRef("exerciseAnswers", user.getId(), user.getCurrentLesson(), user.getCurrentExercise(), name).putStream(is)
            .continueWithTask(task ->
                dbRef("exercises", user.getCurrentLesson()).limitToLast(1).get())
            .continueWithTask(task -> {
                int lastExercise = Integer.parseInt(task.getResult().getChildren().iterator().next().getKey());
                int nextExercise = user.getCurrentExercise() < lastExercise ? user.getCurrentExercise()+1 : 1;
                user.setCurrentExercise(nextExercise);
                return dbRef("users", user.getId(), "currentExercise").setValue(nextExercise);
            });
    }
}
