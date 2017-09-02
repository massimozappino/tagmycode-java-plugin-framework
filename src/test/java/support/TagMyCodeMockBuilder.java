package support;

import com.tagmycode.plugin.Framework;
import com.tagmycode.sdk.SyncSnippets;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.LanguagesCollection;
import com.tagmycode.sdk.model.SnippetsCollection;
import com.tagmycode.sdk.model.SnippetsDeletions;
import com.tagmycode.sdk.model.User;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TagMyCodeMockBuilder {

    private final TagMyCode mockTagMyCode;
    private ResourceGenerate resourceGenerate = new ResourceGenerate();

    public TagMyCodeMockBuilder(Framework framework) throws Exception {
        mockTagMyCode = mock(TagMyCode.class);
        injectField(framework);
        initDefaultValues();
    }

    public TagMyCodeMockBuilder initDefaultValues() throws IOException, TagMyCodeException {
        setAuthenticated(true)
                .setServiceAvailable(true)
                .setAccount(resourceGenerate.aUser())
                .setLanguages(resourceGenerate.aLanguageCollection())
                .setSyncSnippets(new SyncSnippets(new SnippetsCollection(), new SnippetsDeletions()));
        return this;
    }

    private TagMyCodeMockBuilder setAccount(User user) throws TagMyCodeException {
        when(mockTagMyCode.fetchAccount()).thenReturn(user);
        return this;
    }

    private TagMyCodeMockBuilder setServiceAvailable(boolean flag) {
        when(mockTagMyCode.isServiceAvailable()).thenReturn(flag);
        return this;
    }

    public TagMyCodeMockBuilder setAuthenticated(boolean flag) {
        when(mockTagMyCode.isAuthenticated()).thenReturn(flag);
        return this;
    }

    private void injectField(Framework framework) throws NoSuchFieldException, IllegalAccessException {
        Field field = framework.getClass().getDeclaredField("tagMyCode");
        field.setAccessible(true);
        field.set(framework, mockTagMyCode);
    }


    public TagMyCodeMockBuilder setSnippets(SnippetsCollection snippets) throws TagMyCodeException {
        when(mockTagMyCode.fetchSnippetsCollection()).thenReturn(snippets);
        return this;
    }

    public TagMyCodeMockBuilder setLanguages(LanguagesCollection languages) throws TagMyCodeException {
        when(mockTagMyCode.fetchLanguages()).thenReturn(languages);
        return this;

    }

    public TagMyCodeMockBuilder setSyncSnippets(SyncSnippets syncSnippets) throws TagMyCodeException {
        doReturn(syncSnippets).when(mockTagMyCode).syncSnippets((SnippetsCollection) any(), (SnippetsDeletions) any());
        return this;
    }

    public TagMyCode getMock() {
        return mockTagMyCode;
    }
}
