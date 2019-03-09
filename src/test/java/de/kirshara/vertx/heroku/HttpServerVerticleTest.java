package de.kirshara.vertx.heroku;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpServer Test")
@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpServerVerticleTest {

    private WebClient webClient;

    @BeforeAll
    void setUp(Vertx vertx, VertxTestContext testContext) {
        webClient = WebClient.create(vertx);

        ConfigStoreOptions configStoreOptions = new ConfigStoreOptions()
            .setType("file")
            .setConfig(
                new JsonObject()
                    .put("path", "config.json")
            );

        ConfigRetrieverOptions configRetrieverOptions =
            new ConfigRetrieverOptions()
                .addStore(configStoreOptions);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, configRetrieverOptions);

        DeploymentOptions deploymentOptions = new DeploymentOptions();

        retriever.getConfig(testContext.succeeding(config -> {
            deploymentOptions.setConfig(config);

            vertx.deployVerticle(HttpServerVerticle::new, deploymentOptions, testContext.succeeding(id -> {
                testContext.completeNow();
            }));
        }));
    }

    @AfterAll
    @Timeout(value = 3, timeUnit = TimeUnit.SECONDS)
    void tearDown(Vertx vertx, VertxTestContext testContext) {
        webClient.close();
        vertx.close(testContext.succeeding(res -> {
            testContext.completeNow();
        }));
    }

    @Test
    @DisplayName("Test Http Server")
    @Timeout(value = 3, timeUnit = TimeUnit.SECONDS)
    void httpServerTest(VertxTestContext testContext) {
        webClient.get(8080, "localhost", "/")
            .as(BodyCodec.string())
            .send(testContext.succeeding(res ->
                testContext.verify(() -> {
                    assertThat(res.statusCode()).isEqualTo(200);
                    assertThat(res.body()).contains("Hello World!");
                    testContext.completeNow();
                })
            ));
    }
}
