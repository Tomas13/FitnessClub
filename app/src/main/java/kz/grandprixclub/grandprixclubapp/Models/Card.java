package kz.grandprixclub.grandprixclubapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Card {
    private int cardId;
    private String date;
    private String physicianName;
    private String complaint;
    private String diagnosis;
    private String conclusion;
    private String recommendation;
    private String diary;

    public Card(int cardId, String date, String physicianName, String complaint, String diagnosis,
                String conclusion, String recommendation, String diary) {
        this.cardId = cardId;
        this.date = date;
        this.physicianName = physicianName;
        this.complaint = complaint;
        this.diagnosis = diagnosis;
        this.conclusion = conclusion;
        this.recommendation = recommendation;
        this.diary = diary;
    }

    public Card(int cardId, String date, String physicianName, String complaint) {
        this.cardId = cardId;
        this.date = date;
        this.physicianName = physicianName;
        this.complaint = complaint;
    }

    public static Card getCard(JSONObject j) throws JSONException {
        return new Card(j.getInt("card_id"), j.getString("date"), j.getString("physician_name"),
                j.getString("complaint"), j.getString("diagnosis"), j.getString("conclusion"),
                j.getString("recommendation"), j.getString("diary"));
    }

    public static Card[] getCardsAsArray(JSONArray jCards) throws JSONException {
        Card[] cards = new Card[jCards.length()];

        for (int i = 0; i < jCards.length(); i++) {
            JSONObject j = jCards.optJSONObject(i);
            cards[i] = new Card(j.getInt("card_id"), j.getString("date"), j.getString("physician_name"), j.getString("complaint"));
        }

        return cards;
    }

    public int getCardId() {
        return cardId;
    }

    public String getDate() {
        return date;
    }

    public String getPhysicianName() {
        return physicianName;
    }

    public String getComplaint() {
        return complaint;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getConclusion() {
        return conclusion;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public String getDiary() {
        return diary;
    }
}
