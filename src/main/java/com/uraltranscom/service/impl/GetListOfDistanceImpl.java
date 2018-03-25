package com.uraltranscom.service.impl;

import com.uraltranscom.model.Route;
import com.uraltranscom.model.Wagon;
import com.uraltranscom.service.GetListOfDistance;
import com.uraltranscom.service.additional.JavaHelperBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.*;

/**
 *
 * Класс получения списка первоначальных расстояний
 *
 * @author Vladislav Klochkov
 * @version 4.0
 * @create 14.03.2018
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


    // Основная мапа
    private static Map<String, Integer> rootMapWithDistances = new HashMap<>();

    // Заполненные мапы Вагонов и Маршрутов
    private Map<Integer, Route> mapOfRoutes = new HashMap<>();
    private List<Wagon> listOfWagons = new ArrayList<>();

    private static Connection connection;

    @Override
    public void fillMap() {
        logger.info("Start process fill map with distances");

        mapOfRoutes = getListOfRoutesImpl.getMapOfRoutes();
        listOfWagons = getListOfWagonsImpl.getListOfWagons();

        Iterator<Map.Entry<Integer, Route>> iterator = mapOfRoutes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Route> entry = iterator.next();
            for (int i = 0; i < listOfWagons.size(); i++) {
                if (!rootMapWithDistances.containsKey(listOfWagons.get(i).getNameOfStationDestination() + "_" + entry.getValue().getNameOfStationDeparture())) {
                    int distance = getDistanceBetweenStations.getDistanceBetweenStations(listOfWagons.get(i).getKeyOfStationDestination(), entry.getValue().getKeyOfStationDeparture(), connection);
                    if (distance != -1) {
                        if (distance <= MAX_DISTANCE) {
                            rootMapWithDistances.put(listOfWagons.get(i).getNameOfStationDestination() + "_" + entry.getValue().getNameOfStationDeparture(), distance);
                        } else {
                            break;
                        }
                    } else {
                        if (!checkExistKeyOfStationImpl.checkExistKey(entry.getValue().getKeyOfStationDeparture(), connection)) {
                            basicClassLookingForImpl.getListOfError().add("Проверьте код станции " + entry.getValue().getKeyOfStationDeparture());
                            logger.error("Проверьте код станции " + entry.getValue().getKeyOfStationDeparture());
                            basicClassLookingForImpl.getListOfUndistributedRoutes().add(entry.getValue().getNameOfStationDeparture() + " - " + entry.getValue().getNameOfStationDestination());
                            iterator.remove();
                            break;
                        }
                        if (!checkExistKeyOfStationImpl.checkExistKey(listOfWagons.get(i).getKeyOfStationDestination(), connection)) {
                            basicClassLookingForImpl.getListOfError().add("Проверьте код станции " + listOfWagons.get(i).getKeyOfStationDestination());
                            logger.error("Проверьте код станции {}", listOfWagons.get(i).getKeyOfStationDestination());
                            basicClassLookingForImpl.getListOfUndistributedWagons().add(listOfWagons.get(i).getNumberOfWagon());
                            listOfWagons.remove(i);
                            break;

                        }
                    }
                }
            }
        }
        logger.info("Stop process fill map with distances");
    }

    public GetListOfRoutesImpl getGetListOfRoutesImpl() {
        return getListOfRoutesImpl;
    }

    public void setGetListOfRoutesImpl(GetListOfRoutesImpl getListOfRoutesImpl) {
        this.getListOfRoutesImpl = getListOfRoutesImpl;
    }

    public GetDistanceBetweenStationsImpl getGetDistanceBetweenStations() {
        return getDistanceBetweenStations;
    }

    public void setGetDistanceBetweenStations(GetDistanceBetweenStationsImpl getDistanceBetweenStations) {
        this.getDistanceBetweenStations = getDistanceBetweenStations;
    }

    public GetListOfWagonsImpl getGetListOfWagonsImpl() {
        return getListOfWagonsImpl;
    }

    public void setGetListOfWagonsImpl(GetListOfWagonsImpl getListOfWagonsImpl) {
        this.getListOfWagonsImpl = getListOfWagonsImpl;
    }

    public CheckExistKeyOfStationImpl getCheckExistKeyOfStationImpl() {
        return checkExistKeyOfStationImpl;
    }

    public void setCheckExistKeyOfStationImpl(CheckExistKeyOfStationImpl checkExistKeyOfStationImpl) {
        this.checkExistKeyOfStationImpl = checkExistKeyOfStationImpl;
    }

    public BasicClassLookingForImpl getBasicClassLookingForImpl() {
        return basicClassLookingForImpl;
    }

    public void setBasicClassLookingForImpl(BasicClassLookingForImpl basicClassLookingForImpl) {
        this.basicClassLookingForImpl = basicClassLookingForImpl;
    }

    public Map<String, Integer> getRootMapWithDistances() {
        return rootMapWithDistances;
    }

    public void setRootMapWithDistances(Map<String, Integer> rootMapWithDistances) {
        this.rootMapWithDistances = rootMapWithDistances;
    }

    public Map<Integer, Route> getMapOfRoutes() {
        return mapOfRoutes;
    }

    public void setMapOfRoutes(Map<Integer, Route> mapOfRoutes) {
        this.mapOfRoutes = mapOfRoutes;
    }

    public List<Wagon> getListOfWagons() {
        return listOfWagons;
    }

    public void setListOfWagons(List<Wagon> listOfWagons) {
        this.listOfWagons = listOfWagons;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        GetListOfDistanceImpl.connection = connection;
    }
}
