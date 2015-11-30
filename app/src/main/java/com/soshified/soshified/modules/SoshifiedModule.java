package com.soshified.soshified.modules;

import com.soshified.soshified.Soshified;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = Soshified.class
)
public class SoshifiedModule {

    private Soshified application;

    public SoshifiedModule(Soshified application) {
        this.application = application;
    }

    @Provides
    public Soshified provideApplication() {
        return application;
    }

}
