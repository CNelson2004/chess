package ui;
//Notes for ServerFacade class

//Pre-login UI
//Help: Displays text of what actions the user can do
//Help format idea: (This all takes place in terminal)
// register <USERNAME> <PASSWORD> <EMAIL> - to create an account
// login <USERNAME> <PASSWORD> - to play chess
// quit - to stop playing chess
// help - to show all possible commands
//Quit: Exits the program
//Login: Prompts for login information
//       Calls server login API
//       When successful, transition to Postlogin UI
//Register: Prompts for registration info
//          Calls register API
//          If successful user transition to Postlogin UI

//Post-login UI
//Help: ditto
//Help example:
//create <NAME> - a game
//list - games
//join <ID> [WHITE|BLACK] - a game
//observe <ID> - a game
//logout - when you are done
//quit - to exit
//help - to show posisble commands
//Logout: Logs out user
//        Calls logout API
//        Transistions to Prelogin UI
//Create Game: User inputs a name for a new game
//             Calls create API (Does not join the user)
//List Games: Lists all currently existing games in server
//            Calls list API
//            Displays games in a numbered list, including game name and players
//            Numbering is independent of game IDs and starts at 1
//Play Game: User specifies which game they want to join and as what color
//           They should be able to enter the number of the desired game
//           Keep track of which number corresponds to which game from the last time it listed games
//           Calls join API
//Observe Game: Allows user to specify which game they want to observe
//              User inputs number of desired game
//              (Funcitonality added in phase 6)

//Notes:
//When a user plays/observes a game, the client should draw the initial state
//of a Chess game in the terminal.
//It should draw it from the white perspective, then the black perspective
//It will be drawn from hte perspective of black or white depending on player color.
//Observer joins in the perspective of white player
// Gameplay mode is not entered

//Do not display the Game ID
//Don't stack trace, have a simple error message informing hte user.
//Don't crash. If an exception occurs, cathc it.
//Exceptions to catch:
//To many/too few arguments
//Wrong types of arguments (Ex: word instead of number, arguments in wrong order)
//And incorrect arguments (Loging in with incorrect username/password)



/**
 * This class contains constants and functions relating to ANSI Escape Sequences that are useful in the Client display
 */
public class EscapeSequences {

    private static final String UNICODE_ESCAPE = "\u001b";
    private static final String ANSI_ESCAPE = "\033";

    public static final String ERASE_SCREEN = UNICODE_ESCAPE + "[H" + UNICODE_ESCAPE + "[2J";
    public static final String ERASE_LINE = UNICODE_ESCAPE + "[2K";

    public static final String SET_TEXT_BOLD = UNICODE_ESCAPE + "[1m";
    public static final String SET_TEXT_FAINT = UNICODE_ESCAPE + "[2m";
    public static final String RESET_TEXT_BOLD_FAINT = UNICODE_ESCAPE + "[22m";
    public static final String SET_TEXT_ITALIC = UNICODE_ESCAPE + "[3m";
    public static final String RESET_TEXT_ITALIC = UNICODE_ESCAPE + "[23m";
    public static final String SET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[4m";
    public static final String RESET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[24m";
    public static final String SET_TEXT_BLINKING = UNICODE_ESCAPE + "[5m";
    public static final String RESET_TEXT_BLINKING = UNICODE_ESCAPE + "[25m";

    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    private static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";

    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_TEXT_COLOR_LIGHT_GREY = SET_TEXT_COLOR + "242m";
    public static final String SET_TEXT_COLOR_DARK_GREY = SET_TEXT_COLOR + "235m";
    public static final String SET_TEXT_COLOR_RED = SET_TEXT_COLOR + "160m";
    public static final String SET_TEXT_COLOR_GREEN = SET_TEXT_COLOR + "46m";
    public static final String SET_TEXT_COLOR_YELLOW = SET_TEXT_COLOR + "226m";
    public static final String SET_TEXT_COLOR_BLUE = SET_TEXT_COLOR + "12m";
    public static final String SET_TEXT_COLOR_MAGENTA = SET_TEXT_COLOR + "5m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String RESET_TEXT_COLOR = UNICODE_ESCAPE + "[39m";

    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_BG_COLOR_LIGHT_GREY = SET_BG_COLOR + "242m";
    public static final String SET_BG_COLOR_DARK_GREY = SET_BG_COLOR + "235m";
    public static final String SET_BG_COLOR_RED = SET_BG_COLOR + "160m";
    public static final String SET_BG_COLOR_GREEN = SET_BG_COLOR + "46m";
    public static final String SET_BG_COLOR_DARK_GREEN = SET_BG_COLOR + "22m";
    public static final String SET_BG_COLOR_YELLOW = SET_BG_COLOR + "226m";
    public static final String SET_BG_COLOR_BLUE = SET_BG_COLOR + "12m";
    public static final String SET_BG_COLOR_MAGENTA = SET_BG_COLOR + "5m";
    public static final String SET_BG_COLOR_WHITE = SET_BG_COLOR + "15m";
    public static final String RESET_BG_COLOR = UNICODE_ESCAPE + "[49m";

    public static final String WHITE_KING = " ♔ ";
    public static final String WHITE_QUEEN = " ♕ ";
    public static final String WHITE_BISHOP = " ♗ ";
    public static final String WHITE_KNIGHT = " ♘ ";
    public static final String WHITE_ROOK = " ♖ ";
    public static final String WHITE_PAWN = " ♙ ";
    public static final String BLACK_KING = " ♚ ";
    public static final String BLACK_QUEEN = " ♛ ";
    public static final String BLACK_BISHOP = " ♝ ";
    public static final String BLACK_KNIGHT = " ♞ ";
    public static final String BLACK_ROOK = " ♜ ";
    public static final String BLACK_PAWN = " ♟ ";
    public static final String EMPTY = " \u2003 ";

    public static String moveCursorToLocation(int x, int y) { return UNICODE_ESCAPE + "[" + y + ";" + x + "H"; }
}
