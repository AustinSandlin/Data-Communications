/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 4
 * Class:       CSI 4321 - Data Communications
 * Date:        27 October 2015
 *
 * This class serializes and deserializes a location deletion message. It also
 * stores the location record.
 *
 ************************************************/

package myn.notifi.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class serializes and deserializes a location deletion message. It also
 * stores the location record.
 * 
 * @version 27 October 2015
 * @author Austin Sandlin
 */
public class NoTiFiLocationDeletion extends NoTiFiMessage {

    /** Final variable for the LocationAddition code. */
    public static final int CODE = 2;

    /** The location record for the class. */
    LocationRecord locationRecord;

    /**
     * Constructor that takes a stream and pulls the data from that.
     * 
     * @param in
     *            the stream to read from
     * @throws IllegalArgumentException
     *             if there is a problem with the stream
     * @throws IOException
     *             if there was a problem during I/O
     */
    public NoTiFiLocationDeletion(DataInputStream in)
            throws IllegalArgumentException, IOException {
        super(in);
        locationRecord = new LocationRecord(in);
    }

    public NoTiFiLocationDeletion(int msgId, LocationRecord locationRecord)
            throws IllegalArgumentException {
        super(msgId);
        setLocation(locationRecord);
    }

    /**
     * This function overrides NoTiFiMessage's encode function to encode the
     * header and the message's data. This class has no data for its message, so
     * it should return an empty byte array.
     */
    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream toReturn = new ByteArrayOutputStream();
        toReturn.write(super.encode());
        locationRecord.encode(new DataOutputStream(toReturn));

        return toReturn.toByteArray();
    }

    /**
     * This function is an implementation of NoTiFiMessage's abstract function.
     * It simply needs to return the code for a location deletion message.
     */
    public int getCode() {
        return CODE;
    }

    /**
     * Returns the location record.
     * 
     * @return the locationRecord
     */
    public LocationRecord getLocationRecord() {
        return locationRecord;
    }

    /**
     * Sets the location of the location.
     * 
     * @param locationRecord
     *            the location record
     * @throws IllegalArgumentException
     *             if there is a problem with the parameters
     */
    public void setLocation(LocationRecord locationRecord)
            throws IllegalArgumentException {
        if (locationRecord == null) {
            throw new IllegalArgumentException(
                    "Null location record in setter.");
        } else {
            this.locationRecord = locationRecord;
        }
    }

    /**
     * This function is used to check whether two objects are equal. Here, it
     * takes in an object, so we need to check that it's a NoTiFiACK too.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof NoTiFiLocationDeletion) {
            NoTiFiLocationDeletion temp = (NoTiFiLocationDeletion) obj;
            if (super.equals(temp)
                    && locationRecord.equals(temp.getLocationRecord())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function prints a textual representation of the object.
     */
    @Override
    public String toString() {
        return super.toString() + ", location record: "
                + locationRecord.toString();
    }
}
