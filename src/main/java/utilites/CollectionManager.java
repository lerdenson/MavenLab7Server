package utilites;

import exceptions.DatabaseManagerException;
import exceptions.MultiUserException;
import exceptions.UserAlreadyExistException;
import exceptions.UserNotFoundException;
import general.Coordinates;
import general.Location;
import general.Route;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Управляет коллекцией
 */
//работа с коллекцией: создание нового id, добавление элемента, удаление и тд
public class CollectionManager implements Serializable {
    private final DatabaseUserManager databaseUserManager;
    private final DatabaseCollectionManager databaseCollectionManager;
    private LinkedHashSet<Route> routeCollection;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public CollectionManager(DatabaseCollectionManager databaseCollectionManager, DatabaseUserManager databaseUserManager) {
        routeCollection = loadCollection();
        this.databaseUserManager = databaseUserManager;
        this.databaseCollectionManager = databaseCollectionManager;
    }

    public String help() {
        return "help : вывести справку по доступным командам\n" +
                "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "add {element} : добавить новый элемент в коллекцию\n" +
                "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                "remove_by_id id : удалить элемент из коллекции по его id\n" +
                "clear : очистить коллекцию\n" +
                "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                "exit : завершить программу (без сохранения в файл)\n" +
                "add_if_max {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции\n" +
                "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный\n" +
                "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\n" +
                "remove_all_by_distance distance : удалить из коллекции все элементы, значение поля distance которого эквивалентно заданному\n" +
                "count_greater_than_distance distance : вывести количество элементов, значение поля distance которых больше заданного\n" +
                "filter_less_than_distance distance : вывести элементы, значение поля distance которых меньше заданного";
    }

    /**
     * Читает коллекцию из файла
     */
    public LinkedHashSet<Route> loadCollection() {
        readWriteLock.writeLock().lock();
        LinkedHashSet<Route> routes = new LinkedHashSet<>();
        try {
            routes = databaseCollectionManager.getCollection();
        } catch (NullPointerException e) {
            System.out.println("loaded collection is empty");
        }
        readWriteLock.writeLock().unlock();
        return routes;
    }


    /**
     * Чистит коллекцию
     */

    public String clear(User user) {
        readWriteLock.writeLock().lock();
        readWriteLock.readLock().lock();
        try {
            databaseCollectionManager.clearCollection(user);
            routeCollection.clear();
            return "Your collection was successfully cleared";
        } catch (DatabaseManagerException e) {
            System.out.println("Error in clear command");
            return "Error in clear command";
        }
    }

    public String info() {
        String message = ("Тип коллекции: LinkedHashSet\n" +
                "Колличество элементов: " + routeCollection.size() + "\n" +
                "Имена элементов: \n");
        message += routeCollection.stream().map(Route::getName).collect(Collectors.joining("\n"));
        return message;
    }

    public String show() {
        return "\n" + routeCollection.stream().map(Route::toString).collect(Collectors.joining("\n"));
    }

    /**
     * Заменяет элемент по ID
     *
     * @param id ID
     */
    public String update(Integer id, String name, Coordinates coordinates, Location from, Location to, Float distance, User user) {
        try {
            readWriteLock.readLock().lock();
            readWriteLock.writeLock().lock();
            if (databaseCollectionManager.checkRouteByIdAndUserId(id, user)) {
                databaseCollectionManager.updateRouteById(id, new Route(name, coordinates, from, to, distance, user));
                routeCollection = loadCollection();
                return "Success";
            } else return "It's not your element";
        } catch (DatabaseManagerException e) {
            System.out.println("Error in update command");
            return "Error with access to database";
        } finally {
            readWriteLock.writeLock().unlock();
            readWriteLock.readLock().unlock();
        }

    }

