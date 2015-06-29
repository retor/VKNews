package com.retor.vknews.presenters;

import android.os.Parcel;
import android.os.Parcelable;

import com.retor.vklib.mod.VkNewsPost;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKCommentArray;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by retor on 25.06.2015.
 */
public class VKNews implements Parcelable, Serializable {
    private VkNewsPost post;
    private VKApiUser user_owner;
    private VKApiCommunity group_owner;
    private Map<VKApiComment, Object> comments;
    private String next_from;

    public VKNews() {
    }

    protected VKNews(Parcel in) {
        post = in.readParcelable(VkNewsPost.class.getClassLoader());
        user_owner = in.readParcelable(VKApiUser.class.getClassLoader());
        group_owner = in.readParcelable(VKApiCommunity.class.getClassLoader());
        comments = in.readParcelable(VKCommentArray.class.getClassLoader());
        next_from = in.readString();
    }

    public static final Creator<VKNews> CREATOR = new Creator<VKNews>() {
        @Override
        public VKNews createFromParcel(Parcel in) {
            return new VKNews(in);
        }

        @Override
        public VKNews[] newArray(int size) {
            return new VKNews[size];
        }
    };

    public VKNews setPost(VkNewsPost post) {
        this.post = post;
        return this;
    }

    public VKNews setUser_owner(VKApiUser user_owner) {
        this.user_owner = user_owner;
        return this;
    }

    public VKNews setGroup_owner(VKApiCommunity group_owner) {
        this.group_owner = group_owner;
        return this;
    }

    public VkNewsPost getPost() {
        return post;
    }

    public VKApiUser getUser_owner() {
        return user_owner;
    }

    public VKApiCommunity getGroup_owner() {
        return group_owner;
    }

    public Map<VKApiComment, Object> getComments() {
        return comments;
    }

    public VKNews setComments(Map<VKApiComment, Object> comments) {
        this.comments = comments;
        return this;
    }

    public String getNext_from() {
        return next_from;
    }

    public VKNews setNext_from(String next_from) {
        this.next_from = next_from;
        return this;
    }

    @Override
    public String toString() {
        return "VKNews{" +
                "post=" + post +
                ", user_owner=" + user_owner +
                ", group_owner=" + group_owner +
                ", comments=" + comments +
                ", next_from='" + next_from + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.post, flags);
        if (group_owner != null)
            dest.writeParcelable(this.group_owner, flags);
        if (user_owner != null)
            dest.writeParcelable(this.user_owner, flags);
        dest.writeString(this.next_from);
    }
}
