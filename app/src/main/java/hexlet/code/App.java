package hexlet.code;
import hexlet.code.controllers.RootController;
import hexlet.code.controllers.UrlController;

import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.get;

public final class App {

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "5000");
        return Integer.parseInt(port);
    }

    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setCharacterEncoding("UTF-8");
        templateEngine.addTemplateResolver(templateResolver);

        return templateEngine;
    }

    private static void addRoutes(Javalin app) {
        app.get("/", RootController.WELCOME);

        app.routes(() -> {
            path("urls", () -> {
                get(UrlController.LIST_URLS);
                post(UrlController.ADD_URL);
                path("{id}", () -> {
                    get(UrlController.SHOW_URL);
                    post("/checks", UrlController.CHECK_URL);
                });
            });
        });
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.enableDevLogging();
            JavalinThymeleaf.configure(getTemplateEngine());
        });

        addRoutes(app);

        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });

        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
}
