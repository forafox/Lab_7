package commands;


/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 23.02.2023 0:30
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
 * Команда, удаляющая из коллекции элементы с ключем, меньше чем заданный
 */
public class RemoveLowerKeyCommand extends Command {
    /**
     * Поле, хранящее ссылку на объект класса CollectionManager.
     */
    private CollectionManager collectionManager;
    private CollectionDatabaseHandler cdh;

    /**
     * Конструктор класса.
     */
    public RemoveLowerKeyCommand() {
        super("remove_lower_key");
    }

    public RemoveLowerKeyCommand(CollectionManager collectionManager, CollectionDatabaseHandler cdh) {
        this.cdh=cdh;
        this.collectionManager = collectionManager;
    }

    /**
     * Метод, исполняющий команду. В случае успешного выполнения удалятся все элементы коллекции, значения идентификатора которых меньше переданного в качестве аргументов значения, иначе предупреждение.
     * @param invocationEnum режим, с которым должна быть исполнена данная команда.
     * @param printStream поток вывода.
     * @param arguments аргументы команды.
     */
    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData, ReadWriteLock locker) throws CannotExecuteCommandException {
        if (invocationEnum.equals(InvocationStatus.CLIENT)) {
            result = new ArrayList<>();
            if (arguments.length != 1) {
                throw new CannotExecuteCommandException("Количество аргументов у данной команды равно 1.");
            }
            if (LabWorkFieldValidation.validate("id", arguments[0])) {
                result.add(Integer.parseInt(arguments[0])); //сохраняем id, меньше которых следует удалять.
            } else {
                throw new CannotExecuteCommandException("Введены невалидные аргументы: id = " + arguments[0]);
            }
        } else if (invocationEnum.equals(InvocationStatus.SERVER)) {
            Integer id = (Integer) this.getResult().get(0);
            try {
                locker.writeLock().lock();
                locker.readLock().lock();
                Integer[] keys = collectionManager.getLowerKeys(id);
                locker.readLock().unlock();
                for (Integer key : keys) {
                    if (cdh.isOwner(key, userData)) {
                        cdh.deleteRowById(key);
                        collectionManager.removeKey(key);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                locker.writeLock().unlock();
            }
            printStream.println("Элементы с id < " + id + " были удалены, принадлежащие пользователю " + userData.getLogin());
        }
    }

    /**
     * Метод, возвращающий описание команды.
     *
     * @return Возращает описание команды.
     * @see Command
     */
    @Override
    public String getDescription() {
        return "удаляет все элементы коллекции, значение id которых меньше указанного в качестве атрибута команды";
    }
}