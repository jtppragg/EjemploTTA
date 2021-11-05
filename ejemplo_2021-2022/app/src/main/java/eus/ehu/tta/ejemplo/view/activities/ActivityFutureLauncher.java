package eus.ehu.tta.ejemplo.view.activities;

import androidx.activity.result.ActivityResultLauncher;

import java9.util.concurrent.CompletableFuture;

public class ActivityFutureLauncher<I,O> {
    private final ActivityResultLauncher<I> launcher;
    private final CompletableFuture<O> future;

    public ActivityFutureLauncher(ActivityResultLauncher<I> launcher, CompletableFuture<O> future) {
        this.launcher = launcher;
        this.future = future;
    }

    public CompletableFuture<O> launch(I input) {
        launcher.launch(input);
        return future;
    }
}
