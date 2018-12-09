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
    public Route findBestRoute(List<Route> routes, Vehicle vehicle) {
        Route bestRoute = new Route(routes.get(0).getOrigin(), routes.get(0).getDestiny(), routes.get(0).getRoute());
        double lastCost = 0;
        double costRoute = 0;
        double lastSuply = 4.6;

        // define prioridades pela rota
        for(int i = 0; i < routes.size(); i++) {
            for (int m = 0; m < routes.get(i).getGasStationInRoute().size(); m++) {
                routes.get(i).getGasStationInRoute().get(m)
                        .setPrice_priority(routes.get(i)
                                .getGasStationInRoute()
                                .get(m).findPriorityGasStation(routes.get(i).getGasStationInRoute()));

            }
        }

        for(int i = 0; i < routes.size(); i++) {
            int indexPriority = getBestIndexPricePriority(routes.get(i));

            if(canReach(routes.get(i).getRoute().legs().get(indexPriority).distance(), vehicle)) {

            }
        }

        System.out.println("QUANTIDADE DE ROTAS: "+routes.size());
        for(int i = 0; i < routes.size(); i++) {
            System.out.println("QUANTIDADE DE POSTOS: " + routes.get(i).getGasStationInRoute().size());
            for(int k = 0; k < routes.get(i).getGasStationInRoute().size(); k++) {
                System.out.println("TESTE DISTANCIA TOTAL ROTA "+i+": " + routes.get(i).getDistanceRoute());
                System.out.println("TESTE POSTO "+k+": " + routes.get(i).getGasStationInRoute().get(k).getName());
                System.out.println("TESTE LEG "+k+": " + routes.get(i).getRoute().legs().get(k).distance());
            }

            System.out.println("QUANTIDADE DE LEGS: " + routes.get(i).getRoute().legs().size());
            for(int m = 0; m < routes.get(i).getRoute().legs().size(); m++) {
                System.out.println("LEG "+m+":"+routes.get(i).getRoute().legs().get(m).distance());
            }
        }

        /*
        for (int i = 0; i < routes.get(0).getGasStationInRoute().size(); i++) {
            lastCost += routes.get(0).getGasStationInRoute().get(i).getPrice_gas();
        }

        for (int i = 0; i < routes.size(); i++) {
            List<RouteLeg> legs = routes.get(i).getRoute().legs();

            for (int k = 0; k < legs.size(); k++) {
                double fuelPrice = 4;
                double distanceFuelToGasStation = vehicle.getFuel_quantity() * vehicle.getConsumption();

                if (distanceFuelToGasStation < legs.get(k).distance()) {
                    for (int m = 0; m < routes.get(i).getGasStationInRoute().size(); m++) {
                        costRoute += routes.get(i).getGasStationInRoute().get(m).getPrice_gas();
                    }
                    if (lastCost > costRoute) {
                        lastCost = costRoute;

                        bestRoute = new Route (routes.get(i).getOrigin(),
                                routes.get(i).getDestiny(), routes.get(i).getRoute());
                    }
                }
            }
        }
        return bestRoute;
    }*/
        return bestRoute;
    }

    public int getBestIndexPricePriority (Route route) {
        double bestPricePriority = route.getGasStationInRoute().get(0).getPrice_gas();
        int bestIndexPricePriority = 0;

        for(int i = 0; i < route.getGasStationInRoute().size(); i++) {
            if (route.getGasStationInRoute().get(i).getPrice_gas() < bestPricePriority) {
                bestPricePriority = route.getGasStationInRoute().get(i).getPrice_gas();
                bestIndexPricePriority = i;
            } else if (route.getGasStationInRoute().get(i).getPrice_gas() == bestPricePriority) {
                List<RouteLeg> legsToBeCompared = new ArrayList<>(2);
                legsToBeCompared.add(route.getRoute().legs().get(i));
                legsToBeCompared.add(route.getRoute().legs().get(bestIndexPricePriority));
                bestIndexPricePriority = getBestIndexDistance(legsToBeCompared);
                bestPricePriority = route.getGasStationInRoute().get(bestIndexPricePriority).getPrice_gas();
            }
        }
        return bestIndexPricePriority;
    }

    public int getBestIndexDistance (List<RouteLeg> legs) {
        double bestDistanceLeg = legs.get(0).distance();
        int bestIndexLeg = 0;

        for(int i = 0; i < legs.size(); i++) {
            if (legs.get(i).distance() < bestDistanceLeg) {
                bestDistanceLeg = legs.get(i).distance();
                bestIndexLeg = i;
            }
        }
        return bestIndexLeg;
    }

    public boolean canReach (double distance, Vehicle vehicle) {
        if(distance >= vehicle.getConsumption()*vehicle.getFuel_quantity())
            return true;
        else
            return false;
    }

    public double howMuchGas (double distance, Vehicle vehicle) {
        return distance / vehicle.getConsumption();
    }


}
