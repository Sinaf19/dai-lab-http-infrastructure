package ch.heig.dai.lab.http;

import io.javalin.Javalin;

/**
 * API
 *
 */
public class API
{
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(9292);

        MemeController memeController = new MemeController();
        app.get("/api/memes", memeController::getAllMemes);
        app.get("/api/memes/{id}", memeController::getMemeById);
        app.post("/api/memes", memeController::createMeme);
        app.put("/api/memes/{id}", memeController::updateMeme);
        app.delete("/api/memes/{id}", memeController::deleteMeme);
    }
}
