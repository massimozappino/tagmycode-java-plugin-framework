package com.tagmycode.plugin.exception;

import support.TagMyCodeExceptionBaseTest;
import org.junit.Test;


public class TagMyCodeGuiExceptionTest extends TagMyCodeExceptionBaseTest {
    @Test
    @Override
    public void exceptionIsSubclassOfTagMyCodeException() {
        assertClassIsSubclassOfTagMyCodeException(TagMyCodeGuiException.class);
    }
}
