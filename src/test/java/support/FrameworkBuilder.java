package support;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.FrameworkConfig;
import com.tagmycode.plugin.StorageEngine;
import com.tagmycode.sdk.DbService;
import com.tagmycode.sdk.SaveFilePath;
import com.tagmycode.sdk.authentication.TagMyCodeApi;
import com.tagmycode.sdk.authentication.TagMyCodeApiDevelopment;

import java.io.IOException;
import java.sql.SQLException;

public class FrameworkBuilder {
    private TagMyCodeApi tagMyCodeApi = new TagMyCodeApiDevelopment();
    private DbService dbService = new MemDbService();
    private StorageEngine storageEngine = new StorageEngine(dbService);

    public FrameworkBuilder() throws SQLException {
    }

    public FrameworkBuilder(StorageEngineBuilder storageEngineBuilder) throws SQLException {
        storageEngine = storageEngineBuilder.build();
    }

    public Framework build() throws SQLException, IOException {
        FrameworkConfig frameworkConfig = new FrameworkConfig(new SaveFilePath(""), new FakePasswordKeyChain(), storageEngine.getDbService(), new FakeMessageManager(), new FakeTaskFactory(), new FakeVersion(), null);
        return new Framework(tagMyCodeApi, frameworkConfig, new FakeSecret());
    }

    public FrameworkBuilder setStorageEngine(StorageEngine storageEngine) {
        this.storageEngine = storageEngine;
        return this;
    }
}
