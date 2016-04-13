package com.soshified.soshified;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Application class
 */
public class Soshified extends Application {

    private static Realm realmInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        realmInstance = Realm.getInstance(realmConfiguration);
        Realm.setDefaultConfiguration(realmConfiguration);
    }


    public static Realm getRealmInstance() {
        return realmInstance;
    }
}
