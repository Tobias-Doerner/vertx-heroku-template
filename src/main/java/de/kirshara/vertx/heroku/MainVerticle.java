package de.kirshara.vertx.heroku;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

    private ConfigRetriever configRetriever;
    private JsonObject config;

    @Override
    public void start(Future<Void> startFuture) {
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

        final ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions()
            .addStore(configStoreFileOptions)
            .addStore(configStoreSysOptions)
            .addStore(configStoreEnvOptions);

        configRetriever = ConfigRetriever.create(vertx, configRetrieverOptions);

        configRetriever.getConfig(json -> {
            if (json.succeeded()) {
                config = json.result();

                vertx.deployVerticle(
                    HttpServerVerticle::new,
                    new DeploymentOptions().setConfig(config), ar -> {
                        if (ar.succeeded()) {
                            startFuture.complete();
                        } else {
                            startFuture.fail(ar.cause());
                        }
                    });
            } else {
                startFuture.fail(json.cause());
            }
        });
    }

    @Override
    public void stop() throws Exception {
        configRetriever.close();
        super.stop();
    }
}
