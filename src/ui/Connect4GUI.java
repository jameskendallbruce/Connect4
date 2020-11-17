/**
 *
 * Connect4GUI
 *
 * Runs the GUI display for Connect4 gameplay.
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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Platform;
import java.util.List;

/**
 * Radically overhauled version of the Connect4GUI class. Extends JavaFX
 * Application class for functionality. GUI now presents a colored grid for the
 * game board as opposed to a disproportionate String layout as it utilized
 * before.
 *
 * @author James Kendall Bruce
 */
public class Connect4GUI extends Application {

    /**
     * Used for
     */
    private int HEIGHT = 500;

    /**
     * I/O streams
     */
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /**
     * True if the GUI application is currently running.
     */
    private boolean running;
    private int port;

    /**
     * The player whose turn it currently is.
     */
    private char currentPlayer;

    private Object inData = null;

    /**
     * Place holder for various methods.
     */
    private char c = ' ';

    /**
     * 2D Array to hold the entire game board.
     */
    private char[][] gameBoard = null;

    /**
     * Must access the constants through the Connect4Constants class since it
     * extends Application instead. Starts in the state of waiting.
     */
    private String updateMessage = Connect4Constants.WAITING;

    /**
     * Main JavaFX component to attach the other JavaFX components too.
     */
    private GridPane mainPane = new GridPane();

    /**
     * Grid component to represent the gameBoard.
     */
    private GridPane gameGrid = new GridPane();

    /**
     * JavaFX component to output messages to the user.
     */
    private Text message = new Text();

    /**
     * Click event handler subclass to handle when a token column is selected by
     * the user.
     */
    private class ColumnClickHandler implements EventHandler<MouseEvent> {

        /**
         * The column selected by the player.
         */
        private int column;

        /**
         * subclass method to set the column int to match the input col.
         *
         * @param col The column that the click updates column to
         */
        public ColumnClickHandler(int col) {
            this.column = col;
        }

        /**
         * Overrides the mouse click handle event. Makes appropriate calls to
         * updatethe game states to reflect the current play.
         *
         * @param event The column click event
         */
        @Override
        public void handle(MouseEvent event) {

            /**
             * Checks whose turn it is, if they have made a valid play and if
             * they have won. Also checks for a tie game.
             */
            if ((updateMessage.equals(Connect4Constants.P2_WINS)
                    || updateMessage.equals(Connect4Constants.P2_TURN)
                    || updateMessage.equals(Connect4Constants.P2_INVALID))
                    && currentPlayer == Connect4Constants.P1) {
                return;
            } else if ((updateMessage.equals(Connect4Constants.P1_WINS)
                    || updateMessage.equals(Connect4Constants.P1_TURN)
                    || updateMessage.equals(Connect4Constants.P1_INVALID))
                    && currentPlayer == Connect4Constants.P2) {
                return;
            } else if (updateMessage.equals(Connect4Constants.WAITING)
                    || updateMessage.equals(Connect4Constants.TIE)) {
                return;
            }

            /**
             * After all other if/else blocks, it sets the out object to be the
             * column selected.
             */
            try {
                out.writeObject(column);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Overrides the default start method of the JavaFX Application class.
     * Called by tdefault by the Application launch method and runs the GUI
     * display format.
     *
     * @param primaryStage default Stage of the JavaFX GUI
     * @throws Exception exceptions written in for possible scenarios
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        /**
         * Sets the message displayed to the current updateMessage and defines
         * the appearance of this
         */
        message.setText(updateMessage);
        message.setVisible(true);
        message.setFont(Font.font("verdana", FontWeight.EXTRA_BOLD,
                FontPosture.REGULAR, .05 * HEIGHT));

        /**
         * Creates a button array to hold the entire grid.
         */
        Button[][] circles = new Button[Connect4Constants.ROWS][Connect4Constants.COLUMNS];
        GridPane[] columns = new GridPane[Connect4Constants.COLUMNS];
        for (int i = 0; i < Connect4Constants.COLUMNS; i++) {
            columns[i] = new GridPane();
            gameGrid.add(columns[i], i, 0);
        }
        for (int r = Connect4Constants.ROWS - 1; r >= 0; r--) {
            for (int c = 0; c < Connect4Constants.COLUMNS; c++) {
                Button circle = new Button();
                int temp = (5 - r) % Connect4Constants.ROWS;
                circles[temp][c] = circle;
                circles[temp][c].setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                circles[temp][c].setBackground(new Background(new BackgroundFill(
                        Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                columns[c].add(circle, 0, r);
            }
        }

        /**
         * Adds all the GUI components to the mainPane and defines it's
         * appearance.
         */
        mainPane.add(message, 0, 0);
        mainPane.add(gameGrid, 0, 1);
        Scene gameScene = new Scene(mainPane, 600, 600);

        for (int r = 0; r < Connect4Constants.ROWS; r++) {
            for (int c = 0; c < Connect4Constants.COLUMNS; c++) {
                circles[r][c].prefWidthProperty().bind(gameGrid.widthProperty());
                circles[r][c].prefHeightProperty().bind(gameGrid.heightProperty());
            }
        }

        /**
         * Sets the created scene as the primaryStage Scene.
         */
        primaryStage.setTitle(Connect4Constants.WINDOW_TITLE_GUI);
        primaryStage.setScene(gameScene);
        primaryStage.sizeToScene();
        primaryStage.show();

        Application.Parameters params2 = getParameters();
        List<String> rawArguments2 = params2.getRaw();
        currentPlayer = rawArguments2.get(1).charAt(0);
        System.out.println(currentPlayer);
        port = Integer.parseInt(rawArguments2.get(0));
        String host = "localhost";

        /**
         * Initiates a clickHandler for each column and inputs the approrpiate
         * column int.
         */
        ColumnClickHandler eventHandler0 = new ColumnClickHandler(1);
        columns[0].addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler0);

        ColumnClickHandler eventHandler1 = new ColumnClickHandler(2);
        columns[1].addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler1);

        ColumnClickHandler eventHandler2 = new ColumnClickHandler(3);
        columns[2].addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler2);

        ColumnClickHandler eventHandler3 = new ColumnClickHandler(4);
        columns[3].addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler3);

        ColumnClickHandler eventHandler4 = new ColumnClickHandler(5);
        columns[4].addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler4);

