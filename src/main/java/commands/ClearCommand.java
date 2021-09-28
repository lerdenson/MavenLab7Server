package commands;

import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;

/**
 * команда "clear". Удаляет все элементы коллекции
 */
public class ClearCommand extends AbstractCommand implements Serializable {
    private String message;

    public ClearCommand(User user) {
        super("clear", user);
    }


    @Override
    public void execute(CollectionManager collectionManager) {
        message = collectionManager.clear(this.getUser());
    }

    @Override
    public String getMessage() {
        return message;
    }
}
