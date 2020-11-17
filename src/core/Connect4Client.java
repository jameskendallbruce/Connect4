/**
 *
 * Connect4Client
 *
 * Runs the client side of the Connect4 game.
 * Part of the radical overhaul to better implement GUI functionality and include
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

import java.net.Socket;
import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import javafx.application.Application;
import javafx.application.Platform;
import ui.Connect4GUI;
import ui.Connect4TextConsole;

/**
 * Communicates with the player to send and receieve I/O updates.
 *
 * @author James Kendall Bruce
 */
public class Connect4Client extends Connect4Constants {

    /**
     * All the I/O and Sockets between the Client, Server and GUI.
     */
    private int port = 8000;

    private ObjectOutputStream output;

    private ObjectOutputStream outputGUI;

    private ObjectInputStream input;

    private ObjectInputStream inputGUI;

    private Socket socket;

    private Socket socketGUI;

    private ServerSocket serverGUI;

    /**
     * Runs tbe user's gameplay experience by connection with the server.
     *
     * @param args default class args -- not used
     */
    public static void main(String[] args) {
        /**
         * Instance variable of the Connect4Client class.
         */
        Connect4Client client = new Connect4Client();

        /**
         * Instance variable of the Connect4TextConsole class.
         */
        Connect4TextConsole display = new Connect4TextConsole();

        /**
         * Scanner instance to retrieve the user's input
         */
        Scanner scan = new Scanner(System.in);

        /**
         * Holds the user's selected interface format.
         */
        char displayFormat = 0;

        /**
         * Retrieves the player's selected display format.
         */
        display.displayGetFormat();
        while (!(displayFormat == GUI || displayFormat == TEXT_CONSOLE)) {
            /**
             * Gets the display format from the user.
             */
            displayFormat = display.getDisplayFormat(scan);
            if (!(displayFormat == GUI || displayFormat == TEXT_CONSOLE)) {
                display.displayWrongFormat();
            }
        }
        display.displayGetMode();
        /**
         * Initializes the game mode as undecided.
         */
        char mode = 0;
        /**
         * Gets the game mode from the user.
         */
        while (!(mode == COMPUTER || mode == PLAYER)) {
            mode = display.getGameMode(scan);
            if (!(mode == COMPUTER || mode == PLAYER)) {
                display.displayWrongMode();
            }
        }

        /**
         * Establishes Socket, input and output instances.
         */
        try {
            client.socket = new Socket("localhost", client.port);
            client.output = new ObjectOutputStream(client.socket.getOutputStream());
            client.input = new ObjectInputStream(client.socket.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        /**
         * Starts on P1's turn.
         */
        char currentPlayer = P1;
        Object obj = null;
        int portGUI = 0;
        try {
            client.output.writeObject(mode);
            currentPlayer = (char) client.input.readObject();
            portGUI = (int) client.input.readObject();

        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        /**
         * Establishes the board for gameplay.
         */
        char[][] gameBoard = new char[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                gameBoard[r][c] = ' ';
            }
        }

        String playerString = Character.toString(currentPlayer);
        String portGUIString = Integer.toString(portGUI);
        Runnable runnable = () -> {
            try {
                Application.launch(Connect4GUI.class, portGUIString,
                        playerString);
                Platform.setImplicitExit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
        Thread thread = null;
        /**
         * Runs the GUI side of things.
         */
        if (displayFormat == GUI) {
            thread = new Thread(runnable);
            thread.start();
            try {
                client.serverGUI = new ServerSocket(portGUI);
                client.socketGUI = client.serverGUI.accept();
                client.outputGUI = new ObjectOutputStream(
                        client.socketGUI.getOutputStream());
                client.inputGUI = new ObjectInputStream(
                        client.socketGUI.getInputStream());
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
        if (displayFormat == TEXT_CONSOLE) {
            display.displayBoard(gameBoard);
            display.displayStart(mode);
        }
        String text = null;
        while (true) {
            try {
                obj = client.input.readObject();
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }

            /**
             * Series of if/else blocks to check for possible gameplay
             * scenarios, wins, quits, invalid moves and eventually player
             * turns.
             */
            if (obj instanceof String) {
                text = (String) obj;
                if (text.equals(P1_QUIT)) {
                    try {
                        client.outputGUI.writeObject(P1_QUIT);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                } else if (text.equals(P2_QUIT)) {
                    try {
                        client.outputGUI.writeObject(P2_QUIT);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                } else if (text.equals(P1_WINS) || text.equals(P2_WINS)
                        || text.equals(TIE)) {
                    if (displayFormat == TEXT_CONSOLE) {
                        if (text.equals(P1_WINS)) {
                            display.displayWinner(P1_WINNER);
                        } else if (text.equals(P2_WINS)) {
                            display.displayWinner(P2_WINNER);
                        } else {
                            display.displayWinner(TIE_GAME); // Tie game
                        }
                    } else {
                        try {
                            client.outputGUI.writeObject(text);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            break;
                        }
                    }
                    break;
                } else if (mode == COMPUTER && (text.equals(P2_TURN)
                        || text.equals(P2_INVALID))) {
                    if (displayFormat == TEXT_CONSOLE) {
                        if (text.equals(P2_INVALID)) {
                            display.invalidMove();
                        }
                        display.displayPlayerTurn(P2);
                    } else {
                        try {
                            client.outputGUI.writeObject(text);
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                    continue;
                } else if ((text.equals(P1_TURN)
                        || text.equals(P1_INVALID))
                        && currentPlayer == P1) {
                    if (displayFormat == TEXT_CONSOLE) {
                        try {
                            if (text.equals(P1_INVALID)) {
                                display.invalidMove();
                            }
                            display.displayPlayerTurn(P1);
                            client.output.writeObject(display.getMove(scan));
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    } else {
                        try {
                            client.outputGUI.writeObject(text);
                            Object inData = client.inputGUI.readObject();
                            if (inData instanceof String) {
                                client.output.writeObject(inData);
                                client.outputGUI.writeObject(inData);
                                break;
                            }
                            client.output.writeObject(inData);
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                } else if ((text.equals(P2_TURN)
                        || text.equals(P2_INVALID))
                        && currentPlayer == P2) {
                    if (displayFormat == TEXT_CONSOLE) {
                        try {
                            if (text.equals(P2_INVALID)) {
                                display.invalidMove();
                            }
                            display.displayPlayerTurn(P2);
                            client.output.writeObject(display.getMove(scan));
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    } else {
                        try {
                            client.outputGUI.writeObject(text);
                            Object inData = client.inputGUI.readObject();
                            if (inData instanceof String) {
                                client.output.writeObject(inData);
                                client.outputGUI.writeObject(inData);
                                break;
                            }
                            client.output.writeObject(inData);
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                } else {
                    if (displayFormat == TEXT_CONSOLE) {
                        if (currentPlayer != P1) {
                            display.displayMessage(WAIT_FOR_P1_TEXT);
                        } else {
                            display.displayMessage(WAIT_FOR_P2_TEXT);
                        }
                    } else {
                        try {
                            if (currentPlayer != P1) {
                                client.outputGUI.writeObject(P1_TURN);
                            } else {
                                client.outputGUI.writeObject(P2_TURN);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            } else if (obj instanceof char[][]) {
                gameBoard = (char[][]) obj;
                if (displayFormat == TEXT_CONSOLE) {
                    display.displayBoard(gameBoard);
                } else {
                    try {
                        client.outputGUI.writeObject(gameBoard);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            } else {
                System.out.println("Not a String.");
            }
        }
        try {
            if (displayFormat == GUI) {
                client.outputGUI.close();
                client.inputGUI.close();
            }
            client.output.close();
            client.input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
