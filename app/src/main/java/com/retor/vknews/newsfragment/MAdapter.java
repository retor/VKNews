package com.retor.vknews.newsfragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.retor.vknews.newsdialog.NewsDialog;
import com.retor.vknews.R;
import com.retor.vknews.presenters.VKNews;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by retor on 05.05.2015.
 */
public class MAdapter extends RecyclerView.Adapter<MViewHolder> {

    private List<VKNews> items = new ArrayList<>();
    private Context context;
    private FragmentActivity activity;

    public MAdapter(FragmentActivity activity, ArrayList<VKNews> items) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.items.addAll(items);
        Collections.reverse(this.items);
    }

    public void setItems(List<VKNews> items) {
        this.items.addAll(items);
//        Collections.reverse(this.items);
    }

    public void addItem(VKNews item) {
        items.add(item);
        notifyDataSetChanged();
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_card, viewGroup, false);
        return new MViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, final int i) {
        final VKNews tmp = items.get(i);
        if (tmp != null && tmp.getPost() != null) {
            if (tmp.getPost().source_id > 0) {
                holder.getAuthor().setText(tmp.getUser_owner().first_name + " " + tmp.getUser_owner().first_name);
                Picasso.with(context).load(Uri.parse(tmp.getUser_owner().photo_100)).into(holder.getPhoto());
            } else {
                holder.getAuthor().setText(tmp.getGroup_owner().screen_name);
                Picasso.with(context).load(Uri.parse(tmp.getGroup_owner().photo_100)).into(holder.getPhoto());
            }
            if (tmp.getPost().copy_history != null) {
                holder.getHeaderText().setText("Re: " + tmp.getPost().copy_history.get(0).text);
                if (tmp.getPost().copy_history.get(0).attachments != null) {
                    VKAttachments attachments = tmp.getPost().copy_history.get(0).attachments;
                    holder.getContent().removeAllViewsInLayout();
                    attachmentFill(holder, attachments);
                }
            } else {
                holder.getHeaderText().setText(tmp.getPost().text);
                if (tmp.getPost().attachments != null) {
                    VKAttachments attachments = tmp.getPost().attachments;
                    holder.getContent().removeAllViewsInLayout();
                    attachmentFill(holder, attachments);
                }
            }
            holder.getDate().setText(DateFormat.getInstance().format(new Date(tmp.getPost().date * 1000)));
        }
        holder.getCard().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new NewsDialog();
                Bundle arg = new Bundle();
                arg.putParcelable("vknews", items.get(i));
                dialogFragment.setArguments(arg);
                dialogFragment.show(activity.getSupportFragmentManager(), "item");
            }
        });
    }

    private void attachmentFill(MViewHolder holder, VKAttachments attachments) {
        for (VKAttachments.VKApiAttachment attachment : attachments) {
            Log.d("Attachments type", attachment.getType());
            if (attachment.getType().equals("photo")) {
                ImageView img = new ImageView(context);
                Picasso.with(context).load(Uri.parse(((VKApiPhoto) attachment).photo_604)).into(img);
                holder.getContent().addView(img);
                holder.getContent().setScrollContainer(true);
            }
            if (attachment.getType().equals("doc")) {
                TextView textView = new TextView(context);
                textView.setText(((VKApiDocument) attachment).title + " " + ((VKApiDocument) attachment).url);
                holder.getContent().addView(textView);
                holder.getContent().setScrollContainer(true);
            }
        }
    }

/*    private void createDialog(Item tmp, FragmentManager fm) {
        DialogFragment df = new MFragment();
        Bundle arg = new Bundle();
        arg.putSerializable("item", tmp);
        df.setArguments(arg);
        df.show(fm.beginTransaction(), "Media");
    }*/

    public void clear() {
        items.clear();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}