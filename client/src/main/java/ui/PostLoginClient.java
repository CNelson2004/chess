package ui;

public class PostLoginClient {
    private final ServerFacade server;

    public PostLoginClient(int port) {
        server = new ServerFacade(port);
    }
}
