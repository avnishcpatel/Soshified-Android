package com.soshified.soshified.article;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.soshified.soshified.R;
import com.soshified.soshified.data.Comment;
import com.soshified.soshified.util.DateUtils;
import com.soshified.soshified.util.TextUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter to display comments
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    ArrayList<Comment> mComments;

    public CommentsAdapter(ArrayList<Comment> mComments) {
        this.mComments = mComments;
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_list_item, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        Comment comment = mComments.get(position);
        holder.mCommentAuthor.setText(TextUtils.fromHtml(comment.getName()));
        holder.mCommentDate.setText(DateUtils.parseWordPressFormat(DateUtils
                .getUnixTimeStamp(comment.getDate())));
        holder.mCommentContent.setText(TextUtils.fromHtml(comment.getContent()));
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.comment_author) TextView mCommentAuthor;
        @Bind(R.id.comment_date) TextView mCommentDate;
        @Bind(R.id.comment_content) TextView mCommentContent;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
