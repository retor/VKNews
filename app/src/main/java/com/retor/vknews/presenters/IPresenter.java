package com.retor.vknews.presenters;

/**
 * Created by retor on 19.06.2015.
 */
public interface IPresenter {
    void regListener(IView<VKNews> listener);
    IView<VKNews> getListener();
    void removeListener(IView<VKNews> listener);
}
