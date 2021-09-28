package commands;

import general.Coordinates;
import general.Location;
import general.Route;
import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;


/**
 * Команда "remove_lower id". Удаляет эл-ты меньше элемента с данным id
 */
public class RemoveLowerCommand extends AbstractCommand implements Serializable {
    private final String name;
    private final Coordinates coordinates;
    private final Location from;
    private final Location to;
    private final Float distance;
    private String message;

    public RemoveLowerCommand(User user, String name, Coordinates coordinates, Location from, Location to, Float distance) {
        super("remove_lower", user);
        this.name = name;
        this.coordinates = coordinates;
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    /**
     * Выполнение команды
     *
     * @param collectionManager аргумент
     */
    @Override
    public void execute(CollectionManager collectionManager) {
        message = collectionManager.remove_lower(new Route(name, coordinates, from, to, distance, this.getUser()));
    }

    @Override
    public String getMessage() {
        return message;
    }
}
