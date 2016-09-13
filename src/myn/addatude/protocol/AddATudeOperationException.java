/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 3
 * Class:       CSI 4321 - Data Communications
 * Date:        15 October 2015
 *
 * A custom exception for the AddATude protocol.
 *
 ************************************************/

package myn.addatude.protocol;

/**
 * A custom exception for the AddATude protocol.
 * 
 * @version 15 October 2015
 * @author Austin Sandlin
 */
public class AddATudeOperationException extends AddATudeException {

    /** Required because of the extending of Exception. */
    private static final long serialVersionUID = 2L;

    /** Private mapId for reporting the issue. */
    private int mapId;

    /**
     * Constructs an AddATudeVersionException given a string message and
     * throwable cause.
     * 
     * @param message
     *            a string containing information about the reason behind the
     *            exception being thrown.
     * @param cause
     *            the cause of the exception being thrown.
     * @param mapId
     *            the mapId that was given before it picked up a bad operation.
     */
    public AddATudeOperationException(String message, Throwable cause,
            int mapId) {
        super(message, cause);
        this.mapId = mapId;
    }

    /**
     * A simple getter function for the mapId.
     * 
     * @return returns the mapId involved with the exception
     */
    public int getMapId() {
        return mapId;
    }
}