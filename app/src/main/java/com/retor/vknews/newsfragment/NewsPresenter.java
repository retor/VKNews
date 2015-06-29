package com.retor.vknews.newsfragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.retor.vklib.Const;
import com.retor.vklib.DialogsBuilder;
import com.retor.vklib.VkApiPlus;
import com.retor.vklib.mod.VKApiNewsArray;
import com.retor.vklib.mod.VkNewsPost;
import com.retor.vknews.presenters.INewsPresenter;
import com.retor.vknews.presenters.IView;
import com.retor.vknews.presenters.VKNews;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by retor on 22.06.2015.
 */
public class NewsPresenter implements INewsPresenter {
    private Activity activity;
    private Fragment fragment;
    private IView<VKNews> view;

    private Observer<VKNews> observer = new Observer<VKNews>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            showError(e.getLocalizedMessage());
        }

        @Override
        public void onNext(VKNews item) {
            view.onItem(item);
        }
    };

    public NewsPresenter(FragmentActivity activity, IView<VKNews> view) {
        this.activity = activity;
        this.fragment = activity.getSupportFragmentManager().findFragmentByTag(Const.FRAGMENT);
        this.view = view;
    }

    public NewsPresenter(Fragment fragment, IView<VKNews> view) {
        this.activity = fragment.getActivity();
        this.fragment = fragment;
        this.view = view;
    }

    private void showError(String error) {
        if (error == null)
            DialogsBuilder.createAlert(activity, "End reached").show();
        else
            DialogsBuilder.createAlert(activity, error).show();
    }

    @Override
    public void getNews(String from) {
        VKParameters params = new VKParameters();
        params.put("filters", "post");
        if (from != null) {
            params.put("start_from", from);
        }
        params.put(VKApiConst.COUNT, 15);
        VkApiPlus.news().getNews(params).executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                register(fillNews(response));
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                showError(error.errorMessage);
            }
        });
    }

    private Observable<VKNews> fillNews(final VKResponse response) {
        return Observable.create(new Observable.OnSubscribe<VKNews>() {
            @Override
            public void call(Subscriber<? super VKNews> subscriber) {
                VKApiNewsArray array = (VKApiNewsArray) response.parsedModel;
                JSONArray groups = response.json.optJSONObject("response").optJSONArray("groups");
                JSONArray users = response.json.optJSONObject("response").optJSONArray("profiles");
                for (VkNewsPost post : array) {
                    if (post.source_id > 0) {
                        if (users != null)
                            for (int i = 0; i < users.length(); i++) {
                                JSONObject tmp = users.optJSONObject(i);
                                if (tmp.optInt("id") == post.source_id)
                                    try {
                                        subscriber.onNext(new VKNews().setPost(post).setUser_owner(new VKApiUser(tmp)).setNext_from(response.json.optJSONObject("response").optString("next_from")));
                                    } catch (JSONException e) {
                                        subscriber.onError(e);
                                    }
                            }
                    } else if (post.source_id < 0) {
                        if (groups != null)
                            for (int i = 0; i < groups.length(); i++) {
                                JSONObject tmp = groups.optJSONObject(i);
                                if (tmp.optInt("id") == (-post.source_id))
                                    subscriber.onNext(new VKNews().setPost(post).setGroup_owner(new VKApiCommunity(tmp)).setNext_from(response.json.optJSONObject("response").optString("next_from")));
                            }
                    }
                }
                subscriber.onCompleted();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.from(Executors.newCachedThreadPool()));
    }

    private void register(Observable<VKNews> observable) {
        if (fragment != null) {
            AppObservable.bindFragment(fragment, observable).subscribeOn(Schedulers.io()).subscribe(observer);
        } else {
            AppObservable.bindActivity(activity, observable).subscribeOn(Schedulers.io()).subscribe(observer);
        }
    }

    @Override
    public void regListener(IView listener) {
        this.view = listener;
    }
}
