package ch.heig.dai.lab.http;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MemeController {

    private ConcurrentHashMap<Integer, Meme> memes = new ConcurrentHashMap<>();
    private int lastId = 0;

    public MemeController() {
        memes.put(++lastId, new Meme ("Java Library", "creating a new java library\ncreating a new java-library-template",
                "https://thriving.dev/_ipx/f_webp&q_80&s_500x501/assets/blog/12.java-library-development-get-started-quickly-with-java-library-template/creating-java-library-template-meme.png"));
        memes.put(++lastId, new Meme ("32-bit integer", "I'm using a 32-bit signed integer to store the current time",
                "https://www.reddit.com/media?url=https%3A%2F%2Fi.redd.it%2Fwindows7usersriseup-v0-rkzujvbqiy9c1.jpeg%3Fs%3D0975864ac80264f8d207b59144ff04e6e8d7e275"));
        memes.put(++lastId, new Meme ("Exit vim", "wait you guys actually can't exit vim? I thought it was a joke",
                "https://www.reddit.com/media?url=https%3A%2F%2Fpreview.redd.it%2Fithoughtitwasajoke-v0-e0jit5zpuu9c1.jpeg%3Fauto%3Dwebp%26s%3Dfcdc4ffb7caacf65c3fb237610a3e2161ea4e2cb"));
    }

    public void getAllMemes(Context ctx) {
        ctx.json(memes);
    }

    public void getMemeById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Meme meme = memes.get(id);
        if (meme != null) {
            ctx.json(meme);
        } else {
            ctx.status(404).result("Meme not found");
        }
    }

    public void createMeme(Context ctx) {
        Meme meme = ctx.bodyAsClass(Meme.class);
        memes.put(++lastId, meme);
        ctx.status(201).result("Meme created");
    }

    public void updateMeme(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Meme meme = ctx.bodyAsClass(Meme.class);
        memes.put(id, meme);
        ctx.status(200).result("Meme updated");
    }

    public void deleteMeme(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        memes.remove(id);
        ctx.status(204).result("Meme deleted");
    }


}
