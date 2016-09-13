/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 6
 * Class:       CSI 4321 - Data Communications
 * Date:        16 November  2015
 *
 * This class is the main class for the NoTiFiServer. It spawns a thread to
 * handle register and deregister messages.
 *
 ************************************************/

package myn.notifi.app;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import myn.notifi.protocol.*;

/**
 * This is the NoTiFiServer class. It just has basic functionality. It notifies
 * connected clients on location additions and deletions.
 * 
 * @author Austin Sandlin
 * @version 11/10/15
 */
public class NoTiFiServer extends NoTiFiMessageHandler {

    static Comparator<InetSocketAddress> socketAddressComparator = new Comparator<InetSocketAddress>() {
        @Override
        public int compare(InetSocketAddress o1, InetSocketAddress o2) {
            if(o1.equals(o2)) {
                return 0;
            }
            return -1;
        }           
    };
    
    /**
     * This is a static map for storing all the clients. They are stored by
     * message id and InetSocketAddress. The InetSocketAddress stores both
     * address and port, so the map works great.
     */
    public static SortedMap<InetSocketAddress, Integer> clientList = Collections
            .synchronizedSortedMap(new TreeMap<InetSocketAddress, Integer>(socketAddressComparator));
    
    /** The logger from the AddATudeServer */
    public Logger logger;

    /**
     * This constructor starts the server with the port and address passed in.
     * It also is given a logger to use. This logger should be a static one so
     * that both the NoTiFiServer and AddATudeServer can write to the same file.
     * 
     * @param localPort
     *            the port of the server
     * @param inetAddress
     *            the address of the server
     * @param logger
     *            the java logger for logging
     */
    public NoTiFiServer(int localPort, InetAddress inetAddress, Logger logger) {
        /** Attempt to create the socket with given address and port. */
        try {
            socket = new DatagramSocket(localPort, inetAddress);
        } catch (SocketException e) {
            System.err.println(
                    "Failed to construct socket with given parameters.");
        }
        /** Hook up the logger */
        this.logger = logger;

        /** Spawn the thread for handling register and deregister messages. */
        Thread thread = new Thread(new ClientHandler(logger));
        thread.start();
    }

    /**
     * This function notifies all clients currently stored in our map of a
     * location addition.
     * 
     * @param userId
     *            the userId of the location record
     * @param longitude
     *            the longitude of the location record
     * @param latitude
     *            the latitude of the location record
     * @param name
     *            the name of the location record
     * @param description
     *            the description of the location record
     * @throws IOException
     *             if there is a problem with sending a message through the
     *             socket
     */
    public synchronized void notifyAddition(int userId, double longitude,
            double latitude, String name, String description)
                    throws IOException {
        /**
         * For each client in the map, create a location addition record and
         * send it to the client's socket.
         */
        for (InetSocketAddress key : clientList.keySet()) {
            NoTiFiLocationAddition addition = new NoTiFiLocationAddition(
                    clientList.get(key), new LocationRecord(userId, longitude,
                            latitude, name, description));

            sendMessage(addition, key.getAddress(), key.getPort());
        }
    }

    /**
     * This function notifies all clients currently stored in our map of a
     * location deletion.
     * 
     * @param userId
     *            the userId of the location record
     * @param longitude
     *            the longitude of the location record
     * @param latitude
     *            the latitude of the location record
     * @param name
     *            the name of the location record
     * @param description
     *            the description of the location record
     * @throws IOException
     *             if there is a problem with sending a message through the
     *             socket
     */
    public synchronized void notifyDeletion(int userId, double longitude,
            double latitude, String name, String description)
                    throws IOException {
        /**
         * For each client in the map, create a location deletion record and
         * send it to the client's socket.
         */
        for (InetSocketAddress key : clientList.keySet()) {
            NoTiFiLocationDeletion deletion = new NoTiFiLocationDeletion(
                    clientList.get(key), new LocationRecord(userId, longitude,
                            latitude, name, description));

            sendMessage(deletion, key.getAddress(), key.getPort());
        }
    }
}

/**
 * This class is a thread for the NoTiFiServer that handles register and
 * deregister messages. It also logs the fact that a register or deregister
 * message has been sent.
 * 
 * @author Austin Sandlin
 * @version 11/10/15
 */
class ClientHandler extends NoTiFiMessageHandler implements Runnable {

    /** The java logger for logging UDP messages. */
    private Logger logger;

    /** Final strings for errors. */
    private final String UNEXPECTED_MESSAGE_TYPE_ERROR = "Unexpected message type: ";
    private final String PARSING_PROBLEM_ERROR = "Unable to parse message";
    private final String MULTICAST_ERROR = "Bad address";
    private final String ALREADY_REGISTERED_ERROR = "Already registered";
    private final String MISMATCH_ADDRESS_OR_PORT_ERROR = "Incorrect port";
    private final String UNKNOWN_CLIENT_ERROR = "Unknown client";

    /**
     * Constructor for creating the thread. Basically just passes the logger.
     * 
     * @param logger
     *            the java logger for logging
     */
    public ClientHandler(Logger logger) {
        this.logger = logger;
    }

