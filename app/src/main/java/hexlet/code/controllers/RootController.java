package hexlet.code.controllers;

import io.javalin.http.Handler;

public class RootController {
    public static final Handler welcome = ctx -> ctx.render("index.html");
}
