/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 1
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class serializes and deserializes the operation for requesting the
 * location of all users.
 *
 ************************************************/

package myn.addatude.protocol;

/**
 * This class serializes and deserializes the operation for requesting the
 * location of all users.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class AddATudeLocationRequest extends AddATudeMessage {

    /**
     * This function constructs an AddATudeLocationRequest from a MessageInput
     * stream. No private member variables to initialize, so it does nothing.
     * 
     * @param in
     *            a MessageInput stream to read from.
     * @throws AddATudeException
     *             if there is problem in reading from the input stream
     */
    public AddATudeLocationRequest(MessageInput in) throws AddATudeException {

    }

    /**
     * This function constructs an AddATudeLocationRequest given a map ID.
     * 
     * @param mapId
     *            an integer containing the message's map ID
     * @throws AddATudeException
     *             if there is a problem with mapId
     */
    public AddATudeLocationRequest(int mapId) throws AddATudeException {
        setMapId(mapId);
    }

    /**
     * This function serializes the operation specific information and writes it
     * to the MessageOutput stream
     * 
     * @param out
     *            a MessageOutput stream to write to
     */
    public void encodeOperation(MessageOutput out) throws AddATudeException {
        String toWrite = REQUEST_OPERATION + ' ';
        out.write(toWrite);
    }

    /**
     * This function returns the operation.
     * 
     * @see myn.addatude.protocol.AddATudeMessage#getOperation()
     * @return the string containing the operation
     */
    public String getOperation() {
        return REQUEST_OPERATION;
    }

    /**
     * This functions provides equivalence testing for the class.
     */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof AddATudeLocationRequest) {
            return true;
        }
        return false;
    }

    /**
     * This function provides a method to read the objects information easily.
     * 
     * @see myn.addatude.protocol.AddATudeMessage#toString()
     * @return String containing readable information about the object
     */
    public String toString() {
        return super.toString();
    }
}
