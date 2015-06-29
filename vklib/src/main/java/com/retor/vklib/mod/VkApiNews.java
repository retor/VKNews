package com.retor.vklib.mod;

import com.retor.vklib.VkApiPlus;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.methods.VKApiBase;

/**
 * Created by retor on 23.06.2015.
 */
public class VkApiNews extends VKApiBase {

    public VkApiNews() {
    }

    public VKRequest getNews(VKParameters params){
        return this.prepareRequest("get", params, VKRequest.HttpMethod.GET, VKApiNewsArray.class);
    }

    public VKRequest getNewsFrom(String from){
        VKParameters params = new VKParameters();
        params.put("start_from", from);
        return getNews(params);
    }

    /**
     *
     * OWNER(SOURCE_ID), ID(POST_ID), COUNT
     * */
    public VKRequest getComments(VKParameters params){
        return VkApiPlus.wall().getComments(params);
    }

    @Override
    protected String getMethodsGroup() {
        return "newsfeed";
    }
}
