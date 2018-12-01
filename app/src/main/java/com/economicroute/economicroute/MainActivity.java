package com.economicroute.economicroute;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.GnssStatus;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
// classes needed to initialize map
import com.economicroute.economicroute.adapter.GeocoderAdapter;
import com.economicroute.economicroute.model.Config;
import com.economicroute.economicroute.model.Gas_station;
import com.economicroute.economicroute.model.Route;
import com.economicroute.economicroute.model.Vehicle;
import com.mapbox.geocoder.service.models.GeocoderFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapView;
// classes needed to add the location component
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import android.location.Location;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.mapbox.mapboxsdk.geometry.LatLng;
import android.support.annotation.NonNull;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
// classes needed to add a marker
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
// classes to calculate a route
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
// classes needed to launch navigation UI
import android.view.View;
import android.widget.Button;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {

    private MapView mapView;
    // variables for adding location layer
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private Location originLocation;

    // variables for adding a marker
    private Marker destinationMarker;
    private Marker originMarker;
    private Marker [] gasStationMarker;
    private LatLng originCoord;
    private LatLng destinationCoord;

    // variables for calculating and drawing a route
    private Point originPosition;
    private Point destinationPosition;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    // buttons
    private Button button_navigation_view;
    private ImageButton settings;
    private ImageButton user_location;
    private ImageButton open_search_simulate_origin;
    private ImageButton manage_vehicle;
    private EditText search_origin;
    private TextView distance_view;
    private TextView price_fuel_view;
    private TextView spent_view;

    private AutoCompleteTextView input_search_origin;
    private AutoCompleteTextView input_search_destiny;
    private RelativeLayout box_input_search_origin;

    private Realm realm;
    private Vehicle vehicleUsed;
    private Config config;

    //array models
    Gas_station[] gasStation = new Gas_station[22];
    ArrayList<Gas_station> gasStation_route = new ArrayList<>();
    ArrayList<DirectionsRoute> routes = new ArrayList<>();
    ArrayList<ArrayList> gasStationRoutes = new ArrayList<>();
    private ArrayList<LatLng> points_route = new ArrayList<>();

    private int gasTank = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, Constants.MAPBOX_ACCESS_TOKEN);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            if (decor != null) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        button_navigation_view = findViewById(R.id.button_navigation_view);
        user_location = findViewById(R.id.user_location);
        open_search_simulate_origin = findViewById(R.id.open_search_simulate_origin);
        distance_view = findViewById(R.id.distance_view);
        price_fuel_view = findViewById(R.id.price_fuel_view);
        spent_view = findViewById(R.id.spent_view);
        input_search_origin = findViewById(R.id.input_search_origin);
        input_search_destiny = findViewById(R.id.input_search_destiny);
        box_input_search_origin = findViewById(R.id.box_input_search_origin);
        settings = findViewById(R.id.settings);
        manage_vehicle = findViewById(R.id.manage_vehicle);

        realm = Realm.getDefaultInstance();

        LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        boolean isOn = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!isOn){
          createNoGpsDialog();
        }
        onClickButton();
    }

    private void createNoGpsDialog(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog mNoGpsDialog = builder.setMessage("Por favor ative seu GPS para usar esse aplicativo.")
                .setPositiveButton("Ativar", dialogClickListener)
                .create();
        mNoGpsDialog.show();
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.getUiSettings().setCompassEnabled(false);
        enableLocationComponent();
        originCoord = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());
        mapboxMap.addOnMapClickListener(this);
        setArrayGasStation();
        setGasStationMarkers(false);
    }

    public void escondeTeclado (EditText input) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    @Override
    public void onMapClick(@NonNull LatLng point){
        escondeTeclado(input_search_origin);
        escondeTeclado(input_search_destiny);
        setMarker(point, "", true);
    }

    public void setArrayGasStation (){
        gasStation[0] = new Gas_station("Posto 1", 4.379, new LatLng(-30.155677, -51.142399));
        gasStation[1] = new Gas_station("Posto 2", 5.099, new LatLng(-30.148918, -51.148789));
        gasStation[2] = new Gas_station("Posto 3", 4.946, new LatLng(-30.148083, -51.152083));
        gasStation[3] = new Gas_station("Posto 4", 5.217, new LatLng(-30.159469, -51.147549));
        gasStation[4] = new Gas_station("Posto 5", 4.583, new LatLng(-30.163546, -51.151135));
        gasStation[5] = new Gas_station("Posto 6", 4.824, new LatLng(-30.145446, -51.129866));
        gasStation[6] = new Gas_station("Posto 7", 4.253, new LatLng(-30.160141, -51.133555));
        gasStation[7] = new Gas_station("Posto 8", 4.471, new LatLng(-30.163110, -51.144670));
        gasStation[8] = new Gas_station("Posto 9", 5.164, new LatLng(-30.149301, -51.136607));
        gasStation[9] = new Gas_station("Posto 10", 4.369, new LatLng(-30.150952, -51.161509));
        gasStation[10] = new Gas_station("Posto 11", 5.212, new LatLng(-30.154217, -51.161208));
        gasStation[11] = new Gas_station("Posto 12", 4.553, new LatLng(-30.157557, -51.183009));
        gasStation[12] = new Gas_station("Posto 13", 5.136, new LatLng(-30.159152, -51.181335));
        gasStation[13] = new Gas_station("Posto 14", 4.964, new LatLng(-30.162751, -51.181592));
        gasStation[14] = new Gas_station("Posto 15", 4.774, new LatLng(-30.180263, -51.185878));
        gasStation[15] = new Gas_station("Posto 16", 4.859, new LatLng(-30.185480, -51.161551));
        gasStation[16] = new Gas_station("Posto 17", 4.326, new LatLng(-30.180849, -51.175788));
        gasStation[17] = new Gas_station("Posto 18", 5.074, new LatLng(-30.178587, -51.173405));
        gasStation[18] = new Gas_station("Posto 19", 5.212, new LatLng(-30.138013, -51.127486));
        gasStation[19] = new Gas_station("Posto 20", 4.659, new LatLng(-30.179154, -51.179233));
        gasStation[20] = new Gas_station("Posto 21", 4.819, new LatLng(-30.141310, -51.219289));
        gasStation[21] = new Gas_station("Posto 22", 5.096, new LatLng(-30.126720, -51.185645));

        double best_price=gasStation[0].getPrice_gas();
        double worst_price=0;
        double price_factor=0;
        for (int i=0; i<gasStation.length; i++){
            if (gasStation[i].getPrice_gas()>=worst_price){
                worst_price = gasStation[i].getPrice_gas();
            }
            if (gasStation[i].getPrice_gas()<=best_price){
                best_price = gasStation[i].getPrice_gas();
            }
        }
        price_factor=(worst_price+best_price)/2;
        double middle_final=(worst_price+price_factor)/2;
        double middle_initial=(best_price+price_factor)/2;
        for (int i=0; i<gasStation.length; i++){
            if (gasStation[i].getPrice_gas()<=middle_initial){
                gasStation[i].setPrice_priority(1);
            }else if ((gasStation[i].getPrice_gas()>middle_initial)&&(gasStation[i].getPrice_gas()<=middle_final)){
                gasStation[i].setPrice_priority(2);
            }else gasStation[i].setPrice_priority(3);
        }
    }

    public void setGasStationMarkers (boolean isRoute) {
        if (!isRoute) {
            gasStationMarker = new Marker[gasStation.length];

            for (int i = 0; i < gasStationMarker.length; i++) {
                if (gasStationMarker[i] != null) {
                    mapboxMap.removeMarker(gasStationMarker[i]);
                }

                IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                Icon icon = iconFactory.fromResource(gasStation[i].getPrice_priority() == 1 ? R.drawable.ic_gas_station_priority1 : gasStation[i].getPrice_priority() == 2 ? R.drawable.ic_gas_station_priority2 : R.drawable.ic_gas_station_priority3);

                gasStationMarker[i] = mapboxMap.addMarker(new MarkerOptions()
                        .position(gasStation[i].getLocation())
                        .icon(icon)
                        .title(gasStation[i].getName())
                        .snippet("Preço Gasolina: R$ " + Double.toString(gasStation[i].getPrice_gas())));
            }
        }else {
            double distance_gasStation = 0.05;

            for (int i=0; i<gasStationMarker.length; i++){
                if (gasStationMarker[i] != null) {
                    mapboxMap.removeMarker(gasStationMarker[i]);
                }
            }

            gasStation_route.clear();
            for (int i=0; i<points_route.size(); i++){
                for (int k=0; k<gasStationMarker.length; k++){
                    Location startPoint = new Location("locationRoute");
                    startPoint.setLatitude(points_route.get(i).getLatitude());
                    startPoint.setLongitude(points_route.get(i).getLongitude());

                    Location endPoint = new Location("locationGasStation");
                    endPoint.setLatitude(gasStation[k].getLocation().getLatitude());
                    endPoint.setLongitude(gasStation[k].getLocation().getLongitude());

                    double distance = startPoint.distanceTo(endPoint)/1000;

                    if (distance<=distance_gasStation) {
                        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                        Icon icon = iconFactory.fromResource(gasStation[k].getPrice_priority() == 1 ? R.drawable.ic_gas_station_priority1 : gasStation[k].getPrice_priority() == 2 ? R.drawable.ic_gas_station_priority2 : R.drawable.ic_gas_station_priority3);

                        gasStation_route.add(gasStation[k]);

                        gasStationMarker[k] = mapboxMap.addMarker(new MarkerOptions()
                                .position(gasStation[k].getLocation())
                                .icon(icon)
                                .title(gasStation[k].getName())
                                .snippet("Preço Gasolina: R$ " + Double.toString(gasStation[k].getPrice_gas())));
                    }
                }
            }
        }
    }

    public void onClickButton () {
        setDestiny();
        setOrigin();

        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
                intent.putExtra("intentName", "Configurações");
                startActivity(intent);
            }
        });

        manage_vehicle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageVehicleActivity.class);
                intent.putExtra("intentName", "Gerenciar Veículos");
                startActivity(intent);
            }
        });

        button_navigation_view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(false)
                        .waynameChipEnabled(true)
                        .build();

                //Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(MainActivity.this, options);
            }
        });

        user_location.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setMarker(new LatLng(originLocation.getLatitude(), originLocation.getLongitude()), "", false);
            }
        });
        open_search_simulate_origin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(box_input_search_origin.getVisibility()==View.GONE) {
                    open_search_simulate_origin.animate().rotation(180);
                    box_input_search_origin.setVisibility(View.VISIBLE);
                }else {
                    open_search_simulate_origin.animate().rotation(0);
                    box_input_search_origin.setVisibility(View.GONE);
                    setMarker(new LatLng(originLocation.getLatitude(), originLocation.getLongitude()), "Minha Origem", false);
                }
            }
        });
    }

    public void setOrigin (){
        final GeocoderAdapter adapter = new GeocoderAdapter(this);
        input_search_origin.setLines(1);
        input_search_origin.setAdapter(adapter);

        input_search_origin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GeocoderFeature result = adapter.getItem(position);
                escondeTeclado(input_search_origin);
                setMarker(new LatLng(result.getLatitude(), result.getLongitude()), result.getText(), false);
            }
        });
    }

    public void setDestiny () {
        final GeocoderAdapter adapter = new GeocoderAdapter(this);
        input_search_destiny.setLines(1);
        input_search_destiny.setAdapter(adapter);

        input_search_destiny.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GeocoderFeature result = adapter.getItem(position);
                escondeTeclado(input_search_destiny);
                setMarker(new LatLng(result.getLatitude(), result.getLongitude()), result.getText(), true);
            }
        });
    }

    protected void setMarker(LatLng point, String text, boolean isDestiny) {
        if (isDestiny) {
            if (destinationMarker != null) {
                mapboxMap.removeMarker(destinationMarker);
            }
            destinationCoord = point;
            destinationMarker = mapboxMap.addMarker(new MarkerOptions()
                    .position(destinationCoord)
                    .title(text));
            input_search_destiny.setText(text);
        }else{
            if (originMarker != null) {
                mapboxMap.removeMarker(originMarker);
            }

            IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
            Icon icon = iconFactory.fromResource(R.drawable.car_icon_marker);

            originCoord = point;
            originMarker = mapboxMap.addMarker(new MarkerOptions()
                    .position(originCoord)
                    .icon(icon)
                    .title(text));
            input_search_origin.setText(text);
        }
        updateMap(point.getLatitude(), point.getLongitude());
    }

    private void updateMap(double latitude, double longitude) {
        vehicleUsed=realm.where(Vehicle.class).equalTo("isBeingUsed", true).findFirst();

        if (vehicleUsed != null && vehicleUsed.isValid()) {
            if (destinationCoord != null)
                destinationPosition = Point.fromLngLat(destinationCoord.getLongitude(), destinationCoord.getLatitude());
            if (originCoord != null)
                originPosition = Point.fromLngLat(originCoord.getLongitude(), originCoord.getLatitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))
                    .zoom(13)
                    .build();
            this.mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);

            Point waypoint1 = Point.fromLngLat(-51.219289, -30.141310);//Posto 6
            Point waypoint2 = Point.fromLngLat(-51.185645, -30.126720);//Posto 11

            config = realm.where(Config.class).equalTo("active", true).findFirst();
            if (destinationPosition != null && originPosition != null) {
                Route route = new Route(originPosition, destinationPosition);
                routes.clear();
                if (config.getId() == 1 || config.getId() == 2) {
                    for (int i = 0; i < gasStation.length; i++) {
                        Point waypoint = Point.fromLngLat(gasStation[i].getLocation().getLongitude(), gasStation[i].getLocation().getLatitude());
                        getRoute(originPosition, destinationPosition, waypoint);
                    }
                } else {
                    getRoute(originPosition, destinationPosition, Point.fromLngLat(0, 0));
                }

                CameraPosition cameraRoute = new CameraPosition.Builder()
                        .target(new LatLng(originPosition.latitude(), originPosition.longitude()))
                        .zoom(12)
                        .build();
                this.mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraRoute), 5000, null);

                if ((originCoord.getLatitude() == originLocation.getLatitude())
                        && (originCoord.getLongitude() == originLocation.getLongitude())) {
                    button_navigation_view.setVisibility(View.VISIBLE);
                    button_navigation_view.setEnabled(true);
                    button_navigation_view.setBackgroundResource(R.color.mapboxGreen);
                } else {
                    button_navigation_view.setVisibility(View.GONE);
                    button_navigation_view.setEnabled(false);
                    button_navigation_view.setBackgroundResource(R.color.mapboxGrayLight);
                }
            }
        }else {
            Toast.makeText(MainActivity.this, "Selecione um veículo para gerar rotas!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getRoute(Point origin, Point destination) {
        getRoute (origin, destination, Point.fromLngLat(origin.longitude(), origin.latitude()));
    }

    private void getRoute(Point origin, Point destination, Point waypoint) {
        Config config = realm.where(Config.class).equalTo("active", true).findFirst();

        if(config.getId()==1) {
            NavigationRoute.builder(this)
                    .addWaypoint(waypoint)
                    .accessToken(Mapbox.getAccessToken())
                    .origin(origin)
                    .destination(destination)
                    .build()
                    .getRoute(new Callback<DirectionsResponse>() {
                        @Override
                        public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                            // You can get the generic HTTP info about the response
                            Log.d(TAG, "Response code: " + response.code());
                            if (response.body() == null) {
                                Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                return;
                            } else if (response.body().routes().size() < 1) {
                                Log.e(TAG, "No routes found");
                                return;
                            }

                            points_route.clear();
                            points_route.add(new LatLng(origin.latitude(), origin.longitude()));
                            points_route.add(new LatLng(destination.latitude(), destination.longitude()));

                            for (int i = 0; i < response.body().routes().get(0).legs().get(0).steps().size(); i++) {
                                for (int k = 0; k < response.body().routes().get(0).legs().get(0).steps().get(i).intersections().size(); k++) {
                                    double lng = response.body().routes().get(0).legs().get(0).steps().get(i).intersections().get(k).location().coordinates().get(0);
                                    double lat = response.body().routes().get(0).legs().get(0).steps().get(i).intersections().get(k).location().coordinates().get(1);
                                    points_route.add(new LatLng(lat, lng));
                                }
                            }

                            setGasStationMarkers(true);

                            currentRoute = response.body().routes().get(0);

                            calculateCost(currentRoute.distance() / 1000, 0);

                            // Draw the route on the map
                            if (navigationMapRoute != null) {
                                navigationMapRoute.removeRoute();
                            } else {
                                navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                            }

                            routes.add(currentRoute);
                            gasStationRoutes.add(gasStation_route);

                            if (routes.size()>=gasStation.length){
                                System.out.println ("Terminou as rotas");
                                navigationMapRoute.addRoute(calculateBestRoute());
                                routes.clear();
                                gasStationRoutes.clear();
                            }
                            //navigationMapRoute.addRoutes(currentRoute);
                            //System.out.println (navigationMapRoute.showAlternativeRoutes());
                        }

                        @Override
                        public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                            Log.e(TAG, "Error: " + throwable.getMessage());
                        }
                    });
        }else if (config.getId()==2){
            NavigationRoute.builder(this)
                    .addWaypoint(waypoint)
                    .accessToken(Mapbox.getAccessToken())
                    .origin(origin)
                    .destination(destination)
                    .build()
                    .getRoute(new Callback<DirectionsResponse>() {
                        @Override
                        public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                            // You can get the generic HTTP info about the response
                            Log.d(TAG, "Response code: " + response.code());
                            if (response.body() == null) {
                                Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                return;
                            } else if (response.body().routes().size() < 1) {
                                Log.e(TAG, "No routes found");
                                return;
                            }

                            points_route.clear();
                            points_route.add(new LatLng(origin.latitude(), origin.longitude()));
                            points_route.add(new LatLng(destination.latitude(), destination.longitude()));

                            for (int i = 0; i < response.body().routes().get(0).legs().get(0).steps().size(); i++) {
                                for (int k = 0; k < response.body().routes().get(0).legs().get(0).steps().get(i).intersections().size(); k++) {
                                    double lng = response.body().routes().get(0).legs().get(0).steps().get(i).intersections().get(k).location().coordinates().get(0);
                                    double lat = response.body().routes().get(0).legs().get(0).steps().get(i).intersections().get(k).location().coordinates().get(1);
                                    points_route.add(new LatLng(lat, lng));
                                }
                            }

                            setGasStationMarkers(true);

                            currentRoute = response.body().routes().get(0);

                            calculateCost(currentRoute.distance() / 1000, 0);

                            // Draw the route on the map
                            if (navigationMapRoute != null) {
                                navigationMapRoute.removeRoute();
                            } else {
                                navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                            }

                            routes.add(currentRoute);
                            gasStationRoutes.add(gasStation_route);

                            if (routes.size()>=gasStation.length){
                                System.out.println ("Terminou as rotas");
                                navigationMapRoute.addRoute(calculateBestTimeRoute());
                                routes.clear();
                                gasStationRoutes.clear();
                            }
                            //navigationMapRoute.addRoutes(currentRoute);
                            //System.out.println (navigationMapRoute.showAlternativeRoutes());
                        }

                        @Override
                        public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                            Log.e(TAG, "Error: " + throwable.getMessage());
                        }
                    });
        }else {
            NavigationRoute.builder(this)
                    .accessToken(Mapbox.getAccessToken())
                    .origin(origin)
                    .destination(destination)
                    .build()
                    .getRoute(new Callback<DirectionsResponse>() {
                        @Override
                        public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                            // You can get the generic HTTP info about the response
                            Log.d(TAG, "Response code: " + response.code());
                            if (response.body() == null) {
                                Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                return;
                            } else if (response.body().routes().size() < 1) {
                                Log.e(TAG, "No routes found");
                                return;
                            }

                            points_route.clear();

                            setGasStationMarkers(true);

                            currentRoute = response.body().routes().get(0);

                            calculateCost(currentRoute.distance() / 1000, 0);

                            // Draw the route on the map
                            if (navigationMapRoute != null) {
                                navigationMapRoute.removeRoute();
                            } else {
                                navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                            }

                            routes.add(currentRoute);
                            System.out.println ("Quantidade rotas: "+routes.size());

                            navigationMapRoute.addRoute(currentRoute);
                            //System.out.println (navigationMapRoute.showAlternativeRoutes());
                        }

                        @Override
                        public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                            Log.e(TAG, "Error: " + throwable.getMessage());
                        }
                    });
        }
    }

    private DirectionsRoute calculateBestRoute (){
    DirectionsRoute bestRoute=routes.get(0);

    for(int i=0;i<routes.size();i++){
        if (i==0)
            bestRoute=routes.get(i);
        else
            if (bestRoute.distance()>routes.get(i).distance())
                bestRoute=routes.get(i);
    }

    return bestRoute;
    }

    private DirectionsRoute calculateBestTimeRoute (){
        DirectionsRoute bestRoute=routes.get(0);

        for(int i=0;i<routes.size();i++){
            if (i==0)
                bestRoute=routes.get(i);
            else
            if (bestRoute.duration()>routes.get(i).duration())
                bestRoute=routes.get(i);
        }

        return bestRoute;
    }

    private void calculateCost(double distanceRoute, double timeRoute) {
        DecimalFormat distance_formated = new DecimalFormat("#,###.00");
        vehicleUsed = realm.where(Vehicle.class).equalTo("isBeingUsed", true).findFirst();

        double spent = vehicleUsed.getConsumption(), price_fuel = 4.5, liters_fuel;

        if (gasStation_route.size() >= 1) {
            double best_price = gasStation_route.get(0).getPrice_gas();
            for (int i = 0; i < gasStation_route.size(); i++) {
                if (gasStation_route.get(i).getPrice_gas() < best_price) {
                    best_price = gasStation_route.get(i).getPrice_gas();
                }
            }
            price_fuel = best_price;
        }

        liters_fuel = distanceRoute / spent;
        double price = liters_fuel * price_fuel;
        DecimalFormat price_formated = new DecimalFormat("#,###.00");

        if (distanceRoute < 1) {
            distance_view.setText("Distância: 0" + distance_formated.format(distanceRoute) + " Km");
        } else
            distance_view.setText("Distância: " + distance_formated.format(distanceRoute) + " Km");

        distance_view.setText("Distância: " + distance_formated.format(distanceRoute) + " Km");

        price_fuel_view.setText("Preço Gasolina: R$ " + Double.toString(price_fuel));

        if (price < 1) {
            spent_view.setText("Gasto (consid. cons. de "+vehicleUsed.getConsumption()+" Km/Litro): R$ 0" + price_formated.format(price));
        } else spent_view.setText("Gasto (consid. cons. de "+vehicleUsed.getConsumption()+" Km/Litro): " + price_formated.format(price));
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this);
            locationComponent.setLocationComponentEnabled(false);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            originLocation = locationComponent.getLastKnownLocation();

            setMarker(new LatLng(locationComponent.getLocationEngine().getLastLocation().getLatitude(), locationComponent.getLocationEngine().getLastLocation().getLongitude()), "Minha Origem", false);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent();
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