        ColumnClickHandler eventHandler5 = new ColumnClickHandler(6);
        columns[5].addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler5);

        ColumnClickHandler eventHandler6 = new ColumnClickHandler(7);
        columns[6].addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler6);

        /**
         * Checks if either player has quit the game or otherwise exitted out of
         * the game. Turns the application off.
         */
        primaryStage.setOnCloseRequest(event -> {
            try {
                if (running == true) {
                    if (currentPlayer == Connect4Constants.P1) {
                        out.writeObject(Connect4Constants.P1_QUIT);
                    } else {
                        out.writeObject(Connect4Constants.P2_QUIT);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            running = false;
        });

        /**
         * Creates a runnable action for the Application to update the
         * gameBoard. Unselected grid spots remain white. Player 1 is Red and
         * Player 2 is Blue.
         */
        Runnable runUpdateBoard = () -> {
            for (int r = 0; r < Connect4Constants.ROWS; r++) {
                for (int col = 0; col < Connect4Constants.COLUMNS; col++) {
                    c = gameBoard[r][col];
                    if (c == ' ') {
                        circles[r][col].setBackground(new Background(
                                new BackgroundFill(Color.WHITE, CornerRadii.EMPTY,
                                        Insets.EMPTY)));
                    } else if (c == Connect4Constants.P1) {
                        circles[r][col].setBackground(new Background(
                                new BackgroundFill(Color.RED, CornerRadii.EMPTY,
                                        Insets.EMPTY)));
                    } else {
                        circles[r][col].setBackground(new Background(
                                new BackgroundFill(Color.BLUE, CornerRadii.EMPTY,
                                        Insets.EMPTY)));
                    }
                }
            }
        };

        /**
         * Creates a runnable action for the Applicaiton to update the message
         * GUI component. Checks the current player, the token dropped and the
         * state of the updateMessage variable to decipher the appropriate
         * message to output.
         */
        Runnable runUpdateMessage = () -> {
            System.out.println(updateMessage);
            if (currentPlayer == Connect4Constants.P1 && updateMessage.equals(
                    Connect4Constants.P1_WINS)) {
                message.setText(Connect4Constants.P1_WINS_GUI);
                running = false;
            } else if (currentPlayer == Connect4Constants.P1 && updateMessage.equals(
                    Connect4Constants.P2_WINS)) {
                message.setText(Connect4Constants.OPPONENT_P2_WINS_GUI);
                running = false;
            } else if (currentPlayer == Connect4Constants.P2 && updateMessage.equals(
                    Connect4Constants.P1_WINS)) {
                message.setText(Connect4Constants.OPPONENT_P1_WINS_GUI);
                running = false;
            } else if (currentPlayer == Connect4Constants.P2 && updateMessage.equals(
                    Connect4Constants.P2_WINS)) {
                message.setText(Connect4Constants.P2_WINS_GUI);
                running = false;
            } else if (updateMessage.equals(Connect4Constants.TIE)) {
                message.setText(updateMessage);
                running = false;
            } else if ((updateMessage.equals(Connect4Constants.P2_TURN)
                    || updateMessage.equals(Connect4Constants.P2_INVALID))
                    && currentPlayer == Connect4Constants.P2) {
                message.setText(Connect4Constants.P2_YOUR_TURN_GUI);
            } else if ((updateMessage.equals(Connect4Constants.P1_TURN)
                    || updateMessage.equals(Connect4Constants.P1_INVALID))
                    && currentPlayer == Connect4Constants.P1) {
                message.setText(Connect4Constants.P1_YOUR_TURN_GUI);
            } else if ((updateMessage.equals(Connect4Constants.P2_TURN)
                    || updateMessage.equals(Connect4Constants.P2_INVALID))
                    && currentPlayer != Connect4Constants.P2) {
                message.setText(Connect4Constants.WAITING_FOR_P2_GUI);
            } else if ((updateMessage.equals(Connect4Constants.P1_TURN)
                    || updateMessage.equals(Connect4Constants.P1_INVALID))
                    && currentPlayer != Connect4Constants.P1) {
                message.setText(Connect4Constants.WAITING_FOR_P1_GUI);
            } else if (updateMessage.equals(Connect4Constants.P1_QUIT)) {
                running = false;
                message.setText(Connect4Constants.P1_QUIT);
            } else if (updateMessage.equals(Connect4Constants.P2_QUIT)) {
                running = false;
                message.setText(Connect4Constants.P2_QUIT);
            }
        };

        /**
         * Thread instance to establish I/O connections. Monitors the state of
         * the running variable to check for activity and monitors all button
         * clicks and ouput messages to client and GUI application.
         */
        new Thread(() -> {
            Socket gameSocket;
            try {
                gameSocket = new Socket(host, port);
                out = new ObjectOutputStream(gameSocket.getOutputStream());
                in = new ObjectInputStream(gameSocket.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            running = true;
            while (running) {
                try {
                    inData = in.readObject();
                } catch (Exception e) {
                    if (running == false) {
                        break;
                    }
                    e.printStackTrace();
                    break;
                }
                if (inData instanceof char[][]) {
                    gameBoard = (char[][]) inData;
                    Platform.runLater(runUpdateBoard);
                } else if (inData instanceof String) {
                    updateMessage = (String) inData;
                    Platform.runLater(runUpdateMessage);
                } else {
                    System.out.println("Not a String");
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                columns[0].removeEventFilter(MouseEvent.MOUSE_CLICKED,
                        eventHandler0);
                columns[1].removeEventFilter(MouseEvent.MOUSE_CLICKED,
                        eventHandler1);
                columns[2].removeEventFilter(MouseEvent.MOUSE_CLICKED,
                        eventHandler2);
                columns[3].removeEventFilter(MouseEvent.MOUSE_CLICKED,
                        eventHandler3);
                columns[4].removeEventFilter(MouseEvent.MOUSE_CLICKED,
                        eventHandler4);
                columns[5].removeEventFilter(MouseEvent.MOUSE_CLICKED,
                        eventHandler5);
                columns[6].removeEventFilter(MouseEvent.MOUSE_CLICKED,
                        eventHandler6);
                out.close();
                in.close();
                gameSocket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    /**
     * Default main method for Application. Calls the Application launch method
     * which initiaites the start method for the GUI display.
     *
     * @param args The default class args to be passed to the launch method
     */
    public static void main(String args[]) {
        launch(args);
    }
}
