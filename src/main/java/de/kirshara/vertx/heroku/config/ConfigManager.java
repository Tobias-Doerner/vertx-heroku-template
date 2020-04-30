package de.kirshara.vertx.heroku.config;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public interface ConfigManager {

    static ConfigManager getDefaultConfigManager(Vertx vertx) {
        return new DefaultConfigManager(vertx);
    }

    Future<JsonObject> getConfig();

    void close();
}
