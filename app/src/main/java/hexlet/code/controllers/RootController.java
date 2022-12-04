package hexlet.code.controllers;

import io.javalin.http.Handler;

public class RootController {
    public static final Handler Welcome = ctx -> ctx.render("index.html");
}

