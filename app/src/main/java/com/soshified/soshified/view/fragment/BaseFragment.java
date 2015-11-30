package com.soshified.soshified.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.soshified.soshified.Soshified;
import com.soshified.soshified.modules.PostListModule;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by David on 30/11/15.
 */
public class BaseFragment extends Fragment {

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mObjectGraph = Soshified.getApplication(getActivity()).buildScopedObjectGraph(getModules().toArray());
        mObjectGraph.inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mObjectGraph = null;
    }

    protected List<PostListModule> getModules() {
        return new ArrayList<>();
    }
}
