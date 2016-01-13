package com.tagmycode.plugin.exception;

import com.tagmycode.sdk.exception.TagMyCodeException;

public class TagMyCodeStorageException extends TagMyCodeException {
    public TagMyCodeStorageException(Exception e) {
        super(e);
    }
}
