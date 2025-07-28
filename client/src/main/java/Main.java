import client.ServerFacade;
import ui.LoginREPL;


public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client:");

        //start the server
        ServerFacade server = new ServerFacade();
        //loginREPL script
        LoginREPL login = new LoginREPL(server);
        login.run();
        System.out.println("Exited");
    }
}

