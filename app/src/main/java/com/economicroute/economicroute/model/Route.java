package com.economicroute.economicroute.model;

import android.location.Location;

import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.android.navigation.v5.utils.RouteUtils;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private DirectionsRoute route;
    private Double costRoute;
    private Double distanceRoute;
    private Double timeRoute;
    private Point origin;
    private Point destiny;
    private List<Gas_station> gasStationInRoute;
    private List<LatLng> points_route;

    public Route (Point origin, Point destiny, DirectionsRoute route) {
        this.origin = origin;
        this.destiny = destiny;
        this.route = route;

        setTimeRoute(route.duration().doubleValue());
        setDistanceRoute(route.distance().doubleValue());
    }

    public DirectionsRoute getRoute() { return route; }

    public void setRoute(DirectionsRoute route) { this.route = route; }

    public Double getCostRoute() { return costRoute; }

    public void setCostRoute(Double costRoute) { this.costRoute = costRoute; }

    public Double getDistanceRoute() { return distanceRoute; }

    public void setDistanceRoute(Double distanceRoute) { this.distanceRoute = distanceRoute; }

    public Double getTimeRoute() { return timeRoute; }

    public void setTimeRoute(Double timeRoute) { this.timeRoute = timeRoute; }

    public Point getOrigin() { return origin; }

    public void setOrigin(Point origin) { this.origin = origin; }

    public Point getDestiny() { return destiny; }

    public void setDestiny(Point destiny) { this.destiny = destiny; }

    public List<Gas_station> getGasStationInRoute() { return gasStationInRoute; }

    public void setGasStationInRoute(List<Gas_station> gasStationInRoute) { this.gasStationInRoute = gasStationInRoute; }

    public List<LatLng> getPoints_route() { return points_route; }

    public void setPoints_route(List<LatLng> points_route) { this.points_route = points_route; }

    // calculing methods

    public List<Gas_station> findGasStationsInRoute(List<Gas_station> gas_stations) {
        double configDistanceTwoPoints = 1;
        List<LegStep> steps = getRoute().legs().get(0).steps();
        List<Gas_station> gasStationInRoute = new ArrayList<Gas_station>();

        for (int i = 0; i < steps.size(); i++) {
            for (int k = 0; k < steps.get(i).intersections().size(); k++) {
                double lng = steps.get(i).intersections().get(k).location().coordinates().get(0);
                double lat = steps.get(i).intersections().get(k).location().coordinates().get(1);

                for(int j = 0; j<gas_stations.size(); j++){
                    Location startPoint = new Location("locationPointInRoute");
                    startPoint.setLatitude(lat);
                    startPoint.setLongitude(lng);

                    Location endPoint = new Location("locationPointGasStation");
                    endPoint.setLatitude(gas_stations.get(j).getLocation().getLatitude());
                    endPoint.setLongitude(gas_stations.get(j).getLocation().getLongitude());

                    double distance = startPoint.distanceTo(endPoint)/1000;

                    if (distance<=configDistanceTwoPoints) {
                        gasStationInRoute.add(gas_stations.get(j));
                    }
                }
            }
        }
        return gasStationInRoute;
    }
}