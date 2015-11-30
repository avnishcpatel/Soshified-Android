package com.soshified.soshified.presenter;

import com.soshified.soshified.model.Post;
import com.soshified.soshified.util.DateUtils;
import com.soshified.soshified.util.ParseContent;
import com.soshified.soshified.util.TextUtils;
import com.soshified.soshified.view.PostView;

/**
 * Presenter Implementation to deal with all the 'presenter' stuff
 */
public class PostPresenterImpl implements PostPresenter {

    PostView mView;
    Post mPost;

    public PostPresenterImpl(PostView view) {
        this.mView = view;
    }

    @Override
    public void init(Post post) {
        this.mPost = post;
        parsePost(mPost.content);
        parseMeta();

        mView.initToolbar();
        mView.loadHeaderImage(mPost.mThumbnail);
    }

    @Override
    public void parsePost(String postContent) {
        new ParseContent(new ParseContent.OnParseCompleteListener() {
            @Override
            public void onParsed(String mParsedPostContent) {
                mView.loadPostContent(mParsedPostContent);
            }
        }).execute(postContent);
    }

    @Override
    public void parseMeta() {
        String mTitle = TextUtils.fromHtml(mPost.title);
        String mDate = DateUtils.parseWordPressFormat(mPost.date);
        mView.loadPostMeta(mTitle, mPost.mAuthor, mDate);
    }
}
