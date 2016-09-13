/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 0
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class functions as a basic wrapper to the OutputStream class.
 *
 ************************************************/

package myn.addatude.protocol;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class functions as a basic wrapper to the OutputStream class.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class MessageOutput {

    /** A private OutputStream that this class forms a wrapper for. */
    private OutputStream out;

    /**
     * A String that denotes the encoding used for writing to the OutputStream.
     */
    public static final String ENCODING = "ASCII";

    /**
     * Constructs a MessageOutput with a given OutputStream.
     * 
     * @param out
     *            a OutputStream class to form a wrapper for.
     */
    public MessageOutput(OutputStream out) {
        this.out = out;
    }

    /**
     * Writes a byte array to the OutputStream.
     * 
     * @param s
     *            a String to encode and then write to the OutputStream
     * @throws AddATudeException
     *             thrown if there is a failure to write to the stream.
     */
    public void write(String s) throws AddATudeException {
        try {
            out.write(s.getBytes(ENCODING));
        } catch (IOException e) {
            throw new AddATudeException("MessageOutput write failure.", e);
        }
    }

}
