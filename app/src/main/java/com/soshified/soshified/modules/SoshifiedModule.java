package com.soshified.soshified.modules;

import com.soshified.soshified.SoshifiedApplication;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = SoshifiedApplication.class
)
public class SoshifiedModule {

    private SoshifiedApplication application;

    public SoshifiedModule(SoshifiedApplication application) {
        this.application = application;
    }

    @Provides
    public SoshifiedApplication provideApplication() {
        return application;
    }

}
