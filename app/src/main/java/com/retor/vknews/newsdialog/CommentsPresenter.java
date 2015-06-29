package com.retor.vknews.newsdialog;

import android.support.v4.app.Fragment;

import com.retor.vklib.VkApiPlus;
import com.retor.vknews.presenters.ICommentsPresenter;
import com.retor.vknews.presenters.IOwnerPresenter;
import com.retor.vknews.presenters.IView;
import com.retor.vknews.presenters.VKNews;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKCommentArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.schedulers.Schedulers;

/**
 * Created by retor on 26.06.2015.
 */
public class CommentsPresenter implements ICommentsPresenter, IOwnerPresenter {
    private Fragment fragment;
    private IView<VKNews> view;
    private JSONArray profiles;
    private JSONArray groups;
    private Observer<VKNews> observer = new Observer<VKNews>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(VKNews news) {

        }
    };

    public CommentsPresenter(Fragment fragment, IView<VKNews> view) {
        this.fragment = fragment;
        this.view = view;
    }

    @Override
    public void getComments(VKNews news) {
        AppObservable.bindFragment(fragment, loadComments(news)).subscribeOn(Schedulers.io()).subscribe(new Observer<VKNews>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(VKNews vkNews) {
                view.onItem(vkNews);
            }
        });
    }

    private Observable<VKNews> loadComments(final VKNews vkNews) {
        return Observable.create(new Observable.OnSubscribe<VKNews>() {
            @Override
            public void call(final Subscriber<? super VKNews> subscriber) {
                VKParameters params = new VKParameters();
                params.put(VKApiConst.EXTENDED, 1);
                params.put(VKApiConst.OWNER_ID, vkNews.getPost().source_id);
                params.put(VKApiConst.POST_ID, vkNews.getPost().post_id);
                params.put(VKApiConst.COUNT, vkNews.getPost().comments_count);
                VkApiPlus.news().getComments(params).executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        Map<VKApiComment, Object> outMap = new HashMap<VKApiComment, Object>(((VKCommentArray) response.parsedModel).size());
                        fillOwners(response);
                        VKNews out = vkNews;
                        for (VKApiComment comment : (VKCommentArray) response.parsedModel) {
                            outMap.put(comment, getOwner(comment.from_id));
                        }
                        out.setComments(outMap);
                        subscriber.onNext(out);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        subscriber.onError(new Exception(error.errorMessage, error.httpError));
                    }
                });
            }
        });
    }

    private void fillOwners(VKResponse response) {
        this.profiles = response.json.optJSONObject("response").optJSONArray("profiles");
        this.groups = response.json.optJSONObject("response").optJSONArray("groups");
    }

    @Override
    public void regListener(IView listener) {
        this.view = listener;
    }

    @Override
    public Object getOwner(int id) {
        try {
            if (id < 0) {
                return searchGroups(id);
            } else if (id > 0) {
                return searchProfiles(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("Can't find owner");
    }

    private VKApiUser searchProfiles(int id) throws JSONException {
        VKApiUser out = null;
        if (profiles != null) {
            JSONObject o;
            for (int i = 0; i < profiles.length(); i++) {
                o = profiles.optJSONObject(i);
                if (o.optInt("id") == id)
                    out = new VKApiUser(o);
            }
        }
        return out;
    }

    private VKApiCommunity searchGroups(int id) throws JSONException {
        VKApiCommunity out = null;
        if (groups != null) {
            JSONObject o;
            for (int i = 0; i < groups.length(); i++) {
                o = groups.optJSONObject(i);
                if (o.optInt("id") == id)
                    out = new VKApiCommunity(o);
            }
        }
        return out;
    }
}
