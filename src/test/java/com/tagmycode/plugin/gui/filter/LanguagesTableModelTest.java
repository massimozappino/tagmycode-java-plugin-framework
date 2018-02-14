package com.tagmycode.plugin.gui.filter;

import com.tagmycode.plugin.filter.FilterLanguages;
import com.tagmycode.plugin.filter.FilterLanguagesBaseTest;
import com.tagmycode.plugin.filter.LanguageFilterEntry;
import com.tagmycode.sdk.model.Language;
import org.junit.Test;

import static org.junit.Assert.*;

public class LanguagesTableModelTest extends FilterLanguagesBaseTest {

    @Test
    public void setDataMaintainSameObject() {
        FilterLanguages filterLanguagesOld = new FilterLanguages();
        filterLanguagesOld.add(java, 10);
        filterLanguagesOld.add(ruby, 1);
        filterLanguagesOld.add(php, 3);

        FilterLanguages filterLanguagesNew = new FilterLanguages();
        filterLanguagesNew.add(java, 10);
        filterLanguagesNew.add(php, 5);

        LanguagesTableModel languagesTableModel = new LanguagesTableModel();

        languagesTableModel.setFilterLanguages(filterLanguagesOld);
        assertLanguage(languagesTableModel, 0, this.java, 10);
        assertLanguage(languagesTableModel, 1, ruby, 1);
        assertLanguage(languagesTableModel, 2, php, 3);
        assertEquals(3, languagesTableModel.getFilterLanguages().size());

        languagesTableModel.setFilterLanguages(filterLanguagesNew);
        assertLanguage(languagesTableModel, 0, this.java, 10);
        assertLanguage(languagesTableModel, 1, php, 5);
        assertEquals(2, languagesTableModel.getFilterLanguages().size());
    }

    private void assertLanguage(LanguagesTableModel languagesTableModel, int position, Language language, int count) {
        LanguageFilterEntry languageFilterEntry = languagesTableModel.getFilterLanguages().get(position);
        assertEquals(language, languageFilterEntry.getLanguage());
        assertEquals(count, (int) languageFilterEntry.getCount());
    }

}