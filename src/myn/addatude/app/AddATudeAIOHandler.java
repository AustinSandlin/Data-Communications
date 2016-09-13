/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 7
 * Class:       CSI 4321 - Data Communications
 * Date:        1 December 2015
 *
 * This class handles the client's AIO stuff.
 *
 ************************************************/

package myn.addatude.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.logging.Logger;

import mapservice.Location;
import myn.addatude.protocol.AddATudeError;
import myn.addatude.protocol.AddATudeException;
import myn.addatude.protocol.AddATudeLocationResponse;
import myn.addatude.protocol.AddATudeMessage;
import myn.addatude.protocol.AddATudeNewLocation;
import myn.addatude.protocol.AddATudeOperationException;
import myn.addatude.protocol.AddATudeVersionException;
import myn.addatude.protocol.LocationRecord;
import myn.addatude.protocol.MessageInput;
import myn.addatude.protocol.MessageOutput;

/**
 * A handler for various asynchronous I/O operations
 */
public class AddATudeAIOHandler {

    /**
     * Handle read to given buffer and return any message in response to read
     * bytes.
     * 
     * @param messageBuff
     *            read bytes
     * @param logger
     *            logger for recording client things
     * @param socketAddress
     *            used for logging things
     * @return message in response to read bytes (null if none)
     */

    public byte[] handleMessage(byte[] messageBuff, Logger logger,
            InetSocketAddress socketAddress) {
        ByteArrayOutputStream toReturn = new ByteArrayOutputStream();

        MessageInput in = new MessageInput(
                new ByteArrayInputStream(messageBuff));
        MessageOutput out = new MessageOutput(toReturn);

        /** Reset the messages for each loop through. */
        AddATudeMessage messageIn = null;
        AddATudeMessage messageOut = null;

        /** Attempt to decode from the socket stream. */
        try {
            messageIn = AddATudeMessage.decode(in);
        } catch (AddATudeVersionException e) {
            /**
             * In the event of a wrong version being found, create an
             * AddATudeError message to send to the user. Then clear the buffer
             * from the remaining junk.
             */
            messageOut = makeError(0, e.getMessage());
        } catch (AddATudeOperationException e) {
            /**
             * In the event of a wrong operation being found, create an
             * AddATudeError message to send to the user.Then clear the buffer
             * from the remaining junk.
             */
            messageOut = makeError(e.getMapId(), e.getMessage());
        } catch (AddATudeException | EOFException e) {
            /**
             * If the client time's out, send the unable to parse message and
             * disconnect them.
             */
            messageOut = makeError(0, "Unable to parse message");
        }

        /**
         * If the stream hasn't closed and we haven't already generated an error
         * message above, then we have more to do.
         */
        if (messageOut == null) {
            /**
             * If the mapId for the input message exists in our records, then we
             * can continue.
             */
            if (existingMapID(messageIn.getMapId())) {
                switch (messageIn.getOperation()) {
                case AddATudeMessage.NEW_OPERATION:
                    /**
                     * Since we know it's a new operation at this point, then we
                     * can make a LocationRecord for ease of use.
                     */
                    LocationRecord location = ((AddATudeNewLocation) messageIn)
                            .getLocationRecord();

                    /**
                     * Next, we need to check if the user exists in our map. If
                     * the user exists, we can update the map with the right
                     * information.
                     */
                    if (existingUserID(location.getUserId())) {
                        /**
                         * Update the location map with the user's new location
                         * and make a response to send back.
                         */
                        updateMap(messageIn.getMapId(), location);
                        messageOut = makeResponse(messageIn.getMapId());

                        /**
                         * If the message we wound up writing was a new
                         * location,then we need to log that update to position
                         * message.
                         */
                        location = ((AddATudeNewLocation) messageIn)
                                .getLocationRecord();
                        logger.info(socketAddress.getAddress() + " "
                                + socketAddress.getPort() + " "
                                + location.getLocationName() + "-"
                                + location.getLocationDescription() + " at "
                                + location.getLongitude() + ","
                                + location.getLatitude());
                    } else {
                        /**
                         * If the user doesn't exist, report that to the client.
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
                     * If the operation isn't a new operation or a request, then
                     * we have an error message to write.
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
         * If we haven't been marked by done previously, then we need to output
         * the message.
         */
        try {
            messageOut.encode(out);

            /**
             * If the message we wound up writing was an error,then we need to
             * log that error message.
             */
            if (AddATudeMessage.ERROR_OPERATION
                    .equals(messageOut.getOperation())) {
                logger.info(socketAddress.getAddress() + " "
                        + socketAddress.getPort() + " "
                        + ((AddATudeError) messageOut).getErrorMessage());
            }
        } catch (AddATudeException e) {
            System.err.println("Failed to encode to the outputstream.");
        }

        return toReturn.toByteArray();
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
                    AddATudeServerAIO.nameMap.get(mapId));
            /**
             * No easy function for this in AddATudeLocationResponse, so add the
             * location record to the response one by one.
             */
            for (LocationRecord lr : AddATudeServerAIO.locationMap.get(mapId)) {
                toReturn.addLocationRecord(lr);
            }
        } catch (AddATudeException e) {
            System.err.println(e.getMessage());
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
        ArrayList<LocationRecord> mapList = AddATudeServerAIO.locationMap
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
        String userList = AddATudeServerAIO.usernameMap.get(userId);
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
        ArrayList<LocationRecord> mapList = AddATudeServerAIO.locationMap
                .get(mapId);

        /**
         * Attempt to rewrite the locationRecord's location name to include the
         * user's user name.
         */
        try {
            locationRecord.setLocationName(AddATudeServerAIO.usernameMap
                    .get(locationRecord.getUserId()) + ": "
                    + locationRecord.getLocationName());
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
                    AddATudeServerAIO.notifiServer
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

                AddATudeServerAIO.mapManager
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
            AddATudeServerAIO.notifiServer.notifyAddition(
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
        AddATudeServerAIO.mapManager.addLocation(new Location(
                locationRecord.getLocationName(), locationRecord.getLongitude(),
                locationRecord.getLatitude(),
                locationRecord.getLocationDescription(), Location.Color.GREEN));

    }
}
