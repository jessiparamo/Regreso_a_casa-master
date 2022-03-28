package com.example.regresoacasa;


import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    JSONObject jso;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        double latActual=Double.parseDouble(intent.getStringExtra("latActual"));
        double longActual=Double.parseDouble(intent.getStringExtra("longActual"));
        double latCasa=Double.parseDouble(intent.getStringExtra("latCasa"));
        double longCasa=Double.parseDouble(intent.getStringExtra("longCasa"));

        LatLng actual = new LatLng(latActual,longActual);
        LatLng casa = new LatLng(latCasa,longCasa);

        mMap.addMarker(new MarkerOptions().position(actual).title("Ubicacion Actual"));
        mMap.addMarker(new MarkerOptions().position(casa).title("Ubicacion Casa"));

        // Add a marker in Sydney and move the camera
        CameraPosition cameraPosition = new CameraPosition.Builder().target(actual).zoom(18).bearing(30).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+latActual+","+longActual+"&destination="+latCasa+","+longCasa+"&key="+"AIzaSyDZx8ah9aVIZ57h7k1-V2DnivCSKNkLtXA"+"";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    jso = new JSONObject(response);
                    trasaruta(jso);
                    Log.i("JsonRuta:", ""+response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    private void trasaruta(JSONObject jso) {

        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i=0; i<jRoutes.length();i++){

                jLegs = ((JSONObject)jRoutes.get(i)).getJSONArray("legs");

                for (int j=0; j<jLegs.length(); j++){

                    jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");

                    for (int k=0; k<jSteps.length(); k++){
                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");

                        Log.i("end",""+polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        mMap.addPolyline(new PolylineOptions().addAll(list).color(Color.GREEN).width(5));
                    }

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}