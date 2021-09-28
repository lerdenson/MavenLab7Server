package commands;

import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;

/**
 * Команда "remove_all_by_distance". Удаляет эл-ты с определенным кол-ом комнат
 */
public class RemoveAllByDistanceCommand extends AbstractCommand implements Serializable {
    Float distance;
    private String message;

    public RemoveAllByDistanceCommand(User user, Float distance) {

        super("remove_all_by_distance", user);
        this.distance = distance;
    }


    @Override
    public void execute(CollectionManager collectionManager) {
        message = collectionManager.remove_all_by_distance(distance, this.getUser());
    }

    @Override
    public String getMessage() {
        return message;
    }
}
