/**
 *
 * Connect4Constants
 *
 * Holds the universal constant variables for Connect4 gameplay and player
 * updates (including TextConsole gameplay, GUI gameplay and messages to the client's terminal.
 *
 * Completion time: 25 hours (combined)
 *
 * @author James Kendall Bruce
 *
 * @version 3.0
 *
 */
package core;

public class Connect4Constants {

    /**
     * Universal int constants for gameplay.
     */
    public static final int COLUMNS = 7;
    public static final int ROWS = 6;
    public static final int ONGOING = 0;
    public static final int P1_WINNER = 1;
    public static final int P2_WINNER = 2;
    public static final int TIE_GAME = 3;

    /**
     * Text console mode selection constants.
     */
    public static final char GUI = 'G';
    public static final char TEXT_CONSOLE = 'T';
    public static final char COMPUTER = 'C';
    public static final char PLAYER = 'P';

    /**
     * Player constants for text console gameplay. Also for player token icon.
     */
    public static final char P1 = 'X';
    public static final char P2 = 'O';

    /**
     * String constants for console updates.
     */
    public static final String P1_TURN = "Red's turn.";
    public static final String P2_TURN = "Blue's turn.";
    public static final String P1_INVALID = "Red invalid move.";
    public static final String P2_INVALID = "Blue invalid move.";
    public static final String P1_QUIT = "Player1 has left the game!";
    public static final String P2_QUIT = "Player2 has left the game!";
    public static final String WAITING = "Waiting for opponent to join...";
    public static final String P1_WINS = "Red wins!";
    public static final String P2_WINS = "Blue wins!";
    public static final String TIE = "Out of moves! \nTie Game!";

    /**
     * String constants for GUI gameplay communication.
     */
    public static final String WINDOW_TITLE_GUI = "CONNECT 4";
    public static final String P1_YOUR_TURN_GUI = "Your turn. "
            + "\nPick a column to drop a red token into it.";
    public static final String P2_YOUR_TURN_GUI = "Your turn. "
            + "\nPick a column to drop a blue token into it.";
    public static final String WAITING_FOR_P1_GUI = "Waiting for Red's move";
    public static final String WAITING_FOR_P2_GUI = "Waiting for Blue's move";
    public static final String P1_WINS_GUI = "You win, Red!";
    public static final String P2_WINS_GUI = "You win, Blue!";
    public static final String TIE_GAME_GUI = "Out of moves! \nTie Game!";
    public static final String OPPONENT_P1_WINS_GUI = "Red wins!";
    public static final String OPPONENT_P2_WINS_GUI = "Blue wins!";

    /**
     * String constants for console gameplay communication.
     */
    public static final String MODE_REQUEST_TEXT = "Select opponent: "
            + "\n[P] Player v Player \n[C] Player v Computer\n";
    public static final String DISPLAY_REQUEST_TEXT = "Select Interface: "
            + "\n[G] GUI Display \n[T] Text Console\n";
    public static final String INVALID_MODE_TEXT = "Enter 'G' for a GUI display."
            + "\nEnter 'T' for a text console display.";
    public static final String INVALID_DISPLAY_TEXT = "Enter 'P' to play against"
            + " another player. \nEnter 'C' to play a computer opponent.";
    public static final String CPU_GAME_TEXT = "Opponent selected: "
            + "\nLaunching game against computer.";
    public static final String PVP_GAME_TEXT = "Opponent selected: "
            + "\nLaunching game against player."
            + "\nWaiting for another player.";
    public static final String P1_YOUR_TURN_TEXT = "Player X, your turn. "
            + "\nPick a column between 1 and 7 to drop an X token into it.";
    public static final String P2_YOUR_TURN_TEXT = "Player O, your turn. "
            + "\nPick a column between 1 and 7 to drop an O token into it.";
    public static final String INVALID_MOVE_TEXT = "Invalid move."
            + "Choose a number between 1 and 7.";
    public static final String WAIT_FOR_P1_TEXT = "Waiting for Player X's move";
    public static final String WAIT_FOR_P2_TEXT = "Waiting for Player O's move";
    public static final String P1_WINS_TEXT = "Player X wins!";
    public static final String P2_WINS_TEXT = "Player O wins!";
    public static final String TIE_GAME_TEXT = "Out of moves! \nTie Game!";

}
