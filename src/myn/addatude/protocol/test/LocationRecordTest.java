/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 0
 * Class:       CSI 4321 - Data Communications
 * Date:        16 September 2015
 *
 * This class is a JUnit 4 test for the LocationRecord class.
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
 * This class is a JUnit 4 test for the LocationRecord class.
 * 
 * @version 16 September 2015
 * @author Austin Sandlin
 */
public class LocationRecordTest {

    /**
     * This function tests the return of getUserId().
     * 
     * @throws AddATudeException
     *             thrown if the userId format differs from protocol.
     */
    @Test
    public void testGetUserId() throws AddATudeException {
        LocationRecord msg = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        assertEquals(1, msg.getUserId());
    }

    /**
     * This function tests the return of getLongitude().
     * 
     * @throws AddATudeException
     *             thrown if the longitude format differs from protocol.
     */
    @Test
    public void testGetLongitude() throws AddATudeException {
        LocationRecord msg = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        assertEquals("1.2", msg.getLongitude());
    }

    /**
     * This function tests the return of getLatitude().
     * 
     * @throws AddATudeException
     *             thrown if the latitude format differs from protocol.
     */
    @Test
    public void testGetLatitude() throws AddATudeException {
        LocationRecord msg = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        assertEquals("3.4", msg.getLatitude());
    }

    /**
     * This function tests the return of getLocationName().
     * 
     * @throws AddATudeException
     *             thrown if the location name format differs from protocol.
     */
    @Test
    public void testGetLocationName() throws AddATudeException {
        LocationRecord msg = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        assertEquals("BU", msg.getLocationName());
    }

    /**
     * This function tests the return of setLocationDescription().
     * 
     * @throws AddATudeException
     *             thrown if the location description format differs from
     *             protocol.
     */
    @Test
    public void testSetLocationDescription() throws AddATudeException {
        LocationRecord msg = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        assertEquals("Baylor", msg.getLocationDescription());
    }

    /**
     * This function tests the return of setUserId().
     * 
     * @throws AddATudeException
     *             thrown if the userId format differs from the protocol
     */
    @Test
    public void testSetUserId() throws AddATudeException {
        LocationRecord msg = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        msg.setUserId(2);
        assertEquals(2, msg.getUserId());
    }

    /**
     * This function tests the return of setLongitude().
     * 
     * @throws AddATudeException
     *             thrown if the longitude format differs from the protocol
     */
    @Test
    public void testSetLongitude() throws AddATudeException {
        LocationRecord msg = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        msg.setLongitude("8");
        assertEquals("8", msg.getLongitude());
    }

    /**
     * This function tests the return of setLatitude().
     * 
     * @throws AddATudeException
     *             thrown if the latitude format differs from the protocol
     */
    @Test
    public void testSetLatitude() throws AddATudeException {
        LocationRecord msg = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        msg.setLatitude("7.2");
        assertEquals("7.2", msg.getLatitude());
    }

    /**
     * This function tests the return of setLocationName().
     * 
     * @throws AddATudeException
     *             thrown if the location name format differs from the protocol
     */
    @Test
    public void testSetLocationName() throws AddATudeException {
        LocationRecord msg = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        msg.setLocationName("TCU");
        assertEquals("TCU", msg.getLocationName());
    }

    /**
     * This function tests the return of getLocationDescription().
     * 
     * @throws AddATudeException
     *             thrown if the location description differs from the protocol
     */
    @Test
    public void testGetLocationDescription() throws AddATudeException {
        LocationRecord msg = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        msg.setLocationDescription("Texas Christian University");
        assertEquals("Texas Christian University",
                msg.getLocationDescription());
    }

    /**
     * This function tests the equality of LocationRecord objects with the
     * equality function.
     * 
     * @throws AddATudeException
     *             thrown if there is an issue in the constructor for
     *             LocationRecord.
     */
    @Test
    public void testEquality() throws AddATudeException {
        LocationRecord msg1 = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        LocationRecord msg2 = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        LocationRecord msg3 = new LocationRecord(2, "8", "7.2", "TCU",
                "Texas Christian University");
        assertTrue(msg1.equals(msg2));
        assertFalse(msg1.equals(msg3));
    }

    /**
     * This function test the LocationRecord's ability to deserialize from the
     * MessageInput stream.
     * 
     * @throws AddATudeException
     *             thrown if there is an issue in the construction of a
     *             LocationRecord
     * @throws UnsupportedEncodingException
     *             thrown if the ASCII encoding isn't supported
     * @throws EOFException
     *             if reaches end of file during reading
     */
    @Test
    public void testDecode() throws AddATudeException,
            UnsupportedEncodingException, EOFException {
        MessageInput in = new MessageInput(new ByteArrayInputStream(
                "1 1.2 3.4 2 BU6 Baylor".getBytes("ASCII")));
        LocationRecord msg = new LocationRecord(in);
        assertEquals(1, msg.getUserId());
        assertEquals("1.2", msg.getLongitude());
        assertEquals("3.4", msg.getLatitude());
        assertEquals("BU", msg.getLocationName());
        assertEquals("Baylor", msg.getLocationDescription());
    }

