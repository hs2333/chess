package websocket.commands;

public class ResignCommand extends UserGameCommand {
    public ResignCommand(String authToken, Integer gameID) {
        super(CommandType.RESIGN, authToken, gameID);
    }

    public Integer getGameID() {
        return this.getGameIDFromBase(); // You can expose this in UserGameCommand or just call super.getGameID() if public
    }
}
