/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 4
 * Class:       CSI 4321 - Data Communications
 * Date:        27 October 2015
 *
 * This class provides a base class for the NoTiFi messages. It also provides
 * basic serialization and deserialization for the header. It stores the
 * header's information, as well as a few static final variables for reference
 * purposes in the subclasses.
 ************************************************/

package myn.notifi.protocol;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public abstract class NoTiFiMessage {

    /** An int to store the version. Should always be 3. */
    final int VERSION = 3;
    /** A string to store the encoding. */
    public static final String ENCODING = "ASCII";
    /** An int to store the message id. */
    int msgId;

    /**
     * This function constructs the base NoTiFiMessage class. It really just
     * pulls the header information, since every message will have that. It
     * leaves the decoding of the individual messages to the constructors of
     * those sub classes.
     * 
     * @param in
     *            the input stream to read from
     * @throws IllegalArgumentException
     *             if there is a problem with the input stream or the member
     *             variables
     * @throws IOException
     *             if there is a problem with the I/O
     */
    public NoTiFiMessage(DataInputStream in)
            throws IllegalArgumentException, IOException {
        /** Make sure the stream isn't null. */
        if (in == null) {
            throw new IllegalArgumentException(
                    "DataInputStream is null in Message constructor.");
        }

        /** Read in the bytes for the header. */
        int codeByte = in.readUnsignedByte();
        int msgIdByte = in.readUnsignedByte();

        /** Grab the code, then shift it over and grab the version. */
        int codeCode = (codeByte & 0x0F);
        int versionCode = (codeByte >>> 4) & 0x0F;

        /** Check if the code is incorrect. */
        if (getCode() != codeCode) {
            throw new IllegalArgumentException("Wrong code.");
        }

        /** Check if the version is correct. */
        if (VERSION != versionCode) {
            throw new IllegalArgumentException("Wrong version.");
        }

        /** Set the message. */
        setMsgId(msgIdByte);
    }

    /**
     * This constructor creates a NoTiFiMessage with just the message ID as a
     * parameter.
     * 
     * @param msgId
     *            a message ID
     * @throws IllegalArgumentException
     *             if there is a problem with the parameter passed in
     */
    public NoTiFiMessage(int msgId) throws IllegalArgumentException {
        setMsgId(msgId);
    }

    /**
     * This function returns a NoTiFiMessage based off the byte[] array passed
     * in. In the construction of this message, I'm assuming this byte array is
     * the entire packet and only one packet. To get what type of message it is,
     * I'm just pulling the code from the first byte. With that code, I use a
     * switch statement to create the proper message type and return that.
     * 
     * @param pkt
     *            a byte array containing the data message
     * @return a NoTiFiMessage sub class
     * @throws IllegalArgumentException
     *             if there is an issue with the parameter passed in
     * @throws IOException
     *             if there was a problem during I/O
     */
    public static NoTiFiMessage decode(byte[] pkt)
            throws IllegalArgumentException, IOException {

        /** The message to return in this factory function. */
        NoTiFiMessage toReturn = null;

        if (pkt.length == 0) {
            throw new IOException("Byte array contains no data");
        }

        /** Read the code from the byte array. */
        int operation = (pkt[0] & 0x0F);

        /**
         * Create the stream up here rather than in the switch statement so that
         * we can check if it's empty after the message has been created.
         */
        DataInputStream input = new DataInputStream(
                new ByteArrayInputStream(pkt));

        /**
         * Switch on the operation read. If nothing is found, you would need to
         * return an illegal argument problem.
         */
        switch (operation) {
        case NoTiFiRegister.CODE:
            toReturn = new NoTiFiRegister(input);
            break;
        case NoTiFiLocationAddition.CODE:
            toReturn = new NoTiFiLocationAddition(input);
            break;
        case NoTiFiLocationDeletion.CODE:
            toReturn = new NoTiFiLocationDeletion(input);
            break;
        case NoTiFiDeregister.CODE:
            toReturn = new NoTiFiDeregister(input);
            break;
        case NoTiFiError.CODE:
            toReturn = new NoTiFiError(input);
            break;
        case NoTiFiACK.CODE:
            toReturn = new NoTiFiACK(input);
            break;
        default:
            throw new IllegalArgumentException("Unexpected code: " + operation);
        }

        /**
         * Check if there is more in the stream. If there is, throw an error,
         * because we already read all that we expected.
         */
        if (input.available() > 0) {
            throw new IOException("More data than necessary.");
        }

        return toReturn;
    }

    /**
     * This function performs the encode operation for the header. Very much
     * like the constructor, it only takes care of the header.
     * 
     * @return the byte array containing the header information.
     * @throws IOException
     *             if there is a problem with I/O
     */
    public byte[] encode() throws IOException {

        /** Create the first byte and second byte with the values. */
        byte firstByte = 0;
        firstByte |= ((byte) VERSION) << 4;
        firstByte |= ((byte) getCode());
        byte secondByte = ((byte) msgId);

        /** Create the header from those bytes. */
        byte[] header = new byte[] { firstByte, secondByte };

        return header;
    }

    /**
     * This is an abstract function that requires implementation in the sub
     * classes. Obviously this base class wouldn't have a code to use.
     * 
     * @return the code for the message
     */
    public abstract int getCode();

    /**
     * This function just returns the ID of the message.
     * 
     * @return message ID
     */
    public int getMsgId() {
        return msgId;
    }

    /**
     * This function sets the message ID to the value passed in. Message ID is
     * an 8 bit integer, so it is important to check that the message ID is of
     * the right size.
     * 
     * @param msgId
     *            the message id
     * @throws IllegalArgumentException
     *             if there was a problem with the parameters
     */
    public void setMsgId(int msgId) throws IllegalArgumentException {
        if ((msgId & 0xFFFFFF00) != 0) {
            throw new IllegalArgumentException("Message ID is over 1 byte.");
        } else {
            this.msgId = msgId;
        }
    }

    /**
     * This function provides a unique hash value for the object, based on the
     * member variables.
     */
    @Override
    public int hashCode() {
        int prime = 13;
        int hash = 1;

        hash *= prime + VERSION;
        hash *= prime + getCode();
        hash *= prime + msgId;

        return hash;
    }

    /**
     * This function is used to check whether two objects are equal. Here, it
     * takes in an object, so we need to check that it's a NoTiFiACK too.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof NoTiFiMessage) {
            NoTiFiMessage temp = (NoTiFiMessage) obj;
            if (msgId == temp.getMsgId() && getCode() == temp.getCode()) {
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
        return "version: " + VERSION + ", msgId: " + msgId + ", code: "
                + getCode();
    }
}
