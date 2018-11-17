package com.economicroute.economicroute.model;

import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private DirectionsRoute currentRoute;
    private Double costRoute;
    private Double gasRoute;
    private Double distanceRoute;
    private Point waypoint;
    private Point origin;
    private Point destiny;
    private NavigationMapRoute navigationMapRoute;
    private static final String TAG = "DirectionsActivity";
    private List<Gas_station> gasStationInRoute;
    private List<LatLng> points_route;
    private List<DirectionsRoute> routes;

    public Route (Point origin, Point destiny) {
        this.origin = origin;
        this.destiny = destiny;
    }
/*
    private void calculateCost(double distanceRoute, ArrayList<Gas_station> gasStation_route) {

        DecimalFormat distance_formated = new DecimalFormat("#,###.00");

        double spent = 10, price_fuel = 4.5, liters_fuel;

        if (gasStation_route.size()>=1){
            double best_price=gasStation_route.get(0).getPrice_gas();
            for(int i = 0; i<gasStation_route.size(); i++){
                if (gasStation_route.get(i).getPrice_gas()<best_price){
                    best_price=gasStation_route.get(i).getPrice_gas();
                }
            }
            price_fuel=best_price;
        }

        liters_fuel = distanceRoute / spent;

        double price = liters_fuel * price_fuel;
        DecimalFormat price_formated = new DecimalFormat("#,###.00");

        if (distanceRoute<1){
            distance_view.setText("Distância: 0" + distance_formated.format(distanceRoute) + " Km");
        }else distance_view.setText("Distância: " + distance_formated.format(distanceRoute) + " Km");

        distance_view.setText("Distância: " + distance_formated.format(distanceRoute) + " Km");

        price_fuel_view.setText("Preço Gasolina: R$ " + Double.toString(price_fuel));

        if (price<1){
            spent_view.setText("Gasto: R$ 0" + price_formated.format(price));
        }else spent_view.setText("Gasto: R$ " + price_formated.format(price));
    }
    */
    public ArrayList<Gas_station> gasStationOnRoute () {
        ArrayList<Gas_station> teste = new ArrayList<>();
        return teste;
    }

    public DirectionsRoute getCurrentRoute() { return currentRoute; }

    public void setCurrentRoute(DirectionsRoute currentRoute) { this.currentRoute = currentRoute; }

    public Double getCostRoute() { return costRoute; }

    public void setCostRoute(Double costRoute) { this.costRoute = costRoute; }

    public Double getGasRoute() { return gasRoute; }

    public void setGasRoute(Double gasRoute) { this.gasRoute = gasRoute; }

    public Double getDistanceRoute() { return distanceRoute; }

    public void setDistanceRoute(Double distanceRoute) { this.distanceRoute = distanceRoute; }

    public Point getWaypoint() { return waypoint; }

    public void setWaypoint(Point waypoint) { this.waypoint = waypoint; }

    public Point getOrigin() { return origin; }

    public void setOrigin(Point origin) { this.origin = origin; }

    public Point getDestiny() { return destiny; }

    public void setDestiny(Point destiny) { this.destiny = destiny; }

    public NavigationMapRoute getNavigationMapRoute() { return navigationMapRoute; }

    public void setNavigationMapRoute(NavigationMapRoute navigationMapRoute) { this.navigationMapRoute = navigationMapRoute; }

    public static String getTAG() { return TAG; }

    public List<Gas_station> getGasStationInRoute() { return gasStationInRoute; }

    public void setGasStationInRoute(List<Gas_station> gasStationInRoute) { this.gasStationInRoute = gasStationInRoute; }

    public List<LatLng> getPoints_route() { return points_route; }

    public void setPoints_route(List<LatLng> points_route) { this.points_route = points_route; }

    public List<DirectionsRoute> getRoutes() { return routes; }

    public void setRoutes(List<DirectionsRoute> routes) { this.routes = routes; }
}