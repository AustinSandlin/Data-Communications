/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 1
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class is a JUnit 4 test for the AddATudeMessage class.
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
 * This class is a JUnit 4 test for the AddATudeMessage class.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class AddATudeMessageTest {

    /**
     * This function tests a bad operation on the decode function. We expect a
     * AddATudeException
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test(expected = AddATudeException.class)
    public void testDecodeBadOperation() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 DROP \r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeMessage.decode(in);
    }

    /**
     * This function tests a bad version for the message decoding. We expect a
     * AddATudeException
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test(expected = AddATudeException.class)
    public void testBadDecodeVersion() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv2 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeMessage.decode(in);
    }

    /**
     * This function tests a short decode on multiple messages. We expect a
     * AddATudeException
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test(expected = EOFException.class)
    public void testShortDecode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n"
                + "ADDATUDEv1 345 ALL \r\n" + "ADDATUDEv1 345 ERROR 17 no l";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeNewLocation newLocation = (AddATudeNewLocation) AddATudeMessage
                .decode(in);
        AddATudeLocationRequest locationRequest = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);
        AddATudeError addatudeError = (AddATudeError) AddATudeMessage
                .decode(in);
        AddATudeLocationResponse locationResponse = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        assertEquals("NEW", newLocation.getOperation());
        assertEquals(1, newLocation.getLocationRecord().getUserId());
        assertEquals("1.2", newLocation.getLocationRecord().getLongitude());
        assertEquals("3.4", newLocation.getLocationRecord().getLatitude());
        assertEquals("BU", newLocation.getLocationRecord().getLocationName());
        assertEquals("Baylor",
                newLocation.getLocationRecord().getLocationDescription());

        assertEquals("ALL", locationRequest.getOperation());

        assertEquals("ERROR", addatudeError.getOperation());
        assertEquals("no location found", addatudeError.getErrorMessage());

        assertEquals("RESPONSE", locationResponse.getOperation());
        assertEquals("Baylor", locationResponse.getMapName());
        assertEquals(1, locationResponse.getLocationRecordList().size());
    }

    /**
     * This function tests decoding multiple operations back to back.
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
        String input = "ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n"
                + "ADDATUDEv1 345 ALL \r\n"
                + "ADDATUDEv1 345 ERROR 17 no location found\r\n"
                + "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeNewLocation newLocation = (AddATudeNewLocation) AddATudeMessage
                .decode(in);
        AddATudeLocationRequest locationRequest = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);
        AddATudeError addatudeError = (AddATudeError) AddATudeMessage
                .decode(in);
        AddATudeLocationResponse locationResponse = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        assertEquals("NEW", newLocation.getOperation());
        assertEquals(1, newLocation.getLocationRecord().getUserId());
        assertEquals("1.2", newLocation.getLocationRecord().getLongitude());
        assertEquals("3.4", newLocation.getLocationRecord().getLatitude());
        assertEquals("BU", newLocation.getLocationRecord().getLocationName());
        assertEquals("Baylor",
                newLocation.getLocationRecord().getLocationDescription());

        assertEquals("ALL", locationRequest.getOperation());

        assertEquals("ERROR", addatudeError.getOperation());
        assertEquals("no location found", addatudeError.getErrorMessage());

        assertEquals("RESPONSE", locationResponse.getOperation());
        assertEquals("Baylor", locationResponse.getMapName());
        assertEquals(1, locationResponse.getLocationRecordList().size());
    }

    /**
     * This function tests encoding multiple operations back to back.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the encoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testMultipleEncode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n"
                + "ADDATUDEv1 345 ALL \r\n"
                + "ADDATUDEv1 345 ERROR 17 no location found\r\n"
                + "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeNewLocation newLocation = (AddATudeNewLocation) AddATudeMessage
                .decode(in);
        AddATudeLocationRequest locationRequest = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);
        AddATudeError addatudeError = (AddATudeError) AddATudeMessage
                .decode(in);
        AddATudeLocationResponse locationResponse = (AddATudeLocationResponse) AddATudeMessage
                .decode(in);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);

        newLocation.encode(out);
        locationRequest.encode(out);
        addatudeError.encode(out);
        locationResponse.encode(out);

        assertArrayEquals(("ADDATUDEv1 345 NEW 1 1.2 3.4 2 BU6 Baylor\r\n"
                + "ADDATUDEv1 345 ALL \r\n"
                + "ADDATUDEv1 345 ERROR 17 no location found\r\n"
                + "ADDATUDEv1 345 RESPONSE 6 Baylor"
                + "1 1 1.2 3.4 2 BU6 Baylor\r\n")
                        .getBytes(MessageOutput.ENCODING),
                bOut.toByteArray());
    }

    /**
     * This function tests the getOperation() function on a AddATudeMessage
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
        AddATudeMessage addatudeMessage = AddATudeMessage.decode(in);

        assertEquals("RESPONSE", addatudeMessage.getOperation());
    }

    /**
     * This function test the toString() function.
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
        AddATudeMessage addatudeMessage = AddATudeMessage.decode(in);

        assertEquals(
                ("version: ADDATUDEv1, mapId: 345, operation: RESPONSE,"
                        + " mapName: Baylor, locationRecordList:\r\nuserID=1,"
                        + " longitude=1.2, latitude=3.4, location name=BU,"
                        + " location description=Baylor"),
                addatudeMessage.toString());
    }
}
