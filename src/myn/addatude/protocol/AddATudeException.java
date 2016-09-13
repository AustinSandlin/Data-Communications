/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 0
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * A custom exception for the AddATude protocol.
 *
 ************************************************/

package myn.addatude.protocol;

/**
 * A custom exception for the AddATude protocol.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class AddATudeException extends Exception {

    /** Required because of the extending of Exception. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an AddATudeException given a string message and throwable
     * cause.
     * 
     * @param message
     *            a string containing information about the reason behind the
     *            exception being thrown.
     * @param cause
     *            the cause of the exception being thrown.
     */
    public AddATudeException(String message, Throwable cause) {
        super(message, cause);
    }
}
