package com.soshified.soshified;

import android.app.Application;
import android.content.Context;

import com.soshified.soshified.modules.SoshifiedModule;

import dagger.ObjectGraph;


public class SoshifiedApplication extends Application {

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ObjectGraph.create(new SoshifiedModule(this));
        mObjectGraph.inject(this);
    }

    public ObjectGraph buildScopedObjectGraph(Object... modules) {
        return mObjectGraph.plus(modules);
    }

    public static SoshifiedApplication getApplication(Context context) {
        return (SoshifiedApplication) context.getApplicationContext();
    }
}
