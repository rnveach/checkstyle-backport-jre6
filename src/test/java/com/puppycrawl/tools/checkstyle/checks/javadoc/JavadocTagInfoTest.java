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

package com.puppycrawl.tools.checkstyle.checks.javadoc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class JavadocTagInfoTest {

    /* Additional test for jacoco, since valueOf()
     * is generated by javac and jacoco reports that
     * valueOf() is uncovered.
     */
    @Test
    public void testJavadocTagInfoValueOf() {
        final JavadocTagInfo tag = JavadocTagInfo.valueOf("AUTHOR");
        assertEquals("Invalid valueOf result", JavadocTagInfo.AUTHOR, tag);
    }

    /* Additional test for jacoco, since valueOf()
     * is generated by javac and jacoco reports that
     * valueOf() is uncovered.
     */
    @Test
    public void testTypeValueOf() {
        final JavadocTagInfo.Type type = JavadocTagInfo.Type.valueOf("BLOCK");
        assertEquals("Invalid valueOf result", JavadocTagInfo.Type.BLOCK, type);
    }

    /* Additional test for jacoco, since values()
     * is generated by javac and jacoco reports that
     * values() is uncovered.
     */
    @Test
    public void testTypeValues() {
        final JavadocTagInfo.Type[] expected = {
            JavadocTagInfo.Type.BLOCK,
            JavadocTagInfo.Type.INLINE,
        };
        final JavadocTagInfo.Type[] actual = JavadocTagInfo.Type.values();
        assertArrayEquals("Invalid Type values", expected, actual);
    }

    @Test
    public void testAuthor() {
        final DetailAST ast = new DetailAST();

        final int[] validTypes = {
            TokenTypes.PACKAGE_DEF,
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ANNOTATION_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue("Invalid ast type for current tag: " + ast.getType(),
                    JavadocTagInfo.AUTHOR.isValidOn(ast));
        }

        ast.setType(TokenTypes.LAMBDA);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.AUTHOR.isValidOn(ast));
    }

    @Test
    public void testOthers() throws Exception {
        final JavadocTagInfo[] tags = {
            JavadocTagInfo.CODE,
            JavadocTagInfo.DOC_ROOT,
            JavadocTagInfo.LINK,
            JavadocTagInfo.LINKPLAIN,
            JavadocTagInfo.LITERAL,
            JavadocTagInfo.SEE,
            JavadocTagInfo.SINCE,
            JavadocTagInfo.VALUE,
        };
        for (JavadocTagInfo tagInfo : tags) {
            final DetailAST astParent = new DetailAST();
            astParent.setType(TokenTypes.LITERAL_CATCH);

            final DetailAST ast = new DetailAST();
            final Method setParent = ast.getClass().getDeclaredMethod("setParent", DetailAST.class);
            setParent.setAccessible(true);
            setParent.invoke(ast, astParent);

            final int[] validTypes = {
                TokenTypes.PACKAGE_DEF,
                TokenTypes.CLASS_DEF,
                TokenTypes.INTERFACE_DEF,
                TokenTypes.ENUM_DEF,
                TokenTypes.ANNOTATION_DEF,
                TokenTypes.METHOD_DEF,
                TokenTypes.CTOR_DEF,
                TokenTypes.VARIABLE_DEF,
            };
            for (int type: validTypes) {
                ast.setType(type);
                assertTrue("Invalid ast type for current tag: " + ast.getType(),
                        tagInfo.isValidOn(ast));
            }

            astParent.setType(TokenTypes.SLIST);
            ast.setType(TokenTypes.VARIABLE_DEF);
            assertFalse("Should return false when ast type is invalid for current tag",
                    tagInfo.isValidOn(ast));

            ast.setType(TokenTypes.PARAMETER_DEF);
            assertFalse("Should return false when ast type is invalid for current tag",
                    tagInfo.isValidOn(ast));
        }
    }

    @Test
    public void testDeprecated() throws Exception {
        final DetailAST ast = new DetailAST();
        final DetailAST astParent = new DetailAST();
        astParent.setType(TokenTypes.LITERAL_CATCH);
        final Method setParent = ast.getClass().getDeclaredMethod("setParent", DetailAST.class);
        setParent.setAccessible(true);
        setParent.invoke(ast, astParent);

        final int[] validTypes = {
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ANNOTATION_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.ENUM_CONSTANT_DEF,
            TokenTypes.ANNOTATION_FIELD_DEF,
            TokenTypes.VARIABLE_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue("Invalid ast type for current tag: " + ast.getType(),
                    JavadocTagInfo.DEPRECATED.isValidOn(ast));
        }

        astParent.setType(TokenTypes.SLIST);
        ast.setType(TokenTypes.VARIABLE_DEF);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.DEPRECATED.isValidOn(ast));

        ast.setType(TokenTypes.PARAMETER_DEF);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.DEPRECATED.isValidOn(ast));
    }

    @Test
    public void testSerial() throws Exception {
        final DetailAST ast = new DetailAST();
        final DetailAST astParent = new DetailAST();
        astParent.setType(TokenTypes.LITERAL_CATCH);
        final Method setParent = ast.getClass().getDeclaredMethod("setParent", DetailAST.class);
        setParent.setAccessible(true);
        setParent.invoke(ast, astParent);

        final int[] validTypes = {
            TokenTypes.VARIABLE_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue("Invalid ast type for current tag: " + ast.getType(),
                    JavadocTagInfo.SERIAL.isValidOn(ast));
        }

        astParent.setType(TokenTypes.SLIST);
        ast.setType(TokenTypes.VARIABLE_DEF);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.SERIAL.isValidOn(ast));

        ast.setType(TokenTypes.PARAMETER_DEF);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.SERIAL.isValidOn(ast));
    }

    @Test
    public void testException() {
        final DetailAST ast = new DetailAST();

        final int[] validTypes = {
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue("Invalid ast type for current tag: " + ast.getType(),
                    JavadocTagInfo.EXCEPTION.isValidOn(ast));
        }

        ast.setType(TokenTypes.LAMBDA);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.EXCEPTION.isValidOn(ast));
    }

    @Test
    public void testThrows() {
        final DetailAST ast = new DetailAST();

        final int[] validTypes = {
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue("Invalid ast type for current tag: " + ast.getType(),
                    JavadocTagInfo.THROWS.isValidOn(ast));
        }

        ast.setType(TokenTypes.LAMBDA);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.THROWS.isValidOn(ast));
    }

    @Test
    public void testVersions() {
        final DetailAST ast = new DetailAST();

        final int[] validTypes = {
            TokenTypes.PACKAGE_DEF,
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ANNOTATION_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue("Invalid ast type for current tag: " + ast.getType(),
                    JavadocTagInfo.VERSION.isValidOn(ast));
        }

        ast.setType(TokenTypes.LAMBDA);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.VERSION.isValidOn(ast));
    }

    @Test
    public void testParam() {
        final DetailAST ast = new DetailAST();

        final int[] validTypes = {
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue("Invalid ast type for current tag: " + ast.getType(),
                    JavadocTagInfo.PARAM.isValidOn(ast));
        }

        ast.setType(TokenTypes.LAMBDA);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.PARAM.isValidOn(ast));
    }

    @Test
    public void testReturn() {
        final DetailAST ast = new DetailAST();
        final DetailAST astChild = new DetailAST();
        astChild.setType(TokenTypes.TYPE);
        ast.setFirstChild(astChild);
        final DetailAST astChild2 = new DetailAST();
        astChild2.setType(TokenTypes.LITERAL_INT);
        astChild.setFirstChild(astChild2);

        final int[] validTypes = {
            TokenTypes.METHOD_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue("Invalid ast type for current tag: " + ast.getType(),
                    JavadocTagInfo.RETURN.isValidOn(ast));
        }

        astChild2.setType(TokenTypes.LITERAL_VOID);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.RETURN.isValidOn(ast));

        ast.setType(TokenTypes.LAMBDA);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.RETURN.isValidOn(ast));
    }

    @Test
    public void testSerialField() {
        final DetailAST ast = new DetailAST();
        final DetailAST astChild = new DetailAST();
        astChild.setType(TokenTypes.TYPE);
        ast.setFirstChild(astChild);
        final DetailAST astChild2 = new DetailAST();
        astChild2.setType(TokenTypes.ARRAY_DECLARATOR);
        astChild2.setText("ObjectStreamField");
        astChild.setFirstChild(astChild2);

        final int[] validTypes = {
            TokenTypes.VARIABLE_DEF,
        };
        for (int type: validTypes) {
            ast.setType(type);
            assertTrue("Invalid ast type for current tag: " + ast.getType(),
                    JavadocTagInfo.SERIAL_FIELD.isValidOn(ast));
        }

        astChild2.setText("1111");
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.SERIAL_FIELD.isValidOn(ast));

        astChild2.setType(TokenTypes.LITERAL_VOID);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.SERIAL_FIELD.isValidOn(ast));

        ast.setType(TokenTypes.LAMBDA);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.SERIAL_FIELD.isValidOn(ast));
    }

    @Test
    public void testSerialData() {
        final DetailAST ast = new DetailAST();
        ast.setType(TokenTypes.METHOD_DEF);
        final DetailAST astChild = new DetailAST();
        astChild.setType(TokenTypes.IDENT);
        astChild.setText("writeObject");
        ast.setFirstChild(astChild);

        final String[] validNames = {
            "writeObject",
            "readObject",
            "writeExternal",
            "readExternal",
            "writeReplace",
            "readResolve",
        };
        for (String name: validNames) {
            astChild.setText(name);
            assertTrue("Invalid ast type for current tag: " + ast.getType(),
                    JavadocTagInfo.SERIAL_DATA.isValidOn(ast));
        }

        astChild.setText("1111");
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.SERIAL_DATA.isValidOn(ast));

        ast.setType(TokenTypes.LAMBDA);
        assertFalse("Should return false when ast type is invalid for current tag",
                JavadocTagInfo.SERIAL_DATA.isValidOn(ast));
    }

    @Test
    public void testCoverage() {
        assertEquals("Invalid type", JavadocTagInfo.Type.BLOCK, JavadocTagInfo.VERSION.getType());

        assertEquals("Invalid toString result", "text [@version] name [version] type [BLOCK]",
            JavadocTagInfo.VERSION.toString());

        try {
            JavadocTagInfo.fromName(null);
            fail("IllegalArgumentException is expected");
        }
        catch (IllegalArgumentException ex) {
            assertEquals("Invalid exception message",
                    "the name is null", ex.getMessage());
        }

        try {
            JavadocTagInfo.fromName("myname");
            fail("IllegalArgumentException is expected");
        }
        catch (IllegalArgumentException ex) {
            assertEquals("Invalid exception message",
                    "the name [myname] is not a valid Javadoc tag name", ex.getMessage());
        }

        try {
            JavadocTagInfo.fromText(null);
            fail("IllegalArgumentException is expected");
        }
        catch (IllegalArgumentException ex) {
            assertEquals("Invalid exception message", "the text is null", ex.getMessage());
        }

        try {
            JavadocTagInfo.fromText("myname");
            fail("IllegalArgumentException is expected");
        }
        catch (IllegalArgumentException ex) {
            assertEquals("Invalid exception message",
                    "the text [myname] is not a valid Javadoc tag text", ex.getMessage());
        }

        assertEquals("Invalid fromText result",
                JavadocTagInfo.VERSION, JavadocTagInfo.fromText("@version"));
    }
}
