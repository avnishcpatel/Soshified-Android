package com.soshified.soshified.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.soshified.soshified.Soshified;
import com.soshified.soshified.modules.PostModule;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Base activity that handles Dagger injections
 */
public class BaseActivity extends AppCompatActivity {

    private ObjectGraph mObjectGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mObjectGraph = Soshified.getApplication(this).buildScopedObjectGraph(getModules().toArray());
        mObjectGraph.inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mObjectGraph = null;
    }

    protected List<PostModule> getModules() {
        return new ArrayList<>();
    }
}
