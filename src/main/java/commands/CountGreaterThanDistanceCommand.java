package commands;

import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;

/**
 * Класс для команды "count_greater_than_distance"
 */
public class CountGreaterThanDistanceCommand extends AbstractCommand implements Serializable {
    private final Float distance;
    private String message;

    public CountGreaterThanDistanceCommand(User user, Float distance) {
        super("count_greater_than_distance", user);
        this.distance = distance;
    }

    @Override
    public void execute(CollectionManager collectionManager) {
        message = "Collection included elements with distance more than given is: " + collectionManager.countGreaterThanDistance(distance);

    }

    @Override
    public String getMessage() {
        return message;
    }
}