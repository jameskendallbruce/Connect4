/**
 *
 * Connect4
 *
 * Runs the logic for a JavaFX connect4 game.
 * Radical overhaul to better implement GUI functionality and include
 * server/client functionality.
 *
 * Completion time: 25 hours (combined)
 *
 * @author James Kendall Bruce
 *
 * @version 4.0
 *
 */
package core;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Radically reworked version of my previous Connect4 class. Now extends a
 * custom Connect4Constants class to more easily share universal constants
 * between classes.
 *
 * @author James Kendall Bruce
 */
public class Connect4 extends Connect4Constants {

    /**
     * 2D array to match the dimensions of the board.
     */
    private char[][] gameBoard;

    /**
     * Array holding the current height of each column in the gameBoard array.
     */
    private int[] currentHeight;

    /**
     * Contains the total number of players (either 1 or 2).
     */
    private int totalPlayers;

    /**
     * Input and output variables for each player.
     */
    private ObjectInputStream inputP1;
    private ObjectOutputStream outputP1;
    private ObjectInputStream inputP2;
    private ObjectOutputStream outputP2;

    /**
     * Basic constructor method that creates a gameboard and establishes
     * connections based off of the players involved.
     *
     * @param s1 Socket instance for P1 thread
     * @param s2 Socket instance for P2 thread
     * @param out1 output for P1
     * @param out2 output for P2
     * @param in1 input from P1
     * @param in2 input from P2
     * @param num number of playes in this game
     */
    public Connect4(Socket s1, Socket s2, ObjectOutputStream out1, ObjectOutputStream out2,
            ObjectInputStream in1, ObjectInputStream in2, int num) {
        inputP1 = in1;
        inputP2 = in2;
        outputP1 = out1;
        outputP2 = out2;
        totalPlayers = num;
        gameBoard = new char[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                gameBoard[r][c] = ' ';
            }
        }
        currentHeight = new int[COLUMNS];
    }

    /**
     * Replacement method for previous playToken variants. Streamlined to just
     * the column placement. Other classes and methods will handle the input
     * retrieval (or calculation in the case of a computer player).
     *
     * @param column Which column did the player select
     * @param icon Which token is being dropped
     */
    private void playToken(int column, char icon) {
        currentHeight[column - 1] += 1;
        gameBoard[currentHeight[column - 1] - 1][column - 1] = icon;
    }

    /**
     * Method to confirm that the column is not already full.
     *
     * @param column Which column did the player select
     * @return The row that the token will be placed in (or 0 if it is full)
     */
    public int checkMove(int column) {
        column -= 1;
        if (column < 0 || column >= COLUMNS) {
            return 0;
        }
        if (currentHeight[column] >= ROWS) {
            return 0;
        }
        return currentHeight[column] + 1;
    }

    /**
     * Method called after every token drop to check if the last to play has
     * triggered a winning state for the current player. Checks how long the
     * streak (int total) goes in each direction and declares a winner if
     * appropriate. If the board is entirely filled, a Tie Game will be declared
     * instead.
     *
     * @param row Y coordinate of the dropped Token
     * @param column X coordinate of the dropped Token
     * @param turn
     * @return Winning State 0-3: 0 if still ongoing, 1 if P1, 2 if P2 and 3 if
     * a tie game.
     */
    private int checkWin(int row, int column, char icon) {
        row -= 1;
        column -= 1;
        int c = column - 1;
        int total = 1;
        while (c >= 0) {
            if (gameBoard[row][c] == icon) {
                total += 1;
                c -= 1;
            } else {
                break;
            }
        }
        c = column + 1;
        while (c < COLUMNS) {
            if (gameBoard[row][c] == icon) {
                total += 1;
                c += 1;
            } else {
                break;
            }
        }
        if (total >= 4) {
            if (icon == P1) {
                return P1_WINNER;
            } else {
                return P2_WINNER;
            }
        }
        int r = row + 1;
        c = column - 1;
        total = 1;
        while (c >= 0 && r < ROWS) {
            if (gameBoard[r][c] == icon) {
                total += 1;
                c -= 1;
                r += 1;
            } else {
                break;
            }
        }
        c = column + 1;
        r = row - 1;
        while (c < COLUMNS && r >= 0) {
            if (gameBoard[r][c] == icon) {
                total += 1;
                c += 1;
                r -= 1;
            } else {
                break;
            }
        }
        if (total >= 4) {
            if (icon == P1) {
                return P1_WINNER;
            } else {
                return P2_WINNER;
            }
        }
        r = row - 1;
        total = 1;
        while (r >= 0) {
            if (gameBoard[r][column] == icon) {
                total += 1;
                r -= 1;
            } else {
                break;
            }
        }
        r = row + 1;
        while (r < ROWS) {
            if (gameBoard[r][column] == icon) {
                total += 1;
                r += 1;
            } else {
                break;
            }
        }
        if (total >= 4) {
            if (icon == P1) {
                return P1_WINNER;
            } else {
                return P2_WINNER;
            }
        }
        r = row - 1;
        c = column - 1;
        total = 1;
        while (c >= 0 && r >= 0) {
            if (gameBoard[r][c] == icon) {
                total += 1;
                c -= 1;
                r -= 1;
            } else {
                break;
            }
        }
        c = column + 1;
        r = row + 1;
        while (c < COLUMNS && r < ROWS) {
            if (gameBoard[r][c] == icon) {
                total += 1;
                c += 1;
                r += 1;
            } else {
                break;
            }
        }
        if (total >= 4) {
            if (icon == P1) {
                return P1_WINNER;
            } else {
                return P2_WINNER;
            }
        }

        for (c = 0; c < COLUMNS; c++) {
            if (currentHeight[c] < ROWS) {
                return ONGOING;
            }
        }
        return TIE_GAME;
    }

    /**
     * Method to launch and run the game. Orchestrates turn-taking, token
     * playing and win checking.
     */
    public void launchGame() {
        Connect4ComputerPlayer computer = new Connect4ComputerPlayer();

        /**
         * Stores the game mode in play.
         */
        char mode = COMPUTER;
        if (totalPlayers == 2) {
            mode = PLAYER;
        }

        /**
         * Player 1 goes first. Defaults to the X tokem icon.
         */
        char icon = P1;

        /**
         * Holds the current column choice that the token was dropped into.
         */
        int move;

        /**
         * In the current 0 state, this affirms that the game is ongoing. Other
         * possible values: 1 - P1 Wins, 2 - P2 Wins, 3 - Tie Game.
         */
        int outcome = 0;

        /**
         * The default height that a token falls to. Can only have values from 1
         * to 6. Any higher and the token must be played in a different column.
         * (Row 0 and below do not exist)
         */
        int valid = 1;

        /**
         * While the outcome state is still 'ONGOING, while loop will continue
         * to run this main gameplay loop.
         */
        while (outcome == ONGOING) {
            try {
                /**
                 * Checks whose turn it is and communicates it to both players.
                 * Also, communicates if a move was not in bounds.
                 */
                if (icon == P1 && valid != 0) {
                    outputP1.writeObject(P1_TURN);
                    if (totalPlayers == 2) {
                        outputP2.writeObject(P1_TURN);
                    }
                } else if (icon == P1 && valid == 0) {
                    outputP1.writeObject(P1_INVALID);
                    if (totalPlayers == 2) {
                        outputP2.writeObject(P1_INVALID);
                    }
                } else if (icon == P2 && valid != 0) {
                    outputP1.writeObject(P2_TURN);
                    if (totalPlayers == 2) {
                        outputP2.writeObject(P2_TURN);
                    }
                } else if (icon == P1 && valid == 0) {
                    outputP1.writeObject(P2_INVALID);
                    if (totalPlayers == 2) {
                        outputP2.writeObject(P2_INVALID);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            /**
             * Requests the column choice from the current player or calculates
             * it if is the computer's turn. Also checks to see if either player
             * has quit which is a possible exit scenario.
             */
            if (mode == COMPUTER && icon == P2) {
                move = computer.getMove(gameBoard);
            } else {
                Object obj;
                try {
                    if (icon == P1) {
                        obj = inputP1.readObject();
                        if (obj instanceof String) {
                            if (totalPlayers == 2) {
                                outputP2.writeObject(P1_QUIT);
                            }
                            break;
                        } else {
                            move = (int) obj;
                        }
                    } else {
                        obj = inputP2.readObject();
                        if (obj instanceof String) {
                            outputP1.writeObject(P2_QUIT);
                            break;
                        } else {
                            move = (int) obj;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            /**
             * Confirms that the column selected still has room.
             */
            valid = checkMove(move);
            if (valid == 0) {
                continue;
            }

            /**
             * Drops the token into the chosen column.
             */
            playToken(move, icon);
            try {
                outputP1.reset();
                outputP1.writeObject(gameBoard);
                if (totalPlayers == 2) {
                    outputP2.reset();
                    outputP2.writeObject(gameBoard);
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            /**
             * Updates the outcome based on the result of the checkWin method.
             * Most liekly scenario is that the game is still onging.
             */
            outcome = checkWin(valid, move, icon);
            if (icon == P1) {
                icon = P2;
            } else {
                icon = P1;
            }
        }

        /**
         * Checks to see if outcome was changed after the checkWin method call.
         * If so, the game has ended with 1 of 3 outcomes: P1_WINNER, P2_WINNER
         * or TIE_GAME.
         */
        try {
            if (outcome == P1_WINNER) {
                outputP1.writeObject(P1_WINS);
                if (totalPlayers == 2) {
                    outputP2.writeObject(P1_WINS);
                }
            } else if (outcome == P2_WINNER) {
                outputP1.writeObject(P2_WINS);
                if (totalPlayers == 2) {
                    outputP2.writeObject(P2_WINS);
                }
            } else {
                outputP1.writeObject(TIE);
                if (totalPlayers == 2) {
                    outputP2.writeObject(TIE);
                }
            }
            outputP1.close();
            inputP1.close();
            if (totalPlayers == 2) {
                outputP2.close();
                inputP2.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
