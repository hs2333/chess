package ui;

import chess.*;
import client.WebSocketFacade;
import model.GameData;
import websocket.messages.*;

import java.util.Collection;
import java.util.Scanner;

public class GameplayREPL implements WebSocketFacade.MessageHandler {

    private final Scanner scanner = new Scanner(System.in);
    private final WebSocketFacade ws;
    private ChessGame game;
    private final String authToken;
    private final int gameID;
    private final String username;
    private final ChessGame.TeamColor playerColor;

    public GameplayREPL(String authToken, int gameID, String username, ChessGame.TeamColor playerColor) throws Exception {
        this.authToken = authToken;
        this.gameID = gameID;
        this.username = username;
        this.playerColor = playerColor;
        this.ws = new WebSocketFacade(this);

        System.out.println("[Gameplay] Connecting to WebSocket...");
        System.out.println("[Gameplay] Using token: " + authToken);
        ws.connect("ws://localhost:8080/ws", authToken, gameID, username, playerColor);
    }

    public void run() {
        System.out.println("[Gameplay] Type 'help' for options.");
        while (true) {
            System.out.print("\n[Gameplay] > ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> printHelp();
                case "h" -> printHelp();
                case "move" -> {
                    if (playerColor == null) {
                        System.out.println("---- You are observing. Cannot make a move.");
                    } else {
                        doMove();
                    }
                } case "m" -> {
                    if (playerColor == null) {
                        System.out.println("---- You are observing. Cannot make a move.");
                    } else {
                        doMove();
                    }
                }
                case "resign" -> {
                    if (playerColor == null) {
                        System.out.println("---- Observers cannot resign.");
                    } else if (confirm("Are you sure you want to resign? (y/n): ")) {
                        ws.resign(authToken, gameID);
                        //ws.close(); //why would I want to close it so fast why whyw hywy whywhwywhyhwhy
                        return;
                    }
                }
                case "leave" -> {
                    ws.leave(authToken, gameID);
                    ws.close();
                    return;
                } case "l" -> {
                    ws.leave(authToken, gameID);
                    ws.close();
                    return;
                }
                case "redraw" -> {
                    boolean whitePerspective = playerColor == null || playerColor == ChessGame.TeamColor.WHITE;
                    BoardRenderer.render(game, whitePerspective);
                    System.out.println("Turn: " + game.getTeamTurn());
                }
                case "highlight" -> {
                    {
                        doHighlight();
                    }
                }
                default -> System.out.println("---- Unknown command.");
            }
        }
    }

    private void printHelp() {
        System.out.println("""
                Commands:
                - help [h]    Show this help menu
                - highlight   Highlight legal moves for one of your pieces
                
                - move [m]    Make a move
                - leave [l]   Leave the game and return to menu
                
                - resign      Resign from the game
                - redraw      Redraw the current board
                """);
    }

    private void doMove() {
        try {
            System.out.print("From (e.g. e2): ");
            String from = scanner.nextLine();
            System.out.print("To (e.g. e4): ");
            String to = scanner.nextLine();


            ChessPosition fromPos = parsePosition(from);
            ChessPosition toPos = parsePosition(to);

            //System.out.print("Promotion? (q/r/b/n or leave blank): ");
            String promo = scanner.nextLine().trim().toLowerCase();
            ChessPiece.PieceType promotion = switch (promo) {
                case "q" -> ChessPiece.PieceType.QUEEN;
                case "r" -> ChessPiece.PieceType.ROOK;
                case "b" -> ChessPiece.PieceType.BISHOP;
                case "n" -> ChessPiece.PieceType.KNIGHT;
                default -> null;
            };

            ChessMove move = new ChessMove(fromPos, toPos, promotion);

            //check valid pieces
            ChessPiece movingPiece = game.getBoard().getPiece(move.getStartPosition());

            if (movingPiece == null) {
                //this is... just easier... it's 6am in the morning, so let's just use the easy way
                System.out.println("[Error] No piece there.");
                return;
            }

            if (movingPiece.getTeamColor() != game.getTeamTurn()) {
                System.out.println("[Error] Not your piece.");
                return;
            }


            ws.makeMove(authToken, gameID, move);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private void doHighlight() {
        try {
            System.out.print("Enter position to highlight (e.g. e2): ");
            String input = scanner.nextLine();
            ChessPosition position = parsePosition(input);
            ChessPiece piece = game.getBoard().getPiece(position);

//            if (piece == null || piece.getTeamColor() != playerColor) {
//                System.out.println("---- No piece you control at that position.");
//                return;
//            }

            Collection<ChessMove> legalMoves = game.validMoves(position);
            BoardRenderer.renderWithHighlights(game, playerColor == ChessGame.TeamColor.WHITE, position, legalMoves);
        } catch (Exception e) {
            System.out.println("---- Failed to highlight: " + e.getMessage());
        }
    }

    private ChessPosition parsePosition(String input) {
        //check valid input
        if (input == null || input.length() != 2) {
            throw new IllegalArgumentException("[Error] Invalid input");
        }

        char file = input.charAt(0);
        char rank = input.charAt(1);
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            throw new IllegalArgumentException("[Error] Invalid input.");
        }

        int row = Character.getNumericValue(input.charAt(1));
        int col = input.charAt(0) - 'a' + 1;
        return new ChessPosition(row, col);
    }


    private boolean confirm(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    @Override
    public void handleMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                GameData data = ((LoadGameMessage) message).getGame();
                if (data != null && data.game() != null) {
                    this.game = data.game();
                    BoardRenderer.render(game, playerColor == ChessGame.TeamColor.WHITE);
                } else {
                    System.out.println("---- No game data received.");
                }

                System.out.println("[Server] Game loaded:");
                boolean whitePerspective = playerColor == null || playerColor == ChessGame.TeamColor.WHITE;
                //BoardRenderer.render(game, whitePerspective);
                System.out.println("Turn: " + game.getTeamTurn());
            }
            case NOTIFICATION -> {
                String note = ((NotificationMessage) message).getMessage();
                System.out.println("[Notification] " + note);
            }
            case ERROR -> {
                String err = ((ErrorMessage) message).getErrorMessage();
                System.out.println("[Error] " + err);
            }
        }
    }
}

