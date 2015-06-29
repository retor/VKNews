package com.retor.vklib.auth;

/**
 * Created by retor on 22.06.2015.
 */
public interface IAuthListener {
    void onLogin(int userId);
    void onError(String message);
}
