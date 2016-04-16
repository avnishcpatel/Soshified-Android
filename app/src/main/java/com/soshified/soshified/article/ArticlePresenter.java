package com.soshified.soshified.article;

import com.soshified.soshified.data.Article;
import com.soshified.soshified.data.source.ArticlesRepository;
import com.soshified.soshified.util.DateUtils;
import com.soshified.soshified.util.TextUtils;

/**
 * Presenter Implementation to deal with all the 'presenter' stuff for ArticleView
 */
public class ArticlePresenter implements ArticleContract.Presenter {

    private ArticleContract.View mView;


    public ArticlePresenter(ArticlesRepository articlesRepository, int articleID, ArticleContract.View view) {
        mView = view;
        mView.setPresenter(this);
        articlesRepository.getArticleObservable(articleID)
                .subscribe(this::showArticle);
    }

    @Override
    public void showArticle(Article article) {

        mView.setupToolbar();
        mView.loadPostContent(article.getPostContent());

        String mTitle = TextUtils.fromHtml(article.getTitle());
        String mDate = DateUtils.parseWordPressFormat(article.getDate());
        mView.loadPostMeta(mTitle, article.getAuthorName(), mDate);

        mView.loadHeaderImage(article.getThumbnail());
    }
}
