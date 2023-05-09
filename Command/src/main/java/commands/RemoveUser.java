package commands;

import collection.CollectionManager;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.CollectionDatabaseHandler;
import database.UserData;
import database.UserStatus;
import exceptions.CannotExecuteCommandException;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;



/**
 * @author  Karabanov Andrey
 * @version 1.0
 * @date  09.05.2023 15:32
 */
public class RemoveUser extends Command {
    /**
     * Поле, хранящее ссылку на объект класса CollectionManager.
     */
    private CollectionManager collectionManager;

    private CollectionDatabaseHandler cdh;
    public RemoveUser(){
        super("remove_user");
    }

    public RemoveUser(CollectionManager collectionManager, CollectionDatabaseHandler cdh){
         this.collectionManager=collectionManager;
         this.cdh=cdh;
     }
    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData, ReadWriteLock locker) throws CannotExecuteCommandException, SQLException {
        if(invocationEnum.equals(InvocationStatus.CLIENT)){
            result = new ArrayList<>();
            if (arguments.length != 1) {
                throw new CannotExecuteCommandException("Введены неверные аргументы команды. ");
        }
            result.add((arguments[0]));
            }else if (invocationEnum.equals(InvocationStatus.SERVER)) {
            if (cdh.isAdmin(userData).equals(UserStatus.SIMPLE_USER)){
                printStream.println("Пользователь не является администратором");
                return;
            }
            String str = (String) this.getResult().get(0);

            try {
                locker.writeLock().lock();
                if(CollectionDatabaseHandler.findUserByName(str)!=null){

                    Integer[] ids=cdh.removeUser(str);
                for (int id_element : ids) collectionManager.removeKey(id_element);
                printStream.println("User с login = " + userData.getLogin() + " был удален.");
                    }else{
                    printStream.println("User с "+userData.getLogin()+"не существует");
                }
            } finally {
                locker.writeLock().unlock();
            }
        }
    }
    /**
     * Метод, возращающий описание команды
     * @return Метод, возвращающий описание команды.
     * @see Command
     */
    @Override
    public String getDescription() {
        return "удаление user";
    }
}
