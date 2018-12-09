package com.economicroute.economicroute.model;

import android.location.Location;

import com.economicroute.economicroute.Util;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.api.directions.v5.models.RouteLeg;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.services.android.navigation.v5.utils.RouteUtils;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private int id;
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

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    // calculing methods
    public List<Gas_station> findGasStationsInRoute(List<Gas_station> gas_stations) {
        double configDistanceTwoPointsGasStation = 0.2;
        double configDistanceTwoPointsIntersections = 0.04;
        List<RouteLeg> legs = getRoute().legs();
        List<Gas_station> gasStationInRoute = new ArrayList<Gas_station>();
        List<LatLng> intersections = new ArrayList<LatLng>(legs.get(0).steps().size());
        intersections.add(new LatLng(getOrigin().latitude(), getOrigin().longitude()));

        // grava intersections existentes
        for (int i = 0; i < legs.size(); i++) {
            int tamanhoSteps = legs.get(i).steps().size();
            for (int m = 0; m < legs.get(i).steps().size(); m++) {
                int tamanhoIntersections = legs.get(i).steps().get(m).intersections().size();
                for (int k = 0; k < legs.get(i).steps().get(m).intersections().size(); k++) {
                    int tamanhoListInterator = getRoute().legs().size();
                    double lng = legs.get(i).steps().get(m).intersections().get(k).location().coordinates().get(0);
                    double lat = legs.get(i).steps().get(m).intersections().get(k).location().coordinates().get(1);

                    intersections.add(new LatLng(lat, lng));
                }
            }
        }
        intersections.add(new LatLng(getDestiny().latitude(), getDestiny().longitude()));
        // preenche lacunas sem intersections
        int addIntersection = 1;
        while (addIntersection == 1) {
            for (int i = 0; i < intersections.size(); i++) {
                double lngOld = 0;
                double latOld = 0;

                if (i != 0) {
                    lngOld = intersections.get(i - 1).getLongitude();
                    latOld = intersections.get(i - 1).getLatitude();
                }

                if (i != 0) {
                    Location startPoint = new Location("locationStartIntersection");
                    startPoint.setLatitude(latOld);
                    startPoint.setLongitude(lngOld);

                    Location endPoint = new Location("locationEndIntersection");
                    endPoint.setLatitude(intersections.get(i).getLatitude());
                    endPoint.setLongitude(intersections.get(i).getLongitude());

                    double distanceInterseptions = startPoint.distanceTo(endPoint) / 1000;

                    if (distanceInterseptions > configDistanceTwoPointsIntersections) {
                        intersections.add(i + 1, Util.midPoint(latOld, lngOld,
                                intersections.get(i).getLatitude(), intersections.get(i).getLongitude()));
                    } else {
                        if (i == intersections.size()-1)
                            addIntersection = 0;
                    }
                }
            }
        }

        // busca por postos proximos a intersections
        for(int i = 0; i < intersections.size(); i++) {
            for (int j = 0; j < gas_stations.size(); j++) {
                Location startPoint = new Location("locationPointInRoute");
                startPoint.setLatitude(intersections.get(i).getLatitude());
                startPoint.setLongitude(intersections.get(i).getLongitude());

                Location endPoint = new Location("locationPointGasStation");
                endPoint.setLatitude(gas_stations.get(j).getLocation().getLatitude());
                endPoint.setLongitude(gas_stations.get(j).getLocation().getLongitude());

                double distance = startPoint.distanceTo(endPoint) / 1000;

                if (distance <= configDistanceTwoPointsGasStation) {
                    int addGasStation = 1;
                    for (int n = 0; n < gasStationInRoute.size(); n++) {
                        if (gasStationInRoute.get(n)==gas_stations.get(j)) {
                            addGasStation = 0;
                            n = gasStationInRoute.size();
                        }
                    }

                    if (addGasStation == 1)
                        gasStationInRoute.add(gas_stations.get(j));
                }
            }
        }
        return gasStationInRoute;
    }

    // método de teste
    public List<LatLng> findIntersections() {
        double configDistanceTwoPointsIntersections = 0.02;
        List<RouteLeg> legs = getRoute().legs();
        List<Gas_station> gasStationInRoute = new ArrayList<Gas_station>();
        List<LatLng> intersections = new ArrayList<LatLng>(legs.get(0).steps().size());
        intersections.add(new LatLng(getOrigin().latitude(), getOrigin().longitude()));

        // grava intersections existentes
        for (int i = 0; i < legs.size(); i++) {
            int tamanhoSteps = legs.get(i).steps().size();
            for (int m = 0; m < legs.get(i).steps().size(); m++) {
                int tamanhoIntersections = legs.get(i).steps().get(m).intersections().size();
                for (int k = 0; k < legs.get(i).steps().get(m).intersections().size(); k++) {
                    int tamanhoListInterator = getRoute().legs().size();
                    double lng = legs.get(i).steps().get(m).intersections().get(k).location().coordinates().get(0);
                    double lat = legs.get(i).steps().get(m).intersections().get(k).location().coordinates().get(1);

                    intersections.add(new LatLng(lat, lng));
                }
            }
        }

        intersections.add(new LatLng(getDestiny().latitude(), getDestiny().longitude()));

        // preenche lacunas sem intersections
        int addIntersection = 1;
        while (addIntersection == 1) {
            for (int i = 0; i < intersections.size(); i++) {
                double lngOld = 0;
                double latOld = 0;

                if (i != 0) {
                    lngOld = intersections.get(i - 1).getLongitude();
                    latOld = intersections.get(i - 1).getLatitude();
                }

                if (i != 0) {
                    Location startPoint = new Location("locationStartIntersection");
                    startPoint.setLatitude(latOld);
                    startPoint.setLongitude(lngOld);

                    Location endPoint = new Location("locationEndIntersection");
                    endPoint.setLatitude(intersections.get(i).getLatitude());
                    endPoint.setLongitude(intersections.get(i).getLongitude());

                    double distanceInterseptions = startPoint.distanceTo(endPoint) / 1000;

                    if (distanceInterseptions > configDistanceTwoPointsIntersections) {
                        intersections.add(i + 1, Util.midPoint(latOld, lngOld,
                                intersections.get(i).getLatitude(), intersections.get(i).getLongitude()));
                    } else {
                        if (i == intersections.size()-1)
                            addIntersection = 0;
                    }
                }
            }
        }

        return intersections;
    }
}