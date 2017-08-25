package support;

import com.tagmycode.plugin.StorageEngine;
import com.tagmycode.plugin.exception.TagMyCodeStorageException;
import com.tagmycode.sdk.DbService;
import com.tagmycode.sdk.exception.TagMyCodeJsonException;
import com.tagmycode.sdk.model.LanguagesCollection;
import com.tagmycode.sdk.model.User;

import java.io.IOException;
import java.sql.SQLException;

public class StorageEngineBuilder {

    private final ResourceGenerate resourceGenerate = new ResourceGenerate();
    ;
    private StorageEngine storageEngine;


    public StorageEngineBuilder() throws SQLException, TagMyCodeJsonException, TagMyCodeStorageException, IOException {
        storageEngine = new StorageEngine(new MemDbService());
        defaultInitialize();
    }

    public StorageEngineBuilder(DbService dbService) throws SQLException, IOException, TagMyCodeJsonException, TagMyCodeStorageException {
        storageEngine = new StorageEngine(dbService);
        defaultInitialize();
    }

    private void defaultInitialize() throws TagMyCodeStorageException, IOException, TagMyCodeJsonException {
        withAccount(resourceGenerate.aUser());
        withLanguagesCollection(resourceGenerate.aLanguageCollection());
    }

    private StorageEngineBuilder withLanguagesCollection(LanguagesCollection languages) throws TagMyCodeStorageException, IOException, TagMyCodeJsonException {
        storageEngine.saveLanguageCollection(languages);
        return this;
    }

    public StorageEngineBuilder withAccount(User user) throws TagMyCodeStorageException {
        storageEngine.saveAccount(user);
        return this;
    }

    public StorageEngine build() throws SQLException {
        return storageEngine;
    }

    public StorageEngineBuilder withNetworkEnabledFlag(boolean flag) throws TagMyCodeStorageException {
        storageEngine.saveNetworkingEnabledFlag(flag);
        return this;
    }

}
