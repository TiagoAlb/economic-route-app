package com.economicroute.economicroute;

import android.support.v7.app.AppCompatActivity;

import com.economicroute.economicroute.model.Gas_station;
import com.economicroute.economicroute.model.Route;
import com.economicroute.economicroute.model.Vehicle;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteLeg;

import java.util.ArrayList;
import java.util.List;

public class Management extends AppCompatActivity {
    public static Route findBestRoute(List<Route> routes, Vehicle vehicle) {
        /*
        //define prioridades pela rota
        for(int i = 0; i < routes.size(); i++) {
            for (int m = 0; m < routes.get(i).getGasStationInRoute().size(); m++) {
                routes.get(i).getGasStationInRoute().get(m)
                        .setPrice_priority(routes.get(i)
                                .getGasStationInRoute()
                                .get(m).findPriorityGasStation(routes.get(i).getGasStationInRoute()));
            }
        }*/
        List<Route> canReach = new ArrayList<>();
        for(int i = 0; i < routes.size(); i++) {
            int indexPriority = getBestIndexPricePriority(routes.get(i));
            double priceBestIndex = routes.get(i).getGasStationsInRoute().get(indexPriority).getPrice_gas();
            double distanceLeg = 0;

            for (int m = 0; m < indexPriority; m++) {
                if (routes.get(i).getGasStationsInRoute().size() < routes.get(i).getRoute().legs().size())
                    distanceLeg += routes.get(i).getRoute().legs().get(m).distance();
            }
            double lastPriceGas = 4.679;
            if(canReach(distanceLeg, vehicle)) {
                routes.get(i).setCostRoute(costDistance(distanceLeg, vehicle, lastPriceGas)+
                        costDistance(routes.get(i).getDistanceRoute()-distanceLeg, vehicle, priceBestIndex)
                );
                canReach.add(routes.get(i));
            }
            List<Gas_station> gasStationsToFuel = new ArrayList<>(1);
            gasStationsToFuel.add(routes.get(i).getGasStationsInRoute().get(indexPriority));
            routes.get(i).setGasStationsToFuel(gasStationsToFuel);
        }
        return bestCostRoute(canReach);
    }

    public static int getBestIndexPricePriority(Route route) {
        double bestPricePriority = route.getGasStationsInRoute().get(0).getPrice_gas();
        int bestIndexPricePriority = 0;
        for(int i = 0; i < route.getGasStationsInRoute().size(); i++) {
            if (route.getGasStationsInRoute().get(i).getPrice_gas() < bestPricePriority) {
                bestPricePriority = route.getGasStationsInRoute().get(i).getPrice_gas();
                bestIndexPricePriority = i;
            }
        }
        return bestIndexPricePriority;
    }

    public int getBestIndexDistance (List<RouteLeg> legs) {
        double bestDistanceLeg = legs.get(0).distance();
        double distanceLeg = 0;
        int bestIndexLeg = 0;

        for(int i = 0; i < legs.size(); i++) {
            distanceLeg += legs.get(i).distance();
            if (distanceLeg < bestDistanceLeg) {
                bestDistanceLeg = distanceLeg;
                bestIndexLeg = i;
            }
        }
        return bestIndexLeg;
    }

    public static boolean canReach(double distance, Vehicle vehicle) {
        if((distance/1000) < (vehicle.getConsumption()*vehicle.getFuel_quantity()))
            return true;
        else
            return false;
    }

    public static double costDistance(double distance, Vehicle vehicle, double priceGas) {
        return howMuchGas(distance/1000, vehicle)*priceGas;
    }

    public static double howMuchGas(double distance, Vehicle vehicle) {
        return distance / vehicle.getConsumption();
    }

    public static Route bestCostRoute(List<Route> routes) {
        Route bestRoute = routes.get(0);
        for (int i = 0; i < routes.size(); i++) {
            if (routes.get(i).getCostRoute() < bestRoute.getCostRoute()) {
                bestRoute = routes.get(i);
            }
        }
        return bestRoute;
    }
}
