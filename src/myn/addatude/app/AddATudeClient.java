/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 2
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class serves as a client to a server that uses the AddATude protocol. 
 *
 ************************************************/

package myn.addatude.app;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import myn.addatude.protocol.*;

/**
 * This class serves as a client that follows the AddATude protocol.
 * 
 * @version 29 September 2015
 * @author Austin Sandlin
 */
public class AddATudeClient {

    /** Socket variable for making connections to servers. */
    private static Socket socket;

    /** MessageInput variable to encapsulate the socket's input stream. */
    private static MessageInput in;

    /** MessageOutput variable to encapsulate the socket's output stream. */
    private static MessageOutput out;

    /** The server name as a string to use later for connecting the socket. */
    private static String server;

    /** The server port as an int to use later for connecting the socket. */
    private static int serverPort;

    /** Scanner variable for reading from standard in. */
    private static Scanner kb = new Scanner(System.in);

    /**
     * Very simple main function. It reads the command line arguments, checks
     * them for errors, and then saves them in the class' private variables for
     * use later in connecting to the socket.
     * 
     * @param args
     *            command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Parameter(s): <Server Identity> <Server Port>");
            return;
        }

        server = args[0];
        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Port is not a valid integer.");
            return;
        }

        promptUser();
    }

    /**
     * This function repeatedly prompts the user for input. It checks throughly
     * for any mismatched input and for any incorrect or unexpected messages
     * being returned. It catches a lot of exceptions and displays the error
     */
    public static void promptUser() {
        AddATudeMessage messageIn = null;
        AddATudeMessage messageOut = null;

        /** Attempt to make a connection with the server. */
        makeConnection();

        /**
         * Continue prompting until the user sets the variable to no in a
         * prompt.
         */
        boolean donePrompting = false;
        while (!donePrompting) {
            /** Variable instantiated because it's used in a catch later. */
            String operation = "";
            try {
                System.out.print("Operation> ");
                operation = kb.next();

                /**
                 * Call the proper creation functions, depending on the
                 * operation given in from System.in. If it wasn't any of those,
                 * throw an IOException for an invalid operation.
                 */
                if (AddATudeMessage.REQUEST_OPERATION.equals(operation)) {
                    messageOut = promptRequest();
                } else if (AddATudeMessage.NEW_OPERATION.equals(operation)) {
                    messageOut = promptNew();
                } else {
                    throw new IOException("Invalid operation", null);
                }
            } catch (NoSuchElementException | NumberFormatException e) {
                /**
                 * This catch covers invalid inputs for the scanner. i.e., the
                 * user inputs a string for the Map ID
                 */
                System.err.println(
                        "Invalid user input: Invalid data type for input.");
                kb.next();
                donePrompting = promptContinue();
                continue;
            } catch (IOException e) {
                /**
                 * This catches the exception for an invalid operation, and any
                 * others that may be thrown in the reading from console.
                 */
                System.err.println("Invalid user input: " + e.getMessage()
                        + ": " + operation);
                donePrompting = promptContinue();
                continue;
            } catch (AddATudeException e) {
                /**
                 * This catch covers the AddATudeException thrown from the
                 * creation of the messages.
                 */
                System.err.println("Invalid user input: " + e);
                donePrompting = promptContinue();
                continue;
            }

            try {
                messageOut.encode(out);
            } catch (AddATudeException e) {
                /**
                 * If there was an AddATudeException in encode, which would
                 * happen because of an exception in the write function of
                 * MessageOutput.
                 */
                System.err.println("Unable to communicate: " + e.getMessage());
                System.exit(0);
            }

            try {
                /** Attempt to decode the returned message. */
                messageIn = AddATudeMessage.decode(in);

                /**
                 * If there was no exception thrown in decode, then that means
                 * we need to report the information to the screen. I call
                 * helper functions that print them for me. There is the chance
                 * that he sends a non-standard response (ALL or NEW) and since
                 * that would pass the decode function without exception, we
                 * need to manually throw the exception if it isn't ERROR or
                 * RESPONSE.
                 */
                if (AddATudeMessage.RESPONSE_OPERATION
                        .equals(messageIn.getOperation())) {
                    reportResponse((AddATudeLocationResponse) messageIn);
                } else if (AddATudeMessage.ERROR_OPERATION
                        .equals(messageIn.getOperation())) {
                    reportError((AddATudeError) messageIn);
                } else {
                    throw new AddATudeException(
                            "Invalid response message operation.", null);
                }
            } catch (EOFException e) {
                /**
                 * This catches the issue of the server not responding to the
                 * message sent.
                 */
                System.err.println("Unable to communicate: Reached "
                        + "unexpected end of line.");
                System.exit(0);
            } catch (AddATudeException e) {
                /**
                 * This catches several issues. The first is any invalid
                 * operation that are discovered in the decode function. Because
                 * multiple errors are here, it first checks the unexpected
                 * message issue by checking if it's RESPONSE or ERROR. If it's
                 * neither, then we have an unexpected message. If the operation
                 * IS one of those, that means an AddATudeException was thrown
                 * in the creation of the object. In that case, it's a message
                 * with invalid message fields.
                 */
                if (messageIn != null
                        && !(AddATudeMessage.RESPONSE_OPERATION
                                .equals(messageIn.getOperation()))
                        && !(AddATudeMessage.ERROR_OPERATION
                                .equals(messageIn.getOperation()))) {
                    System.err.println(
                            "Unexpected message: " + messageIn.toString());
                    /**
                     * Closing the connection and prompting to continue happen
                     * after the catch, so we don't do it here like we did
                     * above.
                     */
                } else {
                    System.err.println("Invalid message: " + e.getMessage());
                    System.exit(0);
                }
            }

            /** Prompt the user to continue prompting. */
            donePrompting = promptContinue();
        }

        /** Attempt to close the connection with the server. */
        closeConnection();

        kb.close();
    }

