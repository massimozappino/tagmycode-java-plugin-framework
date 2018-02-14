package com.tagmycode.plugin.filter;

import com.tagmycode.plugin.Data;
import com.tagmycode.plugin.StorageEngine;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.DbService;
import com.tagmycode.sdk.model.Language;
import org.junit.Before;
import support.BaseTest;
import support.MemDbService;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class FilterLanguagesBaseTest extends BaseTest {
    protected DbService dbService;

    protected Language java;
    protected Language php;
    protected Language ruby;
    protected Data data;

    @Before
    public void initializeLanguages() throws SQLException, TagMyCodeStorageException {
        dbService = new MemDbService().initialize();

        java = resourceGenerate.languageJava();
        php = resourceGenerate.languagePHP();
        ruby = resourceGenerate.languageRuby();

        insertLanguagesIntoDb();
    }

    private void insertLanguagesIntoDb() throws SQLException, TagMyCodeStorageException {
        dbService.languageDao().createOrUpdate(java);
        dbService.languageDao().createOrUpdate(php);
        dbService.languageDao().createOrUpdate(ruby);
        data = new Data(new StorageEngine(dbService));
        data.loadAll();
        assertEquals(3, data.getLanguages().size());
    }
}