    /**
     * Добавляет новый элемент в коллекцию
     */
    public String add(String name, Coordinates coordinates, Location from, Location to, Float distance, User user) {
        try {
            readWriteLock.readLock().lock();
            readWriteLock.writeLock().lock();
            databaseCollectionManager.insertRoute(new Route(name, coordinates, from, to, distance, user));
            routeCollection = loadCollection();
            return "Success";
        } catch (DatabaseManagerException e) {
            System.out.println("Error in add command");
            return "Error with access to database";
        } finally {
            readWriteLock.writeLock().unlock();
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Удаляет элемент по его ID
     *
     * @param id id
     */
    public String remove_by_id(Integer id, User user) {
        try {
            readWriteLock.readLock().lock();
            readWriteLock.writeLock().lock();
            if (databaseCollectionManager.checkRouteByIdAndUserId(id, user)) {
                databaseCollectionManager.deleteRouteById(id);
                routeCollection = loadCollection();
                return "Element was deleted";
            }
            return "Element with such ID don't exist";
        } catch (DatabaseManagerException e) {
            System.out.println("Error in remove command");
            return "Error with access to database";
        } finally {
            readWriteLock.writeLock().unlock();
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * подсчитывает количество маршрутов с длинной больше заданного
     *
     * @param distance distance
     * @return колличество маршрутов
     */
    public int countGreaterThanDistance(Float distance) {
        return (int) routeCollection.stream()
                .filter(s -> s.getDistance().compareTo(distance) > 0)
                .count();
    }

    public String filterLessThanDistance(Float distance) {
        return routeCollection.stream()
                .filter(s -> s.getDistance().compareTo(distance) < 0)
                .map(Route::toString).collect(Collectors.joining("\n"));
    }

    /**
     * Удаляет все элемнеты с такой длинной пути
     *
     * @param distance distance
     */
    public String remove_all_by_distance(Float distance, User user) {
        int[] a = routeCollection.stream().filter(s -> s.getDistance().compareTo(distance) == 0).filter(route -> !(route.getUser().equals(user))).mapToInt(Route::getId).toArray();
        try {
            readWriteLock.readLock().lock();
            readWriteLock.writeLock().lock();
            for (int id : a) {
                databaseCollectionManager.deleteRouteById(id);
            }
            return "Success";
        } catch (DatabaseManagerException e) {
            System.out.println("Error in remove_all_by_distance command");
            return "Error with access to database";
        } finally {
            readWriteLock.writeLock().unlock();
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Удаляет все элемнеты больше заданного
     *
     * @param route route
     */
    public String remove_greater(Route route) {
        int[] a = routeCollection.stream().filter(s -> s.compareTo(route) < 0).mapToInt(Route::getId).toArray();
        return deleteRoutesWithSuchIDs(a);
    }

    private String deleteRoutesWithSuchIDs(int[] a) {
        try {
            readWriteLock.readLock().lock();
            readWriteLock.writeLock().lock();
            for (int id : a) {
                databaseCollectionManager.deleteRouteById(id);
            }
            return "Success";
        } catch (DatabaseManagerException e) {
            System.out.println("Error in remove type command");
            return "Error with access to database";
        } finally {
            readWriteLock.writeLock().unlock();
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Удаляет все элемнеты меньше заданного
     *
     * @param route route
     */
    public String remove_lower(Route route) {
        int[] a = routeCollection.stream().filter(s -> s.compareTo(route) > 0).mapToInt(Route::getId).toArray();
        return deleteRoutesWithSuchIDs(a);
    }

    /**
     * Добавляет элемент в коллекцию, если он больше всех в коллекции
     */
    public String ifMaxRoute(String name, Coordinates coordinates, Location from, Location to, Float distance, User user) {
        Route route = new Route(name, coordinates, from, to, distance, user);
        Route maxRoute = routeCollection.stream().max(Route::compareTo).get();
        if (route.compareTo(maxRoute) > 0) {
            try {
                readWriteLock.readLock().lock();
                readWriteLock.writeLock().lock();
                databaseCollectionManager.insertRoute(route);
                routeCollection = loadCollection();
                return "Successfully added";
            } catch (DatabaseManagerException e) {
                System.out.println("Error in add_if_max command");
                return "Error with access to database";
            } finally {
                readWriteLock.writeLock().unlock();
                readWriteLock.readLock().unlock();
            }
        }
        return "Element isn't max";
    }

    public String logIn(User user) {
        try {
            if (databaseUserManager.checkUserByUsernameAndPassword(user)) {
                databaseUserManager.updateOnline(user, true);
                return "You was successfully authorised!";
            } else throw new UserNotFoundException();
        } catch (MultiUserException e) {
            return "This user is already logged in";
        } catch (DatabaseManagerException e) {
            return "Something has gone wrong with database";
        } catch (UserNotFoundException e) {
            return "Such user doesn't exist. please try again";
        }
    }

    public String register(User user) {
        try {
            if (databaseUserManager.insertUser(user)) {
                return "You successfully sign up";
            } else throw new UserAlreadyExistException();
        } catch (DatabaseManagerException e) {
            return "Something has gone wrong with database";
        } catch (UserAlreadyExistException e) {
            return "Such user already exist. Please try again";
        }
    }

}
