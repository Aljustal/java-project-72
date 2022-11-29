package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.Handler;

import java.net.URL;
import java.util.List;


public class UrlController {

    public static Handler listUrls = ctx -> {

        List<Url> urls = new QUrl()
                .orderBy()
                .id.asc()
                .findList();

        ctx.attribute("urls", urls);
        ctx.render("urls/index.html");
    };

    public static Handler showUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        ctx.attribute("ursl", url);
        ctx.render("urls/show.html");
    };
    public static Handler addUrl = ctx -> {
        String urlFromPost = ctx.formParam("url");

        //assert urlFromPost != null;
        //URL generalUrl = new URL(urlFromPost);

        Url url = new Url(urlFromPost);
        url.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.redirect("/");
    };
}

