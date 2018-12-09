package com.economicroute.economicroute;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.optimization.v1.MapboxOptimization;
import com.mapbox.api.optimization.v1.models.OptimizationResponse;
import com.mapbox.geocoder.service.models.GeocoderFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
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
    private List<Marker> gasStationMarker;
    private LatLng originCoord;
    private LatLng destinationCoord;

    // variables for calculating and drawing a route
    private Point originPosition;
    private Point destinationPosition;
    DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private MapboxDirections.Builder mapboxDirections;

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
    private NavigationRoute.Builder navigationRoute;

    //array models
    private List<Gas_station> gasStation = new ArrayList<>(22);
    private List<Marker> intersectionsMarker = new ArrayList<>();
    private List<Route> individualRoutes;
    private List<Route> geralRoutes;


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

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isOn = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isOn) {
            createNoGpsDialog();
        }
        onClickButton();
    }

    private void createNoGpsDialog() {
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
        setGasStationMarkers();
    }

    public void hideBoard(EditText input) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        hideBoard(input_search_origin);
        hideBoard(input_search_destiny);
        setMarker(point, "", true);
    }

    public void setArrayGasStation() {
        gasStation.add(new Gas_station("Posto 1", 4.379, new LatLng(-30.155677, -51.142399)));
        gasStation.add(new Gas_station("Posto 2", 5.099, new LatLng(-30.148918, -51.148789)));
        gasStation.add(new Gas_station("Posto 3", 4.946, new LatLng(-30.148083, -51.152083)));
        gasStation.add(new Gas_station("Posto 4", 5.217, new LatLng(-30.159469, -51.147549)));
        gasStation.add(new Gas_station("Posto 5", 4.583, new LatLng(-30.163546, -51.151135)));
        gasStation.add(new Gas_station("Posto 6", 4.824, new LatLng(-30.145446, -51.129866)));
        gasStation.add(new Gas_station("Posto 7", 4.253, new LatLng(-30.160141, -51.133555)));
        gasStation.add(new Gas_station("Posto 8", 4.471, new LatLng(-30.163110, -51.144670)));
        gasStation.add(new Gas_station("Posto 9", 5.164, new LatLng(-30.149301, -51.136607)));
        gasStation.add(new Gas_station("Posto 10", 4.369, new LatLng(-30.150952, -51.161509)));
        gasStation.add(new Gas_station("Posto 11", 5.212, new LatLng(-30.154217, -51.161208)));
        gasStation.add(new Gas_station("Posto 12", 4.553, new LatLng(-30.157557, -51.183009)));
        gasStation.add(new Gas_station("Posto 13", 5.136, new LatLng(-30.159152, -51.181335)));
        gasStation.add(new Gas_station("Posto 14", 4.964, new LatLng(-30.162751, -51.181592)));
        gasStation.add(new Gas_station("Posto 15", 4.774, new LatLng(-30.180263, -51.185878)));
        gasStation.add(new Gas_station("Posto 16", 4.859, new LatLng(-30.185480, -51.161551)));
        gasStation.add(new Gas_station("Posto 17", 4.326, new LatLng(-30.180849, -51.175788)));
        gasStation.add(new Gas_station("Posto 18", 5.074, new LatLng(-30.178587, -51.173405)));
        gasStation.add(new Gas_station("Posto 19", 5.212, new LatLng(-30.138013, -51.127486)));
        gasStation.add(new Gas_station("Posto 20", 4.659, new LatLng(-30.179154, -51.179233)));
        gasStation.add(new Gas_station("Posto 21", 4.819, new LatLng(-30.141310, -51.219289)));
        gasStation.add(new Gas_station("Posto 22", 5.096, new LatLng(-30.126720, -51.185645)));

        for (int i = 0; i < gasStation.size(); i++) {
            gasStation.get(i).setPrice_priority(gasStation.get(i).findPriorityGasStation(gasStation));
        }
        individualRoutes = new ArrayList<>(gasStation.size());
        geralRoutes = new ArrayList<>(gasStation.size());
    }

    public void setGasStationMarkers() {
            gasStationMarker = new ArrayList<Marker>(gasStation.size());

            for (int i = 0; i < gasStation.size(); i++) {

                int priority = gasStation.get(i).getPrice_priority();
                IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);

                Icon icon = iconFactory.fromResource(priority == 1 ?
                        R.drawable.ic_gas_station_priority1 : priority == 2 ?
                        R.drawable.ic_gas_station_priority2 : R.drawable.ic_gas_station_priority3);

                gasStationMarker.add(mapboxMap.addMarker(new MarkerOptions()
                        .position(gasStation.get(i).getLocation())
                        .icon(icon)
                        .title(gasStation.get(i).getName())
                        .snippet("Preço Gasolina: R$ " +
                                Double.toString(gasStation.get(i).getPrice_gas()))));
            }
    }

    public void setMarkersIntersections (Route bestRoute) {
        for (int i = 0; i < intersectionsMarker.size(); i++) {
            if (intersectionsMarker.get(i) != null) {
                mapboxMap.removeMarker(intersectionsMarker.get(i));
            }
        }

        intersectionsMarker.clear();

        for (int i = 0; i < bestRoute.findIntersections().size(); i++) {
            intersectionsMarker.add(mapboxMap.addMarker(new MarkerOptions()
                    .position(bestRoute.findIntersections().get(i))
                    .title("Intersection")));
        }
    }

    public void setMarkersInRoute(Route bestRoute) {
        for (int i = 0; i < gasStationMarker.size(); i++) {
            if (gasStationMarker.get(i) != null) {
                mapboxMap.removeMarker(gasStationMarker.get(i));
            }
        }
        gasStationMarker.clear();

        gasStationMarker = new ArrayList<>(bestRoute.getGasStationInRoute().size());

        for(int i=0; i < bestRoute.getGasStationInRoute().size(); i++) {
            int priority = bestRoute.getGasStationInRoute().get(i).getPrice_priority();
            IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);

            Icon icon = iconFactory.fromResource(priority == 1 ?
                    R.drawable.ic_gas_station_priority1 : priority == 2 ?
                    R.drawable.ic_gas_station_priority2 : R.drawable.ic_gas_station_priority3);

            gasStationMarker.add(mapboxMap.addMarker(new MarkerOptions()
                    .position(bestRoute.getGasStationInRoute().get(i).getLocation())
                    .icon(icon)
                    .title(bestRoute.getGasStationInRoute().get(i).getName())
                    .snippet("Preço Gasolina: R$ " + Double.toString(bestRoute.getGasStationInRoute().get(i).getPrice_gas()))));
        }
    }

    public void onClickButton() {
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
        open_search_simulate_origin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (box_input_search_origin.getVisibility() == View.GONE) {
                    open_search_simulate_origin.animate().rotation(180);
                    box_input_search_origin.setVisibility(View.VISIBLE);
                } else {
                    open_search_simulate_origin.animate().rotation(0);
                    box_input_search_origin.setVisibility(View.GONE);
                    setMarker(new LatLng(originLocation.getLatitude(), originLocation.getLongitude()), "Minha Origem", false);
                }
            }
        });
    }

    public void setOrigin() {
        final GeocoderAdapter adapter = new GeocoderAdapter(this);
        input_search_origin.setLines(1);
        input_search_origin.setAdapter(adapter);

        input_search_origin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GeocoderFeature result = adapter.getItem(position);
                hideBoard(input_search_origin);
                setMarker(new LatLng(result.getLatitude(), result.getLongitude()), result.getText(), false);
            }
        });
    }

    public void setDestiny() {
        final GeocoderAdapter adapter = new GeocoderAdapter(this);
        input_search_destiny.setLines(1);
        input_search_destiny.setAdapter(adapter);

        input_search_destiny.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GeocoderFeature result = adapter.getItem(position);
                hideBoard(input_search_destiny);
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
        } else {
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
        vehicleUsed = realm.where(Vehicle.class).equalTo("isBeingUsed", true).findFirst();

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

            config = realm.where(Config.class).equalTo("active", true).findFirst();
            if (destinationPosition != null && originPosition != null) {
                    for (int i = 0; i < gasStation.size(); i++) {
                        List<Point> waypoints = new ArrayList<>(1);
                        waypoints.add(Point.fromLngLat(gasStation.get(i).getLocation().getLongitude(), gasStation.get(i).getLocation().getLatitude()));
                        getRoute(originPosition, destinationPosition, waypoints, false);
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
        } else {
            Toast.makeText(MainActivity.this, "Selecione um veículo para gerar rotas!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getRoute(Point origin, Point destination, List<Point> waypoints, boolean secondRoutes) {
        navigationRoute = NavigationRoute.builder(this)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination);

                for (int i = 0; i < waypoints.size(); i++) {
                    navigationRoute.addWaypoint(waypoints.get(i));
                }

                navigationRoute.build().getRoute(new Callback<DirectionsResponse>() {
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

                        currentRoute = response.body().routes().get(0);

                        Route newRoute = new Route (originPosition, destinationPosition, currentRoute);
                        newRoute.setGasStationInRoute(newRoute.findGasStationsInRoute(gasStation));

                        if (!secondRoutes)
                            individualRoutes.add(newRoute);
                        else
                            geralRoutes.add(newRoute);

                        if(individualRoutes.size() >= 22 && !secondRoutes) {
                            for(int i = 0; i < individualRoutes.size(); i++) {
                                List<Point> waypoints = new ArrayList<>(individualRoutes.get(i).getGasStationInRoute().size());
                                for(int k = 0; k < individualRoutes.get(i).getGasStationInRoute().size(); k++) {
                                    waypoints.add(Point.fromLngLat(
                                            individualRoutes.get(i).getGasStationInRoute().get(k).getLocation().getLongitude(),
                                            individualRoutes.get(i).getGasStationInRoute().get(k).getLocation().getLatitude()
                                    ));
                                }
                                getRoute(originPosition, destinationPosition, waypoints, true);
                            }
                            individualRoutes.clear();
                        }

                        if (geralRoutes.size() >= 22 && secondRoutes) {
                            // Draw the route on the map
                            if (navigationMapRoute != null) {
                                navigationMapRoute.removeRoute();
                            } else {
                                navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                            }
                            vehicleUsed = realm.where(Vehicle.class).equalTo("isBeingUsed", true).findFirst();
                            navigationMapRoute.addRoute(geralRoutes.get(0).getRoute());
                            setMarkersInRoute(geralRoutes.get(0));
                            Route teste = Management.findBestRoute(geralRoutes, vehicleUsed);
                            geralRoutes.clear();
                            // navigationMapRoute.addRoute(newRoute.getRoute());
                        }
                       // setMarkersInRoute(newRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    private void generateRoutesWithGasStations(List<Route> routesIndividualGasStation) {
        for (int i = 0; i < routesIndividualGasStation.size(); i++) {
            List<Gas_station> gasStationsInRoute = routesIndividualGasStation.get(i).getGasStationInRoute();
            List<Point> waypoints = new ArrayList<>(gasStationsInRoute.size());
            for (int k = 0; k < gasStationsInRoute.size(); k++) {
                waypoints.add(Point.fromLngLat(gasStationsInRoute.get(k)
                        .getLocation().getLongitude(), gasStationsInRoute
                        .get(k).getLocation().getLatitude()));
            }
        }
    }

    @SuppressWarnings({"MissingPermission"})
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
