package com.soshified.soshified.article;

import com.soshified.soshified.data.Article;
import com.soshified.soshified.util.DateUtils;
import com.soshified.soshified.util.ParseContent;
import com.soshified.soshified.util.TextUtils;

/**
 * Presenter Implementation to deal with all the 'presenter' stuff for ArticleView
 */
public class ArticlePresenterImpl implements ArticlePresenter {

    ArticleView mView;
    Article mArticle;

    public ArticlePresenterImpl(ArticleView view) {
        this.mView = view;
    }

    @Override
    public void init(Article article) {
        this.mArticle = article;
        parsePost(mArticle.content);
        parseMeta();

        mView.initToolbar();
        mView.loadHeaderImage(mArticle.mThumbnail);
    }

    @Override
    public void parsePost(String postContent) {
        new ParseContent(mParsedPostContent -> mView.loadPostContent(mParsedPostContent)).execute(postContent);
    }

    @Override
    public void parseMeta() {
        String mTitle = TextUtils.fromHtml(mArticle.title);
        String mDate = DateUtils.parseWordPressFormat(mArticle.date);
        mView.loadPostMeta(mTitle, mArticle.mAuthor, mDate);
    }
}