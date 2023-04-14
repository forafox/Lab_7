package commands;



/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 22.02.2023 0:27
 */

import collection.CollectionManager;
import collection.LabWorkFieldValidation;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.CollectionDatabaseHandler;
import database.UserData;
import exceptions.CannotExecuteCommandException;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Команда, удаляющая элемент по ключу
 */
public class RemoveKeyElementCommand extends Command {
    /**
     * Поле, хранящее ссылку на объект класса CollectionManager.
     */
    private CollectionManager collectionManager;

    private CollectionDatabaseHandler cdh;

    /**
     * Конструктор класса.
     */
    public RemoveKeyElementCommand() {
        super("remove_key");
    }

    public RemoveKeyElementCommand(CollectionManager collectionManager, CollectionDatabaseHandler cdh) {
        this.collectionManager = collectionManager;
        this.cdh=cdh;
    }

    /**
     * Метод, исполняющий команду. В случае успешного выполнения удалится элемент коллекции, значение уникального идентификаторого которого соответствует переданному в качестве аргумента, иначе предупреждение.
     * @param invocationEnum режим, с которым должна быть исполнена данная команда.
     * @param printStream поток вывода.
     * @param arguments аргументы команды.
     */
    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData, ReadWriteLock locker) throws CannotExecuteCommandException, SQLException {
        if (invocationEnum.equals(InvocationStatus.CLIENT)) {
            result = new ArrayList<>();
            if (arguments.length != 1) {
                throw new CannotExecuteCommandException("Введены неверные аргументы команды. ");
            } else if (!LabWorkFieldValidation.validate("id", arguments[0])) {
                throw new CannotExecuteCommandException("Введены невалидные аргументы команды: id = " + arguments[0]);
            }
            result.add(Integer.parseInt(arguments[0]));
        } else if (invocationEnum.equals(InvocationStatus.SERVER)) {
            Integer id = (Integer) this.getResult().get(0);
            try {
                locker.writeLock().lock();
                if (cdh.isOwner(id, userData)) {
                    cdh.deleteRowById(id);
                    collectionManager.removeKey(id);
                    printStream.println("Элемент с id = " + id + " был удален.");
                } else printStream.println("Элемента с указанным id не существует.");
            } finally {
                locker.writeLock().unlock();
            }
        }
    }
    /**
     * Метод, возвращающий описание команды.
     *
     * @return Возвращает описание команды.
     * @see Command
     */
    @Override
    public String getDescription() {
        return "удаляет элемент с указанным ключом";
    }
}
