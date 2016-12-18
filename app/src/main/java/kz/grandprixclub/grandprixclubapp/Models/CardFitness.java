package kz.grandprixclub.grandprixclubapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CardFitness {
    private int fitnessCardId;
    private String fitnessCardDate;
    private int fitnessCardClienId;
    private List<List<String>> fitnessCardInfo;
    private int infoCount;

    public CardFitness(int fitnessCardId, String fitnessCardDate,
                       int fitnessCardClienId, List<List<String>> fitnessCardInfo, int infoCount) {
        this.fitnessCardId = fitnessCardId;
        this.fitnessCardDate = fitnessCardDate;
        this.fitnessCardClienId = fitnessCardClienId;
        this.fitnessCardInfo = fitnessCardInfo;
        this.infoCount = infoCount;
    }

    public static CardFitness[] getCardsFitnessAsArray(JSONArray jCards) throws JSONException {
        CardFitness[] cardFitnesses = new CardFitness[jCards.length()];

        for (int i = 0; i < jCards.length(); i++) {
            JSONObject j = jCards.optJSONObject(i);

            JSONObject jInfo = j.getJSONArray("fitness_card_info").optJSONObject(0);
            Iterator<String> iter = jInfo.keys();

            List<List<String>> fitnessCardInfo = new ArrayList<>();

            List<String> fitnessCardInfoList = new ArrayList<>();
            fitnessCardInfoList.add(0, "Дата");
            fitnessCardInfoList.add(1, j.getString("fitness_card_date"));
            fitnessCardInfo.add(0, fitnessCardInfoList);

            int k = 1;
            while (iter.hasNext()) {
                String key = iter.next();
                String value = jInfo.getString(key);
                List<String> list = new ArrayList<>();
                list.add(0, key);
                list.add(1, value);
                fitnessCardInfo.add(k, list);
                k++;
            }
            cardFitnesses[i] = new CardFitness(j.getInt("fitness_card_id"), j.getString("fitness_card_date"),
                    j.getInt("fitness_card_client_id"), fitnessCardInfo, k);
        }
        return cardFitnesses;
    }

    public int getFitnessCardId() {
        return fitnessCardId;
    }

    public String getFitnessCardDate() {
        return fitnessCardDate;
    }

    public int getInfoCount() {
        return infoCount;
    }

    public int getFitnessCardClienId() {
        return fitnessCardClienId;
    }

    public List<List<String>> getFitnessCardInfo() {
        return fitnessCardInfo;
    }
}
