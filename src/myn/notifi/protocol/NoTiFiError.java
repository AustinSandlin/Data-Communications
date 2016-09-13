/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 4
 * Class:       CSI 4321 - Data Communications
 * Date:        27 October 2015
 *
 * This class serializes and deserializes a NoTiFiError message. It also stores
 * the information required to do so.
 *
 ************************************************/

package myn.notifi.protocol;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class serializes and deserializes a NoTiFiError message. It also stores
 * the information required to do so.
 * 
 * @version 27 October 2015
 * @author Austin Sandlin
 *
 */
public class NoTiFiError extends NoTiFiMessage {

    /** This is the code for the message. */
    public static final int CODE = 4;

    /** This is the error message. */
    String errorMessage;

    /**
     * This constructor takes a stream to make the class.
     * 
     * @param in
     *            the stream to read from
     * @throws IllegalArgumentException
     *             if there is a problem with the parameter
     * @throws IOException
     *             if there was a problem during I/O
     */
    public NoTiFiError(DataInputStream in)
            throws IllegalArgumentException, IOException {
        super(in);

        String error = new BufferedReader(new InputStreamReader(in)).readLine();
        if (error == null) {
            error = "";
        }

        setErrorMessage(error);
    }

    /**
     * This function takes parameters to create the object
     * 
     * @param msgId
     *            the message id
     * @param errorMessage
     *            the error message
     * @throws IllegalArgumentException
     *             if there is a problem with the parameters
     */
    public NoTiFiError(int msgId, String errorMessage)
            throws IllegalArgumentException {
        super(msgId);
        setErrorMessage(errorMessage);
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
        toReturn.write(errorMessage.getBytes(NoTiFiMessage.ENCODING));

        return toReturn.toByteArray();
    }

    /**
     * This function is an implementation of NoTiFiMessage's abstract function.
     * It simply needs to return the code for a error message.
     */
    public int getCode() {
        return CODE;
    }

    /**
     * Returns the error message
     * 
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message
     * 
     * @param errorMessage
     *            the error message
     * @throws IllegalArgumentException
     *             if there was a problem with the parameter
     */
    public void setErrorMessage(String errorMessage)
            throws IllegalArgumentException {
        if (errorMessage == null) {
            throw new IllegalArgumentException("Null error message in setter.");
        } else {
            this.errorMessage = errorMessage;
        }
    }

    /**
     * This function provides a unique hash code for the object.
     */
    @Override
    public int hashCode() {
        int prime = 13;
        int hash = 1;

        hash *= prime + super.hashCode();
        hash *= prime + errorMessage.hashCode();

        return hash;
    }

    /**
     * This function is used to check whether two objects are equal. Here, it
     * takes in an object, so we need to check that it's a NoTiFiACK too.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof NoTiFiError) {
            NoTiFiError temp = (NoTiFiError) obj;
            if (super.equals(temp)
                    && errorMessage.equals(temp.getErrorMessage())) {
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
        return super.toString() + ", error message: " + errorMessage;
    }
}
