/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 4
 * Class:       CSI 4321 - Data Communications
 * Date:        27 October 2015
 *
 * This class is a JUnit 4 test for the NoTiFiError class.
 *
 ************************************************/

package myn.notifi.protocol.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import myn.notifi.protocol.NoTiFiError;
import myn.notifi.protocol.NoTiFiMessage;

public class NoTiFiErrorTest {

    @Test
    public void testStreamConstructor()
            throws IllegalArgumentException, IOException {
        byte[] input = new byte[] { (byte) (0x34), (byte) (0x0F), (byte) (0x45),
                (byte) (0x72), (byte) (0x72), (byte) (0x6f), (byte) (0x72) };

        NoTiFiError temp = (NoTiFiError) NoTiFiMessage.decode(input);
        assertEquals(temp.getCode(), 4);
        assertEquals(temp.getMsgId(), 15);
    }

    @Test
    public void testParamConstructor() {
        NoTiFiError temp = new NoTiFiError(15, "Error");
        assertEquals(temp.getCode(), 4);
        assertEquals(temp.getMsgId(), 15);
        assertEquals(temp.getErrorMessage(), "Error");
    }

    @Test
    public void testGetMessage() {
        NoTiFiError temp = new NoTiFiError(15, "Error");
        assertEquals(temp.getErrorMessage(), "Error");
    }

    @Test
    public void testEncode() throws IOException {
        byte[] input = new byte[] { (byte) (0x34), (byte) (0x0F), (byte) (0x45),
                (byte) (0x72), (byte) (0x72), (byte) (0x6f), (byte) (0x72) };

        NoTiFiError temp = new NoTiFiError(15, "Error");
        assertArrayEquals(temp.encode(), input);
    }

    @Test
    public void testGetCode() {
        NoTiFiError temp = new NoTiFiError(15, "Error");
        assertEquals(temp.getCode(), 4);
    }

    @Test
    public void testEquals() {
        NoTiFiError temp = new NoTiFiError(15, "Error");
        NoTiFiError temp1 = new NoTiFiError(1, "Error");
        NoTiFiError temp2 = new NoTiFiError(15, "Error");

        assertTrue(temp.equals(temp2));
        assertFalse(temp.equals(temp1));
    }

    @Test
    public void testToString() {
        NoTiFiError temp = new NoTiFiError(15, "Error");
        assertEquals(temp.toString(),
                "version: 3, msgId: 15, code: 4, error message: Error");
    }

}
