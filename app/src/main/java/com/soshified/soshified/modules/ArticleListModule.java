package com.soshified.soshified.modules;

import com.soshified.soshified.presenter.ArticleListPresenter;
import com.soshified.soshified.presenter.ArticleListPresenterImpl;
import com.soshified.soshified.view.ArticleListView;
import com.soshified.soshified.view.fragment.ArticleListFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = ArticleListFragment.class,
        addsTo = SoshifiedModule.class,
        library = true,
        complete = false
)
public class ArticleListModule {

    private ArticleListView mArticleListView;

    public ArticleListModule(ArticleListView mArticleListView) {
        this.mArticleListView = mArticleListView;
    }

    @Provides
    public ArticleListView provideView() {
        return mArticleListView;
    }

    @Provides
    public ArticleListPresenter providePresenter(ArticleListView articleListView) {
        return new ArticleListPresenterImpl(articleListView);
    }
}
