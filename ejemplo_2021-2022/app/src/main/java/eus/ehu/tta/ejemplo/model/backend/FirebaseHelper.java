package eus.ehu.tta.ejemplo.model.backend;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

import java9.util.concurrent.CompletableFuture;
import java9.util.concurrent.CompletionException;

public abstract class FirebaseHelper {
    private DatabaseReference db;
    private StorageReference storage;

    protected DatabaseReference dbRef(Object... nodes) {
        if( db == null )
            db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = db;
        for( Object node : nodes )
            child = child.child(node.toString());
        return child;
    }

    protected StorageReference storageRef(Object... nodes) {
        if( storage == null )
            storage = FirebaseStorage.getInstance().getReference();
        StorageReference child = storage;
        for( Object node : nodes )
            child = child.child(node.toString());
        return child;
    }

    protected CompletableFuture<DataSnapshot> getFuture(Query query) {
        CompletableFuture<DataSnapshot> result = new CompletableFuture<>();
        query.get().addOnCompleteListener(task -> {
            if( !task.isSuccessful() )
                result.completeExceptionally(task.getException());
            else
                result.complete(task.getResult());
        });
        return result;
    }

    protected <T> CompletableFuture<T> setFuture(DatabaseReference ref, T value) {
        CompletableFuture<T> result = new CompletableFuture<>();
        ref.setValue(value).addOnCompleteListener(task -> {
            if( !task.isSuccessful() )
                result.completeExceptionally(task.getException());
            else
                result.complete(value);
        });
        return result;
    }

    protected CompletableFuture<Void> uploadFuture(StorageReference ref, InputStream is) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        ref.putStream(is).addOnCompleteListener(task -> {
            if( !task.isSuccessful() )
                result.completeExceptionally(task.getException());
            else
                result.complete(null);
        });
        return result;
    }

    protected void gotoFuture() throws CompletionException {
        throw new CompletionException(new JumpFutureException());
    }

    protected static class JumpFutureException extends Exception {
    }
}
