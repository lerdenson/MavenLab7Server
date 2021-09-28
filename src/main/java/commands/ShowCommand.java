package commands;

import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;

/**
 * Класс команды "show". Выводит коллекцию в строковом представлении
 */
public class ShowCommand extends AbstractCommand implements Serializable {
    private String message;

    public ShowCommand(User user) {
        super("show", user);
    }

    @Override
    public void execute(CollectionManager collectionManager) {
        this.message = collectionManager.show();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
