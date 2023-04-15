package commands;

import collection.*;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.CollectionDatabaseHandler;
import database.UserData;
import exceptions.CannotExecuteCommandException;
import io.UserIO;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 22.02.2023 0:44
 */

/**
 * Команда , обновляющая элемент коллекции
 */
public class UpdateElementCommand extends Command{
    /**
     * Поле, хранящее ссылку на объект класса CollectionManager.
     */
    private CollectionManager collectionManager;
    /**
     * Поле, хранящее ссылку на объект класса UserIO.
     */
    private UserIO userIO;
    private CollectionDatabaseHandler cdh;
    /**
     * Конструктор класса с единственным аргументом UserIO
     */
    public UpdateElementCommand(UserIO userIO){
        super("Update");
        this.userIO=userIO;
    }
    /**
     * Конструктор класса с единственным аргументом CollectionManager
     */
    public UpdateElementCommand(CollectionManager collectionManager, CollectionDatabaseHandler cdh) {
        this.cdh = cdh;
        this.collectionManager = collectionManager;
    }
    /**
     * Поле, хранящее массив аргументов команды.
     */
    private String[] commandArguments;
    /**
     * @param collectionManager Хранит ссылку на созданный в объекте Application объект CollectionManager.
     * @param userIO            Хранит ссылку на объект класса UserIO.
     */
    public UpdateElementCommand(CollectionManager collectionManager,UserIO userIO){
        this.collectionManager=collectionManager;
        this.userIO=userIO;
    }
    /**
     * Метод, исполняющий команду. При вызове изменяется указанной элемент коллекции до тех пор, пока не будет передана пустая строка. В случае некорректного ввода высветится ошибка.
     *
     * @param invocationEnum режим, с которым должна быть исполнена данная команда.
     * @param printStream    поток вывода.
     * @param arguments      аргументы команды.
     */
    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData, ReadWriteLock locker) throws CannotExecuteCommandException, SQLException {
        if (invocationEnum.equals(InvocationStatus.CLIENT)) {
            result = new ArrayList<>();
            try {
                if (arguments.length != 1) {
                    throw new CannotExecuteCommandException("Количество аргументов данной команды должно равняться 1.");
                }
                if (!LabWorkFieldValidation.validate("id", arguments[0])) {
                    throw new CannotExecuteCommandException("Введены невалидные аргументы: id =" + arguments[0]);
                } else {

                    result.add(arguments[0]);
                    printStream.println(CollectionManager.getFieldNames());
                    printStream.println("\nВыберите поля для изменения:");
                    String[] line;

                    boolean isInputEnd = false;

                    do {
                        line = userIO.readLine().trim().split("\\s+");
                        if (line.length == 0 || line[0] == null || line[0].equals("")) isInputEnd = true;
                        else {
                            if (line.length == 1) {
                                if (LabWorkFieldValidation.validate(line[0], "")) {
                                    result.add(line[0] + ";");
                                } else printStream.println("Введены некорректные данные: \"" + line[0] + "\" + null");
                            }
                            if (line.length == 2) {
                                if (LabWorkFieldValidation.validate(line[0], line[1])) {
                                    result.add(line[0] + ";" + line[1]);
                                } else printStream.println("Введены некорректные данные: " + line[0] + " + " + line[1]);
                            }
                        }
                    } while (!isInputEnd);
                }
            } catch (NoSuchElementException ex) {
                throw new CannotExecuteCommandException("Сканнер достиг конца файла.");
            }
        } else if (invocationEnum.equals(InvocationStatus.SERVER)) {
            String[] spArguments = result.toArray(new String[0]);
            Integer id = Integer.parseInt(spArguments[0]);

            String[] fields = new String[spArguments.length - 1];
            String[] values = new String[spArguments.length - 1];
            String delimeter = ";"; // Разделитель

            try {
                locker.writeLock().lock();
                if (cdh.isOwner(id, userData)) {
                    for (int i = 1; i < spArguments.length; i++) {
                        String[] subStr = spArguments[i].split(delimeter);
                        fields[i - 1] = subStr[0];
                        values[i - 1] = subStr[1];
                    }
                    LabWork labWork = LabWorkFieldReplacer.update(cdh.getLabWorkById(id), fields, values, printStream);
                    cdh.replaceRow(labWork);
                    collectionManager.removeKey(labWork.getId());
                    collectionManager.insertWithId(labWork.getId(), labWork, printStream);
                } else {
                    printStream.println("Элемента с указанным id не существует или вы не являетесь его владельцем");
                }
            } finally {
                locker.writeLock().unlock();
            }

        }
    }


    /**
     * Метод, возвращающий описание команды.
     *
     * @return Метод, возвращающий описание команды.
     * @see Command
     */
    @Override
    public String getDescription() {
        return "изменяет указанное поле выбранного по id элемента коллекции";
    }
}
