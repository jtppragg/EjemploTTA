package eus.ehu.tta.ejemplo.view.fragments;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import eus.ehu.tta.ejemplo.R;
import eus.ehu.tta.ejemplo.viewmodel.BaseViewModel;

public abstract class BaseFragment extends Fragment {

    protected void onCreatedViewmodel(BaseViewModel viewModel) {
        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            View view = getActivity().findViewById(R.id.progress_spinner);
            view.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
        });
    }

    public Task<Boolean> checkPermission(String perm, String rationale) {
        if( ContextCompat.checkSelfPermission(getContext(), perm) == PackageManager.PERMISSION_GRANTED)
            return Tasks.forResult(true);
        TaskCompletionSource<Boolean> task = new TaskCompletionSource<>();
        if( ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), perm) ) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(R.string.permission_neccesary);
            alert.setMessage(rationale);
            alert.setIcon(android.R.drawable.ic_dialog_info);
            alert.setPositiveButton(R.string.ok, (dialogInterface, i) -> task.setResult(false));
            alert.show();
        } else
            task.setResult(false);
        return task.getTask();
    }

}
