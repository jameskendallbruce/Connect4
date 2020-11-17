/**
 *
 * Connect4TextConsole
 *
 * Runs the text console interface for Connect4 gameplay.
 *
 * Completion time: 25 hours (combined)
 *
 * @author James Kendall Bruce
 *
 * @version 4.0
 *
 */
package ui;

import core.Connect4Constants;
import java.util.Scanner;

/**
 * Radically overhauled version of the Connect4TextConsole class. Now extends
 * the Connect4Constants class for easy access to String variables.
 *
 * @author James Kendall Bruce
 */
public class Connect4TextConsole extends Connect4Constants {

    /**
     * Gets the user's input selection for whether they will play against an
     * opponent or another player.
     *
     * @param scan Scanner instance to read the user's input
     * @return selection of either P (player) or C (computer)
     */
    public char getGameMode(Scanner scan) {
        String mode = scan.nextLine();
        if (mode.length() == 0) {
            return 0;
        }
        return mode.charAt(0);
    }

    /**
     * Gets the user's input selection for whether they would like a text
     * console interface or GUI for the display format.
     *
     * @param scan Scanner instance to read the user's input
     * @return selection of either T (text console) or G (GUI)
     */
    public char getDisplayFormat(Scanner scan) {
        String format = scan.nextLine();
        if (format.length() == 0) {
            return 0;
        }
        return format.charAt(0);
    }

    /**
     * Reads an input from the user for which column they would like to play
     * their token in.
     *
     * @param scan Scanner instance to read the user's input
     * @return the column selection -- a number between 1 and 7
     */
    public int getMove(Scanner scan) {
        int move;
        try {
            move = scan.nextInt();
        } catch (Exception e) {
            scan.nextLine();
            return 0;
        }
        return move;
    }

    /**
     * Prints a string to request the user to select a game mode.
     *
     * @return string result of the request
     */
    public String displayGetMode() {
        String res = MODE_REQUEST_TEXT;
        System.out.println(res);
        return res;
    }

    /**
     * Prints a string to request the user to select a display format.
     *
     * @return string result of the request
     */
    public String displayGetFormat() {
        String res = DISPLAY_REQUEST_TEXT;
        System.out.println(res);
        return res;
    }

    /**
     * Prints a string to let the user know that they have not made a proper
     * display format seletion.
     *
     * @return string result explaining the user error
     */
    public String displayWrongFormat() {
        String res = INVALID_DISPLAY_TEXT;
        System.out.println(res);
        return res;
    }

    /**
     * Prints a string to let the user know that they have not made a proper
     * game mode seletion.
     *
     * @return string result explaining the user error
     */
    public String displayWrongMode() {
        String res = INVALID_DISPLAY_TEXT;
        System.out.println(res);
        return res;
    }

    /**
     * Updates to show the last move played,
     *
     * @param move the column selected for the play
     * @return string representation of the last move
     */
    public String displayMove(int move) {
        String res = "" + move;
        System.out.println(res);
        return res;
    }

    /**
     * Method for printing any message that does not explicitly belong in
     * another method already.
     *
     * @param msg the message to be printed
     */
    public void displayMessage(String msg) {
        System.out.println(msg);
    }

    /**
     * Checks which game mode that the user ahs selected and sends the user a
     * confimration of teh selection.
     *
     * @param selection the players selection of game mode
     * @return String message explaining what game is launching
     * @throws IllegalArgumentException user did not select an valid opponent.
     * Notifies the player if they need to make another selection
     */
    public String displayStart(char selection) throws IllegalArgumentException {
        if (selection == COMPUTER) {
            String res = CPU_GAME_TEXT;
            System.out.println(res);
            return res;
        } else if (selection == PLAYER) {
            String res = PVP_GAME_TEXT;
            System.out.println(res);
            return res;
        } else {
            throw new IllegalArgumentException("Select either [P] or [C]");
        }
    }

    /**
     * A method to convert the gameBoard 2D array into a String for the text
     * console.
     *
     * @param gameBoard the 2d array representing the columsn and rows
     * @return the completed String with column borders
     */
    public String displayBoard(char[][] gameBoard) {
        StringBuilder res = new StringBuilder();
        for (int i = 5; i >= 0; i--) {
            res.append('|');
            for (int j = 0; j <= 6; j++) {
                res.append(gameBoard[i][j]);
                res.append('|');
            }
            res.append('\n');
        }

        System.out.println(res.toString());
        return res.toString();
    }

    /**
     * Tells the currentPlayer if it is their turn.
     *
     * @param player the current turn player
     * @return String message to the player
     */
    public String displayPlayerTurn(char player) {
        String res;
        if (player == P1) {
            res = P1_YOUR_TURN_TEXT;
        } else {
            res = P2_YOUR_TURN_TEXT;
        }
        System.out.println(res);
        return res.toString();
    }

    /**
     * Notifies the player if the move was out of bounds
     *
     * @return String to communicate the user error
     */
    public String invalidMove() {
        String res = INVALID_MOVE_TEXT;
        System.out.println(res);
        return res;
    }

    /**
     * Notifies the player if the game has ended and specifies who won.
     *
     * @param outcome 1,2 or 3 for P1 win, P2 win or a tie game
     * @return String of the winning message (or tie game message)
     */
    public String displayWinner(int outcome) {
        String winner;

        if (outcome == 1) {
            winner = P1_WINS_TEXT;
        } else if (outcome == 2) {
            winner = P2_WINS_TEXT;
        } else {
            winner = TIE_GAME_TEXT;
        }
        System.out.println(winner);
        return winner.toString();
    }

}
