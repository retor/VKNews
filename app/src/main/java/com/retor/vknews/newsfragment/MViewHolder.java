package com.retor.vknews.newsfragment;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.retor.vknews.R;

/**
 * Created by retor on 05.05.2015.
 */
public class MViewHolder extends RecyclerView.ViewHolder {
    private TextView headerText;
    private ImageView photo;
    private LinearLayout content;
    private TextView author;
    private TextView date;
    private CardView card;

    public MViewHolder(View itemView) {
        super(itemView);
        setHeaderText((TextView) itemView.findViewById(R.id.header));
        setPhoto((ImageView) itemView.findViewById(R.id.photo));
        setContent((LinearLayout) itemView.findViewById(R.id.content));
        setAuthor((TextView) itemView.findViewById(R.id.author));
        setDate((TextView) itemView.findViewById(R.id.date));
        setCard((CardView) itemView.findViewById(R.id.card_view));
    }

    public TextView getHeaderText() {
        return headerText;
    }

    public void setHeaderText(TextView headerText) {
        this.headerText = headerText;
    }

    public ImageView getPhoto() {
        return photo;
    }

    public void setPhoto(ImageView photo) {
        this.photo = photo;
    }

    public LinearLayout getContent() {
        return content;
    }

    public void setContent(LinearLayout content) {
        this.content = content;
    }

    public TextView getAuthor() {
        return author;
    }

    public void setAuthor(TextView author) {
        this.author = author;
    }

    public TextView getDate() {
        return date;
    }

    public void setDate(TextView date) {
        this.date = date;
    }

    public CardView getCard() {
        return card;
    }

    public void setCard(CardView card) {
        this.card = card;
    }
}