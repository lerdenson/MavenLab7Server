package commands;


import utilites.CollectionManager;
import utilites.User;

import java.io.Serializable;

/**
 * Command 'help'.
 */
public class HelpCommand extends AbstractCommand implements Serializable {
    private String message;

    public HelpCommand(User user) {
        super("help", user);
    }

    @Override
    public void execute(CollectionManager collectionManager) {
        message = collectionManager.help();
    }

    @Override
    public String getMessage() {
        return message;
    }
}

