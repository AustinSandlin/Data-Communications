/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 1
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class is a JUnit 4 test for the AddATudeError class.
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
 * This class is a JUnit 4 test for the AddATudeError class.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class AddATudeErrorTest {

    /**
     * This function tests the throwing of an AddATudeException due to a
     * violation of the protocol.
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
        String input = "ADDATUDEv1 345 ERROR 12 test mes";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeMessage.decode(in);
    }

    /**
     * This function tests the construction of an AddATudeError from an input
     * stream.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the construction
     * @throws EOFException
     *             if reaches end of file during reading
     */
    @Test
    public void testConstructorDecode() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "12 test message";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeError addatudeError = new AddATudeError(in);

        assertEquals("ERROR", addatudeError.getOperation());
        assertEquals("test message", addatudeError.getErrorMessage());
    }

    /**
     * This function tests the decoding of an AddATudeError object.
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
        String input = "ADDATUDEv1 345 ERROR 12 test message\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeError addatudeError = (AddATudeError) AddATudeMessage
                .decode(in);

        assertEquals("ERROR", addatudeError.getOperation());
        assertEquals("test message", addatudeError.getErrorMessage());
    }

    /**
     * This function tests the decoding of multiple AddATudeError objects back
     * to back.
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
        String msg1 = "ADDATUDEv1 345 ERROR 12 test message\r\n"
                + "ADDATUDEv1 345 ERROR 6 oh no!\r\n"
                + "ADDATUDEv1 345 ERROR 18 program terminated\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                msg1.getBytes(MessageOutput.ENCODING)));

        AddATudeError addatudeError1 = (AddATudeError) AddATudeMessage
                .decode(in);
        AddATudeError addatudeError2 = (AddATudeError) AddATudeMessage
                .decode(in);
        AddATudeError addatudeError3 = (AddATudeError) AddATudeMessage
                .decode(in);

        assertEquals("ERROR", addatudeError1.getOperation());
        assertEquals("test message", addatudeError1.getErrorMessage());
        assertEquals("ERROR", addatudeError2.getOperation());
        assertEquals("oh no!", addatudeError2.getErrorMessage());
        assertEquals("ERROR", addatudeError3.getOperation());
        assertEquals("program terminated", addatudeError3.getErrorMessage());
    }

    /**
     * This function tests the encoding of an AddATudeError object.
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
        String input = "ADDATUDEv1 345 ERROR 12 test message\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));

        AddATudeError addatudeError = (AddATudeError) AddATudeMessage
                .decode(in);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);

        addatudeError.encode(out);

        assertArrayEquals("ADDATUDEv1 345 ERROR 12 test message\r\n"
                .getBytes(MessageOutput.ENCODING), bOut.toByteArray());
    }

    /**
     * This function tests the encoding of multiple AddATudeError objects back
     * to back.
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
        String msg1 = "ADDATUDEv1 345 ERROR 12 test message\r\n"
                + "ADDATUDEv1 345 ERROR 6 oh no!\r\n"
                + "ADDATUDEv1 345 ERROR 18 program terminated\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                msg1.getBytes(MessageOutput.ENCODING)));

        AddATudeError addatudeError1 = (AddATudeError) AddATudeMessage
                .decode(in);
        AddATudeError addatudeError2 = (AddATudeError) AddATudeMessage
                .decode(in);
        AddATudeError addatudeError3 = (AddATudeError) AddATudeMessage
                .decode(in);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);

        addatudeError1.encode(out);
        addatudeError2.encode(out);
        addatudeError3.encode(out);

        assertArrayEquals(
                ("ADDATUDEv1 345 ERROR 12 test message\r\n"
                        + "ADDATUDEv1 345 ERROR 6 oh no!\r\n"
                        + "ADDATUDEv1 345 ERROR 18 program terminated\r\n")
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

        String input = "ADDATUDEv1 345 ERROR 12 test message\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeError addatudeError = (AddATudeError) AddATudeMessage
                .decode(in);

        assertEquals("ERROR", addatudeError.getOperation());
    }

    /**
     * This function tests the getErrorMessage() function.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testGetErrorMessage() throws UnsupportedEncodingException,
            AddATudeException, EOFException {
        String input = "ADDATUDEv1 345 ERROR 12 test message\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeError addatudeError = (AddATudeError) AddATudeMessage
                .decode(in);

        assertEquals("test message", addatudeError.getErrorMessage());
    }

    /**
     * This function tests the setErrorMessage() function.
     * 
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws AddATudeException
     *             if there is a problem in the decoding or the setting of the
     *             error message
     * @throws EOFException
     *             thrown if decode fails to read eoln character
     */
    @Test
    public void testSetErrorMessage() throws UnsupportedEncodingException,
            AddATudeException, EOFException {

        String input = "ADDATUDEv1 345 ERROR 12 test message\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeError addatudeError = (AddATudeError) AddATudeMessage
                .decode(in);

        assertEquals("test message", addatudeError.getErrorMessage());

        addatudeError.setErrorMessage("Something went horribly wrong!");

        assertEquals("Something went horribly wrong!",
                addatudeError.getErrorMessage());
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
        String input = "ADDATUDEv1 345 ERROR 12 test message\r\n";
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                input.getBytes(MessageOutput.ENCODING)));
        AddATudeError addatudeError = (AddATudeError) AddATudeMessage
                .decode(in);

        assertEquals(
                ("version: ADDATUDEv1, mapId: 345, operation: ERROR"
                        + ", errorMessage: test message"),
                addatudeError.toString());
    }
}
