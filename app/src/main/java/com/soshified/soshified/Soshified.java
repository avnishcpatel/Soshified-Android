package com.soshified.soshified;

import android.app.Application;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Application class
 */
public class Soshified extends Application {

    private static Realm realmInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        //TODO Create Migrations
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        realmInstance = Realm.getInstance(realmConfiguration);
        Realm.setDefaultConfiguration(realmConfiguration);
    }


    public static Realm getRealmInstance() {
        return realmInstance;
    }
}
