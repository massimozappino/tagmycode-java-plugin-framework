package com.tagmycode.plugin;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.tagmycode.plugin.filter.FilterLanguages;
import com.tagmycode.plugin.filter.LanguageFilterEntry;
import com.tagmycode.sdk.DbService;
import com.tagmycode.sdk.model.LanguagesCollection;
import com.tagmycode.sdk.model.Snippet;

import java.sql.SQLException;

public class FilterLanguagesLoader {
    private Data data;

    public FilterLanguagesLoader(Data data) {
        this.data = data;
    }

    public FilterLanguages load() {
        DbService dbService = data.getStorageEngine().getDbService();
        Dao<Snippet, String> snippetDao = dbService.snippetDao();

        GenericRawResults<String[]> query;
        FilterLanguages filterLanguages = new FilterLanguages();
        try {
            LanguagesCollection languages = data.getLanguages();
            query = snippetDao.queryRaw(
                    "SELECT languages.code, count(*) AS total FROM languages"
                            + " INNER JOIN snippets ON snippets.language_id = languages.id"
                            + " GROUP BY name ORDER BY total DESC");
            for (String[] language : query) {
                filterLanguages.add(languages.findByCode(language[0]), Integer.valueOf(language[1]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filterLanguages;
    }

}
