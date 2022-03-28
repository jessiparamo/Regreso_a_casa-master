package com.example.regresoacasa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;
    public TextView txtUltimaLocalizacion;
    private TextView txtLongActual;
    private TextView txtLatActual;
    private TextView txtLongCasa;
    private TextView txtLatCasa;
    private Button btnAcualizarCasa;
    private Button btnDeVueltaACasa;
    private LocationCallback locationCallback;
    private double lat=20.1215431;
    private double lon=-101.1964866;
    private boolean rutaEstablecida=false;

    private MapView vistaCasa;

    private GoogleMap googleMap;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verificiar();

        this.txtUltimaLocalizacion = findViewById(R.id.txtUltimaLocalizacion);
        this.txtLongActual= findViewById(R.id.txtLongActual);
        this.txtLatActual=findViewById(R.id.txtLatAcual);
        this.txtLongCasa=findViewById(R.id.txtLongCasa);
        this.txtLatCasa=findViewById(R.id.txtLatCasa);
        this.btnAcualizarCasa= findViewById(R.id.btnActualizarCasa);
        this.btnDeVueltaACasa=findViewById(R.id.btnDeVueltaACasa);


        Bundle mapViewBundle = null;
        if (savedInstanceState !=null){
            mapViewBundle= savedInstanceState.getBundle("AIzaSyDZx8ah9aVIZ57h7k1-V2DnivCSKNkLtXA");
        }
        this.vistaCasa=findViewById(R.id.mapView);
        vistaCasa.onCreate(mapViewBundle);
        vistaCasa.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            txtUltimaLocalizacion.setText("se requieren permisos");

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    //Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_LONG).show();
                    txtUltimaLocalizacion.setText(location.getLongitude() + " , " + location.getLatitude());
                }
            }
        });
        //createLocationRequest();


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                //Toast.makeText(MainActivity.this, "nueva peticion", Toast.LENGTH_SHORT).show();

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    if (!rutaEstablecida)
                        txtUltimaLocalizacion.setText(location.getLongitude() + " , " + location.getLatitude());


                    txtLongActual.setText(location.getLongitude()+"");
                    lat=location.getLatitude();
                    lon = location.getLongitude();
                    txtLatActual.setText(location.getLatitude()+"");
                }
            }
        };

        btnDeVueltaACasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("latActual",txtLatActual.getText().toString());
                intent.putExtra("longActual",txtLongActual.getText().toString());
                intent.putExtra("latCasa",txtLatCasa.getText().toString());
                intent.putExtra("longCasa",txtLongCasa.getText().toString());
                if (!txtLongCasa.getText().toString().equals("")) {
                    intent.putExtra("longCasa",txtLongCasa.getText().toString());
                    intent.putExtra("latCasa",txtLatCasa.getText().toString());
                }

                startActivity(intent);
            }
        });

        btnAcualizarCasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LatLng nueva = googleMap.getCameraPosition().target;
                LatLng actual = new LatLng(lat,lon);
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(actual).title("Ubicacion Actual"));
                googleMap.addMarker(new MarkerOptions().position(nueva).title("Ubicacion Casa"));
                txtLongCasa.setText(nueva.longitude+"");
                txtLatCasa.setText(nueva.latitude+"");


            }
        });



    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                6);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        vistaCasa.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        vistaCasa.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vistaCasa.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vistaCasa.onDestroy();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            txtUltimaLocalizacion.setText("se requieren permisos para peticiones de ubicacion periodicas");
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void verificiar(){
        int Permisos = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        if (Permisos == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Ya tienes permiso",Toast.LENGTH_SHORT).show();;
        }else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap = googleMap;
        this.googleMap=googleMap;
        Intent intent = getIntent();
        double latActual=lat;
        double longActual=lon;
        LatLng actual = new LatLng(latActual,longActual);

        googleMap.addMarker(new MarkerOptions().position(actual).title("Ubicacion Actual"));
        // Add a marker in Sydney and move the camera
        CameraPosition cameraPosition = new CameraPosition.Builder().target(actual).zoom(18).bearing(30).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
