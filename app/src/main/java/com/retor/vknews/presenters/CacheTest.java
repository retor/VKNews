package com.retor.vknews.presenters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;

import com.retor.vklib.DialogsBuilder;
import com.retor.vklib.VkApiPlus;
import com.retor.vklib.mod.VKApiNewsArray;
import com.retor.vklib.mod.VkNewsPost;
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
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by retor on 02.07.2015.
 */
public class CacheTest implements INewsPresenter{

    private IView<VKNews> view;
    private Fragment fragment;
    private Activity activity;
    private LruCache<Integer, VKNews> cache;
    private int pos = 0;
    private int prev_pos = 0;
    private Observer<VKNews> observer = new Observer<VKNews>() {
        @Override
        public void onCompleted() {
            loadCache();
        }

        @Override
        public void onError(Throwable e) {
            showError(e.getLocalizedMessage());
        }

        @Override
        public void onNext(VKNews vkNews) {
            addToCache(vkNews);
        }
    };

    private void addToCache(VKNews vkNews) {
        cache.put(pos, vkNews);
        pos++;
    }

    private void loadCache() {
        if ((cache.size()-prev_pos)>0)
            for (int i = prev_pos; i < pos; i++) {
                view.onItem(cache.get(i));
            }
        prev_pos = pos;
    }

    public CacheTest(IView<VKNews> view, Fragment fragment) {
        this.view = view;
        this.cache = new LruCache<>(1024);
        this.fragment = fragment;
        this.activity = fragment.getActivity();
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
    public void getNews(final String from) {
        register(getVkNewsObservable(from));
    }

    @NonNull
    private Observable<VKNews> getVkNewsObservable(final String from) {
        return Observable.create(new Observable.OnSubscribe<VKResponse>() {
            @Override
            public void call(final Subscriber<? super VKResponse> subscriber) {
                VKParameters params = new VKParameters();
                params.put("filters", "post");
                if (from != null) {
                    params.put("start_from", from);
                } else {
                    cache.evictAll();
                }
                params.put(VKApiConst.COUNT, 15);
                VkApiPlus.news().getNews(params).executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        subscriber.onNext(response);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        subscriber.onError(new Exception(error.errorMessage, error.httpError));
                    }
                });
            }
        }).flatMap(new Func1<VKResponse, Observable<VKNews>>() {
            @Override
            public Observable<VKNews> call(VKResponse response) {
                return fillNews(response);
            }
        });
    }

    private void showError(String error) {
        if (error == null)
            DialogsBuilder.createAlert(activity, "End reached").show();
        else
            DialogsBuilder.createAlert(activity, error).show();
    }

    @Override
    public void regListener(IView listener) {
        this.view = listener;
    }

    @Override
    public IView<VKNews> getListener() {
        return this.view;
    }

    @Override
    public void removeListener(IView<VKNews> listener) {
        if (this.view != null)
            this.view = null;
    }
}
