package hexlet.code;

import hexlet.code.domain.Url;

import hexlet.code.domain.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;

import static org.assertj.core.api.Assertions.assertThat;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AppTest {
    private static Javalin app;
    private static String baseUrl;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
        Database db = DB.getDefault();
        db.truncate("url");
        Url existingUrl = new Url("https://www.youtube.com");
        existingUrl.save();
    }


    @Test
    void testUrls() {

        // Выполняем GET запрос на адрес http://localhost:port/urls
        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls")
                .asString();
        // Получаем тело ответа
        String content = response.getBody();

        // Проверяем код ответа
        assertThat(response.getStatus()).isEqualTo(200);
        // Проверяем, что страница содержит определенный текст
        assertThat(response.getBody()).contains("https://www.youtube.com");
    }

    @Test
    void testNewUrl() {

        HttpResponse<String> response = Unirest
                .get(baseUrl + "/url/new")
                .asString();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void testAddUrl() {
        HttpResponse<String> responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("name", "https://www.youtube.com")
                .asString();

        // Проверяем статус ответа
        assertThat(responsePost.getStatus()).isEqualTo(500);

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

        //assertThat(responsePost.getBody()).contains("Некорректный URL");

        Url actualUrl = new QUrl()
                .name.equalTo("https://www.youtube.com\"")
                .findOne();
        assertThat(actualUrl).isNull();
    }
}
