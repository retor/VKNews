package com.retor.vknews.newsdialog;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.retor.vknews.R;
import com.retor.vknews.presenters.ICommentsPresenter;
import com.retor.vknews.presenters.IView;
import com.retor.vknews.presenters.VKNews;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;

import java.util.HashMap;

/**
 * Created by retor on 26.06.2015.
 */
public class NewsDialog extends DialogFragment implements IView<VKNews> {
    private VKNews item;
    private ICommentsPresenter presenter;
    private CommentsAdapter adapter;

    private RecyclerView recyclerView;
    private TextView title;
    private TextView description;
    private TextView like_count;
    private Button comments;
    private LinearLayout content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        this.presenter = new CommentsPresenter(this, this);
        this.item = getArguments().getParcelable("vknews");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View out = inflater.inflate(R.layout.news_item, container, false);
        if (item.getComments() != null)
            adapter = new CommentsAdapter(getActivity().getApplicationContext(), item.getComments());
        else
            adapter = new CommentsAdapter(getActivity().getApplicationContext(), new HashMap<VKApiComment, Object>());
        if (savedInstanceState != null && (item = savedInstanceState.getParcelable("item")) != null) {
            adapter.setItems(item.getComments());
        } else {
            presenter.getComments(item);
        }
        initViews(out);
        fillViews();
        return out;
    }

    private void fillViews() {
        if (item != null)
            title.setText(item.getPost().text);
        description.setText(item.getPost().post_type);
        like_count.setText(String.valueOf(item.getPost().likes_count));
        if (item.getPost().attachments != null) {
            VKAttachments attachments = item.getPost().attachments;
            content.removeAllViewsInLayout();
            attachmentFill(attachments);
        }
        comments.setText(String.valueOf(item.getPost().comments_count));
        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getComments().size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void attachmentFill(VKAttachments attachments) {
        for (VKAttachments.VKApiAttachment attachment : attachments) {
            Log.d("Attachments type", attachment.getType());
            if (attachment.getType().equals("photo")) {
                ImageView img = new ImageView(getActivity().getApplicationContext());
                Picasso.with(getActivity().getApplicationContext()).load(Uri.parse(((VKApiPhoto) attachment).photo_604)).into(img);
                content.addView(img);
                content.setScrollContainer(true);
            }
            if (attachment.getType().equals("doc")) {
                TextView textView = new TextView(getActivity().getApplicationContext());
                textView.setText(((VKApiDocument) attachment).title + " " + ((VKApiDocument) attachment).url);
                content.addView(textView);
                content.setScrollContainer(true);
            }
        }
    }

    private void initViews(View out) {
        this.title = (TextView) out.findViewById(R.id.news_title);
        this.description = (TextView) out.findViewById(R.id.description);
        this.like_count = (TextView) out.findViewById(R.id.like_count);
        this.comments = (Button) out.findViewById(R.id.comments_button);
        this.content = (LinearLayout) out.findViewById(R.id.news_content);
        this.recyclerView = (RecyclerView) out.findViewById(R.id.comments);
        initRecycler(recyclerView);
    }

    private void initRecycler(final RecyclerView input) {
        input.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        input.setAdapter(adapter);
        input.setHasFixedSize(true);
        RecyclerView.ItemAnimator im = new DefaultItemAnimator();
        im.setAddDuration(1000);
        input.setItemAnimator(im);
        input.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("item", item);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItem(VKNews item) {
        this.item = item;
        adapter.setItems(item.getComments());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
