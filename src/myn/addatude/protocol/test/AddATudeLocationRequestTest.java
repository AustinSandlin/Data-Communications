/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 1
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class is a JUnit 4 test for the AddATudeLocationRequest class.
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
 * This class is a JUnit 4 test for the AddATudeLocationRequest class.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class AddATudeLocationRequestTest {

    /**
     * This function tests the exception being thrown in encoding.
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
        String input = "ADDATUDEv1 345 ALL ";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeMessage.decode(in);
    }

    /**
     * This function tests the class's constructor that takes in an input
     * stream.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the construction of the class
     */
    @Test
    public void testConstructorDecode()
            throws UnsupportedEncodingException, AddATudeException {
        String input = "";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeLocationRequest addatudeError = new AddATudeLocationRequest(in);

        assertEquals("ALL", addatudeError.getOperation());
    }

    /**
     * This function tests the ability to decode an AddATudeLocationRequest
     * object
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
        String input = "ADDATUDEv1 345 ALL \r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeLocationRequest AddATudeLocationRequest = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);

        assertEquals("ALL", AddATudeLocationRequest.getOperation());
    }

    /**
     * This funciton tests the ability to decode multiple
     * AddATudeLocationRequest objects back to back.
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
        String msg1 = "ADDATUDEv1 345 ALL \r\n" + "ADDATUDEv1 345 ALL \r\n"
                + "ADDATUDEv1 345 ALL \r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                msg1.getBytes(MessageOutput.ENCODING)));

        AddATudeLocationRequest AddATudeLocationRequest1 = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);
        AddATudeLocationRequest AddATudeLocationRequest2 = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);
        AddATudeLocationRequest AddATudeLocationRequest3 = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);

        assertEquals("ALL", AddATudeLocationRequest1.getOperation());
        assertEquals("ALL", AddATudeLocationRequest2.getOperation());
        assertEquals("ALL", AddATudeLocationRequest3.getOperation());
    }

    /**
     * This function tests the ability to encode an AddATudeLocationRequest
     * object.
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
        String input = "ADDATUDEv1 345 ALL \r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeLocationRequest AddATudeLocationRequest = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);

        AddATudeLocationRequest.encode(out);

        assertArrayEquals(
                "ADDATUDEv1 345 ALL \r\n".getBytes(MessageOutput.ENCODING),
                bOut.toByteArray());
    }

    /**
     * This function tests the ability to encode multiple
     * AddATudeLocationRequest objects back to back.
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
        String msg1 = "ADDATUDEv1 345 ALL \r\n" + "ADDATUDEv1 345 ALL \r\n"
                + "ADDATUDEv1 345 ALL \r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                msg1.getBytes(MessageOutput.ENCODING)));

        AddATudeLocationRequest AddATudeLocationRequest1 = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);
        AddATudeLocationRequest AddATudeLocationRequest2 = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);
        AddATudeLocationRequest AddATudeLocationRequest3 = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);

        AddATudeLocationRequest1.encode(out);
        AddATudeLocationRequest2.encode(out);
        AddATudeLocationRequest3.encode(out);

        assertArrayEquals(("ADDATUDEv1 345 ALL \r\n" + "ADDATUDEv1 345 ALL \r\n"
                + "ADDATUDEv1 345 ALL \r\n").getBytes(MessageOutput.ENCODING),
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

        String input = "ADDATUDEv1 345 ALL \r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeLocationRequest AddATudeLocationRequest = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);

        assertEquals("ALL", AddATudeLocationRequest.getOperation());
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
        String input = "ADDATUDEv1 345 ALL \r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeLocationRequest AddATudeLocationRequest = (AddATudeLocationRequest) AddATudeMessage
                .decode(in);

        assertEquals("version: ADDATUDEv1, mapId: 345, operation: ALL",
                AddATudeLocationRequest.toString());
    }
}
