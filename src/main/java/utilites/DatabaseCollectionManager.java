package utilites;

import exceptions.DatabaseManagerException;
import general.Coordinates;
import general.Location;
import general.Route;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashSet;

public class DatabaseCollectionManager {
    private final String SELECT_ALL_ROUTES = "SELECT * FROM " + DatabaseManager.ROUTE_TABLE;
    private final String SELECT_ROUTE_BY_ID = SELECT_ALL_ROUTES + " WHERE " +
            DatabaseManager.ROUTE_TABLE_ID_COLUMN + " = ?";
    DatabaseManager databaseManager;
    DatabaseUserManager databaseUserManager;


    public DatabaseCollectionManager(DatabaseManager databaseManager, DatabaseUserManager databaseUserManager) {
        this.databaseManager = databaseManager;
        this.databaseUserManager = databaseUserManager;
    }


    private Route returnRoute(ResultSet resultSet, int id) throws SQLException {
        String name = resultSet.getString(DatabaseManager.ROUTE_TABLE_NAME_COLUMN);
        Coordinates coordinates = getCoordinatesByID(resultSet.getInt(DatabaseManager.ROUTE_TABLE_COORDINATES_ID_COLUMN));
        LocalDate creationDate = resultSet.getDate(DatabaseManager.ROUTE_TABLE_CREATION_DATE_COLUMN).toLocalDate();
        Location from = getLocationFromById(resultSet.getInt(DatabaseManager.ROUTE_TABLE_FROM_ID_COLUMN));
        Location to = getLocationToById(resultSet.getInt(DatabaseManager.ROUTE_TABLE_TO_ID_COLUMN));
        Float distance = resultSet.getFloat(DatabaseManager.ROUTE_TABLE_DISTANCE_COLUMN);
        User user = databaseUserManager.getUserById(resultSet.getInt(DatabaseManager.ROUTE_TABLE_USER_ID_COLUMN));
        return new Route(id, name, coordinates, creationDate, from, to, distance, user);
    }

