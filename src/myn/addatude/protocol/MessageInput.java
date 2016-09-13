/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 0
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class functions as a wrapper to the InputStream class. It contains
 * several helper functions to read information from the InputStream.
 *
 ************************************************/

package myn.addatude.protocol;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class functions as a wrapper to the InputStream class. It contains
 * several helper functions to read information from the InputStream.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class MessageInput {

    /** A private InputStream that this class forms a wrapper for. */
    private InputStream in;
    public final char DELIMITER = ' ';
    private final int EOF = -1;

    /** A String to match the pattern of an unsigned double. */
    private static final String UNSIGNED_DOUBLE_REGEX = "^[-+]?[0-9]*\\.?[0-9]+$";

    /**
     * Constructs a MessageInput with a given InputStream
     * 
     * @param in
     *            an InputStream to form a wrapper for.
     */
    public MessageInput(InputStream in) {
        this.in = in;
    }

    /**
     * This function reads a byte from the InputStream
     * 
     * @return the byte read from the stream
     * @throws IOException
     *             if there was an issue in reading from the InputStream
     */
    public int read() throws IOException {
        return in.read();
    }

    /**
     * Reads from the InputStream, byte by byte, to obtain an a string
     * representation of an unsigned integer. It throws an AddATudeException if
     * the unsigned integer failed to parse or if it failed to encounter a space
     * after the integer.
     * 
     * @return an integer in an unsigned format.
     * @throws AddATudeException
     *             thrown if the unsigned int string doesn't match the protocol
     * @throws EOFException
     *             if reaches end of file during reading
     */
    public int readUnsignedInt() throws AddATudeException, EOFException {
        String sb = "";
        int toReturn = 0;
        int inByte = 0;

        try {

            inByte = read();
            while (inByte != DELIMITER && inByte != EOF) {
                sb += ((char) inByte);
                inByte = read();
            }
        } catch (IOException e) {
            throw new AddATudeException(
                    "Caught I/O exception during reading of unsigned int.", e);
        }

        if (inByte == EOF) {
            throw new EOFException(
                    "Reached end of file during reading of signed double.");
        } else if (inByte != DELIMITER) {
            throw new AddATudeException(
                    "Failed to finish reading signed double.", null);
        }

        try {
            toReturn = Integer.parseUnsignedInt(sb.toString());
        } catch (NumberFormatException e) {
            throw new AddATudeException("Failed to parse unsigned int.", e);
        }

        return toReturn;
    }

    /**
     * Reads from the InputStream, byte by byte, to obtain an a string
     * representation of a signed integer. It throws an AddATudeException if the
     * signed double failed to match the required regular expression or if it
     * failed to encounter a space after the double.
     * 
     * @return an string representation of a signed double.
     * @throws AddATudeException
     *             thrown if the unsigned double string doesn't match the
     *             protocol
     * @throws EOFException
     *             if reaches end of file during reading
     */
    public String readSignedDouble() throws AddATudeException, EOFException {
        String toReturn = "";
        int inByte = 0;

        try {
            inByte = read();
            while (inByte != DELIMITER && inByte != EOF) {
                toReturn += ((char) inByte);
                inByte = read();
            }
        } catch (IOException e) {
            throw new AddATudeException(
                    "Caught I/O exception during reading of signed double.", e);
        }

        if (inByte == EOF) {
            throw new EOFException(
                    "Reached end of file during reading of signed double.");
        } else if (inByte != DELIMITER) {
            throw new AddATudeException(
                    "Failed to finish reading signed double.", null);
        }

        if (!isValidSignedDouble(toReturn)) {
            throw new AddATudeException("Improper unsigned double format.",
                    null);
        }

        return toReturn;
    }

    /**
     * This function determines if a double matches the signed double format.
     * 
     * @param sDouble
     *            the string representation of a signed double matching the
     *            regular expression.
     * @return boolean representing whether or not the string matches the
     *         regular expression
     */
    public static boolean isValidSignedDouble(String sDouble) {
        boolean validity = false;
        if (sDouble.matches(UNSIGNED_DOUBLE_REGEX)) {
            validity = true;
        }
        return validity;
    }

    /**
     * Reads a byte from the InputStream until a space is found, to obtain a
     * string. It throws an IOException if it reaches the end of the file during
     * reading the string.
     * 
     * @return an integer in an unsigned format.
     * @throws AddATudeException
     *             thrown if the string doesn't match the protocol
     * @throws EOFException
     *             if reaches end of file during reading
     */
    public String readString() throws AddATudeException, EOFException {
        String toReturn = "";
        int inByte = 0;

        try {
            inByte = read();
            while (inByte != DELIMITER && inByte != EOF) {
                toReturn += ((char) inByte);
                inByte = read();
            }
        } catch (IOException e) {
            throw new AddATudeException(
                    "Caught I/O exception during reading of string.", e);
        }

        if (inByte == EOF) {
            throw new EOFException(
                    "Reached end of file during reading of a string.");
        } else if (inByte != DELIMITER) {
            throw new AddATudeException("Failed to finish reading string.",
                    null);
        }

        return toReturn;
    }

    /**
     * Reads a byte from the InputStream a given number of times, to obtain a
     * string. It throws an IOException if it reaches the end of the file during
     * reading the string.
     * 
     * @param length
     *            an integer representing the exact number of bytes to read from
     *            the stream.
     * @return an integer in an unsigned format.
     * @throws AddATudeException
     *             if the string doesn't match the protocol
     */
    public String readString(int length) throws AddATudeException {
        String toReturn = "";
        int inByte = 0;

        try {
            for (int i = 0; Integer.compareUnsigned(i, length) < 0; ++i) {
                inByte = read();
                toReturn += ((char) inByte);
            }
        } catch (IOException e) {
            throw new AddATudeException(
                    "Caught I/O exception during reading of string.", e);
        }

        return toReturn;
    }

    /**
     * This function allows the stream to clean itself and remove all characters
     * up to a certain character sequence.
     * 
     * @param str
     *            the string variable denoting the ending character sequence to
     *            trash all characters up to
     * @throws AddATudeException
     *             if there's an I/O error during purging of inputstream.
     * @throws EOFException
     *             if it encounters an end of file while cleaning up the
     *             inputstream.
     */
    public void purge(String str) throws AddATudeException, EOFException {
        StringBuilder trash = new StringBuilder();
        int inByte = 0;

        try {
            do {
                inByte = in.read();
                trash.append((char) inByte);
            } while (!trash.toString().endsWith(str) && inByte != EOF);

        } catch (IOException e) {
            throw new AddATudeException(
                    "Caught I/O exception during purge of input stream.", e);
        }

        if (inByte == EOF) {
            throw new EOFException(
                    "Reached end of file during purging of input stream.");
        }
    }

    /**
     * Simple wrapper function for available.
     * 
     * @throws IOException
     *             if there's an issue checking the final.
     * @return the number of bytes available to read in the InputStream
     */
    public int available() throws IOException {
        return in.available();
    }
}
