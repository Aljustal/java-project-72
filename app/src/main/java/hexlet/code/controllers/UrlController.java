package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.Handler;

import java.net.MalformedURLException;
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

        ctx.attribute("url", url);
        ctx.render("urls/show.html");
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
                ctx.redirect("/");
                return;
            }

            Url url = new Url(correctURL);
            url.save();

            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.redirect("/urls");

        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect("/");
        }
    };
}

