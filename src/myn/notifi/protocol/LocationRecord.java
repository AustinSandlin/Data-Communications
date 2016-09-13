/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 4
 * Class:       CSI 4321 - Data Communications
 * Date:        27 October 2015
 *
 * This class serializes and deserializes a location's record. It also stores
 * the information.
 *
 ************************************************/

package myn.notifi.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * This class serializes and deserializes a location's record. It also stores
 * the information.
 * 
 * @version 27 October 2015
 * @author Austin Sandlin
 */
public class LocationRecord {

    /** An integer in unsigned format for the userId. */
    private int userId;

    /** The signed double representing the longitude. */
    private double longitude;

    /** The signed double representing the latitude. */
    private double latitude;

    /** The string containing the location name. */
    private String locationName;

    /** The string containing the location description. */
    private String locationDescription;

    private final int BYTE_MAX_VALUE = 255;

    /**
     * This constructor is used to create a LocationRecord with a DataInput
     * object. It properly reads from the binary stream per the protocol
     * specification.
     * 
     * @param in
     *            the DataInput object to read from.
     * @throws IllegalArgumentException
     *             if there was a problem with the
     * @throws IOException
     *             if there was a problem with I/O
     */
    public LocationRecord(DataInput in)
            throws IllegalArgumentException, IOException {
        /** Set the user ID to the unsigned short that was read in. */
        setUserId(in.readUnsignedShort());
        /** Set the longitude and latitude to the 8 little endian bytes read. */
        setLongitude(Double.longBitsToDouble(Long.reverseBytes(in.readLong())));
        setLatitude(Double.longBitsToDouble(Long.reverseBytes(in.readLong())));

        /**
         * Read the number of characters, create an array to hold them and then
         * read them from the DataInput object. Finally, set the location name
         * to the string constructed with the byte array.
         */
        int nameLength = in.readUnsignedByte();
        byte[] nameData = new byte[nameLength];
        in.readFully(nameData);
        setLocationName(new String(nameData));

        /**
         * Read the number of characters, create an array to hold them and then
         * read them from the DataInput object. Finally, set the location
         * description to the string constructed with the byte array.
         */
        int descriptionLength = in.readUnsignedByte();
        byte[] descriptionData = new byte[descriptionLength];
        in.readFully(descriptionData);
        setLocationDescription(new String(descriptionData));
    }

    /**
     * This constructor is used to create the object with passed in values.
     * 
     * @param userId
     *            the user's ID
     * @param longitude
     *            the longitude of the location
     * @param latitude
     *            the latitude of the location
     * @param locationName
     *            the name of the location
     * @param locationDescription
     *            a description of the location
     * @throws IllegalArgumentException
     *             if there was a problem with the arguments passed in
     */
    public LocationRecord(int userId, double longitude, double latitude,
            String locationName, String locationDescription)
                    throws IllegalArgumentException {
        setUserId(userId);
        setLongitude(longitude);
        setLatitude(latitude);
        setLocationName(locationName);
        setLocationDescription(locationDescription);
    }

    /**
     * This function writes the objects information the the DataOutput object,
     * following the protocol specified.
     * 
     * @param out
     *            the DataOutput object to write to.
     * @throws IOException
     *             if there was a problem with I/O
     */
    public void encode(DataOutput out) throws IOException {
        /** Write the user ID to the DataOutput object. */
        out.writeShort(userId);
        /**
         * First, take all the bits in the double and store them into a long.
         * Next, reverse the bytes in the long to convert it into little endian.
         * Then write the long to the DataOutput object.
         */
        out.writeLong(Long.reverseBytes(Double.doubleToLongBits(longitude)));
        out.writeLong(Long.reverseBytes(Double.doubleToLongBits(latitude)));

        /**
         * First, write the byte that gives the length of the string storing the
         * location name. Then write the characters that make up the location
         * name string into the DataOutput object.
         */
        out.writeByte(locationName.length());
        out.write(locationName.getBytes(NoTiFiMessage.ENCODING));

        /**
         * First, write the byte that gives the length of the string storing the
         * location description. Then write the characters that make up the
         * location description string into the DataOutput object.
         */
        out.writeByte(locationDescription.length());
        out.write(locationDescription.getBytes(NoTiFiMessage.ENCODING));
    }

