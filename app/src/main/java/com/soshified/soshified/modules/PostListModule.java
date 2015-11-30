package com.soshified.soshified.modules;

import com.soshified.soshified.presenter.ArticleListPresenter;
import com.soshified.soshified.presenter.ArticleListPresenterImpl;
import com.soshified.soshified.view.PostListView;
import com.soshified.soshified.view.fragment.NewsListFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = NewsListFragment.class,
        addsTo = SoshifiedModule.class,
        library = true,
        complete = false
)
public class PostListModule {

    private PostListView mArticleListView;

    public PostListModule(PostListView mArticleListView) {
        this.mArticleListView = mArticleListView;
    }

    @Provides
    public PostListView provideView() {
        return mArticleListView;
    }

    @Provides
    public ArticleListPresenter providePresenter(PostListView articleListView) {
        return new ArticleListPresenterImpl(articleListView);
    }
}
