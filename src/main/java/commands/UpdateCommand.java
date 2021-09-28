package commands;

import general.Coordinates;
import general.Location;
import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;

/**
 * Класс команды "update". Заменяет элемент по ключу
 */
public class UpdateCommand extends AbstractCommand implements Serializable {
    Integer id;
    String name;
    Coordinates coordinates;
    Location from;
    Location to;
    Float distance;
    private String message;

    public UpdateCommand(User user, Integer id, String name, Coordinates coordinates, Location from, Location to, Float distance) {
        super("update id", user);
        this.id = id;
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
        message = collectionManager.update(id, name, coordinates, from, to, distance, this.getUser());

    }

    @Override
    public String getMessage() {
        return message;
    }
}
