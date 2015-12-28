package com.tagmycode.plugin.gui;


import com.tagmycode.sdk.exception.TagMyCodeException;

public interface IonErrorCallback {
    void onError(TagMyCodeException e);
}
