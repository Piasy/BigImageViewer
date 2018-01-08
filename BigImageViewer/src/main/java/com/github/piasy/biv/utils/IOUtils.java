package com.github.piasy.biv.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class IOUtils {

    /**
     * The default buffer size ({@value}) to use for
     * {@link #copyLarge(InputStream, OutputStream)}
     * and
     * {@link #copyLarge(Reader, Writer)}
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * The default buffer size to use for the skip() methods.
     */
    private static final int SKIP_BUFFER_SIZE = 2048;

    /**
     * Represents the end-of-file (or stream).
     * @since 2.5 (made public)
     */
    public static final int EOF = -1;

    private static char[] SKIP_CHAR_BUFFER;
    private static byte[] SKIP_BYTE_BUFFER;

// copy from InputStream
    //-----------------------------------------------------------------------

    /**
     * Copies bytes from an <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     * Large streams (over 2GB) will return a bytes copied value of
     * <code>-1</code> after the copy has completed since the correct
     * number of bytes cannot be returned as an int. For large streams
     * use the <code>copyLarge(InputStream, OutputStream)</code> method.
     *
     * @param input the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @return the number of bytes copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.1
     */
    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copies bytes from an <code>InputStream</code> to an <code>OutputStream</code> using an internal buffer of the
     * given size.
     * <p>
     * This method buffers the input internally, so there is no need to use a <code>BufferedInputStream</code>.
     * <p>
     *
     * @param input the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @param bufferSize the bufferSize used to copy from the input to the output
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.5
     */
    public static long copy(final InputStream input, final OutputStream output, final int bufferSize)
            throws IOException {
        return copyLarge(input, output, new byte[bufferSize]);
    }

    /**
     * Copies bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.3
     */
    public static long copyLarge(final InputStream input, final OutputStream output)
            throws IOException {
        return copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copies bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     *
     * @param input the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @param buffer the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(final InputStream input, final OutputStream output, final byte[] buffer)
            throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Copies some or all bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>, optionally skipping input bytes.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * </p>
     * <p>
     * Note that the implementation uses {@link #skip(InputStream, long)}.
     * This means that the method may be considerably less efficient than using the actual skip implementation,
     * this is done to guarantee that the correct number of characters are skipped.
     * </p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @param inputOffset : number of bytes to skip from input before copying
     * -ve values are ignored
     * @param length : number of bytes to copy. -ve means all
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(final InputStream input, final OutputStream output, final long inputOffset,
                                 final long length) throws IOException {
        return copyLarge(input, output, inputOffset, length, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Copies some or all bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>, optionally skipping input bytes.
     * <p>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * </p>
     * <p>
     * Note that the implementation uses {@link #skip(InputStream, long)}.
     * This means that the method may be considerably less efficient than using the actual skip implementation,
     * this is done to guarantee that the correct number of characters are skipped.
     * </p>
     *
     * @param input the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @param inputOffset : number of bytes to skip from input before copying
     * -ve values are ignored
     * @param length : number of bytes to copy. -ve means all
     * @param buffer the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(final InputStream input, final OutputStream output,
                                 final long inputOffset, final long length, final byte[] buffer) throws IOException {
        if (inputOffset > 0) {
            skipFully(input, inputOffset);
        }
        if (length == 0) {
            return 0;
        }
        final int bufferLength = buffer.length;
        int bytesToRead = bufferLength;
        if (length > 0 && length < bufferLength) {
            bytesToRead = (int) length;
        }
        int read;
        long totalRead = 0;
        while (bytesToRead > 0 && EOF != (read = input.read(buffer, 0, bytesToRead))) {
            output.write(buffer, 0, read);
            totalRead += read;
            if (length > 0) { // only adjust length if not reading to the end
                // Note the cast must work because buffer.length is an integer
                bytesToRead = (int) Math.min(length - totalRead, bufferLength);
            }
        }
        return totalRead;
    }

    // copy from Reader
    //-----------------------------------------------------------------------

    /**
     * Copies chars from a <code>Reader</code> to a <code>Writer</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p>
     * Large streams (over 2GB) will return a chars copied value of
     * <code>-1</code> after the copy has completed since the correct
     * number of chars cannot be returned as an int. For large streams
     * use the <code>copyLarge(Reader, Writer)</code> method.
     *
     * @param input the <code>Reader</code> to read from
     * @param output the <code>Writer</code> to write to
     * @return the number of characters copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.1
     */
    public static int copy(final Reader input, final Writer output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copies chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input the <code>Reader</code> to read from
     * @param output the <code>Writer</code> to write to
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.3
     */
    public static long copyLarge(final Reader input, final Writer output) throws IOException {
        return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Copies chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     * <p>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p>
     *
     * @param input the <code>Reader</code> to read from
     * @param output the <code>Writer</code> to write to
     * @param buffer the buffer to be used for the copy
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(final Reader input, final Writer output, final char[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Copies some or all chars from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>, optionally skipping input chars.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input the <code>Reader</code> to read from
     * @param output the <code>Writer</code> to write to
     * @param inputOffset : number of chars to skip from input before copying
     * -ve values are ignored
     * @param length : number of chars to copy. -ve means all
     * @return the number of chars copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(final Reader input, final Writer output, final long inputOffset, final long length)
            throws IOException {
        return copyLarge(input, output, inputOffset, length, new char[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Copies some or all chars from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>, optionally skipping input chars.
     * <p>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p>
     *
     * @param input the <code>Reader</code> to read from
     * @param output the <code>Writer</code> to write to
     * @param inputOffset : number of chars to skip from input before copying
     * -ve values are ignored
     * @param length : number of chars to copy. -ve means all
     * @param buffer the buffer to be used for the copy
     * @return the number of chars copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(final Reader input, final Writer output, final long inputOffset, final long length,
                                 final char[] buffer)
            throws IOException {
        if (inputOffset > 0) {
            skipFully(input, inputOffset);
        }
        if (length == 0) {
            return 0;
        }
        int bytesToRead = buffer.length;
        if (length > 0 && length < buffer.length) {
            bytesToRead = (int) length;
        }
        int read;
        long totalRead = 0;
        while (bytesToRead > 0 && EOF != (read = input.read(buffer, 0, bytesToRead))) {
            output.write(buffer, 0, read);
            totalRead += read;
            if (length > 0) { // only adjust length if not reading to the end
                // Note the cast must work because buffer.length is an integer
                bytesToRead = (int) Math.min(length - totalRead, buffer.length);
            }
        }
        return totalRead;
    }

    /**
     * Skips bytes from an input byte stream.
     * This implementation guarantees that it will read as many bytes
     * as possible before giving up; this may not always be the case for
     * skip() implementations in subclasses of {@link InputStream}.
     * <p>
     * Note that the implementation uses {@link InputStream#read(byte[], int, int)} rather
     * than delegating to {@link InputStream#skip(long)}.
     * This means that the method may be considerably less efficient than using the actual skip implementation,
     * this is done to guarantee that the correct number of bytes are skipped.
     * </p>
     *
     * @param input byte stream to skip
     * @param toSkip number of bytes to skip.
     * @return number of bytes actually skipped.
     * @throws IOException              if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @see InputStream#skip(long)
     * @see <a href="https://issues.apache.org/jira/browse/IO-203">IO-203 - Add skipFully() method for InputStreams</a>
     * @since 2.0
     */
    public static long skip(final InputStream input, final long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        /*
         * N.B. no need to synchronize this because: - we don't care if the buffer is created multiple times (the data
         * is ignored) - we always use the same size buffer, so if it it is recreated it will still be OK (if the buffer
         * size were variable, we would need to synch. to ensure some other thread did not create a smaller one)
         */
        if (SKIP_BYTE_BUFFER == null) {
            SKIP_BYTE_BUFFER = new byte[SKIP_BUFFER_SIZE];
        }
        long remain = toSkip;
        while (remain > 0) {
            // See https://issues.apache.org/jira/browse/IO-203 for why we use read() rather than delegating to skip()
            final long n = input.read(SKIP_BYTE_BUFFER, 0, (int) Math.min(remain, SKIP_BUFFER_SIZE));
            if (n < 0) { // EOF
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }

    /**
     * Skips bytes from a ReadableByteChannel.
     * This implementation guarantees that it will read as many bytes
     * as possible before giving up.
     *
     * @param input ReadableByteChannel to skip
     * @param toSkip number of bytes to skip.
     * @return number of bytes actually skipped.
     * @throws IOException              if there is a problem reading the ReadableByteChannel
     * @throws IllegalArgumentException if toSkip is negative
     * @since 2.5
     */
    public static long skip(final ReadableByteChannel input, final long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        final ByteBuffer skipByteBuffer = ByteBuffer.allocate((int) Math.min(toSkip, SKIP_BUFFER_SIZE));
        long remain = toSkip;
        while (remain > 0) {
            skipByteBuffer.position(0);
            skipByteBuffer.limit((int) Math.min(remain, SKIP_BUFFER_SIZE));
            final int n = input.read(skipByteBuffer);
            if (n == EOF) {
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }

    /**
     * Skips characters from an input character stream.
     * This implementation guarantees that it will read as many characters
     * as possible before giving up; this may not always be the case for
     * skip() implementations in subclasses of {@link Reader}.
     * <p>
     * Note that the implementation uses {@link Reader#read(char[], int, int)} rather
     * than delegating to {@link Reader#skip(long)}.
     * This means that the method may be considerably less efficient than using the actual skip implementation,
     * this is done to guarantee that the correct number of characters are skipped.
     * </p>
     *
     * @param input character stream to skip
     * @param toSkip number of characters to skip.
     * @return number of characters actually skipped.
     * @throws IOException              if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @see Reader#skip(long)
     * @see <a href="https://issues.apache.org/jira/browse/IO-203">IO-203 - Add skipFully() method for InputStreams</a>
     * @since 2.0
     */
    public static long skip(final Reader input, final long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        /*
         * N.B. no need to synchronize this because: - we don't care if the buffer is created multiple times (the data
         * is ignored) - we always use the same size buffer, so if it it is recreated it will still be OK (if the buffer
         * size were variable, we would need to synch. to ensure some other thread did not create a smaller one)
         */
        if (SKIP_CHAR_BUFFER == null) {
            SKIP_CHAR_BUFFER = new char[SKIP_BUFFER_SIZE];
        }
        long remain = toSkip;
        while (remain > 0) {
            // See https://issues.apache.org/jira/browse/IO-203 for why we use read() rather than delegating to skip()
            final long n = input.read(SKIP_CHAR_BUFFER, 0, (int) Math.min(remain, SKIP_BUFFER_SIZE));
            if (n < 0) { // EOF
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }

    /**
     * Skips the requested number of bytes or fail if there are not enough left.
     * <p>
     * This allows for the possibility that {@link InputStream#skip(long)} may
     * not skip as many bytes as requested (most likely because of reaching EOF).
     * <p>
     * Note that the implementation uses {@link #skip(InputStream, long)}.
     * This means that the method may be considerably less efficient than using the actual skip implementation,
     * this is done to guarantee that the correct number of characters are skipped.
     * </p>
     *
     * @param input stream to skip
     * @param toSkip the number of bytes to skip
     * @throws IOException              if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @throws EOFException             if the number of bytes skipped was incorrect
     * @see InputStream#skip(long)
     * @since 2.0
     */
    public static void skipFully(final InputStream input, final long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
        }
        final long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
        }
    }

    /**
     * Skips the requested number of bytes or fail if there are not enough left.
     *
     * @param input ReadableByteChannel to skip
     * @param toSkip the number of bytes to skip
     * @throws IOException              if there is a problem reading the ReadableByteChannel
     * @throws IllegalArgumentException if toSkip is negative
     * @throws EOFException             if the number of bytes skipped was incorrect
     * @since 2.5
     */
    public static void skipFully(final ReadableByteChannel input, final long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
        }
        final long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
        }
    }

    /**
     * Skips the requested number of characters or fail if there are not enough left.
     * <p>
     * This allows for the possibility that {@link Reader#skip(long)} may
     * not skip as many characters as requested (most likely because of reaching EOF).
     * <p>
     * Note that the implementation uses {@link #skip(Reader, long)}.
     * This means that the method may be considerably less efficient than using the actual skip implementation,
     * this is done to guarantee that the correct number of characters are skipped.
     * </p>
     *
     * @param input stream to skip
     * @param toSkip the number of characters to skip
     * @throws IOException              if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @throws EOFException             if the number of characters skipped was incorrect
     * @see Reader#skip(long)
     * @since 2.0
     */
    public static void skipFully(final Reader input, final long toSkip) throws IOException {
        final long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Chars to skip: " + toSkip + " actual: " + skipped);
        }
    }
}
