////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2021 the original author or authors.
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

package com.puppycrawl.tools.checkstyle;

import java.io.File;
import java.io.IOException;

import com.puppycrawl.tools.checkstyle.jre6.charset.StandardCharsets;
import com.puppycrawl.tools.checkstyle.jre6.file.Files7;
import com.puppycrawl.tools.checkstyle.jre6.file.Paths;
import com.puppycrawl.tools.checkstyle.jre6.lang.System7;

public abstract class AbstractPathTestSupport {

    // we are using positive lookahead here, to convert \r\n to \n
    // and \\r\\n to \\n (for parse tree dump files),
    // by replacing the full match with the empty string
    private static final String CR_FOLLOWED_BY_LF_REGEX = "(?x)\\\\r(?=\\\\n)|\\r(?=\\n)";

    private static final String EOL = System7.lineSeparator();

    /**
     * Returns the exact location for the package where the file is present.
     *
     * @return path for the package name for the file.
     */
    protected abstract String getPackageLocation();

    /**
     * Retrieves the name of the folder location for resources.
     *
     * @return The name of the folder.
     */
    protected String getResourceLocation() {
        return "test";
    }

    /**
     * Returns canonical path for the file with the given file name.
     * The path is formed base on the root location.
     * This implementation uses 'src/test/resources/'
     * as a root location.
     *
     * @param filename file name.
     * @return canonical path for the file name.
     * @throws IOException if I/O exception occurs while forming the path.
     */
    protected final String getPath(String filename) throws IOException {
        return new File("src/" + getResourceLocation() + "/resources/" + getPackageLocation() + "/"
                + filename).getCanonicalPath();
    }

    protected final String getResourcePath(String filename) {
        return "/" + getPackageLocation() + "/" + filename;
    }

    /**
     * Reads the contents of a file.
     *
     * @param filename the name of the file whose contents are to be read
     * @return contents of the file with all {@code \r\n} replaced by {@code \n}
     * @throws IOException if I/O exception occurs while reading
     */
    protected static String readFile(String filename) throws IOException {
        return toLfLineEnding(new String(Files7.readAllBytes(
                Paths.get(filename)), StandardCharsets.UTF_8));
    }

    /**
     * Join given strings with {@link #EOL} delimiter and add EOL at the end.
     *
     * @param strings strings to join
     * @return joined strings
     */
    public static String addEndOfLine(String... strings) {
        final StringBuilder result = new StringBuilder();
        for (String string : strings) {
            result.append(string);
            result.append(EOL);
        }
        return result.toString();
    }

    protected static String toLfLineEnding(String text) {
        return text.replaceAll(CR_FOLLOWED_BY_LF_REGEX, "");
    }

}
