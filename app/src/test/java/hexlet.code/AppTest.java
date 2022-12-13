package hexlet.code;

import hexlet.code.domain.Url;

import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;

import static org.assertj.core.api.Assertions.assertThat;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class AppTest {
    private static Javalin app;
    private static String baseUrl;
    private static Database database;
    private static final String FIXTURES_DIRECTORY = "src/test/resources/fixtures";
    private final int status200 = 200;
    private final int status500 = 500;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();
    }

    @AfterAll
    public static void afterAll() {
        database.script().run("/truncate.sql");
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
        database.script().run("/truncate.sql");
        Url existingUrl = new Url("https://www.youtube.com");
        existingUrl.save();
    }


    @Test
    void testUrls() {
        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls")
                .asString();
        String content = response.getBody();

        assertThat(response.getStatus()).isEqualTo(status200);
        assertThat(response.getBody()).contains("https://www.youtube.com");
    }
    @Test
    void testAddUrl() {
        HttpResponse<String> responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("name", "https://www.youtube.com")
                .asString();

        assertThat(responsePost.getStatus()).isEqualTo(status500);

        Url actualUrl = new QUrl()
                .name.equalTo("https://www.youtube.com")
                .findOne();
        assertThat(actualUrl).isNotNull();
        assertThat(actualUrl.getName()).isEqualTo("https://www.youtube.com");
    }

    @Test
    void testAddBadUrl() {
        HttpResponse<String> responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("name", "httpppps://www.youtube.com")
                .asString();

        Url actualUrl = new QUrl()
                .name.equalTo("httpppps://www.youtube.com")
                .findOne();
        assertThat(actualUrl).isNull();
    }

    @Test
    void checkUrl() throws IOException {
        String samplePage = Files.readString(Paths.get(FIXTURES_DIRECTORY, "sample.html"));

        MockWebServer mockServer = new MockWebServer();
        String samplePageUrl = mockServer.url("/").toString();
        mockServer.enqueue(new MockResponse().setBody(samplePage));

        HttpResponse response = Unirest
                .post(baseUrl + "/urls/")
                .field("url", samplePageUrl)
                .asEmpty();

        Url url = new QUrl()
                .name.equalTo(samplePageUrl.substring(0, samplePageUrl.length() - 1))
                .findOne();

        assertThat(url).isNotNull();

        HttpResponse response1 = Unirest
                .post(baseUrl + "/urls/" + url.getId() + "/checks")
                .asEmpty();

        HttpResponse<String> response2 = Unirest
                .get(baseUrl + "/urls/" + url.getId())
                .asString();


        UrlCheck check = new QUrlCheck()
                .findList().get(0);

        assertThat(check).isNotNull();
        assertThat(check.getUrl().getId()).isEqualTo(url.getId());

        assertThat(response2.getBody()).contains("Sample title");
        assertThat(response2.getBody()).contains("Sample description");
        assertThat(response2.getBody()).contains("Sample header");

        mockServer.shutdown();
    }
}


