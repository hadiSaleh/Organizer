package com.internshiporganizer.ApiClients;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.internshiporganizer.Entities.Employee;
import com.internshiporganizer.Entities.NewEmployee;
import com.internshiporganizer.Updatable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EmployeeClient extends BaseClient {
    private static final String employeesUrl = "employees";
    private RequestQueue queue;
    private Context context;
    private Updatable<List<Employee>> updatable;

    public EmployeeClient(Context context, Updatable<List<Employee>> updatable) {
        this.context = context;
        this.updatable = updatable;
        queue = Volley.newRequestQueue(context);
    }

    public void get() {
        String url = baseUrl + employeesUrl;
        get(url);
    }

    public void getOne(long employeeId) {
        String url = baseUrl + employeesUrl + "/" + employeeId;
        getOne(url);
    }

    public void getByInternship(long internshipId) {
        String url = baseUrl + employeesUrl + "/byInternship/" + internshipId;
        get(url);
    }

    public void update(Employee employee) {
        String url = baseUrl + employeesUrl + "/update";
        post(url, employee);
    }

    public void updateToken(Employee employee) {
        String url = baseUrl + employeesUrl + "/updateToken";
        post(url, employee);
    }

    public void createEmployee(NewEmployee newEmployee) {
        String url = baseUrl+ employeesUrl;
        post(url, newEmployee);
    }

    private void post(String url, Employee newEmployee) {
        final Gson gson = new Gson();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(gson.toJson(newEmployee));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Employee employee = gson.fromJson(response.toString(), new TypeToken<Employee>() {
                }.getType());
                ArrayList<Employee> arrayList = new ArrayList<>();
                arrayList.add(employee);

                if (updatable != null) {
                    updatable.update(arrayList);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(context, "Cannot create or update employee", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }

    private void get(String url) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                List<Employee> list = gson.fromJson(response.toString(), new TypeToken<List<Employee>>() {
                }.getType());

                updatable.update(list);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load employees", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }

    private void getOne(String url) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                Employee employee = gson.fromJson(response.toString(), new TypeToken<Employee>() {
                }.getType());

                ArrayList<Employee> arrayList = new ArrayList<>();
                arrayList.add(employee);

                if (updatable != null) {
                    updatable.update(arrayList);
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load employee", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }
}
