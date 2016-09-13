/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 4
 * Class:       CSI 4321 - Data Communications
 * Date:        27 October 2015
 *
 * This class is a JUnit 4 test for the NoTiFiLocationDeletion class.
 *
 ************************************************/

package myn.notifi.protocol.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import myn.notifi.protocol.LocationRecord;
import myn.notifi.protocol.NoTiFiLocationDeletion;
import myn.notifi.protocol.NoTiFiMessage;

public class NoTiFiLocationDeletionTest {
    byte[] input = new byte[] {

            (byte) (0x32), (byte) (0x00),

            (byte) (0x00), (byte) (0x4D),

            (byte) (0xB7), (byte) (0x7A), (byte) (0x4E), (byte) (0x7A),
            (byte) (0xDF), (byte) (0x8C), (byte) (0x3F), (byte) (0x40),

            (byte) (0x71), (byte) (0xE6), (byte) (0x57), (byte) (0x73),
            (byte) (0x80), (byte) (0x47), (byte) (0x58), (byte) (0xC0),

            (byte) (0x02), (byte) (0x42), (byte) (0x55), (byte) (0x06),
            (byte) (0x42), (byte) (0x41), (byte) (0x59), (byte) (0x4C),
            (byte) (0x4F), (byte) (0x52) };
    LocationRecord location = new LocationRecord(77, 31.550285, -97.117215,
            "BU", "BAYLOR");

    @Test
    public void testStreamConstructor()
            throws IllegalArgumentException, IOException {

        NoTiFiLocationDeletion temp = (NoTiFiLocationDeletion) NoTiFiMessage
                .decode(input);
        assertEquals(temp.getCode(), 2);
        assertEquals(temp.getMsgId(), 0);
        assertTrue(temp.getLocationRecord().equals(location));
    }

    @Test
    public void testParamConstructor()
            throws IllegalArgumentException, UnknownHostException {
        NoTiFiLocationDeletion temp = new NoTiFiLocationDeletion(0, location);
        assertEquals(temp.getCode(), 2);
        assertEquals(temp.getMsgId(), 0);
        assertTrue(temp.getLocationRecord().equals(location));
    }

    @Test
    public void testEncode() throws IOException {
        NoTiFiLocationDeletion temp = (NoTiFiLocationDeletion) NoTiFiMessage
                .decode(input);
        assertArrayEquals(temp.encode(), input);
    }

    @Test
    public void testGetCode() throws IllegalArgumentException, IOException {
        NoTiFiLocationDeletion temp = (NoTiFiLocationDeletion) NoTiFiMessage
                .decode(input);
        assertEquals(temp.getCode(), 2);
    }

    @Test
    public void testToString() throws IllegalArgumentException, IOException {
        NoTiFiLocationDeletion temp = (NoTiFiLocationDeletion) NoTiFiMessage
                .decode(input);
        assertEquals(temp.toString(),
                "version: 3, msgId: 0, code: 2, location record: userID: 77, longitude: 31.550285, latitude: -97.117215, location name: BU, location description: BAYLOR");
    }

}
