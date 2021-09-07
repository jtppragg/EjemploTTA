package eus.ehu.tta.ejemplo.view.fragments;

import androidx.fragment.app.Fragment;

import eus.ehu.tta.ejemplo.viewmodel.BaseViewModel;

public abstract class BaseFragment extends Fragment {
    protected void onCreatedViewmodel(BaseViewModel viewModel) {
        viewModel.getLoading().observe(getViewLifecycleOwner(), loading ->
            getActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE.equals(loading))
        );
    }
}
