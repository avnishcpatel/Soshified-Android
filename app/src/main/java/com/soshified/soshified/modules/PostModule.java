package com.soshified.soshified.modules;

import com.soshified.soshified.presenter.PostPresenter;
import com.soshified.soshified.presenter.PostPresenterImpl;
import com.soshified.soshified.view.PostView;
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

    private PostView mPostView;

    public PostModule(PostView mPostView) {
        this.mPostView = mPostView;
    }

    @Provides
    public PostView provideView() {
        return mPostView;
    }

    @Provides
    public PostPresenter providePresenter(PostView postView) {
        return new PostPresenterImpl(postView);
    }
}