    public LinkedHashSet<Route> getCollection() {
        LinkedHashSet<Route> routes = new LinkedHashSet<>();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = databaseManager.doPreparedStatement(SELECT_ALL_ROUTES, false);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(DatabaseManager.ROUTE_TABLE_ID_COLUMN);
                routes.add(returnRoute(resultSet, id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }

    private Coordinates getCoordinatesByID(int id) throws SQLException {
        Coordinates coordinates;
        PreparedStatement preparedStatement = null;
        try {
            String SELECT_ALL_COORDINATES = "SELECT * FROM " + DatabaseManager.COORDINATES_TABLE;
            String SELECT_COORDINATES_BY_ID = SELECT_ALL_COORDINATES + " WHERE " + DatabaseManager.COORDINATES_TABLE_ID_COLUMN + " =?";
            preparedStatement = databaseManager.doPreparedStatement(SELECT_COORDINATES_BY_ID, false);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                coordinates = new Coordinates(
                        resultSet.getFloat(DatabaseManager.COORDINATES_TABLE_X_COLUMN),
                        resultSet.getLong(DatabaseManager.COORDINATES_TABLE_Y_COLUMN)
                );
            } else throw new SQLException();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_COORDINATES_BY_ID!");
            throw new SQLException(e);
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return coordinates;
    }

    private Location getLocationFromById(int id) throws SQLException {
        Location from;
        PreparedStatement preparedStatement = null;
        try {
            String SELECT_ALL_LOCATION_FROM = "SELECT * FROM " + DatabaseManager.LOCATION_FROM_TABLE;
            String SELECT_LOCATION_FROM_BY_ID = SELECT_ALL_LOCATION_FROM + " WHERE " + DatabaseManager.LOCATION_FROM_TABLE_ID_COLUMN + " =?";
            preparedStatement = databaseManager.doPreparedStatement(SELECT_LOCATION_FROM_BY_ID, false);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                from = new Location(
                        resultSet.getFloat(DatabaseManager.LOCATION_FROM_TABLE_X_COLUMN),
                        resultSet.getInt(DatabaseManager.LOCATION_FROM_TABLE_Y_COLUMN),
                        resultSet.getString(DatabaseManager.LOCATION_FROM_TABLE_NAME_COLUMN)
                );
            } else throw new SQLException();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_LOCATION_FROM_BY_ID!");
            throw new SQLException(e);
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return from;
    }

    private Location getLocationToById(int id) throws SQLException {
        Location to;
        PreparedStatement preparedStatement = null;
        try {
            String SELECT_ALL_LOCATION_TO = "SELECT * FROM " + DatabaseManager.LOCATION_TO_TABLE;
            String SELECT_LOCATION_TO_BY_ID = SELECT_ALL_LOCATION_TO + " WHERE " + DatabaseManager.LOCATION_TO_TABLE_ID_COLUMN + " =?";
            preparedStatement = databaseManager.doPreparedStatement(SELECT_LOCATION_TO_BY_ID, false);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                to = new Location(
                        resultSet.getFloat(DatabaseManager.LOCATION_TO_TABLE_X_COLUMN),
                        resultSet.getInt(DatabaseManager.LOCATION_TO_TABLE_Y_COLUMN),
                        resultSet.getString(DatabaseManager.LOCATION_TO_TABLE_NAME_COLUMN)
                );
            } else throw new SQLException();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_LOCATION_to_BY_ID!");
            throw new SQLException(e);
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return to;
    }

    public Route insertRoute(Route route) throws DatabaseManagerException {
        route.setCreationDate(LocalDate.now());
        PreparedStatement insertRoute;
        PreparedStatement insertCoordinates;
        PreparedStatement insertLocationFrom;
        PreparedStatement insertLocationTo;
        try {
            databaseManager.setCommit();
            databaseManager.setSavepoint();

            String INSERT_COORDINATES = "INSERT INTO " +
                    DatabaseManager.COORDINATES_TABLE + " (" +
                    DatabaseManager.COORDINATES_TABLE_X_COLUMN + ", " +
                    DatabaseManager.COORDINATES_TABLE_Y_COLUMN + ") VALUES (?, ?)";
            insertCoordinates = databaseManager.doPreparedStatement(INSERT_COORDINATES, true);
            insertCoordinates.setFloat(1, route.getCoordinates().getX());
            insertCoordinates.setLong(2, route.getCoordinates().getY());
            if (insertCoordinates.executeUpdate() == 0) throw new SQLException();
            ResultSet resultSetCoordinates = insertCoordinates.getGeneratedKeys();
            int coordinatesID;
            if (resultSetCoordinates.next()) coordinatesID = resultSetCoordinates.getInt(1);
            else throw new SQLException();

            String INSERT_LOCATION_FROM = "INSERT INTO " +
                    DatabaseManager.LOCATION_FROM_TABLE + " (" +
                    DatabaseManager.LOCATION_FROM_TABLE_NAME_COLUMN + ", " +
                    DatabaseManager.LOCATION_FROM_TABLE_X_COLUMN + ", " +
                    DatabaseManager.LOCATION_FROM_TABLE_Y_COLUMN + ") VALUES (?, ?, ?)";
            insertLocationFrom = databaseManager.doPreparedStatement(INSERT_LOCATION_FROM, true);
            insertLocationFrom.setString(1, route.getFrom().getName());
            insertLocationFrom.setFloat(2, route.getFrom().getX());
            insertLocationFrom.setLong(3, route.getFrom().getY());
            if (insertLocationFrom.executeUpdate() == 0) throw new SQLException();
            ResultSet resultSetFrom = insertLocationFrom.getGeneratedKeys();
            int locationFromId;
            if (resultSetCoordinates.next()) locationFromId = resultSetFrom.getInt(1);
            else throw new SQLException();

            String INSERT_LOCATION_TO = "INSERT INTO " +
                    DatabaseManager.LOCATION_TO_TABLE + " (" +
                    DatabaseManager.LOCATION_TO_TABLE_NAME_COLUMN + ", " +
                    DatabaseManager.LOCATION_TO_TABLE_X_COLUMN + ", " +
                    DatabaseManager.LOCATION_TO_TABLE_Y_COLUMN + ") VALUES (?, ?, ?)";
            insertLocationTo = databaseManager.doPreparedStatement(INSERT_LOCATION_TO, true);
            insertLocationTo.setString(1, route.getTo().getName());
            insertLocationTo.setFloat(2, route.getTo().getX());
            insertLocationTo.setLong(3, route.getTo().getY());
            if (insertLocationTo.executeUpdate() == 0) throw new SQLException();
            ResultSet resultSetTo = insertLocationTo.getGeneratedKeys();
            int locationToId;
            if (resultSetTo.next()) locationToId = resultSetTo.getInt(1);
            else throw new SQLException();

            String INSERT_ROUTE = "INSERT INTO " +
                    DatabaseManager.ROUTE_TABLE + " (" +
                    DatabaseManager.ROUTE_TABLE_NAME_COLUMN + ", " +
                    DatabaseManager.ROUTE_TABLE_COORDINATES_ID_COLUMN + ", " +
                    DatabaseManager.ROUTE_TABLE_CREATION_DATE_COLUMN + ", " +
                    DatabaseManager.ROUTE_TABLE_FROM_ID_COLUMN + ", " +
                    DatabaseManager.ROUTE_TABLE_TO_ID_COLUMN + ", " +
                    DatabaseManager.ROUTE_TABLE_DISTANCE_COLUMN + ", " +
                    DatabaseManager.ROUTE_TABLE_USER_ID_COLUMN + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            insertRoute = databaseManager.doPreparedStatement(INSERT_ROUTE, true);
            insertRoute.setString(1, route.getName());
            insertRoute.setInt(2, coordinatesID);
            insertRoute.setDate(3, Date.valueOf(route.getCreationDate()));
            insertRoute.setInt(4, locationFromId);
            insertRoute.setInt(5, locationToId);
            insertRoute.setFloat(6, route.getDistance());
            insertRoute.setInt(7, databaseUserManager.getUserIdByUsername(route.getUser()));
            if (insertRoute.executeUpdate() == 0) throw new SQLException();
            ResultSet resultSetRoute = insertRoute.getGeneratedKeys();
            int routeID;
            if (resultSetRoute.next()) routeID = resultSetRoute.getInt(1);
            else throw new SQLException();
            route.setId(routeID);
            databaseManager.commit();
            return route;

        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при добавлении нового объекта в БД!");
            exception.printStackTrace();
            databaseManager.rollback();
            throw new DatabaseManagerException();
        }
    }

    public void updateRouteById(int id, Route route) throws DatabaseManagerException {
        PreparedStatement updateRouteName = null;
        PreparedStatement updateRouteCoordinates = null;
        PreparedStatement updateRouteLocationFrom = null;
        PreparedStatement updateRouteLocationTo = null;
        PreparedStatement updateRouteDistance = null;
        try {
            databaseManager.setCommit();
            databaseManager.setSavepoint();

            String UPDATE_ROUTE_NAME_BY_ID = "UPDATE " + DatabaseManager.ROUTE_TABLE + " SET " +
                    DatabaseManager.ROUTE_TABLE_NAME_COLUMN + " = ? WHERE " +
                    DatabaseManager.ROUTE_TABLE_ID_COLUMN + " = ?";
            updateRouteName = databaseManager.doPreparedStatement(UPDATE_ROUTE_NAME_BY_ID, false);
            String UPDATE_COORDINATES_BY_ID = "UPDATE " + DatabaseManager.COORDINATES_TABLE + " SET " +
                    DatabaseManager.COORDINATES_TABLE_X_COLUMN + " = ?, " +
                    DatabaseManager.COORDINATES_TABLE_Y_COLUMN + " = ?" + " WHERE " +
                    DatabaseManager.COORDINATES_TABLE_ID_COLUMN + " = ?";
            updateRouteCoordinates = databaseManager.doPreparedStatement(UPDATE_COORDINATES_BY_ID, false);
            String UPDATE_LOCATION_FROM_BY_ID = " UPDATE " + DatabaseManager.LOCATION_FROM_TABLE + " SET " +
                    DatabaseManager.LOCATION_FROM_TABLE_NAME_COLUMN + " = ?, " +
                    DatabaseManager.LOCATION_FROM_TABLE_X_COLUMN + " = ?, " +
                    DatabaseManager.LOCATION_FROM_TABLE_Y_COLUMN + " = ?" + " WHERE " +
                    DatabaseManager.LOCATION_FROM_TABLE_ID_COLUMN + " = ?";
            updateRouteLocationFrom = databaseManager.doPreparedStatement(UPDATE_LOCATION_FROM_BY_ID, false);
            String UPDATE_LOCATION_TO_BY_ID = " UPDATE " + DatabaseManager.LOCATION_TO_TABLE + " SET " +
                    DatabaseManager.LOCATION_TO_TABLE_NAME_COLUMN + " = ?, " +
                    DatabaseManager.LOCATION_TO_TABLE_X_COLUMN + " = ?, " +
                    DatabaseManager.LOCATION_TO_TABLE_Y_COLUMN + " = ?" + " WHERE " +
                    DatabaseManager.LOCATION_TO_TABLE_ID_COLUMN + " = ?";
            updateRouteLocationTo = databaseManager.doPreparedStatement(UPDATE_LOCATION_TO_BY_ID, false);
            String UPDATE_ROUTE_DISTANCE_BY_ID = "UPDATE " + DatabaseManager.ROUTE_TABLE + " SET " +
                    DatabaseManager.ROUTE_TABLE_DISTANCE_COLUMN + " = ? WHERE " +
                    DatabaseManager.ROUTE_TABLE_ID_COLUMN + " = ?";
            updateRouteDistance = databaseManager.doPreparedStatement(UPDATE_ROUTE_DISTANCE_BY_ID, false);

            if (route.getName() != null) {
                updateRouteName.setString(1, route.getName());
                updateRouteName.setInt(2, id);
                if (updateRouteName.executeUpdate() == 0) throw new SQLException();
            }
            if (route.getCoordinates() != null) {
                updateRouteCoordinates.setFloat(1, route.getCoordinates().getX());
                updateRouteCoordinates.setLong(2, route.getCoordinates().getY());
                updateRouteCoordinates.setInt(3, id);
                if (updateRouteCoordinates.executeUpdate() == 0) throw new SQLException();
            }
            if (route.getFrom() != null) {
                updateRouteLocationFrom.setString(1, route.getFrom().getName());
                updateRouteLocationFrom.setFloat(2, route.getFrom().getX());
                updateRouteLocationFrom.setInt(3, route.getFrom().getY());
                updateRouteLocationFrom.setInt(4, id);
                if (updateRouteLocationFrom.executeUpdate() == 0) throw new SQLException();
            }
            if (route.getTo() != null) {
                updateRouteLocationTo.setString(1, route.getTo().getName());
                updateRouteLocationTo.setFloat(2, route.getTo().getX());
                updateRouteLocationTo.setInt(3, route.getTo().getY());
                updateRouteLocationTo.setInt(4, id);
                if (updateRouteLocationTo.executeUpdate() == 0) throw new SQLException();
            }
            if (route.getDistance() != null) {
                updateRouteDistance.setFloat(1, route.getDistance());
                updateRouteDistance.setInt(2, id);
                if (updateRouteDistance.executeUpdate() == 0) throw new SQLException();
            }
            databaseManager.commit();

        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при выполнении группы запросов на обновление объекта!");
            databaseManager.rollback();
            throw new DatabaseManagerException();
        } finally {
            databaseManager.closePreparedStatement(updateRouteName);
            databaseManager.closePreparedStatement(updateRouteCoordinates);
            databaseManager.closePreparedStatement(updateRouteLocationFrom);
            databaseManager.closePreparedStatement(updateRouteLocationTo);
            databaseManager.closePreparedStatement(updateRouteDistance);
            databaseManager.setAutoCommit();
        }
    }

    private int getCoordinatesIdByRouteID(int id) throws SQLException {
        int coordinatesID;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseManager.doPreparedStatement(SELECT_ROUTE_BY_ID, false);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                coordinatesID = resultSet.getInt(DatabaseManager.ROUTE_TABLE_COORDINATES_ID_COLUMN);
            } else throw new SQLException();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_COORDINATES_ID_BY_ROUTE_BY_ID!");
            throw new SQLException(e);
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return coordinatesID;
    }

    private int getLocationFromIdByRouteID(int id) throws SQLException {
        int locationFromID;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseManager.doPreparedStatement(SELECT_ROUTE_BY_ID, false);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                locationFromID = resultSet.getInt(DatabaseManager.ROUTE_TABLE_FROM_ID_COLUMN);
            } else throw new SQLException();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_LOCATION_FROM_ID_BY_ROUTE_BY_ID!");
            throw new SQLException();
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return locationFromID;
    }

