package com.retor.vknews.newsdialog;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.retor.vknews.R;
import com.retor.vknews.newsfragment.MViewHolder;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by retor on 05.05.2015.
 */
public class CommentsAdapter extends RecyclerView.Adapter<MViewHolder> {

    private Map<VKApiComment, Object> comments = new HashMap<>();
    private Set<VKApiComment> items = new HashSet<>();
    private Context context;
    private Picasso picasso;

    public CommentsAdapter(Context context, Map<VKApiComment, Object> items) {
        this.context = context;
        this.comments.putAll(items);
        this.picasso = new Picasso.Builder(context)
                .memoryCache(new com.squareup.picasso.LruCache(12000))
                .build();
    }

    public void setItems(Map<VKApiComment, Object> items) {
        this.comments.putAll(items);
        this.items.addAll(comments.keySet());
        notifyDataSetChanged();
    }

    public void addItem(Map.Entry<VKApiComment, Object> item) {
        this.comments.put(item.getKey(), item.getValue());
        this.items.addAll(comments.keySet());
        notifyDataSetChanged();
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_card, viewGroup, false);
        return new MViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, final int i) {
        VKApiComment tmp = (VKApiComment) (items.toArray())[i];
        ownerFill(holder, tmp);
        if (tmp != null && tmp.text != null) {
            holder.getHeaderText().setText(tmp.text);
            holder.getDate().setText(DateFormat.getInstance().format(new Date(tmp.date * 1000)));
            holder.getContent().setVisibility(View.GONE);
            if (tmp.attachments != null && !tmp.attachments.isEmpty()) {
                VKAttachments attachments = tmp.attachments;
                holder.getContent().setVisibility(View.VISIBLE);
                holder.getContent().removeAllViewsInLayout();
                attachmentFill(holder, attachments);
            }
        }
    }

    private void ownerFill(MViewHolder holder, VKApiComment tmp) {
        if (tmp != null) {
            Object owner = comments.get(tmp);
            if (owner instanceof VKApiUser) {
                holder.getAuthor().setText(((VKApiUser) owner).first_name + " " + ((VKApiUser) owner).first_name);
                picasso.load(Uri.parse(((VKApiUser) owner).photo_100)).error(R.drawable.no_image).into(holder.getPhoto());
            } else if (owner instanceof VKApiCommunity) {
                holder.getAuthor().setText(((VKApiCommunity) owner).name + " " + ((VKApiCommunity) owner).screen_name);
                picasso.load(Uri.parse(((VKApiCommunity) owner).photo_100)).error(R.drawable.no_image).into(holder.getPhoto());
            }
        }
    }

    private void attachmentFill(MViewHolder holder, VKAttachments attachments) {
        for (VKAttachments.VKApiAttachment attachment : attachments) {
//            Log.d("Attachments type", attachment.getType());
            if (attachment.getType().equals("photo")) {
                photoAttachment(holder, (VKApiPhoto) attachment);
            }
            if (attachment.getType().equals("doc")) {
                docAttachment(holder, (VKApiDocument) attachment);
            }
        }
    }

    private void photoAttachment(MViewHolder holder, VKApiPhoto attachment) {
        ImageView img = new ImageView(context);
        picasso.load(Uri.parse(attachment.photo_604)).error(R.drawable.no_image).into(img);
        holder.getContent().addView(img);
        holder.getContent().setScrollContainer(true);
    }

    private void docAttachment(MViewHolder holder, VKApiDocument attachment) {
        TextView textView = new TextView(context);
        textView.setText(attachment.title + " " + attachment.url);
        holder.getContent().addView(textView);
        holder.getContent().setScrollContainer(true);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}