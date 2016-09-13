/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 4
 * Class:       CSI 4321 - Data Communications
 * Date:        27 October 2015
 *
 * This class serializes and deserializes an ACK message.
 *
 ************************************************/

package myn.notifi.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * This class provides serialization and deserialization for the NoTiFiAck
 * class.
 * 
 * @version 27 October 2015
 * @author Austin Sandlin
 */
public class NoTiFiACK extends NoTiFiMessage {

    public static final int CODE = 5;

    /**
     * This constructor creates a NoTiFiAck object from a DataInputStream. An
     * ACK message has no data, so it only needs to make a call to super's
     * constructor to take care of the header.
     * 
     * @param in
     *            the DataInputStream to read from
     * @throws IllegalArgumentException
     *             if there is a problem with the variable passed in
     * @throws IOException
     *             if there was a problem with I/O
     */
    public NoTiFiACK(DataInputStream in)
            throws IllegalArgumentException, IOException {
        super(in);
    }

    /**
     * This constructor has nothing additional to offer beyond the message ID,
     * so it just calls super to take care of it for it.
     * 
     * @param msgId
     *            the value for the message ID
     * @throws IllegalArgumentException
     *             if there is a problem with the variable passed in
     */
    public NoTiFiACK(int msgId) throws IllegalArgumentException {
        super(msgId);
    }

    /**
     * This function overrides NoTiFiMessage's encode function to encode the
     * header and the message's data. This class has no data for its message, so
     * it should return an empty byte array. We don't just return the super's
     * byte array here, even though that's all that's required, because if the
     * protocol changes, we want the code prepared to add more.
     */
    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream toReturn = new ByteArrayOutputStream();
        toReturn.write(super.encode());

        /**
         * If ACK is given data to pass, call a write to toReturn here with the
         * byte array of the data.
         */

        return toReturn.toByteArray();
    }

    /**
     * This function is an implementation of NoTiFiMessage's abstract function.
     * For this class, it simply needs to return the code for an ACK.
     */
    public int getCode() {
        return CODE;
    }

    /**
     * Generates a hash value based off the member variables. Since ACK has
     * none, it just does the hash code for the base class.
     */
    @Override
    public int hashCode() {
        int prime = 13;
        int hash = 1;

        hash *= prime + super.hashCode();

        return hash;
    }

    /**
     * This function is used to check whether two objects are equal. Here, it
     * takes in an object, so we need to check that it's a NoTiFiACK too.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof NoTiFiACK) {
            NoTiFiACK temp = (NoTiFiACK) obj;
            if (super.equals(temp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function prints a textual representation of the object. ACK has no
     * data in its message, so it doesn't print anything other than the header.
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
