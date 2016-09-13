/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 5 & 6
 * Class:       CSI 4321 - Data Communications
 * Date:        10 November 2015
 *
 * This class is for both the client and server classes to extend from. It
 * contains basic send and receive functionality for the NoTiFi protocol.
 *
 ************************************************/

package myn.notifi.app;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import myn.notifi.protocol.NoTiFiMessage;

/**
 * This class functions as a way to reduce duplicate code between the client and
 * server.
 * 
 * @author Austin Sandlin
 * @version 11/10/15
 */
public class NoTiFiMessageHandler {
    /**
     * A static DatagramSocket so that any class that requires send and receive
     * functionality only has one socket (as required).
     */
    protected static DatagramSocket socket;

    /** A constant to represent the maximum message size in bytes. */
    private final int MAX_MESSAGE_BYTES = 1024;

    /**
     * This function takes a NoTiFiMessage and sends it to the specified
     * location using the class's socket.
     * 
     * @param message
     *            a message to send
     * @param serverAddress
     *            the address to send it to
     * @param serverPort
     *            the port to send it to
     * @throws IOException
     *             if there was a problem with encoding or sending
     */
    public void sendMessage(NoTiFiMessage message, InetAddress serverAddress,
            int serverPort) throws IOException {
        /**
         * Create the datagram packet to send, consisting of the information
         * from the registration message.
         */
        byte[] bytesToSend = message.encode();

        DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
                bytesToSend.length, serverAddress, serverPort);

        /** Send the register message and try to get an ACK back. */
        socket.send(sendPacket);
    }

    /**
     * This function takes in a packet and deciphers it, making sure to trim the
     * excess zero padding since the send byte array is relatively large..
     * 
     * @param packet
     *            the packet to trim and decode
     * @return a NoTiFiMessage pulled from the DatagramPacket
     * @throws IllegalArgumentException
     *             if there was a problem with decode
     * @throws IOException
     *             if there was a problem with decode
     */
    public NoTiFiMessage getMessage(DatagramPacket packet)
            throws IllegalArgumentException, IOException {
        /**
         * Pull the byte array, trim the excess zeros from the message, and
         * decode the message.
         */
        byte[] receivedBytes = packet.getData();
        byte[] trimmedBytes = new byte[packet.getLength()];
        System.arraycopy(receivedBytes, 0, trimmedBytes, 0, packet.getLength());

        NoTiFiMessage receiveMessage = NoTiFiMessage.decode(trimmedBytes);

        return receiveMessage;
    }

    /**
     * This function simply receives and returns a packet from the socket.
     * 
     * @return the DatagramPacket
     * @throws IOException
     *             if there was a problem with receive
     */
    public DatagramPacket receivePacket() throws IOException {
        /**
         * Create a datagram packet to store the received datagram from the
         * socket.
         */
        DatagramPacket receivedPacket = new DatagramPacket(
                new byte[MAX_MESSAGE_BYTES], MAX_MESSAGE_BYTES);

        socket.receive(receivedPacket);

        return receivedPacket;
    }
}
