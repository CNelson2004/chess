package ui;

public interface EvalClient {
    String eval(String input) throws ResponseException;
    String help();
}
