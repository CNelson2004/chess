package Results;

import model.GameData;

import java.util.Collection;

public record ListResult(Collection<GameData> allGames, String message){}
