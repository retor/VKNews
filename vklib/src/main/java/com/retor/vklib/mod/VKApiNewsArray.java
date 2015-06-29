package com.retor.vklib.mod;

import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by retor on 24.06.2015.
 */
public class VKApiNewsArray extends VKList<VkNewsPost> {

    public VKApiNewsArray() {
    }

    public VKApiModel parse(JSONObject response) throws JSONException {
        this.fill(response, VkNewsPost.class);
        return this;
    }
}
