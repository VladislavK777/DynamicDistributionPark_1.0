package com.uraltranscom.dynamicdistributionpark.service.impl;

import com.uraltranscom.dynamicdistributionpark.service.GetTypeOfCargo;
import com.uraltranscom.dynamicdistributionpark.util.ConnectUtil.ConnectionDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * Класс получения класса груза
 * Implementation for {@link GetTypeOfCargo} interface
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
@Component
public class GetTypeOfCargoImpl extends ConnectionDB implements GetTypeOfCargo {
    // Подключаем логгер
    private static Logger logger = LoggerFactory.getLogger(GetTypeOfCargoImpl.class);

    public GetTypeOfCargoImpl() {
    }

    @Override
    public int getTypeOfCargo(String key) {

        int type = 0;

        if (key.equals("")) {
            return -1;
        }

        try (Connection connection = getDataSource().getConnection();
             CallableStatement callableStatement = createCallableStatement(connection, key);
             ResultSet resultSet = callableStatement.executeQuery()) {
            while (resultSet.next()) {
                type = resultSet.getInt(1);
            }
            logger.debug("Get type of cargo: {}", key + ": " + type);
        } catch (SQLException sqlEx) {
            logger.error("Ошибка запроса: {}", sqlEx.getMessage());
        }
        return type;
    }

    private CallableStatement createCallableStatement(Connection connection, String key) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall(" { call getclassofcargo(?) } ");
        callableStatement.setString(1, key);
        return callableStatement;
    }
}
