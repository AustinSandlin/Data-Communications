/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 0
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class serializes and deserializes a location's record. It also stores
 * the information.
 *
 ************************************************/

package myn.addatude.protocol;

import java.io.EOFException;

/**
 * This class serializes and deserializes a location's record. It also stores
 * the information.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class LocationRecord {

    /**
     * An integer in unsigned format for the userId.
     */
    private int userId;

    /**
     * The string representation of the signed double representing the
     * longitude.
     */
    private String longitude;

    /**
     * The string representation of the signed double representing the latitude.
     */
    private String latitude;

    /**
     * The string containing the location name.
     */
    private String locationName;

    /**
     * The string containing the location description.
     */
    private String locationDescription;

    /**
     * Constructs a newly allocated LocationRecord object that represents the
     * location record specified by the given parameters.
     * 
     * @param userId
     *            the id of the user.
     * @param longitude
     *            the longitude of the location
     * @param latitude
     *            the latitude of the location
     * @param locationName
     *            the name of the location
     * @param locationDescription
     *            a description of the location
     * @throws AddATudeException
     *             thrown if any of the parameters don't match the protocol
     */
    public LocationRecord(int userId, String longitude, String latitude,
            String locationName, String locationDescription)
                    throws AddATudeException {

        setUserId(userId);
        setLongitude(longitude);
        setLatitude(latitude);
        setLocationName(locationName);
        setLocationDescription(locationDescription);
    }

    /**
     * Constructs a newly allocated LocationRecord using a MessageInput object.
     * 
     * @param in
     *            a MessageInput stream containing a Location Record
     * @throws AddATudeException
     *             thrown if any of the members don't match the protocol
     * @throws EOFException
     *             if there is an end of line found while reading a string from
     *             MessageInput
     */
    public LocationRecord(MessageInput in)
            throws AddATudeException, EOFException {

        /**
         * Pull the locationRecord variables from in using the AddATude
         * protocol.
         */
        userId = in.readUnsignedInt();
        longitude = in.readSignedDouble();
        latitude = in.readSignedDouble();

        int nameLength = in.readUnsignedInt();
        locationName = in.readString(nameLength);

        int descriptionLength = in.readUnsignedInt();
        locationDescription = in.readString(descriptionLength);
    }

    /**
     * Modifies a MessageOutput stream, writing the LocationRecord to it.
     * 
     * @param out
     *            a MessageOutput stream with the LocationRecord's information
     *            added to it.
     * @throws AddATudeException
     *             thrown if there is an issue in writing to the stream
     */
    public void encode(MessageOutput out) throws AddATudeException {
        String userID = Integer.toUnsignedString(userId);
        String record = userID + ' ' + longitude + ' ' + latitude + ' '
                + locationName.length() + ' ' + locationName
                + locationDescription.length() + ' ' + locationDescription;
        out.write(record);
    }

    /**
     * Checks equivalence of these two objects based off of the members in the
     * class.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof LocationRecord) {
            LocationRecord temp = (LocationRecord) obj;
            if (0 == Integer.compareUnsigned(this.userId, temp.getUserId())
                    && this.longitude.equals(temp.getLongitude())
                    && this.latitude.equals(temp.getLatitude())
                    && this.locationName.equals(temp.getLocationName())
                    && this.locationDescription
                            .equals(temp.getLocationDescription())) {
                return true;
            }
        }
        return false;
    }

    /**
     * A getter function to return the latitude.
     * 
     * @return the string representation of the signed double representing the
     *         latitude.
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * A getter function to return the location description.
     * 
     * @return the string containing the location description.
     */
    public String getLocationDescription() {
        return locationDescription;
    }

    /**
     * A getter function to return the location name.
     * 
     * @return the string containing the location name.
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * A getter function to return the longitude.
     * 
     * @return the string representation of the signed double representing the
     *         longitude.
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * A getter function to return the user ID.
     * 
     * @return the integer in unsigned format that contains the user id.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * A hashCode function that creates a hash based on the classes members.
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int prime = 13;
        int hash = 1;

        hash = hash * prime + userId;
        hash = hash * prime + longitude.hashCode();
        hash = hash * prime + latitude.hashCode();
        hash = hash * prime + locationName.hashCode();
        hash = hash * prime + locationDescription.hashCode();

        return hash;
    }

    /**
     * Assigns the LocationRecords's latitude to the string representation of an
     * unsigned double, passed as a parameter.
     * 
     * @param latitude
     *            a string representation of the signed double representing the
     *            latitude.
     * @throws AddATudeException
     *             thrown if latitude is null
     */
    public void setLatitude(String latitude) throws AddATudeException {
        if (latitude == null || !MessageInput.isValidSignedDouble(longitude)) {
            throw new AddATudeException("Invalid or null latitude in setter..",
                    null);
        }
        this.latitude = latitude;
    }

    /**
     * Assigns the LocationRecord's locationDescription to the string passed in.
     * 
     * @param locationDescription
     *            a string containing the location description.
     * @throws AddATudeException
     *             thrown if locationDescription is null
     */
    public void setLocationDescription(String locationDescription)
            throws AddATudeException {
        if (locationDescription == null) {
            throw new AddATudeException(
                    "Null location description passed in setter.", null);
        }
        this.locationDescription = locationDescription;
    }

    /**
     * Assigns the LocationRecord's locationName to the string passed in.
     * 
     * @param locationName
     *            a string containing the location name.
     * @throws AddATudeException
     *             thrown if locationName is null
     */
    public void setLocationName(String locationName) throws AddATudeException {
        if (locationName == null) {
            throw new AddATudeException("Null location name passed in setter.",
                    null);
        }
        this.locationName = locationName;
    }

    /**
     * Assigns the LocationRecords's longitude to the string representation of
     * an unsigned double, passed as a parameter.
     * 
     * @param longitude
     *            a string representation of the signed double representing the
     *            latitude.
     * @throws AddATudeException
     *             thrown if longitude is null
     */
    public void setLongitude(String longitude) throws AddATudeException {
        if (longitude == null || !MessageInput.isValidSignedDouble(longitude)) {
            throw new AddATudeException("Invalid or null longitude in setter.",
                    null);
        }
        this.longitude = longitude;
    }

    /**
     * Assigns the LocationRecord's userId to the unsigned integer passed in.
     * 
     * @param userId
     *            an integer in unsigned format containing the user id.
     * @throws AddATudeException
     *             thrown if userId is less than zero
     */
    public void setUserId(int userId) throws AddATudeException {
        if (userId < 0) {
            throw new AddATudeException("userId less than zero in setter.",
                    null);
        }
        this.userId = userId;
    }

    /**
     * Returns a string containing a simple output of all the members.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "userID=" + userId + ", longitude=" + longitude + ", latitude="
                + latitude + ", location name=" + locationName
                + ", location description=" + locationDescription;
    }
}