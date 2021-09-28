package commands;

import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;

/**
 * Команда, выводящая элементы, значение поля distance которых меньше заданного
 */
public class FilterLessThanDistance extends AbstractCommand implements Serializable {
    Float distance;
    private String message;

    public FilterLessThanDistance(User user, Float distance) {
        super("filter_less_than_distance", user);
        this.distance = distance;
    }

    @Override
    public void execute(CollectionManager collectionManager) {
        message = collectionManager.filterLessThanDistance(distance);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
