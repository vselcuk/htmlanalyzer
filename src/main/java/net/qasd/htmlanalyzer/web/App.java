package net.qasd.htmlanalyzer.web;

import net.qasd.htmlanalyzer.web.controller.IndexController;
import spark.template.velocity.VelocityTemplateEngine;

import static spark.Spark.*;

public class App {

    public App() {
        staticFiles.location("/public");

        get("/", (req, res) -> IndexController.serveHomePage(req, res), new VelocityTemplateEngine());
        post("/", (req, res) -> IndexController.executeHtmlAnalyzer(req, res), new VelocityTemplateEngine());
    }

    public static void main(String[] args) {
        new App();
    }


}
