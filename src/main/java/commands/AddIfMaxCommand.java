package commands;

import general.Coordinates;
import general.Location;
import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;

/**
 * Команда "add_if_max". Добавляет новый элемент в коллекцию
 */
public class AddIfMaxCommand extends AbstractCommand implements Serializable {
    private final String name;
    private final Coordinates coordinates;
    private final Location from;
    private final Location to;
    private final Float distance;
    private String message;

    public AddIfMaxCommand(User user, String name, Coordinates coordinates, Location from, Location to, Float distance) {
        super("add_if_max", user);
        this.name = name;
        this.coordinates = coordinates;
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    @Override
    public void execute(CollectionManager collectionManager) {
        message = collectionManager.ifMaxRoute(name, coordinates, from, to, distance, this.getUser());
    }

    @Override
    public String getMessage() {
        return message;
    }
}

