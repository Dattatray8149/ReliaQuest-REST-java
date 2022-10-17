package com.example.rqchallenge.employees;

import org.json.JSONObject;

public class JSONConverUtils {

    public static Employee convertJsonToEmployee(JSONObject json) {
        if (json == null) {
            return null;
        }
        return new Employee(
                json.getInt("id"),
                json.getString("employee_name"),
                json.getInt("employee_salary"),
                json.getInt("employee_age"),
                json.getString("profile_image"));

    }
}