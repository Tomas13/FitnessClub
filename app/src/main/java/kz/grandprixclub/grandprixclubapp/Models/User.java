package kz.grandprixclub.grandprixclubapp.Models;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String id;

    private String birthday;

    private String phone;

    private int deposit;

    private String address;

    private String name;

    private String apiToken;

    private String lastname;

    private String role;

    private String avatar;

    SharedPreferences userLocalDatabase;
    public static final String SP_NAME = "userDetails";
    public static final String API_TOKEN_KEY = "apiToken";

    public User(String apiToken, String id, String role, String phone, String name, String lastname, String birthday,
                String address, int deposit, String avatar) {
        this.apiToken = apiToken;
        this.id = id;
        this.role = role;
        this.phone = phone;
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.address = address;
        this.deposit = deposit;
        this.avatar = avatar;
    }

    public User(JSONObject jUser) throws JSONException {
        this.apiToken = jUser.getString("api_token");
        this.id = jUser.getString("id");
        this.role = jUser.getString("role");
        this.phone = jUser.getString("phone");
        this.name = jUser.getString("name");
        this.lastname = jUser.getString("lastname");
        this.birthday = jUser.getString("birthday");
        this.address = jUser.getString("address");
        this.deposit = jUser.getInt("deposit");
        this.avatar = jUser.getString("avatar");
    }

    public void saveApiToken (Context context, String apiToken) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString(API_TOKEN_KEY, apiToken);
        spEditor.commit();
        spEditor.apply();

    }

    public static String getSavedApiToken(Context context) {
        SharedPreferences userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
        String apiToken = userLocalDatabase.getString(API_TOKEN_KEY, "");
        return apiToken;
    }

    public static boolean isAuthenticated (Context context) {
        SharedPreferences userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
        String apiToken = userLocalDatabase.getString(API_TOKEN_KEY, "");
        if (apiToken.length() > 0) {
            return true;
        }
        return false;
    }

    public static void userLogoutAction(Context context) {
        SharedPreferences.Editor spEditor = context.getSharedPreferences(SP_NAME, 0).edit();
        spEditor.remove(API_TOKEN_KEY);
        spEditor.commit();
        spEditor.apply();
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getBirthday ()
    {
        return birthday;
    }

    public void setBirthday (String birthday)
    {
        this.birthday = birthday;
    }

    public String getPhone ()
    {
        return phone;
    }

    public void setPhone (String phone)
    {
        this.phone = phone;
    }

    public int getDeposit ()
    {
        return deposit;
    }

    public void setDeposit (int deposit)
    {
        this.deposit = deposit;
    }

    public String getAddress ()
    {
        if (address.length() < 1) {
            return "Адрес не указан";
        }
        return address;
    }

    public void setAddress (String address)
    {
        this.address = address;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getApiToken ()
    {
        return apiToken;
    }

    public void setApiToken (String apiToken)
    {
        this.apiToken = apiToken;
    }

    public String getLastname ()
    {
        return lastname;
    }

    public void setLastname (String lastname)
    {
        this.lastname = lastname;
    }

    public String getRole ()
    {
        return role;
    }

    public void setRole (String role)
    {
        this.role = role;
    }

    public String getAvatar ()
    {
        return avatar;
    }

    public void setAvatar (String avatar)
    {
        this.avatar = avatar;
    }
}
