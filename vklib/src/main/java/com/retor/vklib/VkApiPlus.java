package com.retor.vklib;

import com.retor.vklib.mod.VkApiNews;
import com.vk.sdk.api.VKApi;

/**
 * Created by retor on 22.06.2015.
 */
public class VkApiPlus extends VKApi{

    public VkApiPlus(){
    }

    public static VkApiNews news(){
        return new VkApiNews();
    }
/*
    public VKRequest getComments(int owner_id, int post_id, int count){
        VKParameters parameters = new VKParameters();
        parameters.put(VKApiConst.OWNER_ID, owner_id);
        parameters.put(VKApiConst.POST_ID, post_id);
        parameters.put(VKApiConst.COUNT, count);
        return wall().getComments(parameters);
    }*/
}
