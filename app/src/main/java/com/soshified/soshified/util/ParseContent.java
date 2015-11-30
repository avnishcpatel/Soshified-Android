package com.soshified.soshified.util;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Parses the content recieved from the server to remove things and fix some things
 */
public class ParseContent extends AsyncTask<String, Void, String> {

    OnParseCompleteListener listener;

    public ParseContent(OnParseCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        Document html = Jsoup.parse(params[0]);

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

    @Override
    protected void onPostExecute(final String s) {
        super.onPostExecute(s);
        listener.onParsed(s);
    }

    public interface OnParseCompleteListener {
        void onParsed(String mParsedPostContent);
    }
}
