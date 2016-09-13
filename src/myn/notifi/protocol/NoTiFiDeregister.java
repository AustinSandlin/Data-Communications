/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 4
 * Class:       CSI 4321 - Data Communications
 * Date:        27 October 2015
 *
 * This class serializes and deserializes a deregister message. It also stores
 * the information required to do so.
 *
 ************************************************/

package myn.notifi.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;

/**
 * This class serializes and deserializes a deregister message. It also stores
 * the information required to do so.
 * 
 * @version 27 October 2015
 * @author Austin Sandlin
 */
public class NoTiFiDeregister extends NoTiFiMessage {

    /** Final int for the code. */
    public static final int CODE = 3;

    /** This is the address to use to create the socket for registering. */
    Inet4Address address;
    /** This is the port to use to create the socket for registering. */
    int port;

    /**
     * This function creates a deregister object from an input stream. It
     * converts the address and port from little to big endian, per protocol.
     * 
     * @param in
     *            the stream to read the information from
     * @throws IllegalArgumentException
     *             if there was a problem with the input stream
     * @throws IOException
     *             if there was a problem with I/O
     */
    public NoTiFiDeregister(DataInputStream in)
            throws IllegalArgumentException, IOException {
        /** Call the super constructor to read the header information. */
        super(in);

        /** Check if we were passed a null object. */
        if (in == null) {
            throw new IllegalArgumentException("DataInputStream is null.");
        }

        /** Read in the deregister data from the DataInputStream. */
        int tempAddress = in.readInt();
        int tempPort = in.readUnsignedShort();

        /**
         * Create a byte array from the integer read in. This particular method
         * also converts the bytes from little to big endian.
         */
        tempAddress = Integer.reverseBytes(tempAddress);
        byte[] addressBytes = { (byte) (tempAddress >>> 24),
                (byte) (tempAddress >>> 16), (byte) (tempAddress >>> 8),
                (byte) tempAddress };

        /** Create an Inet4Address object from the byte array. */
        Inet4Address address = (Inet4Address) Inet4Address
                .getByAddress(addressBytes);

        /**
         * No need for a fancy for loop for this one, just need to reverse the
         * bytes and then shift them over. Constants were used to preserve some
         * sense of flexibility in case of a change of the protocol.
         */
        tempPort = Integer.reverseBytes(tempPort);
        port = (tempPort >>> 16);
        
        /**
         * Finally, call the set address and port functions so they can perform
         * checks on the address and port that was read in.
         */
        setAddress(address);
        setPort(port);
    }

    /**
     * Basic constructor that takes in parameters instead of an input stream. In
     * this case, we just call the setters so that they can perform the data
     * checks for us.
     * 
     * @param msgId
     *            the id for the message
     * @param address
     *            the address for the message
     * @param port
     *            the port for the message
     * @throws IllegalArgumentException
     *             if the address or port don't follow protocol
     */
    public NoTiFiDeregister(int msgId, Inet4Address address, int port)
            throws IllegalArgumentException {
        super(msgId);
        setAddress(address);
        setPort(port);
    }

    /**
     * Returns the Inet4Address for the deregister message.
     * 
     * @return the address
     */
    public Inet4Address getAddress() {
        return address;
    }

    /**
     * This function overrides NoTiFiMessage's encode function to encode the
     * header and the message's data. This class needs to encode the address and
     * port in little endian order.
     */
    @Override
    public byte[] encode() throws IOException {
        /**
         * Convert the address to a byte array, reverse it, and then add it to
         * the address byte array.
         */
        byte[] inetaddr = address.getAddress();
        byte[] addressArray = new byte[inetaddr.length];
        for (int i = 0; i < inetaddr.length; ++i) {
            addressArray[i] = inetaddr[inetaddr.length - (i + 1)];
        }

        byte[] portArray = { (byte) (port), (byte) (port >>> 8) };

        /**
         * Finally, append the header to the address and port arrays and return
         * the product.
         */
        ByteArrayOutputStream toReturn = new ByteArrayOutputStream();
        toReturn.write(super.encode());
        toReturn.write(addressArray);
        toReturn.write(portArray);

        return toReturn.toByteArray();
    }

    /**
     * This function is an implementation of NoTiFiMessage's abstract function.
     * It simply needs to return the code for a deregister message.
     */
    public int getCode() {
        return CODE;
    }

    /**
     * Return the port for the deregister message.
     * 
     * @return the port number
     */
    public int getPort() {
        return port;
    }

    /**
     * This function creates and returns a socket address created from the
     * address and port.
     * 
     * @return the socket address for the message
     */
    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(address, port);
    }

    /**
     * Sets the address to the passed in parameter.
     * 
     * @param address
     *            a Inet4Address to change the objects address to
     * @throws IllegalArgumentException
     *             if the address passed in is null
     */
    public void setAddress(Inet4Address address)
            throws IllegalArgumentException {
        if (address == null) {
            throw new IllegalArgumentException("Null address in setter.");
        } else {
            this.address = address;
        }
    }

    /**
     * Sets the port number to the passed in parameter.
     * 
     * @param port
     *            a port number to set the port to
     * @throws IllegalArgumentException
     *             if the port number is larger than 2 bytes
     */
    public void setPort(int port) throws IllegalArgumentException {
        if ((port & 0xFFFF0000) != 0) {
            throw new IllegalArgumentException(
                    "Port larger than 2 bytes in setter.");
        } else {
            this.port = port;
        }
    }

    /**
     * This function provides a hash value unique to the object. It uses the
     * base hashcode to factor in the header information too.
     */
    @Override
    public int hashCode() {
        int prime = 13;
        int hash = 1;

        hash *= prime + super.hashCode();
        hash *= prime + address.getHostAddress().hashCode();
        hash *= prime + port;

        return hash;
    }

    /**
     * This function is used to check whether two objects are equal. Here, it
     * takes in an object, so we need to check that it's a NoTiFiDeregister too.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof NoTiFiDeregister) {
            NoTiFiDeregister temp = (NoTiFiDeregister) obj;
            if (super.equals(temp) && address.equals(temp.getAddress())
                    && port == temp.getPort()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function prints a textual representation of the object.
     */
    @Override
    public String toString() {
        return super.toString() + ", address: " + address.getHostAddress()
                + ", port: " + port;
    }
}
