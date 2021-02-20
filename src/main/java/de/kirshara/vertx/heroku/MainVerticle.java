package de.kirshara.vertx.heroku;

import de.kirshara.vertx.heroku.config.ConfigManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

    private ConfigManager configManager;

    @Override
    public void start(Promise<Void> startPromise) {
        configManager = ConfigManager.getDefaultConfigManager(vertx);
        configManager.getConfig()
            .onSuccess(json -> vertx.deployVerticle(
                HttpServerVerticle::new,
                new DeploymentOptions().setConfig(json), ar -> {
                    if (ar.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail(ar.cause());
                    }
                }))
            .onFailure(startPromise::fail);
    }

    @Override
    public void stop() throws Exception {
        configManager.close();
        super.stop();
    }
}
