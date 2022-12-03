package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;

import hexlet.code.domain.query.QUrlCheck;
import io.javalin.http.Handler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class UrlController {

    public static Handler listUrls = ctx -> {
        List<Url> urls = new QUrl()
                .orderBy()
                .id.asc()
                .findList();

        ctx.attribute("urls", urls);
        ctx.render("urls.html");
    };


    public static Handler addUrl = ctx -> {
        String urlFromPost = ctx.formParam("url");

        try {
            assert urlFromPost != null;
            URL generalUrl = new URL(urlFromPost);
            String correctURL = generalUrl.getProtocol() + "://" + generalUrl.getAuthority();

            Url checkUrl = new QUrl()
                    .name.equalTo(correctURL)
                    .findOne();

            if (checkUrl != null) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.redirect("/urls");
                return;
            }

            Url url = new Url(correctURL);
            url.save();

            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect("/urls");

        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
        }
    };

    public static Handler showUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        List<UrlCheck> urlChecks = new QUrlCheck()
                .url.equalTo(url)
                .orderBy().id.desc()
                .findList();

        ctx.attribute("urlChecks", urlChecks);
        ctx.attribute("url", url);
        ctx.render("show.html");
    };


    public static Handler checkUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        HttpResponse<String> response = Unirest
                .get(url.getName())
                .asString();

        String content = response.getBody();

        Document body = Jsoup.parse(content);

        int statusCode = response.getStatus();
        String title = body.title();
        String h1 = body.selectFirst("h1") != null
                ? Objects.requireNonNull(body.selectFirst("h1")).text()
                : null;
        String description = body.selectFirst("meta[name=description]") != null
                ? Objects.requireNonNull(body.selectFirst("meta[name=description]")).attr("content")
                : null;

        UrlCheck check = new UrlCheck(statusCode, title, h1, description, url);
        check .save();

        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls/" + id);
    };
}

