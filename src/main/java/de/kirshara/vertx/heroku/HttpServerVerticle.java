package de.kirshara.vertx.heroku;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

public class HttpServerVerticle extends AbstractVerticle {

    private HttpServer server;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        final Router router = Router.router(vertx);
        router.route().handler(ctx ->
            ctx
                .response()
                .putHeader("content-type", "text/plain")
                .end("Hello World!\nJava Runtime Version: "
                    + config().getString("java.runtime.version", ""))
        );

        server = vertx.createHttpServer(
            new HttpServerOptions()
                .setPort(config().getInteger("http.port"))
                .setHost(config().getString("http.address", "0.0.0.0"))
        );

        server.requestHandler(router).listen(ar -> {
            if (ar.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        server.close(ar -> {
            if (ar.succeeded()) {
                stopFuture.complete();
            } else {
                stopFuture.fail(ar.cause());
            }
        });
    }
}
