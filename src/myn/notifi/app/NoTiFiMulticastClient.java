/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 8
 * Class:       CSI 4321 - Data Communications
 * Date:        3 December 2015
 *
 * This class is the client for the NoTiFi protocol. It connects to a server and
 * reports to the console any valid NoTiFiMessages.
 *
 ************************************************/

package myn.notifi.app;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

import myn.notifi.protocol.*;

/**
 * This class runs the client for the NoTiFi protocol.
 * 
 * @author Austin Sandlin
 * @version 11/10/15
 */
public class NoTiFiMulticastClient extends NoTiFiMessageHandler {

    /** This is the max message id for a random id for the client. */
    private static final int MAX_MESSAGE_ID_EXCLUSIVE = 256;

    /** Some static strings for error printing. */
    public static final String UNEXPECTED_CODE_ERROR = "Unexpected code";
    public static final String UNEXPECTED_MESSAGE_TYPE_ERROR = "Unexpected message type";
    public static final String PARSING_PROBLEM_ERROR = "Unable to parse message";
    public static final String REGISTER_PROBLEM_ERROR = "Unable to parse message";
    public static final String DEREGISTER_PROBLEM_ERROR = "Unable to parse message: ";
    public static final String UNEXPECTED_MESSAGE_ID_ERROR = "Unexpected MSG ID";

    /** The client's message id. */
    public static int msgId;

    /**
     * This is just the main function that prepares all the command line stuff.
     * 
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        /** Checks the number of parameters to determine proper use. */
        if (args.length != 2) {
            System.err.println("Parameter(s): <Server> <Port>");
            System.exit(0);
        }

        /** Attempts to construct the InetAddress for the server address */
        InetAddress serverAddress = null;
        try {
            serverAddress = Inet4Address.getByName(args[0]);
        } catch (UnknownHostException e1) {
            System.err.println(
                    "Problem constructing server address with given parameter.");
            System.exit(0);
        }

        /** Attempts to construct the port for the server address */
        int serverPort = 0;
        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Problem parsing port value.");
            System.exit(0);
        }

        /** Client obtains a random value for the message ID. */
        msgId = (int) (Math.random() * MAX_MESSAGE_ID_EXCLUSIVE);

        /** Start the client's server handler part of the thread. */
        Thread thread = new Thread(
                new MulticastServerHandler(serverAddress, serverPort));
        thread.start();
    }

}

/**
 * This class is spawned from the Client's main function. This function handles
 * the construction and deconstruction of a connection to a server and spawns a
 * thread to listen to received messages.
 * 
 * @author Austin Sandlin
 * @version 11/10/15
 */
class MulticastServerHandler extends NoTiFiMessageHandler implements Runnable {

    /** Variable to denote when the listening thread should stop listening. */
    public static boolean doneListening;

    /** An InetAddress that stores the address of the server. */
    private InetAddress multicastAddress;

    /** An int that stores the value of the server's port. */
    private int serverPort;

    /**  */

    /**
     * Simple constructor for the class that initializes the address and port
     * variables.
     * 
     * @param serverAddress
     *            the address of the server
     * @param serverPort
     *            the address of the port
     */
    public MulticastServerHandler(InetAddress serverAddress, int serverPort) {
        this.multicastAddress = serverAddress;
        this.serverPort = serverPort;

        try {
            socket = new MulticastSocket(this.serverPort);
        } catch (IOException e) {
            System.err.println("Failed to construct the multicast socket.");
            System.exit(0);
        }
    }

    /**
     * Overridden run function that does the grunt work for everything.
     */
    @Override
    public void run() {
        /** Create connection. */
        try {
            ((MulticastSocket) socket).joinGroup(multicastAddress);
        } catch (IOException e) {
            System.err.println("Failed to join multicast group.");
            System.exit(0);
        }

        /** Make a thread to listen to and print location updates. */
        Thread thread = new Thread(new MulticastMessageHandler());
        thread.start();

        /** Poll the keyboard for the text: "quit". */
        Scanner keyboard = new Scanner(System.in);
        doneListening = false;
        while (!doneListening) {
            /**
             * If we get quit, close the scanner, note that we're done, and let
             * the thread know we're done.
             */
            if ("quit".equals(keyboard.nextLine())) {
                keyboard.close();
                doneListening = true;
            }
        }

        /** Close connection. */
        try {
            ((MulticastSocket) socket).leaveGroup(multicastAddress);
        } catch (IOException e) {
            System.err.println("Failed to leave multicast group.");
            System.exit(0);
        }
    }
}

/**
 * This class takes care of handling location deletion, location addition, and
 * error messages from the server.
 * 
 * @author Austin Sandlin
 * @version 11/10/15
 */
class MulticastMessageHandler extends NoTiFiMessageHandler implements Runnable {

    /** This variable is for unblocking the receive socket. */
    private final int UNBLOCKING_TIMEOUT = 100;

    /**
     * This is the overridden run function for thread stuff. It sets the socket
     * timeout to zero (possibly fixing it from the register setup). It then
     * loops forever, just catching messages from the server.
     */
    @Override
    public void run() {
        try {
            socket.setSoTimeout(UNBLOCKING_TIMEOUT);
        } catch (SocketException e) {
            System.err.println("Could not set timeout.");
            System.exit(0);
        }
        /**
         * Loops catching input from the server, so long as the main hasn't told
         * it to stop listening.
         */
        while (!MulticastServerHandler.doneListening) {
            /** Receive the message from the server. */
            NoTiFiMessage receivedMessage = null;
            try {
                receivedMessage = getMessage(receivePacket());
            } catch (IllegalArgumentException e) {
                System.out.println(
                        NoTiFiMulticastClient.PARSING_PROBLEM_ERROR + e.getMessage());
            } catch (SocketTimeoutException e) {
                /** Do nothing. This is to periodically unblock the socket. */
            } catch (IOException e) {
                System.out.println(
                        NoTiFiMulticastClient.PARSING_PROBLEM_ERROR + e.getMessage());
            }

            if (receivedMessage != null) {
                /**
                 * Simply switch on the message code and do stuff accordingly
                 */
                switch (receivedMessage.getCode()) {
                case NoTiFiLocationAddition.CODE:
                    LocationRecord locationAddition = ((NoTiFiLocationAddition) receivedMessage)
                            .getLocationRecord();
                    System.out.println("Location Addition: (" + "Latitude: "
                            + locationAddition.getLatitude() + ", Longitude: "
                            + locationAddition.getLongitude() + ", Name: "
                            + locationAddition.getLocationName()
                            + ", Description: "
                            + locationAddition.getLocationDescription() + ")");
                    break;
                case NoTiFiLocationDeletion.CODE:
                    LocationRecord locationDeletion = ((NoTiFiLocationDeletion) receivedMessage)
                            .getLocationRecord();
                    System.out.println("Location Deletion: (" + "Latitude: "
                            + locationDeletion.getLatitude() + ", Longitude: "
                            + locationDeletion.getLongitude() + ", Name: "
                            + locationDeletion.getLocationName()
                            + ", Description: "
                            + locationDeletion.getLocationDescription() + ")");
                    break;
                case NoTiFiError.CODE:
                    System.out.println(
                            ((NoTiFiError) receivedMessage).getErrorMessage());
                    break;
                default:
                    System.out.println(
                            NoTiFiMulticastClient.UNEXPECTED_MESSAGE_TYPE_ERROR);

                }
            }
        }
    }
}
