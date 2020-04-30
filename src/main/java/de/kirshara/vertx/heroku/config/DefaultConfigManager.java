package de.kirshara.vertx.heroku.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class DefaultConfigManager implements ConfigManager {

    private final ConfigRetriever configRetriever;

    protected DefaultConfigManager(Vertx vertx) {
        // Reading config params from resoucres/config.json file
        final ConfigStoreOptions configStoreFileOptions = new ConfigStoreOptions()
            .setType("file")
            .setOptional(true)
            .setConfig(new JsonObject().put("path", "config.json"));

        // Reading the environment variables
        final ConfigStoreOptions configStoreSysOptions = new ConfigStoreOptions()
            .setType("sys")
            .setOptional(true);

        // Reading the Config Vars which are defined in the settings of the application on Heroku
        final ConfigStoreOptions configStoreEnvOptions = new ConfigStoreOptions()
            .setType("env")
            .setOptional(true);

        configRetriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
            .addStore(configStoreFileOptions)
            .addStore(configStoreSysOptions)
            .addStore(configStoreEnvOptions)
        );
    }

    @Override
    public Future<JsonObject> getConfig() {
        final Promise<JsonObject> promise = Promise.promise();
        configRetriever.getConfig(promise);
        return promise.future();
    }

    @Override
    public void close() {
        configRetriever.close();
    }
}
