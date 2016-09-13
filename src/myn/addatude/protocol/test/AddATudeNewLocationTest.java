/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 1
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class is a JUnit 4 test for the AddATudeNewLocation class.
 *
 ************************************************/

package myn.addatude.protocol.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.UnsupportedEncodingException;

import myn.addatude.protocol.*;

import org.junit.Test;

/**
 * This class is a JUnit 4 test for the AddATudeNewLocation class.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class AddATudeNewLocationTest {

    /**
     * This function tests for an exception being thrown since the string to
     * encode is shorter than expected for the protocol.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             when the decode function fails from a short string
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test(expected = EOFException.class)
    public void testShortEncode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 NEW 1";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeMessage.decode(in);
    }

    /**
     * This function tests my constructor that takes an MessageInput to
     * construct itself.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there's a problem with AddATudeNewLocation's constructor
     * @throws EOFException
     *             if reaches end of file during reading
     */
    @Test
    public void testConstructorDecode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "1 1.2 3.4 2 BU6 Baylor";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeNewLocation newLocation = new AddATudeNewLocation(in);

        assertEquals("NEW", newLocation.getOperation());
        assertEquals(1, newLocation.getLocationRecord().getUserId());
        assertEquals("1.2", newLocation.getLocationRecord().getLongitude());
        assertEquals("3.4", newLocation.getLocationRecord().getLatitude());
        assertEquals("BU", newLocation.getLocationRecord().getLocationName());
        assertEquals("Baylor",
                newLocation.getLocationRecord().getLocationDescription());
    }

    /**
     * This function tests my constructor that takes a map id and a location
     * record.
     * 
     * @throws AddATudeException
     *             if there's an error in the construction of the
     *             AddATudeNewLocation object.
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported.
     * @throws EOFException
     *             if reaches end of file during reading
     */
    @Test
    public void testConstructor() throws AddATudeException,
            UnsupportedEncodingException, EOFException {
        String input = "1 1.2 3.4 2 BU6 Baylor";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeNewLocation newLocation = new AddATudeNewLocation(345,
                new LocationRecord(in));

        assertEquals("NEW", newLocation.getOperation());
        assertEquals(345, newLocation.getMapId());
        assertEquals(1, newLocation.getLocationRecord().getUserId());
        assertEquals("1.2", newLocation.getLocationRecord().getLongitude());
        assertEquals("3.4", newLocation.getLocationRecord().getLatitude());
        assertEquals("BU", newLocation.getLocationRecord().getLocationName());
        assertEquals("Baylor",
                newLocation.getLocationRecord().getLocationDescription());
    }

    /**
     * This function tests AddATudeMessage's decode function for the NEW
     * operation.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there's an error in the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testDecode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeNewLocation newLocation = (AddATudeNewLocation) AddATudeMessage
                .decode(in);

        assertEquals("NEW", newLocation.getOperation());
        assertEquals(1, newLocation.getLocationRecord().getUserId());
        assertEquals("1.2", newLocation.getLocationRecord().getLongitude());
        assertEquals("3.4", newLocation.getLocationRecord().getLatitude());
        assertEquals("BU", newLocation.getLocationRecord().getLocationName());
        assertEquals("Baylor",
                newLocation.getLocationRecord().getLocationDescription());
    }

    /**
     * This function tests the decoding of multiple messages in one stream.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is an issue with the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testMultipleDecode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String msg1 = "ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n"
                + "ADDATUDEv1 345 NEW 2 8 7.2 3 TCU"
                + "26 Texas Christian University\r\n"
                + "ADDATUDEv1 345 NEW 3 -45 106 2 TX4 Waco\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                msg1.getBytes(MessageOutput.ENCODING)));

        AddATudeNewLocation newLocation1 = (AddATudeNewLocation) AddATudeMessage
                .decode(in);
        AddATudeNewLocation newLocation2 = (AddATudeNewLocation) AddATudeMessage
                .decode(in);
        AddATudeNewLocation newLocation3 = (AddATudeNewLocation) AddATudeMessage
                .decode(in);

        assertEquals("NEW", newLocation1.getOperation());
        assertEquals(1, newLocation1.getLocationRecord().getUserId());
        assertEquals("1.2", newLocation1.getLocationRecord().getLongitude());
        assertEquals("3.4", newLocation1.getLocationRecord().getLatitude());
        assertEquals("BU", newLocation1.getLocationRecord().getLocationName());
        assertEquals("Baylor",
                newLocation1.getLocationRecord().getLocationDescription());
        assertEquals("NEW", newLocation2.getOperation());
        assertEquals(2, newLocation2.getLocationRecord().getUserId());
        assertEquals("8", newLocation2.getLocationRecord().getLongitude());
        assertEquals("7.2", newLocation2.getLocationRecord().getLatitude());
        assertEquals("TCU", newLocation2.getLocationRecord().getLocationName());
        assertEquals("Texas Christian University",
                newLocation2.getLocationRecord().getLocationDescription());
        assertEquals("NEW", newLocation3.getOperation());
        assertEquals(3, newLocation3.getLocationRecord().getUserId());
        assertEquals("-45", newLocation3.getLocationRecord().getLongitude());
        assertEquals("106", newLocation3.getLocationRecord().getLatitude());
        assertEquals("TX", newLocation3.getLocationRecord().getLocationName());
        assertEquals("Waco",
                newLocation3.getLocationRecord().getLocationDescription());
    }

    /**
     * This function tests if there is an issue in the encoding of an
     * AddATudeNewLocation object to the MessageOutput stream.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there's an issue with the decoding or encoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testEncode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeNewLocation newLocation = (AddATudeNewLocation) AddATudeMessage
                .decode(in);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);

        newLocation.encode(out);

        assertArrayEquals("ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n"
                .getBytes(MessageOutput.ENCODING), bOut.toByteArray());
    }

    /**
     * This function tests the ability to encode multiple messages back to back.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is an issue in the decoding or encoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testMultipleEncode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String msg1 = "ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n"
                + "ADDATUDEv1 345 NEW 2 8 7.2 3 TCU"
                + "26 Texas Christian University\r\n"
                + "ADDATUDEv1 345 NEW 3 -45 106 2 TX4 Waco\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                msg1.getBytes(MessageOutput.ENCODING)));

        AddATudeNewLocation newLocation1 = (AddATudeNewLocation) AddATudeMessage
                .decode(in);
        AddATudeNewLocation newLocation2 = (AddATudeNewLocation) AddATudeMessage
                .decode(in);
        AddATudeNewLocation newLocation3 = (AddATudeNewLocation) AddATudeMessage
                .decode(in);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);

        newLocation1.encode(out);
        newLocation2.encode(out);
        newLocation3.encode(out);

        assertArrayEquals(
                ("ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n"
                        + "ADDATUDEv1 345 NEW 2 8 7.2 3 TCU"
                        + "26 Texas Christian University\r\n"
                        + "ADDATUDEv1 345 NEW 3 -45 106 2 TX4 Waco\r\n")
                                .getBytes(MessageOutput.ENCODING),
                bOut.toByteArray());
    }

    /**
     * This function tests the getOperation() function.
     * 
     * @throws UnsupportedEncodingException
     *             thrown if the encoding isn't supported
     * @throws AddATudeException
     *             thrown if there is an issue in decoding the message from the
     *             input stream
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testGetOperation() throws UnsupportedEncodingException,
            AddATudeException, EOFException {

        String input = "ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeNewLocation newLocation = (AddATudeNewLocation) AddATudeMessage
                .decode(in);

        assertEquals("NEW", newLocation.getOperation());
    }

    /**
     * This function tests the getLocationRecord() function.
     * 
     * @throws UnsupportedEncodingException
     *             thrown if the encoding isn't supported
     * @throws AddATudeException
     *             thrown if there is an issue in decoding the message from the
     *             input stream
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testGetLocationRecord() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeNewLocation newLocation = (AddATudeNewLocation) AddATudeMessage
                .decode(in);

        assertEquals(1, newLocation.getLocationRecord().getUserId());
        assertEquals("1.2", newLocation.getLocationRecord().getLongitude());
        assertEquals("3.4", newLocation.getLocationRecord().getLatitude());
        assertEquals("BU", newLocation.getLocationRecord().getLocationName());
        assertEquals("Baylor",
                newLocation.getLocationRecord().getLocationDescription());
    }

    /**
     * This function tests the toString() function.
     * 
     * @throws UnsupportedEncodingException
     *             thrown if the encoding isn't supported
     * @throws AddATudeException
     *             thrown if there is an issue in decoding the message from the
     *             input stream
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testToString() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeNewLocation newLocation = (AddATudeNewLocation) AddATudeMessage
                .decode(in);

        assertEquals(
                ("version: ADDATUDEv1, mapId: 345, operation: NEW,"
                        + " location record: userID=1, longitude=1.2,"
                        + " latitude=3.4, location name=BU,"
                        + " location description=Baylor"),
                newLocation.toString());
    }
}
