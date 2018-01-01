////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2017 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.jre6.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.puppycrawl.tools.checkstyle.jre6.charset.StandardCharsets;

public final class Files7 {
    private Files7() {
    }

    public static BufferedReader newBufferedReader(Path path) throws IOException {
        return newBufferedReader(path, StandardCharsets.UTF_8);
    }

    public static BufferedReader newBufferedReader(Path path, Charset cs) throws IOException {
        final CharsetDecoder decoder = cs.newDecoder();
        final Reader reader = new InputStreamReader(new FileInputStream(path.getFile()), decoder);
        return new BufferedReader(reader);
    }

    public static void createDirectories(Path directory) {
        directory.getFile().mkdirs();
    }

    public static boolean exists(Path path) {
        return path.getFile().exists();
    }

    public static void delete(Path path) {
        path.getFile().delete();
    }

    public static byte[] readAllBytes(Path path) throws IOException {
        final RandomAccessFile f = new RandomAccessFile(path.getFile(), "r");
        try {
            final byte[] result = new byte[(int) f.length()];
            f.readFully(result);
            return result;
        }
        finally {
            f.close();
        }
    }

    public static void write(Path path, byte[] bytes) throws IOException {
        final FileOutputStream stream = new FileOutputStream(path.toFile());
        try {
            stream.write(bytes);
        }
        finally {
            stream.close();
        }
    }

    public static String toString(File file, Charset charset) throws IOException {
        return new String(readAllBytes(new Path(file)), charset.name());
    }
}
