package commands;

import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;

/**
 * Команда "remove_key". Удаляет эл-т по ключу
 */
public class RemoveByIdCommand extends AbstractCommand implements Serializable {
    private final Integer id;
    private String message;

    public RemoveByIdCommand(User user, Integer id) {
        super("remove_by_id", user);
        this.id = id;

    }


    @Override
    public void execute(CollectionManager collectionManager) {
        message = collectionManager.remove_by_id(id, this.getUser());
    }

    @Override
    public String getMessage() {
        return message;
    }
}
