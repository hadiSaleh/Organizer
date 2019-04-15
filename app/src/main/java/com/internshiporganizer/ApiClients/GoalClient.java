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
import com.internshiporganizer.Entities.Goal;
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.Updatable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GoalClient extends BaseClient {
    private static final String goalsUrl = "/goals";
    private RequestQueue queue;
    private Context context;
    private Updatable<List<Goal>> updatable;

    public GoalClient(Context context, Updatable<List<Goal>> updatable) {
        this.context = context;
        this.updatable = updatable;
        queue = Volley.newRequestQueue(context);
    }

    public void createGoal(Goal newGoal, Internship internship, List<Employee> employees) {
        String url = baseUrl + goalsUrl;

        for (Employee employee : employees) {
            Goal goal = new Goal(newGoal);
            goal.setInternship(internship);
            goal.setEmployee(employee);
            create(url, goal);
        }
    }

    public void getAllByEmployeeAndInternship(long internshipId, long employeeId) {
        String url = baseUrl + goalsUrl + "/byInternship/" + internshipId + "/byEmployee/" + employeeId;
        get(url);
    }

    public void getById(long id) {
        String url = baseUrl + goalsUrl + "/" + id;
        getOne(url);
    }

    private void create(String url, Goal newGoal) {
        final Gson gson = new Gson();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(gson.toJson(newGoal));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Goal goal = gson.fromJson(response.toString(), new TypeToken<Goal>() {
                }.getType());
                ArrayList<Goal> arrayList = new ArrayList<>();
                arrayList.add(goal);

                if (updatable != null) {
                    updatable.update(arrayList);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(context, "Cannot create goal", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }

    private void get(String url) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                List<Goal> list = gson.fromJson(response.toString(), new TypeToken<List<Goal>>() {
                }.getType());

                if (updatable != null) {
                    updatable.update(list);
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load goals", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }

    private void getOne(String url) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                Goal goal = gson.fromJson(response.toString(), new TypeToken<Goal>() {
                }.getType());

                ArrayList<Goal> arrayList = new ArrayList<>();
                arrayList.add(goal);

                if (updatable != null) {
                    updatable.update(arrayList);
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load goal", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }
}
