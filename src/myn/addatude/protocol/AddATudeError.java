/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 1
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class serializes and deserializes the operation for an error. It also
 * stores all the information for the error.
 *
 ************************************************/

package myn.addatude.protocol;

import java.io.EOFException;

/**
 * This class serializes and deserializes the operation for an error. It also
 * stores all the information for the error.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class AddATudeError extends AddATudeMessage {

    /** A String containing the operation's error message. */
    private String errorMessage;

    /**
     * This function constructs an AddATudeError from a MessageInput stream.
     * 
     * @param in
     *            a MessageInput stream to read from
     * @throws AddATudeException
     *             if there is an issue reading from the MessageInput stream
     * @throws EOFException
     *             if there is an end of line found while reading a string from
     *             MessageInput
     */
    public AddATudeError(MessageInput in)
            throws AddATudeException, EOFException {
        int errorMessageLength = in.readUnsignedInt();
        errorMessage = in.readString(errorMessageLength);
    }

    /**
     * This function constructs an AddATudeError given a mapId and an error
     * message.
     * 
     * @param mapId
     *            an integer that contains the map ID for the message
     * @param errorMessage
     *            a String containing an error message for the message
     * @throws AddATudeException
     *             if there is an issue with mapId or errorMessage
     */
    public AddATudeError(int mapId, String errorMessage)
            throws AddATudeException {
        setMapId(mapId);
        setErrorMessage(errorMessage);
    }

    /**
     * This function serializes the operation specific information and writes it
     * to the MessageOutput stream
     * 
     * @param out
     *            a MessageOutput stream to write to
     */
    public void encodeOperation(MessageOutput out) throws AddATudeException {
        String toWrite = ERROR_OPERATION + ' ' + errorMessage.length() + ' '
                + errorMessage;
        out.write(toWrite);
    }

    /**
     * This function returns the operation.
     * 
     * @see myn.addatude.protocol.AddATudeMessage#getOperation()
     * @return the string containing the operation
     */
    public String getOperation() {
        return ERROR_OPERATION;
    }

    /**
     * This function returns the error message for the message.
     * 
     * @return a String containing the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * This function sets the message's error message to the one passed in.
     * 
     * @param errorMessage
     *            a String containing an error message for the object
     * @throws AddATudeException
     *             if the parameter is negative
     */
    public void setErrorMessage(String errorMessage) throws AddATudeException {
        if (errorMessage == null) {
            throw new AddATudeException(
                    "Null error message for mapId: " + mapId, null);
        }
        this.errorMessage = errorMessage;
    }

    /**
     * This functions provides equivalence testing for the class.
     */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof AddATudeError) {
            AddATudeError temp = (AddATudeError) obj;
            if (this.getErrorMessage().equals(temp.getErrorMessage())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function provides a method to read the objects information easily.
     * 
     * @see myn.addatude.protocol.AddATudeMessage#toString()
     * @return a String containing readable information about the object
     */
    public String toString() {
        return super.toString() + ", errorMessage: " + errorMessage;
    }

}
