import client.ServerFacade;
import ui.LoginREPL;


public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client:");

        //start the server
        WebServerFacade server = new ServerFacade();
        //loginREPL script
        GameLoginREPL login = new LoginREPL(server);
        login.run();
        System.out.println("Exited");
    }
}

