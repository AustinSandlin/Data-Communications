/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 1
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class serializes and deserializes the message for all operations. It
 * also stores the basic information.
 *
 ************************************************/

package myn.addatude.protocol;

import java.io.EOFException;
import java.io.IOException;

/**
 * This class serializes and deserializes the message for all operations. It
 * also stores the basic information.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public abstract class AddATudeMessage {

    /** A String containing the AddATude version for the header. */
    public final static String VERSION = "ADDATUDEv1";
    /** A String containing the EOLN character sequence. */
    public final static String EOLN = "\r\n";
    /** An integer to store the map id for each message. */
    protected int mapId;

    /** A String containing the keyword for a new location operation. */
    public static final String NEW_OPERATION = "NEW";

    /** A String containing the keyword for an error operation. */
    public static final String ERROR_OPERATION = "ERROR";

    /** A String containing the keyword for a request operation. */
    public static final String REQUEST_OPERATION = "ALL";

    /** A String containing the keyword for a response operation. */
    public static final String RESPONSE_OPERATION = "RESPONSE";

    /**
     * This function deserializes a message, given a MessageInput stream. First
     * it reads the header and checks that the version is correct. It then
     * stores the mapId to pass to the operation object later. Then it reads the
     * operation to determine what object to create. Once it obtains the
     * operation, it creates the operation object and stores it in the base
     * AddATudeMessage object to return later. Then it sets the object's mapId.
     * It then reads the end of message sequence and returns the
     * AddATudeMessage. This function follows the Factory pattern.
     * 
     * @param in
     *            A MessageInput stream to read from.
     * @return A subclass AddATudeMessage object.
     * @throws AddATudeException
     *             if there is an issue in deserialization
     * @throws EOFException
     *             thrown if the end of the file is read before finishing
     *             reading the message
     */
    public static AddATudeMessage decode(MessageInput in)
            throws AddATudeException, EOFException {

        /**
         * Read the version from the header and check if it's the expected one.
         */
        String version = in.readString();
        if (!VERSION.equals(version)) {
            throw new AddATudeVersionException(
                    "Unexpected version: " + version, null);
        }

        /** Read the map ID in the header. */
        int tempMapId = in.readUnsignedInt();

        /** Read the operation to determine what object to make. */
        String operation = in.readString();

        /**
         * Create a new AddATudeMessage object based on the operation read
         * above.
         */
        AddATudeMessage toReturn = null;

        switch (operation) {
        case NEW_OPERATION:
            toReturn = new AddATudeNewLocation(in);
            break;
        case REQUEST_OPERATION:
            toReturn = new AddATudeLocationRequest(in);
            break;
        case ERROR_OPERATION:
            toReturn = new AddATudeError(in);
            break;
        case RESPONSE_OPERATION:
            toReturn = new AddATudeLocationResponse(in);
            break;
        default:
            throw new AddATudeOperationException(
                    "Unknown operation: " + operation, null,
                    tempMapId);
        }

        /** Set the map ID that was read earlier. */
        toReturn.setMapId(tempMapId);

        /** Read the end of line character sequence. */
        String eoln = "";
        try {
            eoln += (char) in.read();
            eoln += (char) in.read();
        } catch (IOException e) {
            throw new AddATudeException("Failed to read eoln.", null);
        }
        if (!EOLN.equals(eoln)) {
            throw new EOFException("Null or improper eoln format");
        }

        /** Return the AddATudeMessage class. */
        return toReturn;
    }

    /**
     * This function serializes a message. It first writes the header to the
     * MessageOutput stream, then calls the abstract encodeOperation() function
     * that's specific to each operation. The operation then serializes it's own
     * information. After that one's done, this function then writes the EOLN
     * sequence to the output stream.
     * 
     * @param out
     *            a MessageOutput stream to write to.
     * @throws AddATudeException
     *             if there is a problem in writing to the output stream.
     */
    public void encode(MessageOutput out) throws AddATudeException {
        String toWrite = VERSION + ' ' + mapId + ' ';
        out.write(toWrite);

        encodeOperation(out);

        toWrite = EOLN;
        out.write(toWrite);
    }

    /**
     * This function returns the map ID for the message.
     * 
     * @return the mapId variable
     */
    public int getMapId() {
        return mapId;
    }

    /**
     * This function is a helper function to serialize the operation specific
     * information. This is called by the encode() function.
     * 
     * @param out
     *            a MessageOutput stream to write to.
     * @throws AddATudeException
     *             if there is a problem in writing to the output stream.
     */
    public abstract void encodeOperation(MessageOutput out)
            throws AddATudeException;

    /**
     * This function returns the operation that this message is performing.
     * 
     * @return a String that contains the operation
     */
    public abstract String getOperation();

    /**
     * This function sets the mapId to the passed in parameter.
     * 
     * @param mapId
     *            an integer to set the mapId member variable to
     * @throws AddATudeException
     *             if the mapId is signed.
     */
    public final void setMapId(int mapId) throws AddATudeException {
        if (mapId < 0) {
            throw new AddATudeException("Signed mapId in setter.", null);
        }
        this.mapId = mapId;
    }

    /**
     * This function returns the hash of the object.
     */
    public int hashCode() {
        int prime = 13;
        int hash = 1;

        hash = hash * prime + mapId;
        hash = hash * prime + VERSION.hashCode();
        hash = hash * prime + EOLN.hashCode();

        return hash;
    }

    /**
     * This function returns a String that contains readable class information.
     * 
     * @see java.lang.Object#toString()
     * @return a string containing readable class information
     */
    public String toString() {
        return "version: " + VERSION + ", mapId: " + mapId + ", operation: "
                + getOperation();
    }
}
