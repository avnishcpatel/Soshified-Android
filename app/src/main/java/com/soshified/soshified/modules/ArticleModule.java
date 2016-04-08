package com.soshified.soshified.modules;

import com.soshified.soshified.presenter.ArticlePresenter;
import com.soshified.soshified.presenter.ArticlePresenterImpl;
import com.soshified.soshified.view.ArticleView;
import com.soshified.soshified.view.activity.ArticleViewerActivity;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = ArticleViewerActivity.class,
        addsTo = SoshifiedModule.class,
        library = true,
        complete = false
)
public class ArticleModule {

    private ArticleView mArticleView;

    public ArticleModule(ArticleView mArticleView) {
        this.mArticleView = mArticleView;
    }

    @Provides
    public ArticleView provideView() {
        return mArticleView;
    }

    @Provides
    public ArticlePresenter providePresenter(ArticleView articleView) {
        return new ArticlePresenterImpl(articleView);
    }
}
