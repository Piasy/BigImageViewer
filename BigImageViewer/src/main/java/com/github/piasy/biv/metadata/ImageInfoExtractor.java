/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.piasy.biv.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Piasy{github.com/Piasy} on 2018/8/12.
 */
public final class ImageInfoExtractor {
    public static final int TYPE_STILL_IMAGE = 0;
    public static final int TYPE_GIF = 1;
    public static final int TYPE_WEBP = 2;

    private ImageInfoExtractor() {
        // no instance
    }

    public static int getImageType(File file) {
        int type = TYPE_STILL_IMAGE;
        try {
            FileInputStream inputStream = new FileInputStream(file);

            byte[] header = new byte[12];
            int read = inputStream.read(header);
            if (read >= 3 && isGifHeader(header)) {
                type = TYPE_GIF;
            } else if (read >= 12 && isWebpHeader(header)) {
                type = TYPE_WEBP;
            }

            inputStream.close();
        } catch (IOException e) {
        }

        return type;
    }

    public static String typeName(int type) {
        switch (type) {
            case TYPE_GIF:
                return "GIF";
            case TYPE_WEBP:
                return "WEBP";
            case TYPE_STILL_IMAGE:
            default:
                return "STILL_IMAGE";
        }
    }

    private static boolean isGifHeader(byte[] header) {
        return header[0] == 'G' && header[1] == 'I' && header[2] == 'F';
    }

    private static boolean isWebpHeader(byte[] header) {
        return header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
               && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
    }
}