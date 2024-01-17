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
        memes.put(++lastId, new Meme ("why it works", "why it works",
                "https://s3.amazonaws.com/rails-camp-tutorials/blog/programming+memes/works-doesnt-work.jpg"));
        memes.put(++lastId, new Meme ("Why", "Why",
                "https://uploads-ssl.webflow.com/5f3c19f18169b62a0d0bf387/60d33be8cf4ba7565123c8bc_YPD3ulQQAGQpOcnqIm3QzSTRgzmr1SexpW9ZjMpJ1mAnUxx4iF05XOTu44sk0qQG-8XgBcYmGZGAD-5SAZvJl3TjtmhgWnn-w0C2XKwhBscV78RVvhwZfyp0v_Pa6sNj5zxpOvRW.png"));
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
        ctx.status(202).result("Meme updated");
    }

    public void deleteMeme(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        memes.remove(id);
        ctx.status(204).result("Meme deleted");
    }


}
