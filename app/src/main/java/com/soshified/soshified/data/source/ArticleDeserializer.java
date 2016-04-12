package com.soshified.soshified.data.source;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.soshified.soshified.data.Article;

import java.lang.reflect.Type;

/**
 * Custom serializer for the Article object. Bleh.
 */
public class ArticleDeserializer implements JsonDeserializer<Article> {
    @Override
    public Article deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        Article article = gson.fromJson(json, Article.class);
        article.setAuthorName(json.getAsJsonObject().get("author")
                .getAsJsonObject().get("name").getAsString());

        article.setThumbnail(json.getAsJsonObject().get("thumbnail_images")
                .getAsJsonObject().get("full").getAsJsonObject().get("url").getAsString());

        return article;
    }
}
