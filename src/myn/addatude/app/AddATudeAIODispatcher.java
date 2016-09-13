/************************************************
 *
 * Author:      Austin Sandlin
 * Assignment:  Program 7
 * Class:       CSI 4321 - Data Communications
 * Date:        1 December 2015
 *
 * This class dispatches jobs for the handlers to perform AIO.
 *
 ************************************************/

package myn.addatude.app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import myn.addatude.protocol.AddATudeMessage;

/**
 * A dispatcher for the AddATudeAIO server
 */
public class AddATudeAIODispatcher {

    /** Default buffer size */
    private static final int BUFSIZE = 1024;
    /** Protocol-specific handler */
    private final AddATudeAIOHandler handler;
    /** Server logger */
    private final Logger logger;
    /** Buffer to read into */
    private ByteBuffer readBuffer = ByteBuffer.allocateDirect(BUFSIZE);
    /** Buffers to write from */
    private List<ByteBuffer> writeBufferList = new ArrayList<>();
    /** Local byte buffer for processing */
    private final byte[] localBuffer = new byte[BUFSIZE];

    /**
     * Instantiate dispatcher for new client
     * 
     * @param handler
     *            protocol-specific handler
     * @param logger
     *            server logger
     */
    public AddATudeAIODispatcher(final AddATudeAIOHandler handler,
            final Logger logger) {
        this.handler = handler;
        this.logger = logger;
    }

    /**
     * Handle client connection accept
     * 
     * @param clientChannel
     *            accepted channel
     */
    public void handleAccept(final AsynchronousSocketChannel clientChannel) {
        /** Prepare for reading. We don't need to send anything back. */
        processWriteBuffer(clientChannel, null);
    }

    /**
     * Prepare the write buffer containing given bytes to be sent on given
     * channel
     * 
     * @param clientChannel
     *            channel on which to send bytes
     * @param buf
     *            bytes to send
     */
    private void processWriteBuffer(
            final AsynchronousSocketChannel clientChannel, final byte[] buf) {
        /** If buffer contains data to write, prepare for sending. */
        if (buf != null && buf.length > 0) {
            writeBufferList.add(ByteBuffer.wrap(buf));
            clientChannel.write(writeBufferList.toArray(new ByteBuffer[] {}), 0,
                    writeBufferList.size(), -1, null, this,
                    makeWriteCompletionHandler(clientChannel, logger));
            /** If buffer does not contain data, prepare for reading */
        } else {
            clientChannel.read(readBuffer, this,
                    makeReadCompletionHandler(clientChannel, logger));
        }
    }

    /**
     * Handle client read
     * 
     * @param clientChannel
     *            channel for reading
     */
    public void handleRead(final AsynchronousSocketChannel clientChannel) {
        /** Read next set of bytes */
        readBuffer.flip();
        readBuffer.get(localBuffer, 0, readBuffer.limit());

        /**
         * Get the position of the end of line character, -1 if it doesn't
         * exist.
         */
        int pos = getEOLNPosition();

        byte buf[] = null;
        if (pos != -1) {
            /**
             * Because there was a message amidst the buffer's bytes, tell the
             * handler to handle it.
             */
            try {
                InetSocketAddress socketAddress = (InetSocketAddress) clientChannel
                        .getRemoteAddress();
                buf = handler.handleMessage(Arrays.copyOf(localBuffer, pos),
                        logger, socketAddress);
            } catch (IOException e) {
                System.err
                        .println("Problem fetching channel's socket address.");
            }
            /**
             * After handling it, reset the read buffer to hold what was left
             * from reading the message.
             */
            readBuffer.position(0);
            readBuffer.put(localBuffer, pos, readBuffer.limit());
            readBuffer.limit(readBuffer.limit() - pos);
        } else {
            /**
             * If no message has been received, readjust the buffer's stuff to
             * keep the old information.
             */
            readBuffer.position(readBuffer.limit());
            readBuffer.limit(readBuffer.capacity());
        }

        /** Write result */
        processWriteBuffer(clientChannel, buf);
    }

