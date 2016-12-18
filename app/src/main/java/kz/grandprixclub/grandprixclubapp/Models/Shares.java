package kz.grandprixclub.grandprixclubapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Shares implements Serializable {
    private String title, announce, content, media;

    public Shares(String title, String announce, String content, String media) {
        this.title = title;
        this.announce = announce;
        this.content = content;
        this.media = media;
    }

    public static Shares[] getSharesAsArray(JSONArray j) throws JSONException {
        if (j.length() < 1) {
            return null;
        }
        Shares[] shareses = new Shares[j.length()];
        for (int i = 0; i < j.length(); i++) {
            JSONObject jShare = j.optJSONObject(i);
            shareses[i] = new Shares(jShare.getString("title"),
                    jShare.getString("announce"),
                    jShare.getString("content"),
                    jShare.getString("media"));
        }

        return shareses;
    }

    public String getTitle() {
        return title;
    }

    public String getAnnounce() {
        return announce;
    }

    public String getContent() {
        return content;
    }

    public String getMedia() {
        return media;
    }
}
