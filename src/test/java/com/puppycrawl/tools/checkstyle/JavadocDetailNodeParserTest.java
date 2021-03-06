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

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.jre6.charset.StandardCharsets;
import com.puppycrawl.tools.checkstyle.jre6.file.Files7;
import com.puppycrawl.tools.checkstyle.jre6.file.Paths;

public class JavadocDetailNodeParserTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/javadocdetailnodeparser";
    }

    @Test
    public void testParseJavadocAsDetailNode() throws Exception {
        final DetailAST ast = JavaParser.parseFile(
            new File(getPath("InputJavadocDetailNodeParser.java")),
            JavaParser.Options.WITH_COMMENTS)
                .getNextSibling().getFirstChild().getFirstChild();
        final JavadocDetailNodeParser parser = new JavadocDetailNodeParser();
        final JavadocDetailNodeParser.ParseStatus status = parser.parseJavadocAsDetailNode(ast);
        final String actual = toLfLineEnding(DetailNodeTreeStringPrinter.printTree(status.getTree(),
                "", ""));
        final String expected = toLfLineEnding(new String(Files7.readAllBytes(Paths.get(
                getPath("ExpectedJavadocDetailNodeParser.txt"))),
                StandardCharsets.UTF_8));
        assertEquals(expected, actual, "Invalid parse result");
    }

}
