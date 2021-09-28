package commands;


import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;

/**
 * Команда "info". Выводит информацию о коллекции
 */
public class InfoCommand extends AbstractCommand implements Serializable {
    private String message;

    public InfoCommand(User user) {
        super("info", user);
    }


    @Override
    public void execute(CollectionManager collectionManager) {
        message = collectionManager.info();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
