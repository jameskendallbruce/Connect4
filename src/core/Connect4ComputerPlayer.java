/**
 *
 * Connect4ComputerPlayer
 *
 * Runs the calculations for the computer player's logic.
 *
 * Completion time: 25 hours (combined)
 *
 * @author James Kendall Bruce
 *
 * @version 4.0
 *
 */
package core;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Basic Computer Player class with no real AI.
 *
 * @author James Kendall Bruce
 */
public class Connect4ComputerPlayer {

    /**
     * Makes calculations for the computer player's logic.
     *
     * @param gameBoard The gameBoard gets sent to the computer player for
     * gameplay.
     * @return a value between 1 and 7 to decide which column the computer will
     * drop the token into
     */
    public int getMove(char[][] gameBoard) {
        int move = ThreadLocalRandom.current().nextInt(1, 8);
        return move;
    }
}
