import io.UserIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 18.03.2023 21:55
 */
public class MainClient {
    private static final Logger rootLogger = LogManager.getRootLogger();
    public static void main(String[] args){
        Application application;
        if (args.length != 0) {
            application = new Application(Integer.parseInt(args[0]));
        }else{
            System.out.println("Please enter the port value...\nThe value must be an integer!");
            UserIO userIO = new UserIO();
            application = new Application(Integer.parseInt(userIO.readLine()));
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            //To Do
            System.out.print("\nGoodBye!\n");
        }));
            try {
                application.start();
            } catch (NumberFormatException ex) {
                rootLogger.warn("The port value must be an integer.\n Entered value: " + args[0]);
            }

    }
}
