package acceptance;

import com.tagmycode.plugin.Framework;
import com.tagmycode.plugin.StorageEngine;
import support.AbstractTestBase;

public class AcceptanceTestBase extends AbstractTestBase {

    protected Framework acceptanceFramework() throws Exception {
        StorageEngine storage = createStorageEngineWithData();
        storage.saveNetworkingEnabledFlag(false);
        Framework framework = createFramework(storage);
        mockTagMyCodeReturningValidAccountData(framework);
        return framework;
    }
}
