package com.internshiporganizer.ApiClients;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.internshiporganizer.Entities.Employee;
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.Entities.InternshipParticipant;
import com.internshiporganizer.Updatable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InternshipCreationClient extends BaseClient {
    private static final String internshipsUrl = "internships";
    private static final String internshipParticipantsUrl = "internshipParticipants";
    private RequestQueue queue;
    private Context context;
    private Updatable<Internship> updatable;

    public InternshipCreationClient(Context context, Updatable<Internship> updatable) {
        this.context = context;
        this.updatable = updatable;
        queue = Volley.newRequestQueue(context);
    }

    public void create(Internship newInternship, Employee administrator, List<Employee> employees) {
        create(baseUrl + internshipsUrl, newInternship, administrator, employees);

    }

    private void create(String url, Internship newInternship, final Employee administrator, final List<Employee> employees) {
        final Gson gson = new Gson();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(gson.toJson(newInternship));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Internship internship = gson.fromJson(response.toString(), new TypeToken<Internship>() {
                }.getType());

                addAdministrator(internship, administrator);
                addEmployees(internship, employees);

                if (updatable != null) {
                    updatable.update(internship);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(context, "Cannot create internship", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }

    private void addAdministrator(Internship internship, Employee administrator) {
        InternshipParticipant internshipParticipant = new InternshipParticipant();
        internshipParticipant.setAdmin(true);
        internshipParticipant.setEmployee(administrator);
        internshipParticipant.setInternship(internship);

        addEmployee(baseUrl + internshipParticipantsUrl, internshipParticipant);
    }

    private void addEmployees(Internship internship, List<Employee> employees) {
        for (Employee employee : employees) {
            InternshipParticipant internshipParticipant = new InternshipParticipant();
            internshipParticipant.setAdmin(false);
            internshipParticipant.setEmployee(employee);
            internshipParticipant.setInternship(internship);

            addEmployee(baseUrl + internshipParticipantsUrl, internshipParticipant);
        }
    }

    private void addEmployee(String url, InternshipParticipant internshipParticipant) {
        final Gson gson = new Gson();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(gson.toJson(internshipParticipant));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                InternshipParticipant internshipParticipant = gson.fromJson(response.toString(), new TypeToken<InternshipParticipant>() {
                }.getType());
                ArrayList<InternshipParticipant> arrayList = new ArrayList<>();
                arrayList.add(internshipParticipant);

//                String registrationToken = "YOUR_REGISTRATION_TOKEN";
//                RemoteMessage remoteMessage = new RemoteMessage
//                FirebaseMessaging.getInstance().send();
//                        Message message = Message.builder()
//                        .putData("score", "850")
//                        .putData("time", "2:45")
//                        .setToken(registrationToken)
//                        .build();
//
//                String response = FirebaseMessaging.getInstance().send(message);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(context, "Cannot add internship participant", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }
}