/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 5
 * Class:       CSI 4321 - Data Communications
 * Date:        10 November 2015
 *
 * This class is the client for the NoTiFi protocol. It connects to a server and
 * reports to the console any valid NoTiFiMessages.
 *
 ************************************************/

package myn.notifi.app;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

import myn.notifi.protocol.*;

/**
 * This class is runs the client for the NoTiFi protocol.
 * 
 * @author Austin Sandlin
 * @version 11/10/15
 */
public class NoTiFiClient extends NoTiFiMessageHandler {

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
        if (args.length != 3) {
            System.err.println("Parameter(s): <Server> <Port> <Local>");
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

        /** Attempts to construct the InetAddress for the local address */
        InetAddress localAddress = null;
        try {
            localAddress = Inet4Address.getByName(args[2]);
        } catch (UnknownHostException e1) {
            System.err.println(
                    "Problem constructing server address with given parameter.");
            System.exit(0);
        }

        /** Client obtains a random value for the message ID. */
        msgId = (int) (Math.random() * MAX_MESSAGE_ID_EXCLUSIVE);

        /** Start the client's server handler part of the thread. */
        Thread thread = new Thread(
                new ServerHandler(serverAddress, serverPort, localAddress));
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
class ServerHandler extends NoTiFiMessageHandler implements Runnable {

    /** Public timeout variable in milliseconds. */
    public static final int TIMEOUT = 3000;

    /** Stores the number of response attempts to be made by the client. */
    public static final int MAX_RESPONSE_ATTEMPTS = 2;

    public static boolean doneListening;

    /** An InetAddress that stores the address of the server. */
    private InetAddress serverAddress;

    /** An int that stores the value of the server's port. */
    private int serverPort;

    /**
     * Simple constructor for the class that initializes the address and port
     * variables.
     * 
     * @param serverAddress
     *            the address of the server
     * @param serverPort
     *            the address of the port
     * @param localAddress
     *            the client's own address
     */
    public ServerHandler(InetAddress serverAddress, int serverPort,
            InetAddress localAddress) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        try {
            socket = new DatagramSocket(0, (Inet4Address) localAddress);
        } catch (SocketException e) {
            System.err.println("Could not construct socket with local address");
            System.exit(0);
        }
    }

    /**
     * Overridden run function that does the grunt work for everything.
     */
    @Override
    public void run() {
        /** Function to create connection. */
        makeConnection();

        /** Make a thread to listen to and print location updates. */
        Thread thread = new Thread(new MessageHandler());
        thread.start();

        /** Poll the keyboard for the text: "quit". */
        Scanner keyboard = new Scanner(System.in);
        boolean done = false;
        doneListening = false;
        while (!done) {
            /**
             * If we get quit, close the scanner, note that we're done, and let
             * the thread know we're done.
             */
            if ("quit".equals(keyboard.nextLine())) {
                keyboard.close();
                done = true;
                doneListening = true;
            }
        }

        /**
         * Send a specific error message to trigger the thread's dormant receive
         * so that the function can be completely interrupted. Then begin to
         * close the connection.
         */
        try {
            sendMessage(new NoTiFiACK(NoTiFiClient.msgId),
                    socket.getLocalAddress(), socket.getLocalPort());
        } catch (IllegalArgumentException e) {
            System.err
                    .println("Failed to construct empty NoTiFiError message.");
            System.exit(0);
        } catch (IOException e) {
            System.err.println(
                    "Failed to send message to unblock interrupted thread.");
            System.exit(0);
        }

        /** Function to close connection. */
        closeConnection();
    }

    /**
     * This function simply constructs a register message to send to the server.
     * It then passes that particular NoTiFiMessage to a function that performs
     * the 3 second wait, per the NoTiFi protocol.
     */
    public void makeConnection() {
        /** Create a registration message to send to the server. */
        NoTiFiRegister message = null;
        try {
            message = new NoTiFiRegister(NoTiFiClient.msgId,
                    (Inet4Address) socket.getLocalAddress(),
                    socket.getLocalPort());
        } catch (IllegalArgumentException e1) {
            System.err.println("Failed to construct deregister message.");
            System.exit(0);
        }

        /** Check if we got a message back. If we didn't, let the user know. */
        if (!expectACK(message)) {
            System.out.println(NoTiFiClient.REGISTER_PROBLEM_ERROR);
            System.exit(0);
        }
    }

    /**
     * This function simply constructs a deregister message to send to the
     * server. It then passes that particular NoTiFiMessage to a function that
     * performs the 3 second wait, per the NoTiFi protocol.
     */
    public void closeConnection() {
        /** Create a registration message to send to the server. */
        NoTiFiDeregister message = null;
        try {
            message = new NoTiFiDeregister(NoTiFiClient.msgId,
                    (Inet4Address) socket.getLocalAddress(),
                    socket.getLocalPort());
        } catch (IllegalArgumentException e1) {
            System.err.println("Failed to construct deregister message.");
            System.exit(0);
        }

        /** Check if we got a message back. If we didn't, let the user know. */
        if (!expectACK(message)) {
            System.out.println(NoTiFiClient.DEREGISTER_PROBLEM_ERROR);
            System.exit(0);
        }
    }

    /**
     * This function just sends a message and expects an ACK back. It performs
     * the proper work for the startup and shutdown procedures per the NoTiFi
     * protocol.
     * 
     * @param message
     *            the NoTiFi message to send.
     */
    public boolean expectACK(NoTiFiMessage message) {
        /**
         * Try, as many times as the protocol dictates, to get the ACK from the
         * server.
         */
        boolean receivedResponse = false;
        for (int i = 0; i < ServerHandler.MAX_RESPONSE_ATTEMPTS
                && !receivedResponse; ++i) {
            /**
             * Store the intended end time based off the current time and the
             * number of milliseconds to timeout.
             */
            long endTime = System.currentTimeMillis() + ServerHandler.TIMEOUT;

            /** Tell the socket to timeout with the given time left. */
            int timeLeft = ServerHandler.TIMEOUT;

            /**
             * Attempt to set the timeout. You should be able to do this without
             * issue...
             */
            try {
                socket.setSoTimeout(timeLeft);
            } catch (SocketException e) {
                System.err.println("Could not set timeout.");
                System.exit(0);
            }

            /** Try to send the message. */
            try {
                sendMessage(message, serverAddress, serverPort);
            } catch (IOException e) {
                System.err.println("Failed to send message to server.");
                System.exit(0);
            }

            /**
             * Complicated part... loop until we have run out of time or have
             * received a response.
             */
            while ((!receivedResponse) && (timeLeft > 0)) {
                /** Try to receive a message. */
                NoTiFiMessage receivedMessage = null;
                try {
                    receivedMessage = getMessage(receivePacket());
                } catch (IOException e) {
                    /**
                     * If the exception we catch is an IOException, but NOT a
                     * socket timeout exception, then we have an issue and need
                     * to quit.
                     */
                    if (!(e instanceof SocketTimeoutException)) {
                        System.err.println("Problem reading from socket.");
                        System.exit(0);
                    }
                }

                /**
                 * If the message received is an ACK and has the same message
                 * ID, then we have received an ACK and the connection has been
                 * made. Otherwise, we correct the time left for the timeout and
                 * update the timeout for the socket.
                 */
                if (receivedMessage != null) {
                    if (receivedMessage.getCode() == NoTiFiACK.CODE) {
                        if (receivedMessage.getMsgId() == NoTiFiClient.msgId) {
                            receivedResponse = true;
                        } else {
                            System.out.println(
                                    NoTiFiClient.UNEXPECTED_MESSAGE_ID_ERROR);
                        }
                    } else if (receivedMessage.getCode() == NoTiFiError.CODE) {
                        System.out.println(((NoTiFiError) receivedMessage)
                                .getErrorMessage());
                    }
                } else {
                    timeLeft = (int) (endTime - System.currentTimeMillis());

                    /**
                     * Make sure the timeout is greater than zero before setting
                     * the socket timeout.
                     */
                    try {
                        if (timeLeft > 0) {
                            socket.setSoTimeout(timeLeft);
                        }
                    } catch (SocketException e) {
                        System.err.println("Could not set timeout.");
                        System.exit(0);
                    }
                }
            }
        }
        return receivedResponse;
    }
}

/**
 * This class takes care of handling location deletion, location addition, and
 * error messages from the server.
 * 
 * @author Austin Sandlin
 * @version 11/10/15
 */
class MessageHandler extends NoTiFiMessageHandler implements Runnable {

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
        while (!ServerHandler.doneListening) {
            /** Receive the message from the server. */
            NoTiFiMessage receivedMessage = null;
            try {
                receivedMessage = getMessage(receivePacket());
            } catch (IllegalArgumentException e) {
                System.out.println(
                        NoTiFiClient.PARSING_PROBLEM_ERROR + e.getMessage());
            } catch (SocketTimeoutException e) {
                /** Do nothing. This is to periodically unblock the socket. */
            } catch (IOException e) {
                System.out.println(
                        NoTiFiClient.PARSING_PROBLEM_ERROR + e.getMessage());
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
                    if (receivedMessage.getCode() != NoTiFiACK.CODE
                            || !ServerHandler.doneListening) {
                        System.out.println(
                                NoTiFiClient.UNEXPECTED_MESSAGE_TYPE_ERROR);
                    }

                }
            } else {

            }
        }
    }
}
