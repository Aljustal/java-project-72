package hexlet.code.controllers;

import io.javalin.http.Handler;

public class RootController {
    public static Handler welcome = ctx -> {
        //ctx.render("index.html");
        ctx.render("layouts/application.html");
    };
}
