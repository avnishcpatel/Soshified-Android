package com.soshified.soshified.article;

import com.soshified.soshified.data.Article;
import com.soshified.soshified.util.DateUtils;
import com.soshified.soshified.util.ParseContent;
import com.soshified.soshified.util.TextUtils;

/**
 * Presenter Implementation to deal with all the 'presenter' stuff for ArticleView
 */
public class ArticlePresenter implements ArticleContract.Presenter {

    ArticleContract.View mView;
    Article mArticle;

    public ArticlePresenter(ArticleContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void init(Article article) {
        this.mArticle = article;
        parsePost(mArticle.getContent());
        parseMeta();

        mView.setupToolbar();
        mView.loadHeaderImage(mArticle.getThumbnail());
    }

    @Override
    public void parsePost(String postContent) {
        new ParseContent(mParsedPostContent -> mView.loadPostContent(mParsedPostContent)).execute(postContent);
    }

    @Override
    public void parseMeta() {
        String mTitle = TextUtils.fromHtml(mArticle.getTitle());
        String mDate = DateUtils.parseWordPressFormat(mArticle.getDate());
        mView.loadPostMeta(mTitle, mArticle.getAuthorName(), mDate);
    }
}
