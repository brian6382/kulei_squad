import static java.lang.Integer.parseInt;
import static spark.Spark.*;

import models.Heroes;
import models.Squads;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;
import java.util.Map;
import java.util.HashMap;


  class App {
        static int getHerokuAssignedPort() {
            ProcessBuilder processBuilder = new ProcessBuilder();
            if (processBuilder.environment().get("PORT") != null) {
                return Integer.parseInt(processBuilder.environment().get("PORT"));
            }
            return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
        }

    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        staticFileLocation("/public");
        //Get: View the Home page
        get("/", (request, response) -> {
            Map<String, Object> user = new HashMap<>();
            return new ModelAndView(user, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //Get: Add squad form
        get("/squads/new", (request, response) -> {
            Map<String, Object> user = new HashMap<>();
            return new ModelAndView(user, "Squad-form.hbs");
        }, new HandlebarsTemplateEngine());

        //Post: Submit data from the add squad form
        post("/squads/new", App::handle, new HandlebarsTemplateEngine());

        //Get: View all squads
        get("/squads", (request, response) -> {
            Map<String, Object> user = new HashMap<>();
            user.put("userSquad", Squads.getAllSquads());
            return new ModelAndView(user, "Squads.hbs");
        }, new HandlebarsTemplateEngine());

        //Get: View squad-details e.g heroes
        get("/squads/:id", (request, response) -> {
            Map<String, Object> user = new HashMap<>();
            int theId = parseInt(request.params("id"));
            Squads pageId = Squads.squadWithId(theId);
            user.put("squadHeroes", pageId);
            return new ModelAndView(user, "Heroes.hbs");
        }, new HandlebarsTemplateEngine());

        //Get: Add hero form
        get("/squads/:id/new", (request, response) -> {
            Map<String, Object> user = new HashMap<>();
            int theId = parseInt(request.params("id"));
            Squads heroes = Squads.squadWithId(theId);
            user.put("heroes", heroes);
            user.put("squads", Squads.getAllSquads());
            return new ModelAndView(user, "Hero-form.hbs");
        }, new HandlebarsTemplateEngine());

        //Get: View the added heroes
        get("/squads/:id/heroes", (request, response) -> {
            Map<String, Object> user  = new HashMap<>();
            int theId = parseInt(request.params("id"));
            Squads squad = Squads.squadWithId(theId);
            user.put("allHeroes", squad.getHeroesInSquad());
            return new ModelAndView(user, "View-heroes.hbs");
        }, new HandlebarsTemplateEngine());

        //Post: Submit add hero form
        post("/squads/:id/new", (request, response) -> {
            Map<String, Object> user = new HashMap<>();
            int theId = parseInt(request.params("id"));
            Squads squad = Squads.squadWithId(theId);
            String heroName = request.queryParams("hero");
            String superPowers = request.queryParams("superPowers");
            String role = request.queryParams("role");
            Heroes newHero = new Heroes(heroName, superPowers, role);
            if (squad.getMaxHeroes() <= squad.getHeroesInSquad().size()){
                String sorry = "Sorry, this squad is full";
                user.put("full", sorry);
            }else{
                squad.addHero(newHero);
                user.put("heroes", squad.getHeroesInSquad());
            }

            return new ModelAndView(user, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //Get: Delete a squad
        get("/squads/:id/delete", (req, res) -> {
            Map<String, Object> user = new HashMap<>();
            int theId = parseInt(req.params("id"));
            Squads pageId = Squads.squadWithId(theId);
            pageId.deleteSquad();
            return new ModelAndView(user, "success.hbs");
        }, new HandlebarsTemplateEngine());
    }

    private static ModelAndView handle(Request request, Response response) {
        Map<String, Object> user = new HashMap<>();
        String squadName = request.queryParams("squadName");
        String theme = request.queryParams("theme");
        String url = request.queryParams("url");
        int numberOf = parseInt(request.queryParams("max"));
        Squads userSquad;
        userSquad = new Squads(squadName, theme, url, numberOf);
        user.put("userSquad", Squads.getAllSquads());
        return new ModelAndView(user, "success.hbs");
    }ajah
}
