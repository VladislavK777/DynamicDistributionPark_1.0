package com.uraltranscom.dynamicdistributionpark.service.impl;

import com.uraltranscom.dynamicdistributionpark.model.Route;
import com.uraltranscom.dynamicdistributionpark.model.Wagon;
import com.uraltranscom.dynamicdistributionpark.service.GetListOfDistance;
import com.uraltranscom.dynamicdistributionpark.service.additional.JavaHelperBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *
 * Класс получения списка первоначальных расстояний
 * Implementation for {@link GetListOfDistance} interface
 *
 * @author Vladislav Klochkov
 * @version 1.0
 * @create 19.07.2018
 *
 * 19.07.2018
 *   1. Версия 1.0
 *
 */

@Service
public class GetListOfDistanceImpl extends JavaHelperBase implements GetListOfDistance {
    // Подключаем логгер
    private static Logger logger = LoggerFactory.getLogger(GetListOfDistanceImpl.class);

    @Autowired
    private GetListOfRoutesImpl getListOfRoutesImpl;
    @Autowired
    private GetDistanceBetweenStationsImpl getDistanceBetweenStations;
    @Autowired
    private GetListOfWagonsImpl getListOfWagonsImpl;
    @Autowired
    private CheckExistKeyOfStationImpl checkExistKeyOfStationImpl;
    @Autowired
    private BasicClassLookingForImpl basicClassLookingForImpl;
    @Autowired
    private GetTypeOfCargoImpl getTypeOfCargo;

    // Основная мапа
    private Map<String, List<Integer>> rootMapWithDistances = new HashMap<>();

    // Мапа с расстояниями больше максимального значения
    private Map<String, List<Integer>> rootMapWithDistanceMoreMaxDist = new HashMap<>();

    @Override
    public void fillMap() {
        logger.info("Start process fill map with distances");

        Map<Integer, Route> mapOfRoutes = new HashMap<>(getListOfRoutesImpl.getMapOfRoutes());
        List<Wagon> listOfWagons = new ArrayList<>(getListOfWagonsImpl.getListOfWagons());

        Iterator<Map.Entry<Integer, Route>> iterator = mapOfRoutes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Route> entry = iterator.next();
            for (int i = 0; i < listOfWagons.size(); i++) {

                String wagonKeyOfStationDestination = listOfWagons.get(i).getKeyOfStationDestination();
                String routeKeyOfStationDeparture = entry.getValue().getKeyOfStationDeparture();

                String key = wagonKeyOfStationDestination + "_" + routeKeyOfStationDeparture;
                
                // Заполняем мапы расстояний
                if (!rootMapWithDistanceMoreMaxDist.containsKey(key)) {
                    if (!rootMapWithDistances.containsKey(key)) {
                        List<Integer> listDistance = getDistanceBetweenStations.getDistanceBetweenStations(wagonKeyOfStationDestination, routeKeyOfStationDeparture);
                        int distance = listDistance.get(0);
                        if (distance == -1) {
                            if (!checkExistKeyOfStationImpl.checkExistKey(routeKeyOfStationDeparture)) {
                                basicClassLookingForImpl.getListOfError().add("Проверьте код станции " + routeKeyOfStationDeparture + " в файле заявок");
                                logger.error("Проверьте код станции " + routeKeyOfStationDeparture + " в файле заявок");
                                iterator.remove();
                                break;
                            }
                            if (!checkExistKeyOfStationImpl.checkExistKey(wagonKeyOfStationDestination)) {
                                basicClassLookingForImpl.getListOfError().add("Проверьте код станции " + wagonKeyOfStationDestination + " в файле дислокации вагонов");
                                logger.error("Проверьте код станции {}", wagonKeyOfStationDestination + " в файле дислокации вагонов");
                                listOfWagons.remove(i);
                                break;
                            }
                            if (checkExistKeyOfStationImpl.checkExistKey(routeKeyOfStationDeparture) && checkExistKeyOfStationImpl.checkExistKey(wagonKeyOfStationDestination)) {
                                basicClassLookingForImpl.getListOfError().add("Не нашел расстояние между " + wagonKeyOfStationDestination + " и " + routeKeyOfStationDeparture);
                                logger.error("Не нашел расстояние между " + wagonKeyOfStationDestination + " и " + routeKeyOfStationDeparture);
                                break;
                            }
                        } else {
                            if (distance != -20000) {
                                rootMapWithDistances.put(key, listDistance);
                            } else {
                                rootMapWithDistanceMoreMaxDist.put(key, listDistance);
                            }
                        }
                    }
                }
            }
        }
        
        logger.info("Stop process fill map with distances");
    }

    public Map<String, List<Integer>> getRootMapWithDistances() {
        return rootMapWithDistances;
    }

    public void setRootMapWithDistances(Map<String, List<Integer>> rootMapWithDistances) {
        this.rootMapWithDistances = rootMapWithDistances;
    }

    public Map<String, List<Integer>> getRootMapWithDistanceMoreMaxDist() {
        return rootMapWithDistanceMoreMaxDist;
    }

    public void setRootMapWithDistanceMoreMaxDist(Map<String, List<Integer>> rootMapWithDistanceMoreMaxDist) {
        this.rootMapWithDistanceMoreMaxDist = rootMapWithDistanceMoreMaxDist;
    }

    public GetListOfRoutesImpl getGetListOfRoutesImpl() {
        return getListOfRoutesImpl;
    }

    public void setGetListOfRoutesImpl(GetListOfRoutesImpl getListOfRoutesImpl) {
        this.getListOfRoutesImpl = getListOfRoutesImpl;
    }

    public GetListOfWagonsImpl getGetListOfWagonsImpl() {
        return getListOfWagonsImpl;
    }

    public void setGetListOfWagonsImpl(GetListOfWagonsImpl getListOfWagonsImpl) {
        this.getListOfWagonsImpl = getListOfWagonsImpl;
    }
}