package appmoviles.com.pratico1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    //--------------------------------------------
    //Atributos
    //--------------------------------------------
    private static final int REQUEST_CODE = 11;
    private LocationManager manager;
    private GoogleMap mMap;
    private LatLng usuario;
    private List<MarkerOptions> markerOptions;
    private FloatingActionButton pregunta;
    private FloatingActionButton tienda;
    private Marker markerActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        markerOptions = new ArrayList<>();
        pregunta = findViewById(R.id.pregunta);
        pregunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent(MapsActivity.this,Pregunta.class);
                startActivity(j);
            }
        });

        tienda = findViewById(R.id.tienda);
        tienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent(MapsActivity.this,Tienda.class);
                startActivity(j);
            }
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, REQUEST_CODE);

        // Add a marker in Icesi and move the camera
        usuario = new LatLng(3.341683, -76.530434);
        mMap.addMarker(new MarkerOptions().position(usuario).title("Yo"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(usuario));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(usuario,20));

        MarkerOptions options = new MarkerOptions().title("Dificil")
                .position(new LatLng(3.341200,-76.529392));
        markerOptions.add(options);
        mMap.addMarker(markerOptions.get(markerOptions.size()-1));

        options = new MarkerOptions().title("Facil")
                .position(new LatLng(3.342550,-76.529698));
        markerOptions.add(options);
        mMap.addMarker(markerOptions.get(markerOptions.size()-1));

        options = new MarkerOptions().title("Tienda")
                .position(new LatLng(3.341773,-76.529896));
        markerOptions.add(options);
        mMap.addMarker(markerOptions.get(markerOptions.size()-1));

        mMap.setOnMapClickListener(this);

        //Agregar un listener de ubicacion
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear();
                for(int i=0;i<markerOptions.size();i++){
                    mMap.addMarker(markerOptions.get(i));
                }
                usuario=new LatLng(location.getLatitude(),location.getLongitude());
                markerActual =  mMap.addMarker(new MarkerOptions().position(usuario).title("Yo"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(usuario));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(usuario,20));
                calcularCercano();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

    @SuppressLint("RestrictedApi")
    public void calcularCercano(){
        Location act = new Location("actual");
        act.setLatitude(usuario.latitude);
        act.setLongitude(usuario.longitude);
        float referencia = 100000;
        int indice = 0;
        for(int i=0;i<markerOptions.size();i++){
            Location lc = new Location("n"+i);
            lc.setLatitude(markerOptions.get(i).getPosition().latitude);
            lc.setLongitude(markerOptions.get(i).getPosition().longitude);
            float dist = act.distanceTo(lc);
            if(dist<=referencia){
                referencia=dist;
                indice=i;
            }
        }
        float refCer = 10;
        if(referencia<refCer) {
            //cercano.setText("El lugar mas cercano es: " + markerOptions.get(indice).getTitle() + " a " + referencia + " metros");
            if(markerOptions.get(indice).getTitle()=="Facil"){
                pregunta.show();
                pregunta.callOnClick();
            }
            else if(markerOptions.get(indice).getTitle()=="Dificil"){
                pregunta.show();
                pregunta.callOnClick();
            }
            else if(markerOptions.get(indice).getTitle()=="Tienda"){
                tienda.show();
                tienda.callOnClick();
            }
            else{
                pregunta.hide();
                tienda.hide();
            }
        }

    }

}
