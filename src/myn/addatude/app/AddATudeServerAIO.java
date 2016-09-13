/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 7
 * Class:       CSI 4321 - Data Communications
 * Date:        1 December 2015
 *
 * This class serves as a server for the AddATude protocol that does AIO.
 *
 ************************************************/

package myn.addatude.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import mapservice.*;
import myn.addatude.protocol.*;
import myn.notifi.app.NoTiFiServer;

/**
 * This class serves as a server that follows the AddATude protocol.
 * 
 * @version 1 December 2015
 * @author Austin Sandlin
 */
public class AddATudeServerAIO {
    /** Server socket variable. */
    private static AsynchronousServerSocketChannel serverChannel;
    /** Final variable for the marker file. */
    private static final String MARKER_FILE = "markers.js";
    /** Final variable for the log file. */
    private static final String LOG_FILE = "connections.log";
    /** Logger for the logging of things in the client handler. */
    public static Logger logger = Logger.getLogger(LOG_FILE);

    public static NoTiFiServer notifiServer;

    /** Map manager for google. */
    public static MapManager mapManager;

    /** Final variable for server timeout. */
    public static final int TIMEOUT = 50000;

    /** Maps for linking information and storing it for the client handler. */
    public static SortedMap<Integer, String> nameMap = Collections
            .synchronizedSortedMap(new TreeMap<Integer, String>());

    public static SortedMap<Integer, ArrayList<LocationRecord>> locationMap = Collections
            .synchronizedSortedMap(
                    new TreeMap<Integer, ArrayList<LocationRecord>>());

    public static SortedMap<Integer, String> usernameMap = Collections
            .synchronizedSortedMap(new TreeMap<Integer, String>());

    /**
     * Main function for the server.
     * 
     * @param args
     *            input arguments
     * @throws IOException
     *             if something bad happens
     */
    public static void main(String[] args) throws IOException {
        /** Check parameters! */
        if (args.length != 2) {
            System.err.println("Parameter(s): <Server Port> <Password File>");
            return;
        }

        /** Read and set the server port. */
        int serverPort = 0;
        try {
            serverPort = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Port is not a valid integer.");
            System.exit(0);
        }

        /** Read the user names from the password file. */
        try {
            Scanner passwordFile = new Scanner(new File(args[1]),
                    MessageOutput.ENCODING);
            passwordFile.useDelimiter(":");
            while (passwordFile.hasNextInt()) {
                /** Read in the userId and then the user name. */
                usernameMap.put(passwordFile.nextInt(), passwordFile.next());
                /** Skip password for user. */
                passwordFile.nextLine();
            }
            passwordFile.close();
        } catch (FileNotFoundException e) {
            System.err.println("Password file not found.");
            System.exit(0);
        } catch (NoSuchElementException e) {
            System.err.println("Expected something, but found nothing.");
            System.exit(0);
        }

        /** Instantiate the google map stuff with the proper file. */
        mapManager = new MemoryMapManager();
        mapManager.register(new GoogleMapMaker(MARKER_FILE, mapManager));

        /** Setup the logger handlers for writing to the file. */
        FileHandler fileHandler = new FileHandler(LOG_FILE);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);

        /** Hard coded mapId and class name, since we weren't given any... */
        nameMap.put(345, "Class Map");
        /** Make a blank ArrayList for the map with mapId and locations. */
        locationMap.put(345, new ArrayList<LocationRecord>());

        /** Set the server socket and allow it to be restarted instantly. */
        try {
            serverChannel = AsynchronousServerSocketChannel.open()
                    .bind(new InetSocketAddress(serverPort));
            serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        } catch (IOException e) {
            System.err.println("Unable to make server socket.");
            System.exit(0);
        }

        /** Create a NoTiFiServer to handle any UDP packets for updates. */
        notifiServer = new NoTiFiServer(serverPort,
                ((InetSocketAddress) serverChannel.getLocalAddress())
                        .getAddress(),
                logger);

        /** Accept any incoming client connection. */
        serverChannel.accept(null,
                makeAcceptCompletionHandler(serverChannel, logger));

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            /** Do nothing here. */
        }
    }

    /**
     * Completion handler for accepting a connection
     * 
     * @param serverChannel
     *            the serversocketchannel to call accept on
     * @param logger
     *            to log important stuff
     * @return the completion handler
     */
    public static CompletionHandler<AsynchronousSocketChannel, Void> makeAcceptCompletionHandler(
            final AsynchronousServerSocketChannel serverChannel,
            final Logger logger) {
        return new CompletionHandler<AsynchronousSocketChannel, Void>() {

            @Override
            public void completed(AsynchronousSocketChannel clientChannel,
                    Void attachment) {
                serverChannel.accept(null, this);
                AddATudeAIODispatcher aioDispatcher = new AddATudeAIODispatcher(
                        new AddATudeAIOHandler(), logger);
                try {
                    logger.log(Level.INFO, "Handling client "
                            + clientChannel.getRemoteAddress());
                } catch (IOException e) {
                    System.err.println("Failed to log handling of client.");
                }
                aioDispatcher.handleAccept(clientChannel);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                logger.log(Level.WARNING, "Failed on accept", exc);
            }
        };
    }
}