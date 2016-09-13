/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 3
 * Class:       CSI 4321 - Data Communications
 * Date:        16 October 2015
 *
 * This class serves as a client to a server that uses the AddATude protocol. 
 *
 ************************************************/

package myn.addatude.app;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import mapservice.*;
import myn.addatude.protocol.*;
import myn.notifi.app.NoTiFiServer;

/**
 * This class serves as a server that follows the AddATude protocol.
 * 
 * @version 16 October 2015
 * @author Austin Sandlin
 */
public class AddATudeServer {
    /** Server socket variable. */
    private static ServerSocket serverSocket;
    /** Final variable for the marker file. */
    private static final String MARKER_FILE = "markers.js";
    /** Final variable for the log file. */
    private static final String LOG_FILE = "connections.log";
    /** Logger for the logging of things in the client handler. */
    private static Logger logger = Logger.getLogger(LOG_FILE);

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

    public static void main(String[] args) throws IOException {
        /** Check parameters! */
        if (args.length != 3) {
            System.err.println(
                    "Parameter(s): <Server Port> <Thread Pool Size> <Password File>");
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

        /** Read and set the thread pool size. */
        int threadPoolSize = 0;
        try {
            threadPoolSize = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Thread pool size is not a valid integer.");
            System.exit(0);
        }

        /** Read the user names from the password file. */
        try {
            Scanner passwordFile = new Scanner(new File(args[2]),
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

        /** Set the server socket and allow it to be restarted instantly. */
        try {
            serverSocket = new ServerSocket(serverPort);
            serverSocket.setReuseAddress(true);
        } catch (IOException e) {
            System.err.println("Unable to make server socket.");
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

        /** Create a NoTiFiServer to handle any UDP packets for updates. */
        notifiServer = new NoTiFiServer(serverSocket.getLocalPort(),
                serverSocket.getInetAddress(), logger);

        /**
         * Do the threading here. The threads run a client handler that do all
         * the work...
         */
        for (int i = 0; i < threadPoolSize; ++i) {
            Thread thread = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            /** Block on waiting for the client connection. */
                            Socket clientSocket = serverSocket.accept();
                            ClientHandler.handleClient(clientSocket, logger);
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
            };
            thread.start();
        }
    }
}

/**
 * This class serves as the handlers for the client connections.
 * 
 * @version 16 October 2015
 * @author Austin Sandlin
 */
class ClientHandler implements Runnable {
    /** Socket variable for the client socket. */
    private Socket clientSocket;
    /** Logger variable for the logger. */
    private Logger logger;

    /**
     * Constructor for the client handler class.
     * 
     * @param clientSocket
     *            socket for the client
     * @param logger
     *            logger for the server
     */
    public ClientHandler(Socket clientSocket, Logger logger) {
        this.clientSocket = clientSocket;
        this.logger = logger;
    }

    /**
     * This function does the bulk of the client handling. It calls various
     * functions for help, but the main portion is in here.
     * 
     * @param clientSocket
     *            socket for the client
     * @param logger
     *            logger for the server
     */
    public static void handleClient(Socket clientSocket, Logger logger) {
        /** Set the timeout to 5 seconds for the client. */
        try {
            clientSocket.setSoTimeout(AddATudeServer.TIMEOUT);
        } catch (SocketException e1) {
            System.err.println("Could not set timeout for client socket.");
        }

        /** Instantiate the streams for reading and writing. */
        MessageInput in = null;
        MessageOutput out = null;

        /** Make the socket streams. */
        try {
            in = new MessageInput(clientSocket.getInputStream());
            out = new MessageOutput(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Problem creating socket streams.");
        }

        /** Report that we're handling a client to the console. */
        System.out.println("Handling client " + clientSocket.getInetAddress()
                + "-" + clientSocket.getPort() + " with thread id "
                + Thread.currentThread().getId() + "\n");

        boolean done = false;
        while (!done) {
            /** Reset the messages for each loop through. */
            AddATudeMessage messageIn = null;
            AddATudeMessage messageOut = null;

            /** Attempt to decode from the socket stream. */
            try {
                messageIn = AddATudeMessage.decode(in);
            } catch (AddATudeVersionException e) {
                /**
                 * In the event of a wrong version being found, create an
                 * AddATudeError message to send to the user. Then clear the
                 * buffer from the remaining junk.
                 */
                messageOut = makeError(0, e.getMessage());
                done = purge(in);
            } catch (AddATudeOperationException e) {
                /**
                 * In the event of a wrong operation being found, create an
                 * AddATudeError message to send to the user.Then clear the
                 * buffer from the remaining junk.
                 */
                messageOut = makeError(e.getMapId(), e.getMessage());
                done = purge(in);
            } catch (AddATudeException e) {
                /**
                 * If the client time's out, send the unable to parse message
                 * and disconnect them.
                 */
                messageOut = makeError(0, "Unable to parse message");
                /**
                 * If there is a cause for the AddATudeException and the cause
                 * is an instance of an IOException, that means the
                 * AddATudeException was thrown because of the server disconnect
                 * (SocketTimeoutException). In this case, we need to mark the
                 * loop as over so we can send the last message of
                 * "Unable to parse" to the client. If it doesn't have a cause,
                 * then we need to purge the rest of the input so as to prepare
                 * for the next message.
                 */
                if (e.getCause() != null
                        && e.getCause() instanceof IOException) {
                    done = true;
                } else {
                    done = purge(in);
                }
            } catch (EOFException e) {
                /**
                 * If they disconnect, terminate the while immediately so you
                 * don't send a message.
                 */
                break;
            }

            /**
             * If the stream hasn't closed and we haven't already generated an
             * error message above, then we have more to do.
             */
            if (!done && messageOut == null) {
                /**
                 * If the mapId for the input message exists in our records,
                 * then we can continue.
                 */
                if (existingMapID(messageIn.getMapId())) {
                    switch (messageIn.getOperation()) {
                    case AddATudeMessage.NEW_OPERATION:
                        /**
                         * Since we know it's a new operation at this point,
                         * then we can make a LocationRecord for ease of use.
                         */
                        LocationRecord location = ((AddATudeNewLocation) messageIn)
                                .getLocationRecord();

                        /**
                         * Next, we need to check if the user exists in our map.
                         * If the user exists, we can update the map with the
                         * right information.
                         */
                        if (existingUserID(location.getUserId())) {
                            /**
                             * Update the location map with the user's new
                             * location and make a response to send back.
                             */
                            updateMap(messageIn.getMapId(), location);
                            messageOut = makeResponse(messageIn.getMapId());

                            /**
                             * If the message we wound up writing was a new
                             * location,then we need to log that update to
                             * position message.
                             */
                            location = ((AddATudeNewLocation) messageIn)
                                    .getLocationRecord();
                            logger.info(clientSocket.getInetAddress() + " "
                                    + clientSocket.getPort() + " "
                                    + location.getLocationName() + "-"
                                    + location.getLocationDescription() + " at "
                                    + location.getLongitude() + ","
                                    + location.getLatitude());
                        } else {
                            /**
                             * If the user doesn't exist, report that to the
                             * client.
                             */
                            messageOut = makeError(messageIn.getMapId(),
                                    "No such user: " + location.getUserId());
                        }
                        break;
                    case AddATudeMessage.REQUEST_OPERATION:
                        /** Make a response to send back. */
                        messageOut = makeResponse(messageIn.getMapId());
                        break;
                    default:
                        /**
                         * If the operation isn't a new operation or a request,
                         * then we have an error message to write.
                         */
                        messageOut = makeError(messageIn.getMapId(),
                                "Unexpected message type: "
                                        + messageIn.getOperation());
                    }
                } else {
                    /**
                     * If the mapId doesn't exist, we need to report that to the
                     * client.
                     */
                    messageOut = makeError(messageIn.getMapId(),
                            "No such map: " + messageIn.getMapId());
                }
            }

            /**
             * If we haven't been marked by done previously, then we need to
             * output the message.
             */
            try {
                messageOut.encode(out);

                /**
                 * If the message we wound up writing was an error,then we need
                 * to log that error message.
                 */
                if (AddATudeMessage.ERROR_OPERATION
                        .equals(messageOut.getOperation())) {
                    logger.info(clientSocket.getInetAddress() + " "
                            + clientSocket.getPort() + " "
                            + ((AddATudeError) messageOut).getErrorMessage());
                }
            } catch (AddATudeException e) {
                System.err.println("Failed to encode to the outputstream.");
            }
        }

        /**
         * Upon disconnect, log that the client disconnected and then close the
         * socket.
         */
        try {
            logger.warning("***client terminated" + System.lineSeparator());
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Ran into issue closing the socket.");
        }

    }

    /**
     * This is a helper function for making an error message. This cleans up
     * some code in the main looping function because it limits the number of
     * try-catch blocks.
     * 
     * @param mapId
     *            the map id for the error
     * @param message
     *            the message about the error
     * @return an AddATudeError message to eventually be sent to the client
     */
    private static AddATudeError makeError(int mapId, String message) {
        AddATudeError toReturn = null;
        try {
            toReturn = new AddATudeError(mapId, message);
        } catch (AddATudeException e) {
            System.err.println(
                    "Problem making error message with message: " + message);
        }
        return toReturn;
    }

    /**
     * This is a helper function for making a location response message. This
     * cleans up some code in the main looping function because it limits the
     * number of try-catch blocks needed.
     * 
     * @param mapId
     *            the map id for the error
     * @return an AddATudeLocationResponse message to eventually be sent to the
     *         client
     */
    private static AddATudeLocationResponse makeResponse(int mapId) {
        AddATudeLocationResponse toReturn = null;
        /**
         * As long as the user sent a new location or request operation with
         * valid fields, they will always get a response message back. The
         * insertion of the new location was already done above, so we just need
         * to worry about the response. We assume that we've reached all error
         * messages already, so this won't override any.
         */
        try {
            /**
             * Make a response with the original mapId and the name in the
             * table.
             */
            toReturn = new AddATudeLocationResponse(mapId,
                    AddATudeServer.nameMap.get(mapId));
            /**
             * No easy function for this in AddATudeLocationResponse, so add the
             * location record to the response one by one.
             */
            for (LocationRecord lr : AddATudeServer.locationMap.get(mapId)) {
                toReturn.addLocationRecord(lr);
            }
        } catch (AddATudeException e) {
            System.err.println(e.getMessage());
        }
        return toReturn;
    }

    /**
     * This is a wrapper function for MessageInput's purge function that cleans
     * up the InputStream from a failed message that left stuff in the stream.
     * If there was an exception caught in performing this action, that means it
     * encountered an end of the stream. In that instance, we need to terminate
     * the connection to the client, so we return a boolean representing whether
     * or not the server needs to disconnect.
     * 
     * @param in
     *            the MessageInput class to clean up
     * @return a boolean if there was an exception caught.
     */
    private static boolean purge(MessageInput in) {
        boolean toReturn = false;
        try {
            /**
             * If there was a failure in reading the message, we need to clean
             * everything for the next message to be read properly.
             */
            in.purge(AddATudeMessage.EOLN);
        } catch (EOFException | AddATudeException e1) {
            /**
             * If it failed to purge, the stream is done, client is gone.
             */
            toReturn = true;
        }
        return toReturn;
    }

    /**
     * This function checks if a particular mapId exists in our map of
     * locationRecords
     * 
     * @param mapId
     *            the mapId to check existence
     * @return a boolean representing whether or not the mapId is valid
     */
    private static boolean existingMapID(int mapId) {
        ArrayList<LocationRecord> mapList = AddATudeServer.locationMap
                .get(mapId);
        return !(null == mapList);
    }

    /**
     * This function checks if a particular userId exists in our map of user
     * names
     * 
     * @param userId
     *            the userId to check existence
     * @return a boolean representing whether or not the userId is valid
     */
    private static boolean existingUserID(int userId) {
        String userList = AddATudeServer.usernameMap.get(userId);
        return !(null == userList);
    }

    /**
     * This function updates the map containing user locations for a particular
     * map. This should only be called if there is a valid new location message
     * that needs to update the map. It also updates the location with the
     * Google Maps manager.
     * 
     * @param mapId
     *            the map to insert the new locationRecord to
     * @param locationRecord
     *            the locationRecord that needs inserting/updating
     */
    private static void updateMap(int mapId, LocationRecord locationRecord) {
        /** Get the map list to use for updating. */
        ArrayList<LocationRecord> mapList = AddATudeServer.locationMap
                .get(mapId);

        /**
         * Attempt to rewrite the locationRecord's location name to include the
         * user's user name.
         */
        try {
            locationRecord.setLocationName(
                    AddATudeServer.usernameMap.get(locationRecord.getUserId())
                            + ": " + locationRecord.getLocationName());
        } catch (AddATudeException e) {
            System.err.println(
                    "Trouble doing userId stuff for the location name.");
        }

        /** Attempt to update the user's location in the map. */
        boolean found = false;
        for (int i = 0; i < mapList.size() && !found; ++i) {
            if (mapList.get(i).getUserId() == locationRecord.getUserId()) {
                /**
                 * Notify the client about a duplicate. Since there is a
                 * duplicate, we need to alert the client that a location was
                 * deleted.
                 */
                try {
                    AddATudeServer.notifiServer
                            .notifyDeletion(mapList.get(i).getUserId(),
                                    Double.parseDouble(
                                            mapList.get(i).getLongitude()),
                            Double.parseDouble(mapList.get(i).getLatitude()),
                            mapList.get(i).getLocationName(),
                            mapList.get(i).getLocationDescription());
                } catch (NumberFormatException e) {
                    System.err.println(
                            "LocationRecord's string longitude or latitude "
                                    + "could not be converted to a double.");
                } catch (IOException e) {
                    System.err.println(
                            "Could not send notification of location deletion.");
                }

                AddATudeServer.mapManager
                        .deleteLocation(mapList.get(i).getLocationName());
                mapList.set(i, locationRecord);
                found = true;
            }
        }
        /** If the location wasn't found, add it to the map. */

        if (!found) {
            mapList.add(locationRecord);
        }

        /**
         * Tell the client about an addition to the location. This happens
         * regardless of there being a duplicate, because there is always a
         * location being added. (As long as there isn't an error.)
         */
        try {
            AddATudeServer.notifiServer.notifyAddition(
                    locationRecord.getUserId(),
                    Double.parseDouble(locationRecord.getLongitude()),
                    Double.parseDouble(locationRecord.getLatitude()),
                    locationRecord.getLocationName(),
                    locationRecord.getLocationDescription());
        } catch (NumberFormatException e) {
            System.err.println(
                    "LocationRecord's string longitude or latitude could not "
                            + "be converted to a double.");
        } catch (IOException e) {
            System.err.println(
                    "Could not send notification of location addition.");
        }

        /** Add the location to Google Maps. */
        AddATudeServer.mapManager.addLocation(new Location(
                locationRecord.getLocationName(), locationRecord.getLongitude(),
                locationRecord.getLatitude(),
                locationRecord.getLocationDescription(), Location.Color.GREEN));

    }

    /**
     * This function just overrides the run function to run our particular
     * handleClient function.
     */
    @Override
    public void run() {
        handleClient(clientSocket, logger);
    }
}