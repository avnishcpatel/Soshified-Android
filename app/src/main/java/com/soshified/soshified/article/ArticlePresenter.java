package com.soshified.soshified.article;

import com.soshified.soshified.data.Article;
import com.soshified.soshified.data.source.ArticlesRepository;
import com.soshified.soshified.util.DateUtils;
import com.soshified.soshified.util.ParseContent;
import com.soshified.soshified.util.TextUtils;

/**
 * Presenter Implementation to deal with all the 'presenter' stuff for ArticleView
 */
public class ArticlePresenter implements ArticleContract.Presenter {

    private ArticleContract.View mView;
    private ArticlesRepository mRepository;
    Article mArticle;


    public ArticlePresenter(ArticlesRepository articlesRepository, int articleID, ArticleContract.View view) {
        mView = view;
        mRepository = articlesRepository;
        mView.setPresenter(this);
        mRepository.getArticleObservable(articleID)
                .subscribe(this::init);
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
