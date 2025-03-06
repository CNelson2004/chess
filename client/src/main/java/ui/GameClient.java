package ui;

public class GameClient {
    private final ServerFacade server;

    public GameClient(int port) {
        server = new ServerFacade(port);
    }
}
