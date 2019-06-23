////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2019 the original author or authors.
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

package com.puppycrawl.tools.checkstyle.checks;

import static com.puppycrawl.tools.checkstyle.checks.OrderedPropertiesCheck.MSG_IO_EXCEPTION_KEY;
import static com.puppycrawl.tools.checkstyle.checks.OrderedPropertiesCheck.MSG_KEY;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.jre6.file.Files7;
import com.puppycrawl.tools.checkstyle.jre6.file.Path;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class OrderedPropertiesCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/checks/orderedproperties";
    }

    /**
     * Tests the ordinal work of a check.
     * Test of sub keys, repeating key pairs in wrong order
     */
    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(OrderedPropertiesCheck.class);
        final String[] expected = {
            "8: " + getCheckMessage(MSG_KEY, "key1", "key2"),
            "11: " + getCheckMessage(MSG_KEY, "B", "key4"),
            "14: " + getCheckMessage(MSG_KEY, "key3", "key5"),
            "17: " + getCheckMessage(MSG_KEY, "key3", "key5"),
        };
        verify(checkConfig, getPath("InputOrderedProperties.properties"), expected);
    }

    @Test
    public void testKeysOnly() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(OrderedPropertiesCheck.class);
        final String[] expected = {
            "3: " + getCheckMessage(MSG_KEY, "key1", "key2"),
        };
        verify(checkConfig, getPath("InputOrderedProperties1OrderKey.properties"), expected);
    }

    @Test
    public void testEmptyKeys() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(OrderedPropertiesCheck.class);
        final String[] expected = {
            "3: " + getCheckMessage(MSG_KEY, "key11", "key2"),
        };
        verify(checkConfig, getPath("InputOrderedProperties2EmptyValue.properties"), expected);
    }

    @Test
    public void testMalformedValue() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(OrderedPropertiesCheck.class);
        final String fileName =
                getPath("InputOrderedProperties3MalformedValue.properties");

        verify(checkConfig, fileName, "1: "
                + getCheckMessage(MSG_IO_EXCEPTION_KEY, fileName, "Malformed \\uxxxx encoding."));
    }

    @Test
    public void testCommentsMultiLine() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(OrderedPropertiesCheck.class);
        final String[] expected = {
            "5: " + getCheckMessage(MSG_KEY, "aKey", "multi.line"),
        };
        verify(checkConfig, getPath("InputOrderedProperties5CommentsMultiLine.properties"),
                expected);
    }

    @Test
    public void testLineNumberRepeatingPreviousKey() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(OrderedPropertiesCheck.class);
        final String[] expected = {
            "3: " + getCheckMessage(MSG_KEY, "a", "b"),
        };
        verify(checkConfig, getPath("InputOrderedProperties6RepeatingPreviousKey.properties"),
                expected);
    }

    @Test
    public void testShouldNotProcessFilesWithWrongFileExtension() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(OrderedPropertiesCheck.class);
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputOrderedProperties.txt"), expected);
    }

    /**
     * Tests IO exception, that can occur during reading of properties file.
     */
    @Test
    public void testIoException() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(OrderedPropertiesCheck.class);
        final OrderedPropertiesCheck check = new OrderedPropertiesCheck();
        check.configure(checkConfig);
        final String fileName =
                getPath("InputOrderedPropertiesCheckNotExisting.properties");
        final File file = new File(fileName);
        final FileText fileText = new FileText(file, Collections.<String>emptyList());
        final SortedSet<LocalizedMessage> messages =
                check.process(file, fileText);
        assertEquals("Wrong messages count: " + messages.size(),
                1, messages.size());
        final LocalizedMessage message = messages.iterator().next();
        final String retrievedMessage = messages.iterator().next().getKey();
        assertEquals("Message key '" + retrievedMessage
                        + "' is not valid", "unable.open.cause",
                retrievedMessage);
        assertEquals("Message '" + message.getMessage()
                        + "' is not valid", message.getMessage(),
                getCheckMessage(MSG_IO_EXCEPTION_KEY, fileName, getFileNotFoundDetail(file)));
    }

    /**
     * This test validates the PIT mutation of getIndex().
     * Here the for statement for
     * (int index = startLineNo; index < fileText.size(); index++)
     * will change to
     * for (int index = startLineNo; true; index++)
     * By creating a FileText having no lines it makes sure that
     * fileText.size() returning zero size.
     * This will keep the for loop intact.
     */
    @Test
    public void testKeepForLoopIntact() throws Exception {

        final DefaultConfiguration checkConfig = createModuleConfig(OrderedPropertiesCheck.class);
        final OrderedPropertiesCheck check = new OrderedPropertiesCheck();
        check.configure(checkConfig);
        final String fileName =
                getPath("InputOrderedProperties2EmptyValue.properties");
        final File file = new File(fileName);
        final FileText fileText = new FileText(file, Collections.<String>emptyList());
        final SortedSet<LocalizedMessage> messages = check.process(file, fileText);

        assertEquals("Wrong messages count: " + messages.size(),
                1, messages.size());
    }

    @Test
    public void testFileExtension() {

        final OrderedPropertiesCheck check = new OrderedPropertiesCheck();
        assertEquals("File extension should be set", ".properties", check.getFileExtensions()[0]);
    }

    /**
     * Method generates IOException details. It tries to a open file that does not exist.
     * @param file to be opened
     * @return localized detail message of {@link IOException}
     */
    private static String getFileNotFoundDetail(File file) {
        // Create exception to know detail message we should wait in LocalisedMessage
        try {
            Files7.newInputStream(new Path(file));
            throw new IllegalStateException("File " + file.getPath() + " should not exist");
        }
        catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }
}
