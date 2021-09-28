package utilites;


import ch.qos.logback.classic.Logger;
import commands.AbstractCommand;
import org.slf4j.LoggerFactory;

public class CommandExecutor {
    private final Logger logger = (Logger) LoggerFactory.getLogger(CommandExecutor.class);

    public String executeCommand(AbstractCommand command, CollectionManager collectionManager) {
        User hashUser;
        if (command.getUser() == null) {
            hashUser = null;
        } else {
            hashUser = new User(command.getUser().getLogin(), DataHasher.hash(command.getUser().getPassword()));
        }
        command.setUser(hashUser);

        command.execute(collectionManager);
        logger.info("Command executed - CommandExecutor.executeCommand() - " + command.getName());
        logger.debug("Successfully saved");
        return command.getMessage();
    }
}
