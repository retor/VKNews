package com.retor.vklib.mod;

import android.os.Parcel;
import android.os.Parcelable;

import com.vk.sdk.api.model.Identifiable;
import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by retor on 24.06.2015.
 */
public class VkNewsPost extends VKApiModel implements Identifiable, Parcelable {
    public int post_id;
    public String text;
    public int source_id;
    public long date;
    public String post_type;
    public boolean can_like;
    public boolean user_likes;
    public int likes_count;
    public int comments_count;
    public boolean can_post_comment;
    public VKList<VKApiPost> copy_history;
    public VKAttachments attachments = new VKAttachments();

    public VkNewsPost(JSONObject from) throws JSONException {
        this.parse(from);
    }

    public static final Creator<VkNewsPost> CREATOR = new Creator<VkNewsPost>() {
        @Override
        public VkNewsPost createFromParcel(Parcel in) {
            return new VkNewsPost(in);
        }

        @Override
        public VkNewsPost[] newArray(int size) {
            return new VkNewsPost[size];
        }
    };

    public VkNewsPost parse(JSONObject source) throws JSONException {
        this.post_id = source.optInt("post_id");
        this.source_id = source.optInt("source_id");
        this.date = source.optLong("date");
        this.text = source.optString("text");
        JSONObject comments = source.optJSONObject("comments");
        if (comments != null) {
            this.comments_count = comments.optInt("count");
            this.can_post_comment = parseBoolean(comments, "can_post");
        }

        JSONObject likes = source.optJSONObject("likes");
        if (likes != null) {
            this.likes_count = likes.optInt("count");
            this.user_likes = parseBoolean(likes, "user_likes");
            this.can_like = parseBoolean(likes, "can_like");
        }
        JSONArray history = source.optJSONArray("copy_history");
        if (history != null){
            this.copy_history = new VKList(source.optJSONArray("copy_history"), VKApiPost.class);
        }

/*        JSONObject reposts = source.optJSONObject("reposts");
        if(reposts != null) {
            this.reposts_count = reposts.optInt("count");
            this.user_reposted = ParseUtils.parseBoolean(reposts, "user_reposted");
        }*/

            this.post_type = source.optString("post_type");
        this.attachments.fill(source.optJSONArray("attachments"));
        JSONObject geo = source.optJSONObject("geo");
/*        if(geo != null) {
            this.geo = (new VKApiPlace()).parse(geo);
        }*/
        return this;
    }

    public VkNewsPost(Parcel in) {
        this.post_id = in.readInt();
        this.source_id = in.readInt();
        this.date = in.readLong();
        this.text = in.readString();
        this.comments_count = in.readInt();
        this.can_post_comment = in.readByte() != 0;
        this.likes_count = in.readInt();
        this.user_likes = in.readByte() != 0;
        this.can_like = in.readByte() != 0;
        this.post_type = in.readString();
        this.attachments = (VKAttachments) in.readParcelable(VKAttachments.class.getClassLoader());
    }

    public VkNewsPost() {
    }

    public int getId() {
        return this.post_id;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.post_id);
        dest.writeInt(this.source_id);
        dest.writeLong(this.date);
        dest.writeString(this.text);
        dest.writeInt(this.comments_count);
        dest.writeByte((byte) (this.can_post_comment ? 1 : 0));
        dest.writeInt(this.likes_count);
        dest.writeByte((byte) (this.user_likes ? 1 : 0));
        dest.writeByte((byte) (this.can_like ? 1 : 0));
        dest.writeString(this.post_type);
        dest.writeParcelable(this.attachments, flags);
    }

    private boolean parseBoolean(JSONObject from, String name) {
        return from != null && from.optInt(name, 0) == 1;
    }
}
