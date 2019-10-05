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

package com.puppycrawl.tools.checkstyle.api;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.JavaParser;
import com.puppycrawl.tools.checkstyle.jre6.charset.StandardCharsets;

public class FullIdentTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/api/fullident/";
    }

    @Test
    public void testToString() {
        final DetailAstImpl ast = new DetailAstImpl();
        ast.setType(TokenTypes.LITERAL_NEW);
        ast.setColumnNo(14);
        ast.setLineNo(15);
        ast.setText("MyTest");

        final FullIdent indent = FullIdent.createFullIdent(ast);
        Assert.assertEquals("Invalid full indent", "MyTest[15x14]", indent.toString());
        Assert.assertEquals("Invalid text", "MyTest", indent.getText());
        Assert.assertEquals("Invalid line", 15, indent.getLineNo());
        Assert.assertEquals("Invalid column", 14, indent.getColumnNo());
    }

    @Test
    public void testCreateFullIdentBelow() {
        final DetailAST ast = new DetailAstImpl();

        final FullIdent indent = FullIdent.createFullIdentBelow(ast);
        Assert.assertEquals("Invalid full indent", "", indent.getText());
    }

    @Test
    public void testGetDetailAst() throws Exception {
        final FileText testFileText = new FileText(
                new File(getPath("InputFullIdentTestArrayType.java")).getAbsoluteFile(),
                System.getProperty("file.encoding", StandardCharsets.UTF_8.name()));
        final DetailAST packageDefinitionNode = JavaParser.parse(new FileContents(testFileText));
        final DetailAST packageName = packageDefinitionNode.getFirstChild().getNextSibling();
        final FullIdent ident = FullIdent.createFullIdent(packageName);
        Assert.assertEquals("Invalid full indent", "com[1x8]", ident.getDetailAst().toString());
    }

    @Test
    public void testNonValidCoordinatesWithNegative() {
        final FullIdent fullIdent = prepareFullIdentWithCoordinates(14, 15);
        Assert.assertEquals("Invalid full indent", "MyTest.MyTestik[15x14]", fullIdent.toString());
    }

    @Test
    public void testNonValidCoordinatesWithZero() {
        final FullIdent fullIdent = prepareFullIdentWithCoordinates(0, 0);
        Assert.assertEquals("Invalid full indent", "MyTest.MyTestik[15x14]", fullIdent.toString());
    }

    @Test
    public void testWithArrayCreateFullIdentWithArrayDeclare() throws Exception {
        final FileText testFileText = new FileText(
                new File(getPath("InputFullIdentTestArrayType.java")).getAbsoluteFile(),
                System.getProperty("file.encoding", StandardCharsets.UTF_8.name()));
        final DetailAST packageDefinitionNode = JavaParser.parse(new FileContents(testFileText));
        final DetailAST arrayDeclarator = packageDefinitionNode.getNextSibling()
                .findFirstToken(TokenTypes.OBJBLOCK)
                .findFirstToken(TokenTypes.VARIABLE_DEF)
                .findFirstToken(TokenTypes.TYPE)
                .getFirstChild();
        final FullIdent ident = FullIdent.createFullIdent(arrayDeclarator);
        Assert.assertEquals("Invalid full indent", "int[][][5x12]", ident.toString());
    }

    private static FullIdent prepareFullIdentWithCoordinates(int columnNo, int lineNo) {
        final DetailAstImpl ast = new DetailAstImpl();
        ast.setType(TokenTypes.DOT);
        ast.setColumnNo(1);
        ast.setLineNo(2);
        ast.setText("Root");

        final DetailAstImpl ast2 = new DetailAstImpl();
        ast2.setType(TokenTypes.LE);
        ast2.setColumnNo(columnNo);
        ast2.setLineNo(lineNo);
        ast2.setText("MyTestik");

        final DetailAstImpl ast1 = new DetailAstImpl();
        ast1.setType(TokenTypes.LITERAL_NEW);
        ast1.setColumnNo(14);
        ast1.setLineNo(15);
        ast1.setText("MyTest");

        ast.addChild(ast1);
        ast.addChild(ast2);

        return FullIdent.createFullIdent(ast);
    }

}
