package eus.ehu.tta.ejemplo.view.fragments;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import eus.ehu.tta.ejemplo.R;
import eus.ehu.tta.ejemplo.view.activities.ActivityFutureLauncher;
import eus.ehu.tta.ejemplo.viewmodel.BaseViewModel;
import java9.util.concurrent.CompletableFuture;

public abstract class BaseFragment extends Fragment {
    private final ActivityFutureLauncher<String, Boolean> launcherPerm = registerForActivityFuture(
        new ActivityResultContracts.RequestPermission());

    protected void onCreatedViewmodel(BaseViewModel viewModel) {
        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            View view = getActivity().findViewById(R.id.progress_spinner);
            view.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
        });
    }

    public final <I,O> ActivityFutureLauncher<I,O> registerForActivityFuture(
        @NonNull ActivityResultContract<I, O> contract
    ) {
        CompletableFuture<O> future = new CompletableFuture<>();
        ActivityResultLauncher<I> launcher = registerForActivityResult(contract, result -> future.complete(result));
        return new ActivityFutureLauncher<>(launcher, future);
    }

    public CompletableFuture<Boolean> requestPermission(String perm, String rationale) {
        if( ContextCompat.checkSelfPermission(getContext(), perm) == PackageManager.PERMISSION_GRANTED)
            return CompletableFuture.completedFuture(true);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if( ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), perm) ) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(R.string.permission_neccesary);
            alert.setMessage(rationale);
            alert.setIcon(android.R.drawable.ic_dialog_info);
            alert.setPositiveButton(R.string.ok, (dialogInterface, i) ->
                launcherPerm.launch(perm).thenAccept(granted -> future.complete(granted)));
            alert.show();
        } else
            launcherPerm.launch(perm).thenAccept(granted -> future.complete(granted));
        return future;
    }
}
