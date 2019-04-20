package com.internshiporganizer.ApiClients;

import android.content.Context;
import android.widget.Toast;

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
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.Entities.Request;
import com.internshiporganizer.Updatable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestClient extends BaseClient {
    private static final String requstsUrl = "/requests";
    private RequestQueue queue;
    private Context context;
    private Updatable<List<Request>> updatable;

    public RequestClient(Context context, Updatable<List<Request>> updatable) {
        this.context = context;
        this.updatable = updatable;
        queue = Volley.newRequestQueue(context);
    }

    public void createRequest(Request newRequest, Internship internship, List<Employee> employees) {
        String url = baseUrl + requstsUrl;

        for (Employee employee : employees) {
            Request request = new Request(newRequest);
            request.setInternship(internship);
            request.setEmployee(employee);
            post(url, request);
        }
    }

    public void completeRequest(Request request) {
        String url = baseUrl + requstsUrl + "/complete";

        post(url, request);
    }

    public void getAllByEmployeeAndInternship(long internshipId, long employeeId) {
        String url = baseUrl + requstsUrl + "/byInternship/" + internshipId + "/byEmployee/" + employeeId;
        get(url);
    }

    public void getById(long id) {
        String url = baseUrl + requstsUrl + "/" + id;
        getOne(url);
    }

    private void post(String url, Request newRequest) {
        final Gson gson = new Gson();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(gson.toJson(newRequest));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Request request = gson.fromJson(response.toString(), new TypeToken<Request>() {
                }.getType());
                ArrayList<Request> arrayList = new ArrayList<>();
                arrayList.add(request);

                if (updatable != null) {
                    updatable.update(arrayList);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(context, "Cannot create or update request", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }

    private void get(String url) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(com.android.volley.Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                List<Request> list = gson.fromJson(response.toString(), new TypeToken<List<Request>>() {
                }.getType());

                if (updatable != null) {
                    updatable.update(list);
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load requests", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }

    private void getOne(String url) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                Request request = gson.fromJson(response.toString(), new TypeToken<Request>() {
                }.getType());

                ArrayList<Request> arrayList = new ArrayList<>();
                arrayList.add(request);

                if (updatable != null) {
                    updatable.update(arrayList);
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Cannot load request", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }
}