    /**
     * This function tests the LocationRecord's ability to deserialize multiple
     * locations from MessageInput.
     * 
     * @throws AddATudeException
     *             thrown if there is an issue in the construction of a Location
     *             Record
     * @throws UnsupportedEncodingException
     *             thrown if the ASCII encoding isn't supported
     * @throws EOFException
     *             if reaches end of file during reading
     */
    @Test
    public void testMultipleDecode() throws AddATudeException,
            UnsupportedEncodingException, EOFException {
        MessageInput in = new MessageInput(
                new ByteArrayInputStream(("1 1.2 3.4 2 BU6 Baylor2 8 7.2 3 TCU"
                        + "26 Texas Christian University3 -45 106 2 TX4 Waco")
                                .getBytes("ASCII")));
        LocationRecord msg1 = new LocationRecord(in);
        LocationRecord msg2 = new LocationRecord(in);
        LocationRecord msg3 = new LocationRecord(in);

        /** Check msg1's deserialize */
        assertEquals(1, msg1.getUserId());
        assertEquals("1.2", msg1.getLongitude());
        assertEquals("3.4", msg1.getLatitude());
        assertEquals("BU", msg1.getLocationName());
        assertEquals("Baylor", msg1.getLocationDescription());

        assertEquals(2, msg2.getUserId());
        assertEquals("8", msg2.getLongitude());
        assertEquals("7.2", msg2.getLatitude());
        assertEquals("TCU", msg2.getLocationName());
        assertEquals("Texas Christian University",
                msg2.getLocationDescription());

        assertEquals(3, msg3.getUserId());
        assertEquals("-45", msg3.getLongitude());
        assertEquals("106", msg3.getLatitude());
        assertEquals("TX", msg3.getLocationName());
        assertEquals("Waco", msg3.getLocationDescription());
    }

    /**
     * This function tests the LocationRecord's ability to serialize locations
     * to MessageOutput.
     * 
     * @throws AddATudeException
     *             thrown if there is an issue with the encode function in
     *             LocationRecord
     * @throws UnsupportedEncodingException
     *             thrown if the ASCII encoding isn't supported
     */
    @Test
    public void testEncode()
            throws AddATudeException, UnsupportedEncodingException {
        LocationRecord msg = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);
        msg.encode(out);
        assertArrayEquals("1 1.2 3.4 2 BU6 Baylor".getBytes("ASCII"),
                bOut.toByteArray());
    }

    /**
     * This function tests the LocationRecord's ability to serialize multiple
     * locations to MessageOutput.
     * 
     * @throws AddATudeException
     *             thrown if there is an issue with LocationRecord's
     *             construction
     * @throws UnsupportedEncodingException
     *             thrown if the ASCII encoding isn't supported
     */
    @Test
    public void testMultipleEncode()
            throws AddATudeException, UnsupportedEncodingException {
        LocationRecord msg1 = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        LocationRecord msg2 = new LocationRecord(2, "8", "7.2", "TCU",
                "Texas Christian University");
        LocationRecord msg3 = new LocationRecord(3, "-45", "106", "TX", "Waco");
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bOut);
        msg1.encode(out);
        msg2.encode(out);
        msg3.encode(out);
        assertArrayEquals(("1 1.2 3.4 2 BU6 Baylor2 8 7.2 3 TCU"
                + "26 Texas Christian University3 -45 106 2 TX4 Waco")
                        .getBytes("ASCII"),
                bOut.toByteArray());
    }

    /**
     * Test that LocationRecord's toString function prints what's expected.
     * 
     * @throws AddATudeException
     *             thrown if there is an issue constructing a LocationRecord
     */
    @Test
    public void testToString() throws AddATudeException {
        LocationRecord msg = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        assertEquals(("userID=1, longitude=1.2, latitude=3.4, location name=BU,"
                + " location description=Baylor"), msg.toString());
    }

    /**
     * Test that classes have the same or different hash codes.
     * 
     * @throws AddATudeException
     *             thrown if there is an issue with LocationRecord's constructor
     */
    @Test
    public void testHashCode() throws AddATudeException {
        LocationRecord msg1 = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        LocationRecord msg2 = new LocationRecord(1, "1.2", "3.4", "BU",
                "Baylor");
        LocationRecord msg3 = new LocationRecord(2, "8", "7.2", "TCU",
                "Texas Christian University");
        assertTrue(msg1.hashCode() == msg2.hashCode());
        assertFalse(msg1.hashCode() == msg3.hashCode());
    }
}
