package ui;

import client.ServerFacade;
import model.GameData;

import java.util.*;

public class loginREPL {
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade;
    private String authToken = null;
    private String username = null;
    private final Map<Integer, GameData> gameIndexMap = new HashMap<>();

    public loginREPL(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() {
        while (true) {
            System.out.print("\n[Prelogin] > ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> printPreloginHelp();
                case "register" -> doRegister();
                case "login" -> doLogin();
                case "quit" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("[!] Unknown command. Type 'help' for options.");
            }
        }
    }

    private void runPostloginLoop() {
        while (authToken != null) {
            System.out.print("\n[Postlogin] > ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> printPostloginHelp();
                case "create game" -> doCreateGame();
                case "list games" -> doListGames();
                case "play game" -> doJoinGame();
                case "observe game" -> doObserveGame();
                case "logout" -> doLogout();
                default -> System.out.println("[!] Unknown command. Type 'help' for options.");
            }
        }
    }

    private void printPreloginHelp() {
        System.out.println("""
            Commands:
            - register
            - login
            - help
            - quit
            """);
    }

    private void printPostloginHelp() {
        System.out.println("""
            Commands:
            - create game
            - list games
            - play game
            - observe game
            - logout
            - help
            """);
    }

    private void doRegister() {
        try {
            System.out.print("Username: ");
            String user = scanner.nextLine();
            System.out.print("Password: ");
            String pass = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();

            var result = facade.register(user, pass, email);
            authToken = result.authToken();
            username = result.username();
            System.out.println("Registered and logged in as " + username);
            runPostloginLoop();
        } catch (Exception ex) {
            System.out.println("[!] Registration failed: " + ex.getMessage());
        }
    }

    private void doLogin() {
        try {
            System.out.print("Username: ");
            String user = scanner.nextLine();
            System.out.print("Password: ");
            String pass = scanner.nextLine();

            var result = facade.login(user, pass);
            authToken = result.authToken();
            username = result.username();
            System.out.println("Logged in as " + username);
            runPostloginLoop();
        } catch (Exception ex) {
            System.out.println("[!] Login failed: " + ex.getMessage());
        }
    }

    private void doLogout() {
        try {
            facade.logout(authToken);
            System.out.println("Logged out.");
            authToken = null;
            username = null;
            gameIndexMap.clear();
        } catch (Exception ex) {
            System.out.println("[!] Logout failed: " + ex.getMessage());
        }
    }

    private void doCreateGame() {
        try {
            System.out.print("Game name: ");
            String name = scanner.nextLine();
            var result = facade.createGame(authToken, name);
            System.out.println("Created game with ID: " + result.gameID());
        } catch (Exception ex) {
            System.out.println("[!] Game creation failed: " + ex.getMessage());
        }
    }

    private void doListGames() {
        try {
            var games = facade.listGames(authToken);
            gameIndexMap.clear();
            int i = 1;
            for (var game : games) {
                System.out.printf("%d. \"%s\" - White: %s, Black: %s%n",
                        i, game.gameName(),
                        game.whiteUsername() != null ? game.whiteUsername() : "-",
                        game.blackUsername() != null ? game.blackUsername() : "-"
                );
                gameIndexMap.put(i, game);
                i++;
            }
        } catch (Exception ex) {
            System.out.println("[!] Could not list games: " + ex.getMessage());
        }
    }

    private void doJoinGame() {
        try {
            System.out.print("Enter game number: ");
            int num = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter color (WHITE or BLACK): ");
            String color = scanner.nextLine().toUpperCase();

            var game = gameIndexMap.get(num);
            if (game == null) {
                System.out.println("[!] Invalid game number.");
                return;
            }

            facade.joinGame(authToken, game.gameID(), color);
            System.out.println("Joined game " + game.gameName() + " as " + color);

            boolean isWhitePerspective = color.equalsIgnoreCase("WHITE");
            ui.BoardRenderer.render(game.game(), isWhitePerspective);

        } catch (Exception ex) {
            System.out.println("[!] Join failed: " + ex.getMessage());
        }
    }

    private void doObserveGame() {
        try {
            System.out.print("Enter game number to observe: ");
            int num = Integer.parseInt(scanner.nextLine());

            var game = gameIndexMap.get(num);
            if (game == null) {
                System.out.println("[!] Invalid game number.");
                return;
            }

            facade.observeGame(authToken, game.gameID());
            System.out.println("Observing game: " + game.gameName());

            String color = scanner.nextLine().toUpperCase();
            boolean isWhitePerspective = color.equalsIgnoreCase("WHITE");
            ui.BoardRenderer.render(game.game(), isWhitePerspective);
        } catch (Exception ex) {
            System.out.println("[!] Observe failed: " + ex.getMessage());
        }
    }
}
