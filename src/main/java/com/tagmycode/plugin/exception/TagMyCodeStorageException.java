package com.tagmycode.plugin.exception;

import com.tagmycode.sdk.exception.TagMyCodeException;

public class TagMyCodeStorageException extends TagMyCodeException {
    @Override
    public String getMessage() {
        return "Error on storage";
    }
}
