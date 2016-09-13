/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 1
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class serializes and deserializes the operation for responding to a
 * location search request. It also stores all the information for the response.
 *
 ************************************************/

package myn.addatude.protocol;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class serializes and deserializes the operation for responding to a
 * location search request. It also stores all the information for the response.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class AddATudeLocationResponse extends AddATudeMessage {

    /** A String containing the response's map name. */
    private String mapName;

    /** A list for storing LocationRecords */
    List<LocationRecord> locationRecordList = new ArrayList<LocationRecord>();

    /**
     * This function constructs an AddATudeLocationResponse from a MessageInput
     * stream.
     * 
     * @param in
     *            a MessageInput stream to read from
     * @throws AddATudeException
     *             if there is an issue in reading from the stream.
     * @throws EOFException
     *             if there is an end of line found while reading a string from
     *             MessageInput
     */
    public AddATudeLocationResponse(MessageInput in)
            throws AddATudeException, EOFException {
        int mapNameLength = in.readUnsignedInt();
        mapName = in.readString(mapNameLength);

        int locationRecordListSize = in.readUnsignedInt();
        for (int i = 0; i < locationRecordListSize; ++i) {
            LocationRecord temp = new LocationRecord(in);
            addLocationRecord(temp);
        }
    }

    /**
     * This function constructs an AddATudeLocationResponse given a map ID and
     * map name.
     * 
     * @param mapId
     *            an integer containing the message's map ID
     * @param mapName
     *            a string containing the response's map name
     * @throws AddATudeException
     *             if mapId is less than zero or if mapName is null
     */
    public AddATudeLocationResponse(int mapId, String mapName)
            throws AddATudeException {
        setMapId(mapId);
        setMapName(mapName);
    }

    /**
     * This function serializes the operation specific information and writes it
     * to the MessageOutput stream
     * 
     * @param out
     *            a MessageOutput stream to write to
     */
    public void encodeOperation(MessageOutput out) throws AddATudeException {
        String toWrite = RESPONSE_OPERATION + ' ' + mapName.length() + ' '
                + mapName + locationRecordList.size() + ' ';
        out.write(toWrite);

        for (int i = 0; i < locationRecordList.size(); ++i) {
            locationRecordList.get(i).encode(out);
        }
    }

    /**
     * This function returns the list of LocationRecords for the response.
     * 
     * @return a list of LocationRecords
     */
    public List<LocationRecord> getLocationRecordList() {
        return locationRecordList;
    }

    /**
     * This function adds a LocationRecord passed in to the list of
     * LocationRecords.
     * 
     * @param location
     *            a LocationRecord to add to the list of LocationRecords
     */
    public void addLocationRecord(LocationRecord location) {
        locationRecordList.add(location);
    }

    /**
     * This function returns the operation.
     * 
     * @see myn.addatude.protocol.AddATudeMessage#getOperation()
     * @return the string containing the operation
     */
    public String getOperation() {
        return RESPONSE_OPERATION;
    }

    /**
     * This function returns the map name for the response.
     * 
     * @return the mapName variable
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * This function sets the map name to the String passed in.
     * 
     * @param mapName
     *            a String containing a new map name to set the object's map
     *            name to
     * @throws AddATudeException
     *             if the map name is null
     */
    public final void setMapName(String mapName) throws AddATudeException {
        if (mapName == null) {
            throw new AddATudeException("Null mapName for mapId: " + mapId,
                    null);
        }
        this.mapName = mapName;
    }

    /**
     * This functions provides equivalence testing for the class.
     */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof AddATudeLocationResponse) {
            AddATudeLocationResponse temp = (AddATudeLocationResponse) obj;
            if (this.mapId == temp.getMapId()
                    && mapName.equals(temp.getMapName())) {
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
        String toReturn = super.toString() + ", mapName: " + mapName
                + ", locationRecordList:\r\n";
        for (int i = 0; i < locationRecordList.size(); ++i) {
            toReturn += locationRecordList.get(i).toString();
        }
        return toReturn;
    }

}