    /**
     * This function is used to check whether two objects are equal. Here, it
     * takes in an object, so we need to check that it's a LocationRecord too.
     */
    @Override
    public boolean equals(Object obj) {
        /**
         * First check that the object is not null and is an instance of
         * LocationRecord.
         */
        if (obj != null && obj instanceof LocationRecord) {
            /**
             * Since it should be an instance of LocationRecord at this point,
             * create a copy of it with cast to LocationRecord.
             */
            LocationRecord temp = (LocationRecord) obj;
            /** Compare all the individual member variables. */
            if (0 == Integer.compareUnsigned(this.userId, temp.getUserId())
                    && 0 == Double.compare(this.longitude, temp.getLongitude())
                    && 0 == Double.compare(this.latitude, temp.getLatitude())
                    && this.locationName.equals(temp.getLocationName())
                    && this.locationDescription
                            .equals(temp.getLocationDescription())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the location's latitude.
     * 
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the location's description.
     * 
     * @return the location description
     */
    public String getLocationDescription() {
        return locationDescription;
    }

    /**
     * Returns the location's name.
     * 
     * @return the location name
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * Returns the location's longitude.
     * 
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns the user ID for the location record.
     * 
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Returns a hash value for the object. This function utilizes all the
     * member variables to produce a unique hash value.
     */
    @Override
    public int hashCode() {
        int prime = 13;
        int hash = 1;

        hash *= prime + userId;
        hash *= prime + Double.hashCode(longitude);
        hash *= prime + Double.hashCode(latitude);
        hash *= prime + locationName.hashCode();
        hash *= prime + locationDescription.hashCode();

        return hash;
    }

    /**
     * This function sets the latitude to a passed in value.
     * 
     * @param latitude
     *            a value to replace latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * This function sets the location's description to a passed in value.
     * 
     * @param locationDescription
     *            a value to replace the location description
     * @throws IllegalArgumentException
     *             if there is a problem with the argument passed in
     */
    public void setLocationDescription(String locationDescription)
            throws IllegalArgumentException {
        if (locationDescription == null) {
            throw new IllegalArgumentException(
                    "Null location description in setter.");
        } else if (locationDescription.length() > BYTE_MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Description length greater than 255 characters.");
        } else {
            this.locationDescription = locationDescription;
        }
    }

    /**
     * This function sets the location's name to a passed in value.
     * 
     * @param locationName
     *            a value to replace the location name
     * @throws IllegalArgumentException
     *             if there is a problem with the argument passed in
     */
    public void setLocationName(String locationName)
            throws IllegalArgumentException {
        if (locationName == null) {
            throw new IllegalArgumentException("Null location name in setter.");
        } else if (locationName.length() > BYTE_MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Name length greater than 255 characters.");
        } else {
            this.locationName = locationName;
        }
    }

    /**
     * This function sets the longitude to a passed in value.
     * 
     * @param longitude
     *            a value to replace the longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * This function sets the user ID to a passed in value. The userId is a
     * 16-bit value so there is a check for that.
     * 
     * @param userId
     *            the value to replace the user ID
     * @throws IllegalArgumentException
     *             if there is a problem with the argument passed in
     */
    public void setUserId(int userId) throws IllegalArgumentException {
        if ((userId & 0xFFFF0000) != 0) {
            throw new IllegalArgumentException(
                    "User ID greater than 2 bytes in setter.");
        } else {
            this.userId = userId;
        }
    }

    /**
     * This function prints a textual representation of the object.
     */
    @Override
    public String toString() {
        return "userID: " + userId + ", longitude: " + longitude
                + ", latitude: " + latitude + ", location name: " + locationName
                + ", location description: " + locationDescription;
    }
}
