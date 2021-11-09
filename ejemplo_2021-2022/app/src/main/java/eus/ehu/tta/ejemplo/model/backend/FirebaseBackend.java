package eus.ehu.tta.ejemplo.model.backend;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

import eus.ehu.tta.ejemplo.model.beans.Exercise;
import eus.ehu.tta.ejemplo.model.beans.Test;
import eus.ehu.tta.ejemplo.model.beans.UserProfile;
import java9.util.concurrent.CompletableFuture;

public class FirebaseBackend implements Backend {
    private DatabaseReference db;
    private StorageReference storage;
    private UserProfile userProfile;

    private DatabaseReference dbRef(Object... nodes) {
        if( db == null )
            db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = db;
        for( Object node : nodes )
            child = child.child(node.toString());
        return child;
    }

    private StorageReference storageRef(Object... nodes) {
        if( storage == null )
            storage = FirebaseStorage.getInstance().getReference();
        StorageReference child = storage;
        for( Object node : nodes )
            child = child.child(node.toString());
        return child;
    }

    private CompletableFuture<DataSnapshot> getFuture(Query query) {
        CompletableFuture<DataSnapshot> result = new CompletableFuture<>();
        query.get().addOnCompleteListener(task -> {
            if( !task.isSuccessful() )
                result.completeExceptionally(task.getException());
            else
                result.complete(task.getResult());
        });
        return result;
    }

    private <T> CompletableFuture<T> setFuture(DatabaseReference ref, T value) {
        CompletableFuture<T> result = new CompletableFuture<>();
        ref.setValue(value).addOnCompleteListener(task -> {
            if( !task.isSuccessful() )
                result.completeExceptionally(task.getException());
            else
                result.complete(value);
        });
        return result;
    }

    private CompletableFuture<Void> uploadFuture(StorageReference ref, InputStream is) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        ref.putStream(is).addOnCompleteListener(task -> {
            if( !task.isSuccessful() )
                result.completeExceptionally(task.getException());
            else
                result.complete(null);
        });
        return result;
    }

    @Override
    public CompletableFuture<UserProfile> login() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if( user == null )
            return CompletableFuture.failedFuture(new Exception("Not logged"));
        userProfile = new UserProfile();
        userProfile.setId(user.getUid());
        userProfile.setName(user.getDisplayName());
        userProfile.setPictureUrl(user.getPhotoUrl().toString());
        return getFuture(dbRef("users", user.getUid())).thenApply(data -> {
            userProfile.setCurrentLesson((Integer)data.child("currentLesson").getValue());
            userProfile.setLessonTitle(data.child("lessonTitle").toString());
            userProfile.setCurrentTest((Integer)data.child("currentTest").getValue());
            userProfile.setCurrentExercise((Integer)data.child("currentExercise").getValue());
            return userProfile;
        }).exceptionallyCompose(throwable -> getFuture(dbRef("lessons", 1)).thenApply(lesson -> {
            userProfile.setCurrentLesson(1);
            userProfile.setLessonTitle(lesson.child("title").getValue().toString());
            userProfile.setCurrentTest(1);
            userProfile.setCurrentExercise(1);
            return null;
        }).thenCompose(o -> setFuture(dbRef("users", user.getUid()), userProfile)));
    }

    @Override
    public CompletableFuture<Void> logout() {
        userProfile = null;
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public UserProfile getUserProfile() {
        return userProfile;
    }

    @Override
    public CompletableFuture<Test> getTest() {
        return getFuture(dbRef("tests", userProfile.getCurrentLesson(), userProfile.getCurrentTest())).thenApply(data -> {
            Test test = data.getValue(Test.class);
            test.setId(userProfile.getCurrentTest());
            while( test.getChoices().remove(null) );
            int i = 0;
            for(DataSnapshot choice : data.child("choices").getChildren())
                test.getChoices().get(i++).setId(Integer.parseInt(choice.getKey()));
            return test;
        });
    }

    @Override
    public CompletableFuture<Exercise> getExercise() {
        return getFuture(dbRef("exercises", userProfile.getCurrentLesson(), userProfile.getCurrentExercise())).thenApply(data -> {
            Exercise ex = data.getValue(Exercise.class);
            ex.setId(userProfile.getCurrentExercise());
            return ex;
        });
    }

    @Override
    public CompletableFuture<Void> uploadChoice(int choiceId) {
        return setFuture(dbRef("testAnswers", userProfile.getId(), userProfile.getCurrentLesson(), userProfile.getCurrentTest()), choiceId)
            .thenRun(() -> {});
    }

    @Override
    public CompletableFuture<Void> uploadExercise(InputStream is, String name) {
        return uploadFuture(storageRef("exerciseAnswers", userProfile.getId(), userProfile.getCurrentLesson(), userProfile.getCurrentExercise(), name), is);
    }
}
