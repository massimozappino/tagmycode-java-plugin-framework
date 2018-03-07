package com.tagmycode.plugin.filter;

import com.tagmycode.sdk.model.Language;

public class LanguageFilterEntry {
    private Language language;
    private Integer count;

    public LanguageFilterEntry(Language language, Integer count) {
        this.language = language;
        this.count = count;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return language.toString() + " (" + count + ")";
    }
}
