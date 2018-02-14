package com.tagmycode.plugin.filter;

import com.tagmycode.plugin.FilterLanguagesLoader;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class FilterLanguagesLoaderTest extends FilterLanguagesBaseTest {

    @Test
    public void testLoad() throws SQLException, IOException, TagMyCodeJsonException {
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(java));
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(java));
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(php));
        dbService.snippetDao().createOrUpdate(resourceGenerate.aSnippet().setLanguage(ruby));

        FilterLanguages filterLanguages = new FilterLanguagesLoader(data).load();
        assertEquals("[Java=2, Ruby=1, PHP=1]", filterLanguages.toString());
    }
}