    /**
     * Get the position of the character after the eoln sequence
     * 
     * @return position of the character after the eoln sequence
     */
    public int getEOLNPosition() {
        int pos = -1;
        boolean found = false;
        for (int i = 1; i < readBuffer.limit() && !found; ++i) {
            if ((char) localBuffer[i - 1] == AddATudeMessage.EOLN.charAt(0)
                    && (char) localBuffer[i] == AddATudeMessage.EOLN
                            .charAt(1)) {
                pos = i + 1;
                found = true;
            }
        }
        return pos;
    }

    /**
     * Handle client write
     * 
     * @param clientChannel
     *            channel for writing
     */
    public void handleWrite(final AsynchronousSocketChannel clientChannel) {
        /**
         * Remove first buffer in list until list is empty or the first buffer
         * has bytes left to write
         */
        while (!writeBufferList.isEmpty()
                && !writeBufferList.get(0).hasRemaining()) {

            writeBufferList.remove(0);
        }
        /** Nothing to write, so read */
        if (writeBufferList.isEmpty()) {
            clientChannel.read(readBuffer, this,
                    makeReadCompletionHandler(clientChannel, logger));
        } else {
            /** More to write */
            clientChannel.write(writeBufferList.toArray(new ByteBuffer[] {}), 0,
                    writeBufferList.size(), -1, null, this,
                    makeWriteCompletionHandler(clientChannel, logger));
        }
    }

    /**
     * Create completion handler for read
     * 
     * @param clientChannel
     *            channel for reading
     * @param logger
     *            server logger
     * 
     * @return read completion handler
     */
    public static CompletionHandler<Integer, AddATudeAIODispatcher> makeReadCompletionHandler(
            final AsynchronousSocketChannel clientChannel,
            final Logger logger) {
        return new CompletionHandler<Integer, AddATudeAIODispatcher>() {

            /**
             * Called when read completes
             * 
             * @param clientChannel
             *            channel for read
             * 
             * @param aioDispatcher
             *            AIO dispatcher for handling read
             */
            public void completed(final Integer bytesRead,
                    final AddATudeAIODispatcher aioDispatcher) {
                try {
                    /** If other end closed, we will */
                    if (bytesRead == -1) {
                        clientChannel.close();
                        logger.log(Level.INFO, "***client terminated");
                        return;
                    }
                    /** Call protocol-specific handler */
                    aioDispatcher.handleRead(clientChannel);
                } catch (IOException ex) {
                    failed(ex, aioDispatcher);
                }
            }

            /**
             * Called if read fails
             * 
             * @param ex
             *            exception triggered by read failure
             * 
             * @param aioDispatcher
             *            AIO dispatcher for handling read
             */
            public void failed(final Throwable ex,
                    final AddATudeAIODispatcher aioDispatcher) {
                logger.log(Level.WARNING, "read failed", ex);
                try {
                    clientChannel.close();
                } catch (IOException e) {
                    logger.warning("Attempted to close " + clientChannel
                            + " and failed");
                }
            }
        };
    }

    /**
     * Create completion handler for write
     * 
     * @param clientChannel
     *            channel for writing
     * @param logger
     *            server logger
     * 
     * @return write completion handler
     */
    public static CompletionHandler<Long, AddATudeAIODispatcher> makeWriteCompletionHandler(
            final AsynchronousSocketChannel clientChannel,
            final Logger logger) {
        return new CompletionHandler<Long, AddATudeAIODispatcher>() {

            /**
             * Called when write completes
             * 
             * @param clientChannel
             *            channel for write
             * 
             * @param aioDispatcher
             *            AIO dispatcher for handling write
             */
            public void completed(final Long bytesWritten,
                    final AddATudeAIODispatcher aioDispatcher) {
                aioDispatcher.handleWrite(clientChannel);
            }

            /**
             * Called if read fails
             * 
             * @param ex
             *            exception triggered by read failure
             * 
             * @param aioDispatcher
             *            AIO dispatcher for handling read
             */
            public void failed(final Throwable ex,
                    final AddATudeAIODispatcher aioDispatcher) {
                logger.log(Level.WARNING, "write failed", ex);
                try {
                    clientChannel.close();
                } catch (IOException e) {
                    logger.warning("Attempted to close " + clientChannel
                            + " and failed");
                }
            }
        };
    }
}