    /**
     * This function serves as a helper function to clean up the code a little.
     * The function attempts to make a socket that connects to a server. If
     * there is an error in connecting, it displays the issue to the user and
     * exits.
     */
    public static void makeConnection() {
        try {
            socket = new Socket(server, serverPort);
        } catch (IOException e) {
            System.err.println(
                    "Unable to communicate: Could not make socket. Address: "
                            + server + ", Port: " + serverPort);
            System.exit(0);
        }

        try {
            in = new MessageInput(socket.getInputStream());
            out = new MessageOutput(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Unable to communicate: "
                    + "Could not retreive socket's I/O streams.");
            System.exit(0);
        }
    }

    /**
     * This function serves as a helper function to clean up the code a little.
     * The function attempts to close the connection with the server by closing
     * the socket. Any errors thrown in the closing of the socket are displayed
     * to the user.
     */
    public static void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err
                    .println("Unable to communicate: Could not close socket.");
            System.exit(0);
        }
    }

    /**
     * This function is used to allow prompting in multiple places. It
     * continuously loops until [y] or [n] are entered for the prompt.
     * 
     * @return a boolean representing whether or not to we're done looping.
     */
    public static boolean promptContinue() {
        boolean toReturn = false;

        boolean validInput = false;
        while (!validInput) {
            System.out.print("Continue (y/n)> ");
            String cont = kb.next();
            if ("n".equals(cont)) {
                toReturn = true;
                validInput = true;
            } else if ("y".equals(cont)) {
                toReturn = false;
                validInput = true;
            } else {
                System.err
                        .println("Invalid user input: Invalid answer: " + cont);

            }
        }

        return toReturn;
    }

    /**
     * This function prompts the user to fill in the information necessary for
     * constructing an AddATudeLocationRequest. It then creates the object with
     * the given values and returns it.
     * 
     * @return an AddATudeLocationRequest constructed from input from System.in
     * @throws AddATudeException
     *             if there is an issue in the construction of the
     *             AddATudeLocationRequest
     * @throws NoSuchElementException
     *             if there is an unexpected value read from System.in
     */
    public static AddATudeMessage promptRequest() throws AddATudeException,
            NoSuchElementException, NumberFormatException {
        System.out.print("Map ID> ");
        int mapId = kb.nextInt();
        return new AddATudeLocationRequest(mapId);
    }

    /**
     * This function prompts the user to fill in the information necessary for
     * constructing an AddATudeNewLocation. It then creates the object with the
     * given values and returns it.
     * 
     * @return an AddATudeNewLocation constructed from input from System.in
     * @throws AddATudeException
     *             if there is an issue in the construction of the
     *             AddATudeNewLocation
     * @throws NoSuchElementException
     *             if there is an unexpected value read from System.in
     */
    public static AddATudeMessage promptNew()
            throws AddATudeException, NoSuchElementException {
        System.out.print("Map ID> ");
        int mapId = kb.nextInt();
        System.out.print("User ID> ");
        int userId = kb.nextInt();
        System.out.print("Longitude> ");
        String longitude = kb.next();
        System.out.print("Latitude> ");
        String latitude = kb.next();
        kb.nextLine();
        System.out.print("Location Name> ");
        String locationName = kb.nextLine();
        System.out.print("Location Description> ");
        String locationDescription = kb.nextLine();

        LocationRecord location = new LocationRecord(userId, longitude,
                latitude, locationName, locationDescription);
        return new AddATudeNewLocation(mapId, location);
    }

    /**
     * This function prints the AddATudeLocationResponse to System.out in the
     * format given by example in the documentation.
     * 
     * @param response
     *            the AddATudeLocationResponse to display to System.out
     */
    public static void reportResponse(AddATudeLocationResponse response) {
        System.out.println(
                "mapID=" + response.getMapId() + " - " + response.getMapName());
        ArrayList<LocationRecord> locationList = (ArrayList<LocationRecord>) response
                .getLocationRecordList();

        for (int i = 0; i < locationList.size(); ++i) {
            LocationRecord temp = locationList.get(i);
            System.out.println("User " + temp.getUserId() + ":"
                    + temp.getLocationName() + " - "
                    + temp.getLocationDescription() + " at ("
                    + temp.getLongitude() + ", " + temp.getLatitude() + ")");
        }
        System.out.println();
    }

    /**
     * This function prints the AddATudeError to System.err in the format given
     * by example in the documentation.
     * 
     * @param error
     *            the AddATudeError to display to System.out
     */
    public static void reportError(AddATudeError error) {
        System.err.println("Error: " + error.getErrorMessage());
    }
}
