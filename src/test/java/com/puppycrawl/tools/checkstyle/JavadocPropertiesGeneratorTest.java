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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.internal.utils.TestUtil;
import com.puppycrawl.tools.checkstyle.jre6.charset.StandardCharsets;
import com.puppycrawl.tools.checkstyle.jre6.lang.System7;

public class JavadocPropertiesGeneratorTest extends AbstractPathTestSupport {

    private static final String EOL = System7.lineSeparator();
    private static final String USAGE = String.format(Locale.ROOT,
            "Usage: java com.puppycrawl.tools.checkstyle.JavadocPropertiesGenerator [-hV]%n"
                    + "       --destfile=<outputFile> <inputFile>%n"
                    + "      <inputFile>   The input file.%n"
                    + "      --destfile=<outputFile>%n"
                    + "                    The output file.%n"
                    + "  -h, --help        Show this help message and exit.%n"
                    + "  -V, --version     Print version information and exit.%n");
    private static final File DESTFILE = new File("target/tokentypes.properties");
    private static final String DESTFILE_ABSOLUTE_PATH = DESTFILE.getAbsolutePath();

    @Rule
    public final SystemErrRule systemErr = new SystemErrRule().enableLog().mute();
    @Rule
    public final SystemOutRule systemOut = new SystemOutRule().enableLog().mute();

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/javadocpropertiesgenerator";
    }

    /**
     * Returns canonical path for the file with the given file name.
     * The path is formed base on the non-compilable resources location.
     * This implementation uses 'src/test/resources-noncompilable/com/puppycrawl/tools/checkstyle/'
     * as a non-compilable resource location.
     *
     * @param filename file name.
     * @return canonical path for the file with the given file name.
     * @throws IOException if I/O exception occurs while forming the path.
     */
    protected final String getNonCompilablePath(String filename) throws IOException {
        return new File("src/test/resources-noncompilable/" + getPackageLocation() + "/"
                + filename).getCanonicalPath();
    }

    @Test
    public void testIsProperUtilsClass() throws Exception {
        assertTrue(TestUtil.isUtilsClassHasPrivateConstructor(
            JavadocPropertiesGenerator.class, false), "Constructor is not private");
    }

    @Test
    public void testNonExistentArgument()
            throws Exception {
        JavadocPropertiesGenerator.main("--nonexistent-argument");

        final String expected = String.format(Locale.ROOT,
                "Missing required options and parameters: "
                + "'--destfile=<outputFile>', '<inputFile>'%n")
                + USAGE;
        assertEquals(expected, systemErr.getLog(), "Unexpected error log");
        assertEquals("", systemOut.getLog(), "Unexpected output log");
    }

    @Test
    public void testNoDestfileSpecified() throws Exception {
        JavadocPropertiesGenerator.main(getPath("InputMain.java"));

        final String expected = String.format(Locale.ROOT,
                "Missing required option: '--destfile=<outputFile>'%n") + USAGE;
        assertEquals(expected, systemErr.getLog(), "Unexpected error log");
        assertEquals("", systemOut.getLog(), "Unexpected output log");
    }

    @Test
    public void testNoInputSpecified() throws Exception {
        JavadocPropertiesGenerator.main("--destfile", DESTFILE_ABSOLUTE_PATH);

        final String expected = String.format(Locale.ROOT,
                "Missing required parameter: '<inputFile>'%n") + USAGE;
        assertEquals(expected, systemErr.getLog(), "Unexpected error log");
        assertEquals("", systemOut.getLog(), "Unexpected output log");
    }

    @Test
    public void testNotClass() throws Exception {
        JavadocPropertiesGenerator.main("--destfile", DESTFILE_ABSOLUTE_PATH,
            getPath("InputJavadocPropertiesGeneratorNotClass.java"));
        assertEquals("", systemErr.getLog(), "Unexpected error log");
        assertEquals("", systemOut.getLog(), "Unexpected output log");
    }

    @Test
    public void testNotExistentInputSpecified() {
        try {
            JavadocPropertiesGenerator.main(
                "--destfile", DESTFILE_ABSOLUTE_PATH, "NotExistent.java");
            fail("Exception was expected");
        }
        catch (CheckstyleException ex) {
            assertEquals(
                    "Failed to write javadoc properties of 'NotExistent.java' to '"
                    + DESTFILE_ABSOLUTE_PATH + "'",
                ex.getMessage(), "Invalid error message");

            final Throwable cause = ex.getCause();
            assertTrue(cause instanceof FileNotFoundException, "Invalid error message");
            assertTrue(cause.getMessage().contains("NotExistent.java"), "Invalid error message");
        }
        assertEquals("", systemErr.getLog(), "Unexpected error log");
        assertEquals("", systemOut.getLog(), "Unexpected output log");
    }

    @Test
    public void testInvalidDestinationSpecified() throws Exception {
        try {
            // Passing a folder name will cause the FileNotFoundException.
            JavadocPropertiesGenerator.main("--destfile", "..",
                getPath("InputJavadocPropertiesGeneratorCorrect.java"));
            fail("Exception was expected");
        }
        catch (CheckstyleException ex) {
            final String expectedError = "Failed to write javadoc properties of '"
                + getPath("InputJavadocPropertiesGeneratorCorrect.java") + "' to '..'";
            assertEquals(expectedError, ex.getMessage(), "Invalid error message");

            final Throwable cause = ex.getCause();
            assertTrue(cause instanceof FileNotFoundException, "Invalid error message");
            assertTrue(cause.getMessage().contains(".."), "Invalid error message");
        }
        assertEquals("", systemErr.getLog(), "Unexpected error log");
        assertEquals("", systemOut.getLog(), "Unexpected output log");
    }

    @Test
    public void testCorrect() throws Exception {
        final String expectedContent = "EOF1=The end of file token." + EOL
            + "EOF2=The end of file token." + EOL
            + "TYPE_EXTENSION_AND='&amp;' symbol when used in a generic upper or lower bounds"
            + " constrain e.g&#46;"
            + " <code>Comparable<T extends Serializable & CharSequence></code>!" + EOL
            + "LCURLY=A left curly brace (<code>{</code>)." + EOL
            + "DEPRECATED_LITERAL='@deprecated' literal in @deprecated Javadoc tag?" + EOL;

        JavadocPropertiesGenerator.main(getPath("InputJavadocPropertiesGeneratorCorrect.java"),
            "--destfile", DESTFILE_ABSOLUTE_PATH);
        assertEquals("", systemErr.getLog(), "Unexpected error log");
        assertEquals("", systemOut.getLog(), "Unexpected output log");
        final String fileContent = FileUtils.readFileToString(DESTFILE, StandardCharsets.UTF_8);
        assertEquals(expectedContent, fileContent, "File content is not expected");
    }

    @Test
    public void testEmptyJavadoc() throws Exception {
        JavadocPropertiesGenerator.main(getPath("InputJavadocPropertiesGeneratorEmptyJavadoc.java"),
            "--destfile", DESTFILE_ABSOLUTE_PATH);
        assertEquals("", systemErr.getLog(), "Unexpected error log");
        assertEquals("", systemOut.getLog(), "Unexpected output log");
        final long size = FileUtils.sizeOf(DESTFILE);
        assertEquals(0L, size, "File '" + DESTFILE + "' must be empty");
    }

    @Test
    public void testNotConstants() throws Exception {
        JavadocPropertiesGenerator.main(getPath("InputJavadocPropertiesGeneratorNotConstants.java"),
            "--destfile", DESTFILE_ABSOLUTE_PATH);
        assertEquals("", systemErr.getLog(), "Unexpected error log");
        assertEquals("", systemOut.getLog(), "Unexpected output log");
        final long size = FileUtils.sizeOf(DESTFILE);
        assertEquals(0L, size, "File '" + DESTFILE + "' must be empty");
    }

    @Test
    public void testHelp() throws Exception {
        JavadocPropertiesGenerator.main("-h");
        assertEquals("", systemErr.getLog(), "Unexpected error log");
        assertEquals(USAGE, systemOut.getLog(), "Unexpected output log");
    }

    @Test
    public void testJavadocParseError() throws Exception {
        final String path = getPath("InputJavadocPropertiesGeneratorJavadocParseError.java");
        try {
            JavadocPropertiesGenerator.main(path, "--destfile", DESTFILE_ABSOLUTE_PATH);
            fail("Exception was expected");
        }
        catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage()
                            .contains("mismatched input '<EOF>' expecting JAVADOC_INLINE_TAG_END"),
                    "Invalid error message");
        }
        final long size = FileUtils.sizeOf(DESTFILE);
        assertEquals(0L, size, "File '" + DESTFILE + "' must be empty");
    }

    @Test
    public void testNotImplementedTag() throws Exception {
        final String path = getPath("InputJavadocPropertiesGeneratorNotImplementedTag.java");
        try {
            JavadocPropertiesGenerator.main(path, "--destfile", DESTFILE_ABSOLUTE_PATH);
            fail("Exception was expected");
        }
        catch (CheckstyleException ex) {
            assertEquals("Unsupported inline tag LINK_LITERAL",
                ex.getMessage(), "Invalid error message");
        }
        final long size = FileUtils.sizeOf(DESTFILE);
        assertEquals(0L, size, "File '" + DESTFILE + "' must be empty");
    }

    @Test
    public void testParseError() throws Exception {
        final String path = getNonCompilablePath("InputJavadocPropertiesGeneratorParseError.java");
        try {
            JavadocPropertiesGenerator.main(path, "--destfile", DESTFILE_ABSOLUTE_PATH);
            fail("Exception was expected");
        }
        catch (CheckstyleException ex) {
            assertTrue(ex.getMessage().contains("InputJavadocPropertiesGeneratorParseError.java"),
                    "Invalid error message");

            final Throwable cause = ex.getCause();
            assertTrue(cause instanceof IllegalStateException, "Invalid error message");
            assertTrue(cause.getMessage().contains("9:0: mismatched input '!' expecting '}'"),
                    "Invalid error message");
        }
    }

}
