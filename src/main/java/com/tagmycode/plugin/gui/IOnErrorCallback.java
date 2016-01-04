package com.tagmycode.plugin.gui;


import com.tagmycode.sdk.exception.TagMyCodeException;

public interface IOnErrorCallback {
    void onError(TagMyCodeException e);
}
