/**
 *
 * Connect4Server
 *
 * Server end of the Connect4 game.
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Simple server for the client to connect to. Runs much of the connections for
 * the logic of the Connect4 game.
 *
 * @author James Kendall Bruce
 */
public class Connect4Server extends Connect4Constants {

    /**
     * Whether the game is currently active
     */
    public boolean active;

    /**
     * Socket instance for connection to client
     */
    public ServerSocket serverSocket;

    /**
     * Basic constructor. Turns the state of the Server instance to "on" by
     * default--Cannot be "true" if it doesn't exist.
     */
    public Connect4Server() {
        this.active = true;
    }

    /**
     * Custom thread subclass to run Connct4 gameplay.
     */
    private static class Game implements Runnable {

        /**
         * Instance variable of the Connect4 class. Initiates to null before
         * gaemplay starts.
         */
        private Connect4 game = null;

        /**
         * Sets the thread's run method to also launch gameplay in the Connect4
         * class.
         */
        @Override
        public void run() {
            game.launchGame();
        }

        /**
         * Constructor method to set Game.game to a specific Connect4 instance.
         *
         * @param game The instance that you want to set it to.
         */
        public Game(Connect4 game) {
            this.game = game;
        }
    }

    /**
     * Thread subclass to listen to input retrieved from the keyboard.
     */
    private static class KeyListener implements Runnable {

        /**
         * Establishes a server instance.
         */
        private Connect4Server server;

        /**
         * Constructor method to create a KeyListener
         *
         * @param server instance to set the server variable to
         */
        public KeyListener(Connect4Server server) {
            this.server = server;
        }

        /**
         * Run method for the class. Checks for a handful of Scanner scenarios
         * for when to close the Scanner and keeps reading inputs otherwise.
         */
        @Override
        public void run() {

            /**
             * Scanner instance to read the inputs
             */
            Scanner scan = new Scanner(System.in);
            while (server.active) {
                String text;
                try {
                    text = scan.nextLine();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                if (!text.equals("q")) {
                    continue;
                }
                server.active = false;
                try {
                    System.out.println("Closing Server");
                    server.serverSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /**
             * Makes sure that we close out the Scanner instance.
             */
            scan.close();
        }
    }

    /**
     * Main method for the Connect4Server class. Launches the server and runs
     * matchmaking for the player(s).
     *
     * @param args default class args -- not used
     */
    public static void main(String[] args) {
        /**
         * Initializes ports, Sockets, inputs and outputs.
         */
        int portServer = 8000;
        int portGUI = portServer + 1;
        int player = 1;
        Socket p1 = null;
        Socket p2;
        ObjectOutputStream output1 = null;
        ObjectOutputStream output2;
        ObjectInputStream input1 = null;
        ObjectInputStream input2;
        Connect4 game;

        /**
         * Thread for gameplay.
         */
        Thread threadGame;

        /**
         * Establishes a defautl server (calls the defautl constructor).
         */
        Connect4Server server = new Connect4Server();

        /**
         * Initiates an instance of the KeyListener class listening on the
         * server instance at the established port.
         */
        Thread key = new Thread(new KeyListener(server));
        key.start();
        char mode;

        /**
         * Launches the server.
         */
        System.out.println("Launching Server");
        try {
            server.serverSocket = new ServerSocket(portServer);
        } catch (Exception ex) {
            ex.printStackTrace();
            server.active = false;
        }

        /**
         * Waits for clients to establish connections for the users. Sets up
         * appropriate I/O connections between all sockets, clients and the
         * server. Checks for computer opponent.
         */
        while (server.active) {
            if (player == 1) {
                System.out.println("Searching for Players");
                try {
                    p1 = server.serverSocket.accept();
                } catch (Exception e) {
                    if (server.active == false) {
                        break;
                    } else {
                        e.printStackTrace();
                        break;
                    }
                }
                try {
                    output1 = new ObjectOutputStream(p1.getOutputStream());
                    input1 = new ObjectInputStream(p1.getInputStream());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
                try {
                    mode = (char) input1.readObject();
                    output1.writeObject(P1);
                    output1.writeObject(portGUI++);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
                System.out.println("Found Player:"
                        + "\nPlayer 1 Port: " + (portGUI - 1));
                if (mode == PLAYER) {
                    player = 2;
                } else {
                    game = new Connect4(p1, null, output1, null,
                            input1, null, 1);
                    Game playGame = new Game(game);
                    threadGame = new Thread(playGame);
                    threadGame.start();
                    System.out.println("Launching game against computer opponent.");
                }
            } else {
                try {
                    p2 = server.serverSocket.accept();
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                try {
                    output2 = new ObjectOutputStream(p2.getOutputStream());
                    input2 = new ObjectInputStream(p2.getInputStream());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
                try {
                    mode = (char) input2.readObject();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
                if (mode == PLAYER) {
                    try {
                        output2.writeObject(P2);
                        output2.writeObject(portGUI++);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        continue;
                    }
                    System.out.println("Found Player:"
                            + "\nPlayer 2 Port: " + (portGUI - 1));
                    game = new Connect4(p1, p2, output1,
                            output2, input1, input2, 2);
                    Game playGame = new Game(game);
                    threadGame = new Thread(playGame);
                    threadGame.start();
                    player = 1;
                    System.out.println("Launching 2 player game.");
                } else {
                    try {
                        output2.writeObject(P1);
                        output2.writeObject(portGUI++);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        continue;
                    }
                    System.out.println("Found Player:"
                            + "\nPlayer 1 Port: " + (portGUI - 1));
                    game = new Connect4(p2, null, output2, null,
                            input2, null, 1);
                    Game playGame = new Game(game);
                    threadGame = new Thread(playGame);
                    threadGame.start();
                    System.out.println("Launching game against computer opponent.");
                }
            }
        }
    }
}
