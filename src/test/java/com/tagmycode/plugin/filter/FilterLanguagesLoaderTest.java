package com.tagmycode.plugin.filter;

import com.tagmycode.plugin.FilterLanguagesLoader;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class FilterLanguagesLoaderTest extends FilterLanguagesBaseTest {
    @Test
    public void testLoadSortByCount() throws SQLException, IOException, TagMyCodeJsonException {
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(java));
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(java));
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(java));
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(php));
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(ruby));
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(ruby));

        FilterLanguages filterLanguages = new FilterLanguagesLoader(data).load();
        assertEquals("[Java (3), Ruby (2), PHP (1)]", filterLanguages.toString());
    }

    @Test
    public void testLoadSortByName() throws SQLException, IOException, TagMyCodeJsonException {
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(java));
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(ruby));
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(php));

        FilterLanguages filterLanguages = new FilterLanguagesLoader(data).load();
        assertEquals("[Java (1), PHP (1), Ruby (1)]", filterLanguages.toString());
    }
}