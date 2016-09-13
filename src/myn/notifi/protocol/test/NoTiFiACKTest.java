/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 4
 * Class:       CSI 4321 - Data Communications
 * Date:        27 October 2015
 *
 * This class is a JUnit 4 test for the NoTiFiACK class.
 *
 ************************************************/

package myn.notifi.protocol.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import myn.notifi.protocol.NoTiFiACK;
import myn.notifi.protocol.NoTiFiMessage;

public class NoTiFiACKTest {

    @Test
    public void testStreamConstructor()
            throws IllegalArgumentException, IOException {
        byte[] input = new byte[] { (byte) (0x35), (byte) (0x0F) };

        NoTiFiACK temp = (NoTiFiACK) NoTiFiMessage.decode(input);
        assertEquals(temp.getCode(), 5);
        assertEquals(temp.getMsgId(), 15);
    }

    @Test
    public void testParamConstructor() {
        NoTiFiACK temp = new NoTiFiACK(15);
        assertEquals(temp.getCode(), 5);
        assertEquals(temp.getMsgId(), 15);
    }

    @Test
    public void testEncode() throws IOException {
        byte[] input = new byte[] { (byte) (0x35), (byte) (0x0F) };

        NoTiFiACK temp = new NoTiFiACK(15);
        assertArrayEquals(temp.encode(), input);
    }

    @Test
    public void testGetCode() {
        NoTiFiACK temp = new NoTiFiACK(15);
        assertEquals(temp.getCode(), 5);
    }

    @Test
    public void testEquals() {
        NoTiFiACK temp = new NoTiFiACK(15);
        NoTiFiACK temp1 = new NoTiFiACK(1);
        NoTiFiACK temp2 = new NoTiFiACK(15);

        assertTrue(temp.equals(temp2));
        assertFalse(temp.equals(temp1));
    }

    @Test
    public void testToString() {
        NoTiFiACK temp = new NoTiFiACK(15);
        assertEquals(temp.toString(), "version: 3, msgId: 15, code: 5");
    }
}
