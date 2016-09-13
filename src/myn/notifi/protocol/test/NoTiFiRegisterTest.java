/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 4
 * Class:       CSI 4321 - Data Communications
 * Date:        27 October 2015
 *
 * This class is a JUnit 4 test for the NoTiFiRegister class.
 *
 ************************************************/

package myn.notifi.protocol.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import myn.notifi.protocol.NoTiFiRegister;
import myn.notifi.protocol.NoTiFiMessage;

public class NoTiFiRegisterTest {

    @Test
    public void testStreamConstructor()
            throws IllegalArgumentException, IOException {
        byte[] input = new byte[] { (byte) (0x30), (byte) (0x00), (byte) (0x01),
                (byte) (0x02), (byte) (0x03), (byte) (0x04), (byte) (0x88),
                (byte) (0x13) };

        NoTiFiRegister temp = (NoTiFiRegister) NoTiFiMessage.decode(input);
        assertEquals(temp.getCode(), 0);
        assertEquals(temp.getMsgId(), 0);
        assertTrue(temp.getAddress()
                .equals((Inet4Address) InetAddress.getByName("4.3.2.1")));
        assertEquals(temp.getPort(), 5000);
    }

    @Test
    public void testParamConstructor()
            throws IllegalArgumentException, UnknownHostException {
        NoTiFiRegister temp = new NoTiFiRegister(15,
                (Inet4Address) InetAddress.getByName("127.0.0.1"), 5000);
        assertEquals(temp.getCode(), 0);
        assertEquals(temp.getMsgId(), 15);
        assertTrue(temp.getAddress()
                .equals((Inet4Address) InetAddress.getByName("127.0.0.1")));
        assertEquals(temp.getPort(), 5000);
    }

    @Test
    public void testEncode() throws IOException {
        byte[] input = new byte[] { (byte) (0x30), (byte) (0x00), (byte) (0x01),
                (byte) (0x02), (byte) (0x03), (byte) (0x04), (byte) (0x88),
                (byte) (0x13) };

        NoTiFiRegister temp = (NoTiFiRegister) NoTiFiMessage.decode(input);
        assertArrayEquals(temp.encode(), input);
    }

    @Test
    public void testGetCode() throws IllegalArgumentException, IOException {
        byte[] input = new byte[] { (byte) (0x30), (byte) (0x00), (byte) (0x01),
                (byte) (0x02), (byte) (0x03), (byte) (0x04), (byte) (0x88),
                (byte) (0x13) };

        NoTiFiRegister temp = (NoTiFiRegister) NoTiFiMessage.decode(input);
        assertEquals(temp.getCode(), 0);
    }

    @Test
    public void testToString() throws IllegalArgumentException, IOException {
        byte[] input = new byte[] { (byte) (0x30), (byte) (0x00), (byte) (0x01),
                (byte) (0x02), (byte) (0x03), (byte) (0x04), (byte) (0x88),
                (byte) (0x13) };

        NoTiFiRegister temp = (NoTiFiRegister) NoTiFiMessage.decode(input);
        assertEquals(temp.toString(),
                "version: 3, msgId: 0, code: 0, address: 4.3.2.1, port: 5000");
    }

}
