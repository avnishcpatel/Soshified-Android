package com.soshified.soshified.modules;

import com.soshified.soshified.presenter.PostPresenter;
import com.soshified.soshified.presenter.PostPresenterImpl;
import com.soshified.soshified.view.BasePostView;
import com.soshified.soshified.view.activity.NewsViewerActivity;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = NewsViewerActivity.class,
        addsTo = SoshifiedModule.class,
        library = true,
        complete = false
)
public class PostModule {

    private BasePostView mBasePostView;

    public PostModule(BasePostView mBasePostView) {
        this.mBasePostView = mBasePostView;
    }

    @Provides
    public BasePostView provideView() {
        return mBasePostView;
    }

    @Provides
    public PostPresenter providePresenter(BasePostView mBasePostView) {
        return new PostPresenterImpl(mBasePostView);
    }
}
