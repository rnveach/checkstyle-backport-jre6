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

package com.puppycrawl.tools.checkstyle;

import static com.puppycrawl.tools.checkstyle.internal.TestUtils.assertUtilsClassHasPrivateConstructor;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import antlr.NoViableAltException;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.jre6.charset.StandardCharsets;
import com.puppycrawl.tools.checkstyle.jre6.file.Files7;
import com.puppycrawl.tools.checkstyle.jre6.file.Paths;

public class AstTreeStringPrinterTest {

    private static String getNonCompilablePath(String filename) {
        return "src/test/resources-noncompilable/com/puppycrawl/tools/checkstyle/" + filename;
    }

    private static String getPath(String filename) {
        return "src/test/resources/com/puppycrawl/tools/checkstyle/astprinter/" + filename;
    }

    @Test
    public void testIsProperUtilsClass() throws Exception {
        assertUtilsClassHasPrivateConstructor(AstTreeStringPrinter.class);
    }

    @Test
    public void testParseFileThrowable() throws Exception {
        try {
            AstTreeStringPrinter.printFileAst(
                new File(getNonCompilablePath("InputAstTreeStringPrinter.java")), false);
            Assert.fail("exception expected");
        }
        catch (CheckstyleException ex) {
            Assert.assertSame(NoViableAltException.class, ex.getCause().getClass());
            Assert.assertEquals("unexpected token: classD", ex.getCause().getMessage());
        }
    }

    @Test
    public void testParseFile() throws Exception {
        final String actual = AstTreeStringPrinter.printFileAst(
            new File(getPath("InputAstTreeStringPrinterComments.java")), false);
        final String expected = new String(Files7.readAllBytes(Paths.get(
            getPath("expectedInputAstTreeStringPrinter.txt"))), StandardCharsets.UTF_8);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseFileWithComments() throws Exception {
        final String actual = AstTreeStringPrinter.printFileAst(
            new File(getPath("InputAstTreeStringPrinterComments.java")), true)
                .replaceAll("\\\\r\\\\n", "\\\\n");
        final String expected = new String(Files7.readAllBytes(Paths.get(
                getPath("expectedInputAstTreeStringPrinterComments.txt"))), StandardCharsets.UTF_8)
                .replaceAll("\\\\r\\\\n", "\\\\n");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseFileWithJavadoc1() throws Exception {
        final String actual = AstTreeStringPrinter.printJavaAndJavadocTree(
                new File(getPath("InputAstTreeStringPrinterJavadoc.java")))
                .replaceAll("\\\\r\\\\n", "\\\\n");
        final String expected = new String(Files7.readAllBytes(Paths.get(
                getPath("expectedInputAstTreeStringPrinterJavadoc.txt"))),
                        StandardCharsets.UTF_8)
                .replaceAll("\\\\r\\\\n", "\\\\n");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseFileWithJavadoc2() throws Exception {
        final String actual = AstTreeStringPrinter.printJavaAndJavadocTree(
                new File(getPath("InputAstTreeStringPrinterJavaAndJavadoc.java")))
                .replaceAll("\\\\r\\\\n", "\\\\n");
        final String expected = new String(Files7.readAllBytes(Paths.get(
                getPath("expectedInputAstTreeStringPrinterJavaAndJavadoc.txt"))),
                        StandardCharsets.UTF_8)
                .replaceAll("\\\\r\\\\n", "\\\\n");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseFileWithJavadoc3() throws Exception {
        final String actual = AstTreeStringPrinter.printJavaAndJavadocTree(
                new File(getPath("InputAstTreeStringPrinterAttributesAndMethodsJavadoc.java")))
                .replaceAll("\\\\r\\\\n", "\\\\n");
        final String expected = new String(Files7.readAllBytes(Paths.get(
                getPath("expectedInputAstTreeStringPrinterAttributesAndMethodsJavadoc.txt"))),
                        StandardCharsets.UTF_8)
                .replaceAll("\\\\r\\\\n", "\\\\n");
        Assert.assertEquals(expected, actual);
    }

}
