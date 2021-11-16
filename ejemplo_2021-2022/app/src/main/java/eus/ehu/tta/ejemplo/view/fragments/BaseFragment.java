package eus.ehu.tta.ejemplo.view.fragments;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import eus.ehu.tta.ejemplo.R;
import eus.ehu.tta.ejemplo.viewmodel.BaseViewModel;
import java9.util.concurrent.CompletableFuture;

public abstract class BaseFragment extends Fragment {

    protected void onCreatedViewmodel(BaseViewModel viewModel) {
        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            View view = getActivity().findViewById(R.id.progress_spinner);
            view.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
        });
    }

    public CompletableFuture<Boolean> checkPermission(String perm, String rationale) {
        if( ContextCompat.checkSelfPermission(getContext(), perm) == PackageManager.PERMISSION_GRANTED)
            return CompletableFuture.completedFuture(true);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if( ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), perm) ) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(R.string.permission_neccesary);
            alert.setMessage(rationale);
            alert.setIcon(android.R.drawable.ic_dialog_info);
            alert.setPositiveButton(R.string.ok, (dialogInterface, i) -> future.complete(false));
            alert.show();
        } else
            future.complete(false);
        return future;
    }

}
