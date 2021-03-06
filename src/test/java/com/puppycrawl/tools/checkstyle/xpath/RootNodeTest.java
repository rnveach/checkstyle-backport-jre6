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

package com.puppycrawl.tools.checkstyle.xpath;

import static com.google.common.truth.Truth.assertWithMessage;
import static com.puppycrawl.tools.checkstyle.internal.utils.XpathUtil.getXpathItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractPathTestSupport;
import com.puppycrawl.tools.checkstyle.JavaParser;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.om.NamespaceBinding;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.tree.iter.EmptyIterator;

public class RootNodeTest extends AbstractPathTestSupport {

    private static RootNode rootNode;

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/xpath/xpathmapper";
    }

    @Before
    public void init() throws Exception {
        final File file = new File(getPath("InputXpathMapperAst.java"));
        final DetailAST rootAst = JavaParser.parseFile(file, JavaParser.Options.WITHOUT_COMMENTS);
        rootNode = new RootNode(rootAst);
    }

    @Test
    public void testCompareOrder() {
        try {
            rootNode.compareOrder(null);
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testXpath() throws Exception {
        final String xpath = "/";
        final List<NodeInfo> nodes = getXpathItems(xpath, rootNode);
        assertEquals(1, nodes.size(), "Invalid number of nodes");
        final NodeInfo firstNode = nodes.get(0);
        assertTrue(firstNode instanceof RootNode,
                "Should return true, because selected node is RootNode");
        assertEquals(firstNode, rootNode, "Result node should have same reference as expected");
    }

    @Test
    public void testGetDepth() {
        assertWithMessage("Root node depth should be 0")
                .that(rootNode.getDepth())
                .isEqualTo(0);
    }

    @Test
    public void testGetTokenType() {
        assertEquals(TokenTypes.EOF, rootNode.getTokenType(), "Invalid token type");
    }

    @Test
    public void testGetLineNumber() {
        assertEquals(1, rootNode.getLineNumber(), "Invalid line number");
    }

    @Test
    public void testGetColumnNumber() {
        assertEquals(0, rootNode.getColumnNumber(), "Invalid column number");
    }

    @Test
    public void testGetLocalPart() {
        assertEquals("ROOT", rootNode.getLocalPart(), "Invalid local part");
    }

    @Test
    public void testIterate() {
        final AxisIterator following = rootNode.iterateAxis(AxisInfo.FOLLOWING);
        try {
            assertEquals(EmptyIterator.OfNodes.THE_INSTANCE, following,
                    "Result iterator does not match expected");
        }
        finally {
            following.close();
        }
        final AxisIterator followingSibling = rootNode.iterateAxis(AxisInfo.FOLLOWING_SIBLING);
        try {
            assertEquals(EmptyIterator.OfNodes.THE_INSTANCE, followingSibling,
                    "Result iterator does not match expected");
        }
        finally {
            followingSibling.close();
        }
        final AxisIterator preceding = rootNode.iterateAxis(AxisInfo.PRECEDING);
        try {
            assertEquals(EmptyIterator.OfNodes.THE_INSTANCE, preceding,
                    "Result iterator does not match expected");
        }
        finally {
            preceding.close();
        }
        final AxisIterator precedingSibling = rootNode.iterateAxis(AxisInfo.PRECEDING_SIBLING);
        try {
            assertEquals(EmptyIterator.OfNodes.THE_INSTANCE, precedingSibling,
                    "Result iterator does not match expected");
        }
        finally {
            precedingSibling.close();
        }
        final AxisIterator parent = rootNode.iterateAxis(AxisInfo.PARENT);
        try {
            assertEquals(EmptyIterator.OfNodes.THE_INSTANCE, parent,
                    "Result iterator does not match expected");
        }
        finally {
            parent.close();
        }
        final AxisIterator parentNull = rootNode.iterateAxis(AxisInfo.PARENT, null);
        try {
            assertEquals(EmptyIterator.OfNodes.THE_INSTANCE, parentNull,
                    "Result iterator does not match expected");
        }
        finally {
            parentNull.close();
        }
    }

    @Test
    public void testRootWithNullDetailAst() {
        final RootNode emptyRootNode = new RootNode(null);
        assertFalse(emptyRootNode.hasChildNodes(), "Empty node should not have children");

        final AxisIterator descendant = emptyRootNode.iterateAxis(AxisInfo.DESCENDANT);
        try {
            assertEquals(EmptyIterator.OfNodes.THE_INSTANCE, descendant,
                    "Result iterator does not match expected");
        }
        finally {
            descendant.close();
        }
        final AxisIterator child = emptyRootNode.iterateAxis(AxisInfo.CHILD);
        try {
            assertEquals(EmptyIterator.OfNodes.THE_INSTANCE, child,
                    "Result iterator does not match expected");
        }
        finally {
            child.close();
        }
    }

    @Test
    public void testIterateWithoutArgument() {
        try {
            rootNode.iterate();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                    ex.getMessage(),
                    "Invalid exception message");
        }
    }

    @Test
    public void testGetStringValue() {
        try {
            rootNode.getStringValue();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                    ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testGetAttributeValue() {
        try {
            rootNode.getAttributeValue("", "");
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testGetDeclaredNamespaces() {
        final NamespaceBinding[] namespaceBindings = {new NamespaceBinding("prefix", "uri")};
        try {
            rootNode.getDeclaredNamespaces(namespaceBindings);
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testIsId() {
        try {
            rootNode.isId();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testIsIdref() {
        try {
            rootNode.isIdref();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testIsNilled() {
        try {
            rootNode.isNilled();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testIsStreamed() {
        try {
            rootNode.isStreamed();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testGetConfiguration() {
        try {
            rootNode.getConfiguration();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testSetSystemId() {
        try {
            rootNode.setSystemId("1");
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testGetSystemId() {
        try {
            rootNode.getSystemId();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testGetPublicId() {
        try {
            rootNode.getPublicId();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testBaseUri() {
        try {
            rootNode.getBaseURI();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testSaveLocation() {
        try {
            rootNode.saveLocation();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testComparePosition() {
        try {
            rootNode.comparePosition(null);
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals(
                "Operation is not supported",
                ex.getMessage(),
                "Invalid exception message");
        }
    }

    @Test
    public void testHead() {
        try {
            rootNode.head();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals(
                "Operation is not supported",
                ex.getMessage(),
                "Invalid exception message");
        }
    }

    @Test
    public void testGetStringValueCs() {
        try {
            rootNode.getStringValueCS();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testFingerprint() {
        try {
            rootNode.getFingerprint();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testGetDisplayName() {
        try {
            rootNode.getDisplayName();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testGetPrefix() {
        try {
            rootNode.getPrefix();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testGetSchemaType() {
        try {
            rootNode.getSchemaType();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testAtomize() {
        try {
            rootNode.atomize();
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testGenerateId() {
        try {
            rootNode.generateId(null);
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testCopy() {
        try {
            rootNode.copy(null, -1, null);
            fail("Exception is excepted");
        }
        catch (UnsupportedOperationException ex) {
            assertEquals("Operation is not supported",
                ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testSameNodeInfo() {
        assertTrue(rootNode.isSameNodeInfo(rootNode),
                "Should return true, because object is being compared to itself");
        assertFalse(rootNode.isSameNodeInfo(null),
                "Should return false, because object does not equal null");
    }
}
