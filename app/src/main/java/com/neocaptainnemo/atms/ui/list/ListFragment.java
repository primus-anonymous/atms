package com.neocaptainnemo.atms.ui.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neocaptainnemo.atms.R;
import com.neocaptainnemo.atms.app.App;
import com.neocaptainnemo.atms.databinding.FragmentListBinding;
import com.neocaptainnemo.atms.model.AtmNode;
import com.neocaptainnemo.atms.ui.IAtmsView;

import java.util.List;

import javax.inject.Inject;

public class ListFragment extends Fragment implements IAtmsView, AtmAdapter.OnAtmClicked {

    public static final String TAG = "ListFragment";
    @Inject
    AtmAdapter adapter;
    private FragmentListBinding binding;
    private OnAtmSelected atmSelectedListener;

    public static ListFragment instance() {
        return new ListFragment();
    }

    public static IAtmsView onStack(@NonNull FragmentManager manager) {
        Fragment fragment = manager.findFragmentByTag(TAG);
        if (fragment instanceof IAtmsView) {
            return (IAtmsView) fragment;
        }
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAtmSelected) {
            atmSelectedListener = (OnAtmSelected) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        atmSelectedListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((App) getActivity().getApplication()).getAppComponent().inject(this);
        adapter.setAtmListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.atmList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.atmList.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void showAtms(@NonNull List<AtmNode> atmNodes) {
        adapter.add(atmNodes);
        adapter.notifyDataSetChanged();

        if (atmNodes.isEmpty()) {
            binding.empty.setVisibility(View.VISIBLE);
        } else {
            binding.empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void setMyLocation(Location location) {
        adapter.setLocation(location);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clear() {
        adapter.clear();
    }

    @Override
    public void onAtmClicked(@NonNull AtmNode atmNode) {

        if (atmSelectedListener != null) {
            atmSelectedListener.onAtmSelected(atmNode, TAG);
        }

    }


}