    /**
     * This is the main function for the thread that does everything.
     */
    public void handleClients() {
        /** Create the string for compiling the logger message */
        String loggerString = null;
        /** Create the packet to be sent out. */
        DatagramPacket packet = null;
        /** Receive the packet from the client. */
        try {
            packet = receivePacket();
            loggerString = packet.getAddress() + " " + packet.getPort() + " ";
        } catch (IOException e1) {
            System.err.println("Error during receiving of UDP client message.");
            System.exit(0);
        }

        /** Make the NoTiFiMessage that was received, and one to send. */
        NoTiFiMessage responseMessage = null;
        NoTiFiMessage receivedMessage = null;
        try {
            receivedMessage = getMessage(packet);
        } catch (IllegalArgumentException e) {
            responseMessage = new NoTiFiError(0, e.getMessage());
            loggerString += e.getMessage();
        } catch (IOException e) {
            responseMessage = new NoTiFiError(0, PARSING_PROBLEM_ERROR);
            loggerString += PARSING_PROBLEM_ERROR;
        }

        if (receivedMessage != null) {
            InetSocketAddress saddr = new InetSocketAddress(
                    packet.getAddress(), packet.getPort());
            if (receivedMessage.getCode() == NoTiFiRegister.CODE) {
                /**
                 * If it was a register message, then we need to make sure that
                 * it's all valid and working.
                 */
                NoTiFiRegister registerMessage = (NoTiFiRegister) receivedMessage;
                /**
                 * Check that it wasn't a multicast address. If it was, make an
                 * error message to reply with.
                 */
                if (!registerMessage.getAddress().isMulticastAddress()) {
                    /**
                     * If the address and port equal the address and port from
                     * the packet, respond with an ACK message and add the
                     * client to our map. Also, log that a register message was
                     * received. There is a chance that the user is already in
                     * the map. In that case, make an error to send back instead
                     * of an ack. If the address and port in the register
                     * message don't match the ones from the packet, make an
                     * error instead.
                     */
                    if (registerMessage.getPort() == packet.getPort()) {
                        if (NoTiFiServer.clientList.get(saddr) == null) {
                            responseMessage = new NoTiFiACK(
                                    receivedMessage.getMsgId());
                            NoTiFiServer.clientList.put(saddr,
                                    registerMessage.getMsgId());
                            loggerString += "NoTiFiRegister message received";
                        } else {
                            responseMessage = new NoTiFiError(
                                    registerMessage.getMsgId(),
                                    ALREADY_REGISTERED_ERROR);
                            loggerString += ALREADY_REGISTERED_ERROR;
                        }
                    } else {
                        responseMessage = new NoTiFiError(
                                receivedMessage.getMsgId(),
                                MISMATCH_ADDRESS_OR_PORT_ERROR);
                        loggerString += MISMATCH_ADDRESS_OR_PORT_ERROR;
                    }

                } else {
                    responseMessage = new NoTiFiError(
                            receivedMessage.getMsgId(), MULTICAST_ERROR);
                    loggerString += MULTICAST_ERROR;
                }
            } else if (receivedMessage.getCode() == NoTiFiDeregister.CODE) {
                /**
                 * If the client exists in the map, respond with an ACK message
                 * and remove the client from our map. There is a chance that
                 * the user isn't in the map. In that case, make an error to
                 * send back instead of an ack. Also, log that we got a
                 * deregister message.
                 */
                NoTiFiDeregister deregisterMessage = (NoTiFiDeregister) receivedMessage;
                
                if (NoTiFiServer.clientList.get(saddr) != null) {
                    responseMessage = new NoTiFiACK(receivedMessage.getMsgId());
                    NoTiFiServer.clientList.remove(saddr);
                    loggerString += "NoTiFiDeregister message received";
                } else {
                    responseMessage = new NoTiFiError(
                            deregisterMessage.getMsgId(), UNKNOWN_CLIENT_ERROR);
                    loggerString += UNKNOWN_CLIENT_ERROR;
                }
            } else {
                /**
                 * If we get an ACK, Error, LocationAddition, or
                 * LocationDeletion, make an error to return. We only handle
                 * register and deregister.
                 */
                responseMessage = new NoTiFiError(receivedMessage.getMsgId(),
                        UNEXPECTED_MESSAGE_TYPE_ERROR
                                + receivedMessage.getCode());
                loggerString += UNEXPECTED_MESSAGE_TYPE_ERROR
                        + receivedMessage.getCode();
            }
        }

        /** Finally log the received info to the log file. */
        logger.info(loggerString);

        /** Try to send the constructed response back to the client. */
        try {
            sendMessage(responseMessage, packet.getAddress(), packet.getPort());
        } catch (IOException e) {
            System.err.println(
                    "Could not send acknowledgment message for deregister "
                            + "message.");
        }
    }

    /**
     * Overridden function for threads. Just constantly loops the handling of
     * clients.
     */
    @Override
    public void run() {
        while (true) {
            handleClients();
        }
    }
}