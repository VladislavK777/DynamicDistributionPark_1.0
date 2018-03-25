package com.uraltranscom.service.impl;

import com.uraltranscom.service.GetFullMonthCircleOfWagon;
import com.uraltranscom.service.additional.JavaHelperBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * Класс расчета количества дней, затраченных вагоном за один цикл. По вагонам количесво дней суммируется
 *
 * @author Vladislav Klochkov
 * @version 4.0
 * @create 08.11.2017
 *
 * 12.01.2018
 *   1. Версия 3.0
 * 14.03.2018
 *   1. Версия 4.0
 *
 */

@Service
public class GetFullMonthCircleOfWagonImpl extends JavaHelperBase implements GetFullMonthCircleOfWagon {
    // Подключаем логгер
    private static Logger logger = LoggerFactory.getLogger(GetFullMonthCircleOfWagonImpl.class);



    Map<String, Double> mapOfDaysOfWagon = new HashMap<>();

    /*
     * Метод расчета дней, пройденных вагоном за один цикл
     * По вагонам количесво дней суммируется
     */

    @Override
    public void fullDays(String numberOfWagon, String typeOfWagon, Integer distanceOfEmpty, String distanceOfRoute) {
        double fullMonthCircle = 0;
        if (mapOfDaysOfWagon.get(numberOfWagon) == null) {
            fullMonthCircle += Double.valueOf(distanceOfEmpty) / 300 + 1;
            if (typeOfWagon.equals(TYPE_OF_WAGON_KR)) {
                fullMonthCircle += LOADING_OF_WAGON_KR;
            } else {
                fullMonthCircle += LOADING_OF_WAGON_PV;
            }
            fullMonthCircle += Double.valueOf(distanceOfRoute) / 300 + 1;
            fullMonthCircle += UNLOADING_OF_WAGON;
            mapOfDaysOfWagon.put(numberOfWagon, fullMonthCircle);
        } else {
            for (Map.Entry<String, Double> map : mapOfDaysOfWagon.entrySet()) {
                if (map.getKey().equals(numberOfWagon)) {
                    double tempDays = map.getValue();
                    tempDays += Double.valueOf(distanceOfEmpty) / 300 + 1;
                    if (typeOfWagon.equals(TYPE_OF_WAGON_KR)) {
                        tempDays += LOADING_OF_WAGON_KR;
                    } else {
                        tempDays += LOADING_OF_WAGON_PV;
                    }
                    tempDays += Double.valueOf(distanceOfRoute) / 300 + 1;
                    tempDays += UNLOADING_OF_WAGON;
                    mapOfDaysOfWagon.replace(map.getKey(), tempDays);
                }
            }
        }
    }

    public double getNumberOfDaysOfWagon(String numberOfWagon) {
        return mapOfDaysOfWagon.get(numberOfWagon);
    }

    public void deleteFromMap(String numberOfWagon) {
        Iterator<Map.Entry<String, Double>> it = mapOfDaysOfWagon.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Double> entry = it.next();
            if (entry.getKey().equals(numberOfWagon)) {
                logger.info("Удалено: {}" + numberOfWagon);
                it.remove();
            }
        }
    }

    public Map<String, Double> getMapOfDaysOfWagon() {
        return mapOfDaysOfWagon;
    }

    public void setMapOfDaysOfWagon(Map<String, Double> mapOfDaysOfWagon) {
        this.mapOfDaysOfWagon = mapOfDaysOfWagon;
    }
}