    private int getLocationToIdByRouteID(int id) throws SQLException {
        int locationToID;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseManager.doPreparedStatement(SELECT_ROUTE_BY_ID, false);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                locationToID = resultSet.getInt(DatabaseManager.ROUTE_TABLE_TO_ID_COLUMN);
            } else throw new SQLException();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_LOCATION_FROM_ID_BY_ROUTE_BY_ID!");
            throw new SQLException();
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
        return locationToID;
    }

    public void deleteRouteById(int id) throws DatabaseManagerException {
        PreparedStatement deleteRoute = null;
        PreparedStatement deleteCoordinates = null;
        PreparedStatement deleteLocationFrom = null;
        PreparedStatement deleteLocationTo = null;

        try {
            int coordinatesID = getCoordinatesIdByRouteID(id);
            int locationFromID = getLocationFromIdByRouteID(id);
            int locationToID = getLocationToIdByRouteID(id);
            String DELETE_ROUTE_BY_ID = "DELETE FROM " + DatabaseManager.ROUTE_TABLE +
                    " WHERE " + DatabaseManager.ROUTE_TABLE_ID_COLUMN + " = ?";
            deleteRoute = databaseManager.doPreparedStatement(DELETE_ROUTE_BY_ID, false);
            deleteRoute.setInt(1, id);
            if (deleteRoute.executeUpdate() == 0) throw new SQLException();
            String DELETE_COORDINATES_BY_ID = "DELETE FROM " + DatabaseManager.COORDINATES_TABLE +
                    " WHERE " + DatabaseManager.COORDINATES_TABLE_ID_COLUMN + " = ?";
            deleteCoordinates = databaseManager.doPreparedStatement(DELETE_COORDINATES_BY_ID, false);
            deleteCoordinates.setInt(1, coordinatesID);
            if (deleteCoordinates.executeUpdate() == 0) throw new SQLException();
            String DELETE_LOCATION_FROM_BY_ID = "DELETE FROM " + DatabaseManager.LOCATION_FROM_TABLE +
                    " WHERE " + DatabaseManager.LOCATION_FROM_TABLE_ID_COLUMN + " = ?";
            deleteLocationFrom = databaseManager.doPreparedStatement(DELETE_LOCATION_FROM_BY_ID, false);
            deleteLocationFrom.setInt(1, locationFromID);
            if (deleteLocationFrom.executeUpdate() == 0) throw new SQLException();
            String DELETE_LOCATION_TO_BY_ID = "DELETE FROM " + DatabaseManager.LOCATION_TO_TABLE +
                    " WHERE " + DatabaseManager.LOCATION_TO_TABLE_ID_COLUMN + " = ?";
            deleteLocationTo = databaseManager.doPreparedStatement(DELETE_LOCATION_TO_BY_ID, false);
            deleteLocationTo.setInt(1, locationToID);
            if (deleteLocationTo.executeUpdate() == 0) throw new SQLException();

        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при выполнении запроса DELETE_ROUTE_BY_ID!");
            throw new DatabaseManagerException();
        } finally {
            databaseManager.closePreparedStatement(deleteRoute);
            databaseManager.closePreparedStatement(deleteCoordinates);
            databaseManager.closePreparedStatement(deleteLocationFrom);
            databaseManager.closePreparedStatement(deleteLocationTo);
        }
    }

    public void clearCollection(User user) throws DatabaseManagerException {
        LinkedHashSet<Route> routes = getCollection();
        for (Route route : routes) {
            if (user == route.getUser()) deleteRouteById(route.getId());
        }
    }

    public boolean checkRouteByIdAndUserId(int RouteID, User user) throws DatabaseManagerException {
        PreparedStatement preparedStatement = null;
        try {
            String SELECT_ROUTE_BY_ID_AND_USER_ID = SELECT_ROUTE_BY_ID + " AND " +
                    DatabaseManager.ROUTE_TABLE_USER_ID_COLUMN + " = ?";
            preparedStatement = databaseManager.doPreparedStatement(SELECT_ROUTE_BY_ID_AND_USER_ID, false);
            preparedStatement.setInt(1, RouteID);
            preparedStatement.setInt(2, databaseUserManager.getUserIdByUsername(user));
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при выполнении запроса SELECT_ROUTE_BY_ID_AND_USER_ID!");
            throw new DatabaseManagerException();
        } finally {
            databaseManager.closePreparedStatement(preparedStatement);
        }
    }
}
