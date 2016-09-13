/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 4
 * Class:       CSI 4321 - Data Communications
 * Date:        27 October 2015
 *
 * This class serializes and deserializes a location addition message. It also
 * stores the location record.
 *
 ************************************************/

package myn.notifi.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class serializes and deserializes a location addition message. It also
 * stores the location record.
 * 
 * @version 27 October 2015
 * @author Austin Sandlin
 */
public class NoTiFiLocationAddition extends NoTiFiMessage {

    /** Final variable for the LocationAddition code. */
    public static final int CODE = 1;

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
    public NoTiFiLocationAddition(DataInputStream in)
            throws IllegalArgumentException, IOException {
        super(in);
        locationRecord = new LocationRecord(in);
    }

    /**
     * Constructor that takes in parameters.
     * 
     * @param msgId
     *            the message id
     * @param locationRecord
     *            the location record
     * @throws IllegalArgumentException
     *             if there is a problem with the parameters
     */
    public NoTiFiLocationAddition(int msgId, LocationRecord locationRecord)
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
     * It simply needs to return the code for a location addition message.
     */
    public int getCode() {
        return CODE;
    }

    /**
     * Returns the location record.
     * 
     * @return the location record
     */
    public LocationRecord getLocationRecord() {
        return locationRecord;
    }

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
     * This function provides the hash value for the object.
     */
    @Override
    public int hashCode() {
        int prime = 13;
        int hash = 1;

        prime *= prime + super.hashCode();
        prime *= prime + locationRecord.hashCode();

        return hash;
    }

    /**
     * This function is used to check whether two objects are equal. Here, it
     * takes in an object, so we need to check that it's a
     * NoTiFiLocationAddition too.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof NoTiFiLocationAddition) {
            NoTiFiLocationAddition temp = (NoTiFiLocationAddition) obj;
            if (super.equals(temp) && locationRecord.equals(locationRecord)) {
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
