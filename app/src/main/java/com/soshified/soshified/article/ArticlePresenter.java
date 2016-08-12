package com.soshified.soshified.article;

import com.soshified.soshified.data.Article;
import com.soshified.soshified.data.source.ArticlesRepository;
import com.soshified.soshified.util.DateUtils;
import com.soshified.soshified.util.TextUtils;
import com.soshified.soshified.article.ArticleContract.*;

/**
 * Presenter Implementation to deal with all the 'presenter' stuff for ArticleView
 */
public class ArticlePresenter implements Presenter {

    private View mView;

    public ArticlePresenter(ArticlesRepository articlesRepository, int articleID, View view) {
        mView = view;
        mView.setPresenter(this);
        articlesRepository.getArticleObservable(articleID)
                .subscribe(this::showArticle);
    }

    @Override
    public void showArticle(Article article) {

        mView.setupScroll();
        mView.loadPostContent(article.getPostContent());
        mView.loadHeaderImage(article.getThumbnail());
        mView.loadComments(article.getComments());

        String mTitle = TextUtils.fromHtml(article.getTitle());
        String mDate = DateUtils.parseWordPressFormat(article.getDate());
        mView.loadPostMeta(mTitle, article.getAuthorName(), mDate);

    }
}
