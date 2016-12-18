package kz.grandprixclub.grandprixclubapp.Models;

import org.json.JSONArray;
import org.json.JSONException;

public class Employe {
    private String employeeId, fullname;


    public Employe(String employeeId, String fullname) {
        this.employeeId = employeeId;
        this.fullname = fullname;
    }

    public static Employe[] getEmployeesAsArray(JSONArray jEmploye) throws JSONException {
        Employe[] employes = new Employe[jEmploye.length()];

        for (int i = 0; i < jEmploye.length(); i++) {
            employes[i] = new Employe(jEmploye.optJSONObject(i).getString("employee_id"),
                    jEmploye.optJSONObject(i).getString("fullname"));
        }
        return employes;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getFullname() {
        return fullname;
    }

    @Override
    public String toString() {
        return fullname;
    }
}
