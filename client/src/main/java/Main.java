import client.ServerFacade;
import ui.loginREPL;


public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client:");

        //start the server
        ServerFacade server = new ServerFacade();
        //loginREPL script
        loginREPL login = new loginREPL(server);
        login.run();
        System.out.println("Exited");
    }
}

