package com.soshified.soshified.data.source.remote;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.soshified.soshified.data.Article;
import com.soshified.soshified.util.DateUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;

/**
 * Custom serializer for the Article object. Bleh.
 */
public class ArticleDeserializer implements JsonDeserializer<Article> {
    @Override
    public Article deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        Article article = gson.fromJson(json, Article.class);

        JsonObject root = json.getAsJsonObject();

        article.setAuthorName(root.get("author").getAsJsonObject().get("name").getAsString());

        article.setDate(DateUtils.getUnixTimeStamp(root.get("date").getAsString()));

        article.setThumbnail(root.get("thumbnail_images")
                .getAsJsonObject().get("full").getAsJsonObject().get("url").getAsString());

        article.setPostContent(parsePost(root.get("content").getAsString()));

        return article;
    }

    private String parsePost(String post) {

        Document html = Jsoup.parse(post);

        html.body().attr("style", "color: #444444");

        if (html.select("img").first() != null)
            html.select("img").first().remove();

        if (html.select("br").first() != null)
            html.select("br").first().remove();

        if (html.select("p").last() != null)
            html.select("p").last().remove();

        Elements images = html.select("img");
        images.attr("style", "max-width:100%; margin: 10px 0px");
        images.attr("height", "auto");

        Elements iframes = html.select("iframe");
        iframes.attr("style", "max-width:100%; max-height: auto; margin: 10px 0px");

        return html.html();
    }
}
