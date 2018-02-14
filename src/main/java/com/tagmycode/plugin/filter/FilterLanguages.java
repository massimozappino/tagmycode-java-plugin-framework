package com.tagmycode.plugin.filter;

import com.tagmycode.sdk.model.Language;

import java.util.ArrayList;

public class FilterLanguages extends ArrayList<LanguageFilterEntry> {
    public void add(Language language, Integer count) {
        add(new LanguageFilterEntry(language, count));
    }
}
