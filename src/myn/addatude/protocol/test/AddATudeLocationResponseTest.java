/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 1
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class is a JUnit 4 test for the AddATudeLocationResponse class.
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
 * This class is a JUnit 4 test for the AddATudeLocationResponse class.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class AddATudeLocationResponseTest {

    /**
     * This function tests the exception being thrown in encoding
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test(expected = EOFException.class)
    public void testShortEncode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 RESP";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeMessage.decode(in);
    }

    /**
     * This function tests the constructor's ability to decode from a
     * MessageInput.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the construction
     * @throws EOFException
     *             if there is an end of line found while reading a string from
     *             MessageInput
     */
    @Test
    public void testConstructorDecode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "6 Baylor1 1 1.2 3.4 2 BU6 Baylor";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeLocationResponse locationResponse = new AddATudeLocationResponse(
                in);

        assertEquals("RESPONSE", locationResponse.getOperation());
        assertEquals("Baylor", locationResponse.getMapName());
        assertEquals(1, locationResponse.getLocationRecordList().size());
    }

    /**
     * This function tests the decoding of the operation.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testDecode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeLocationResponse locationResponse = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        assertEquals("RESPONSE", locationResponse.getOperation());
        assertEquals("Baylor", locationResponse.getMapName());
        assertEquals(1, locationResponse.getLocationRecordList().size());
    }

    /**
     * This function tests the ability to decode multiple location responses
     * back to back.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testMultipleDecode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String msg1 = "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n"
                + "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 2 8 7.2 3 TCU26 Texas Christian University\r\n"
                + "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 3 -45 106 2 TX4 Waco\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                msg1.getBytes(MessageOutput.ENCODING)));

        AddATudeLocationResponse locationResponse1 = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);
        AddATudeLocationResponse locationResponse2 = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);
        AddATudeLocationResponse locationResponse3 = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        assertEquals("RESPONSE", locationResponse1.getOperation());
        assertEquals("Baylor", locationResponse1.getMapName());
        assertEquals(1, locationResponse1.getLocationRecordList().size());
        assertEquals("RESPONSE", locationResponse2.getOperation());
        assertEquals("Baylor", locationResponse2.getMapName());
        assertEquals(1, locationResponse2.getLocationRecordList().size());
        assertEquals("RESPONSE", locationResponse3.getOperation());
        assertEquals("Baylor", locationResponse2.getMapName());
        assertEquals(1, locationResponse2.getLocationRecordList().size());
    }

    /**
     * This function tests the ability to decode multiple locations in the
     * response back to back.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testDecodeMultipleLocations()
            throws UnsupportedEncodingException, AddATudeException,
            EOFException {
        String input = "ADDATUDEv1 345 RESPONSE 6 Baylor2 1 1.2 3.4 2 BU"
                + "6 Baylor2 8 7.2 3 TCU26 Texas Christian University\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeLocationResponse locationResponse = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        assertEquals("RESPONSE", locationResponse.getOperation());
        assertEquals("Baylor", locationResponse.getMapName());
        assertEquals(2, locationResponse.getLocationRecordList().size());

    }

    /**
     * This function tests the encoding of a AddATudeLocationResponse.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding or encoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testEncode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeLocationResponse locationResponse = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);

        locationResponse.encode(out);

        assertArrayEquals(
                "ADDATUDEv1 345 RESPONSE 6 Baylor1 1 1.2 3.4 2 BU6 Baylor\r\n"
                        .getBytes(MessageOutput.ENCODING),
                bOut.toByteArray());
    }

    /**
     * This function tests the ability to encode multiple
     * AddATudeLocationResponse's back to back.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding or encoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testMultipleEncode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String msg1 = "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n"
                + "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 2 8 7.2 3 TCU26 Texas Christian University\r\n"
                + "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 3 -45 106 2 TX4 Waco\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                msg1.getBytes(MessageOutput.ENCODING)));

        AddATudeLocationResponse locationResponse1 = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);
        AddATudeLocationResponse locationResponse2 = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);
        AddATudeLocationResponse locationResponse3 = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);

        locationResponse1.encode(out);
        locationResponse2.encode(out);
        locationResponse3.encode(out);

        assertArrayEquals(("ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n"
                + "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 2 8 7.2 3 TCU26 Texas Christian University\r\n"
                + "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 3 -45 106 2 TX4 Waco\r\n")
                        .getBytes(MessageOutput.ENCODING),
                bOut.toByteArray());
    }

    /**
     * This function tests the getOperation() function.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testGetOperation() throws UnsupportedEncodingException,
            AddATudeException, EOFException {

        String input = "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeLocationResponse locationResponse = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        assertEquals("RESPONSE", locationResponse.getOperation());
    }

    /**
     * This function tests the getMapName() function.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testGetMapName() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeLocationResponse locationResponse = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        assertEquals("Baylor", locationResponse.getMapName());
    }

    /**
     * This function tests the setMapName() function.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding or the setting of the
     *             map name
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testSetMapName() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeLocationResponse locationResponse = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        assertEquals("Baylor", locationResponse.getMapName());

        locationResponse.setMapName("Temple");

        assertEquals("Temple", locationResponse.getMapName());
    }

    /**
     * This function tests the setMapName() function with a null value.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding or the setting of the
     *             map name
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test(expected = AddATudeException.class)
    public void testNullSetMapName() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeLocationResponse locationResponse = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        assertEquals("Baylor", locationResponse.getMapName());

        locationResponse.setMapName(null);
    }

    /**
     * This function tests the toString() function.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testToString() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeLocationResponse locationResponse = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        assertEquals(
                ("version: ADDATUDEv1, mapId: 345, operation: RESPONSE,"
                        + " mapName: Baylor, locationRecordList:\r\nuserID=1,"
                        + " longitude=1.2, latitude=3.4, location name=BU,"
                        + " location description=Baylor"),
                locationResponse.toString());
    }
}
