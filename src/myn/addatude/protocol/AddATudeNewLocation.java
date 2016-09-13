/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 1
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class serializes and deserializes the operation for making a new
 * LocationRecord. It also stores the information.
 *
 ************************************************/

package myn.addatude.protocol;

import java.io.EOFException;

/**
 * This class serializes and deserializes the operation for making a new
 * LocationRecord. It also stores the information.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class AddATudeNewLocation extends AddATudeMessage {

    /** A LocationRecord object for the making of a new LocationRecord. */
    private LocationRecord location;

    /**
     * This function constructs an AddATudeNewLocation by pulling from the
     * information from the MessageInput stream.
     * 
     * @param in
     *            a MessageInput stream to be read from
     * @throws AddATudeException
     *             if there is a problem deserializing the LocationRecord.
     * @throws EOFException
     *             if there is an end of line found while reading a string from
     *             MessageInput
     */
    public AddATudeNewLocation(MessageInput in)
            throws AddATudeException, EOFException {
        location = new LocationRecord(in);
    }

    /**
     * This function constructs an AddATudeNewLocation, given a mapId and
     * LocationRecord.
     * 
     * @param mapId
     *            a mapId for the operation
     * @param location
     *            a location to store for the operation
     * @throws AddATudeException
     *             if there is a problem with mapId or location
     */
    public AddATudeNewLocation(int mapId, LocationRecord location)
            throws AddATudeException {
        setMapId(mapId);
        setLocationRecord(location);
    }

    /**
     * This function serializes the operation specific information and writes it
     * to the MessageOutput stream
     * 
     * @param out
     *            a MessageOutput stream to write to
     */
    public void encodeOperation(MessageOutput out) throws AddATudeException {
        String toWrite = NEW_OPERATION + ' ';
        out.write(toWrite);

        location.encode(out);
    }

    /**
     * This function sets the location record to the one passed in.
     * 
     * @param location
     *            a function to set the member variable 'location' to
     * @throws AddATudeException
     *             if the LocationRecord is null.
     */
    public final void setLocationRecord(LocationRecord location)
            throws AddATudeException {
        if (location == null) {
            throw new AddATudeException(
                    "Null LocationRecord in AddATudeNewLocation setter.", null);
        }
        this.location = location;
    }

    /**
     * This function returns the operation.
     * 
     * @see myn.addatude.protocol.AddATudeMessage#getOperation()
     * @return the string containing the operation
     */
    public String getOperation() {
        return NEW_OPERATION;

    }

    /**
     * This function returns the LocationRecord object 'location'.
     * 
     * @return the 'location' variable
     */
    public final LocationRecord getLocationRecord() {
        return location;

    }

    /**
     * This functions provides equivalence testing for the class.
     */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof AddATudeNewLocation) {
            AddATudeNewLocation temp = (AddATudeNewLocation) obj;
            if (this.mapId == temp.getMapId()
                    && location.equals(temp.getLocationRecord())) {
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
        return super.toString() + ", location record: " + location.toString();

    }

}
