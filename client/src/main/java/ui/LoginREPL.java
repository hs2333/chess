package ui;
import chess.ChessGame;
import client.*;
import model.GameData;
import java.util.*;

//show in the terminal window
public class LoginREPL {
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade;
    private final Map<Integer, GameData> gameIndexMap = new HashMap<>();

    private String authToken = null;
    private String username = null;

    public LoginREPL(ServerFacade facade) {
        this.facade = facade;
    }


    public void run() {
        while (true) {
            //before login
            System.out.print("\n[Prelogin] > ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> printPreloginHelp();
                case "register" -> doRegister();
                case "login" -> doLogin();
                case "h" -> printPreloginHelp();
                case "r" -> doRegister();
                case "l" -> doLogin();
                case "quit" -> {
                    System.out.println("DONE");
                    return;
                }
                default -> System.out.println("---- Unknown command. Type 'help' for options.");

            }

        }
    }

    private void runPostloginLoop() {
        while (true) {
            System.out.print("\n[Postlogin] > ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> printPostloginHelp();
                case "create game" -> doCreateGame();
                case "list games" -> doListGames();
                case "play game" -> doJoinGame();
                case "observe game" -> doObserveGame();
                case "h" -> printPostloginHelp();
                case "c" -> doCreateGame();
                case "l" -> doListGames();
                case "p" -> doJoinGame();
                case "o" -> doObserveGame();
                case "logout" -> {
                    if (facade.logout()) {
                        System.out.println("LOGGED OUT");
                        return;
                    } else {
                        System.out.println("---- Logout failed.");
                    }
                }
                default -> System.out.println("---- Unknown command. Type 'help' for options.");
            }
        }
    }


    //help
    private void printPreloginHelp() {
        System.out.println("""
                Commands:
                - help [h]
                - register [r]
                - login [l]
                
                - quit
                """);
    }

    private void printPostloginHelp() {
        System.out.println("""
                Commands:
                - help [h]
                - create game [c]
                - list games [l]
                - play game [p]
                - observe game [o]
                
                - logout
                """);
    }

    //register
    private void doRegister() {
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        if (facade.register(user, pass, email)) {
            System.out.println("Registered and logged in as " + user);
            runPostloginLoop();
        } else {
            //normally bc of repeated user info
            System.out.println("---- Registration failed.");
        }
    }

    //login
    private void doLogin() {
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        if (facade.login(user, pass)) {
            System.out.println("Logged in as " + user);
            runPostloginLoop();
        } else {
            System.out.println("---- Login failed.");
        }
    }

    //create game
    private void doCreateGame() {
        System.out.print("Game name: ");
        String name = scanner.nextLine();
        int gameId = facade.createGame(name);
        if (gameId > 0) {
            System.out.println("Created game with ID: " + gameId);
        } else {
            System.out.println("---- Game creation failed.");
        }
    }

    //list games
    private void doListGames() {
        var games = facade.listGames();
        gameIndexMap.clear();
        int i = 1;
        for (var game : games) {
            System.out.printf("%d. \"%s\" - White: %s, Black: %s\n",
                    i, game.gameName(),
                    game.whiteUsername() != null ? game.whiteUsername() : "-",
                    game.blackUsername() != null ? game.blackUsername() : "-"
            );

            gameIndexMap.put(i, game);
            i++;
        }
    }

    //join game
    private void doJoinGame() {
        System.out.print("Enter game number: ");
        int num = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter color (WHITE or BLACK): ");
        String color = scanner.nextLine().toUpperCase();

        var game = gameIndexMap.get(num);
        if (game == null) {
            System.out.println("---- Invalid game number.");
            return;
        }

        if (facade.joinGame(game.gameID(), color)) {
            System.out.println("Joined game " + game.gameName() + " as " + color);
            try {
                ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(color);
                GameplayREPL repl = new GameplayREPL(authToken, game.gameID(), username, teamColor);
                repl.run();
            } catch (Exception exception) {
                System.out.println("---- Error starting gameplay UI: " + exception.getMessage());
                exception.printStackTrace();
            }
        } else {
            System.out.println("---- Failed to join game.");
        }
    }



    //observe game
    private void doObserveGame() {
        System.out.print("Enter game number to observe: ");
        int num = Integer.parseInt(scanner.nextLine());

        var game = gameIndexMap.get(num);
        if (game == null) {
            System.out.println("---- Invalid game number.");
            return;
        }

        try {
            System.out.print("Observe as WHITE or BLACK perspective? ");
            String color = scanner.nextLine().toUpperCase();
            boolean isWhitePerspective = color.equals("WHITE");

            //pass null as playerColor to indicate observer mode
            GameplayREPL repl = new GameplayREPL(authToken, game.gameID(), username, null);
            repl.run();
        } catch (Exception exception) {
            System.out.println("---- Error starting observer UI: " + exception.getMessage());
            exception.printStackTrace();
        }
    }


}
