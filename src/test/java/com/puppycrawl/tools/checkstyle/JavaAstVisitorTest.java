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

import static com.google.common.truth.Truth.assertWithMessage;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.grammar.java.JavaLanguageParserBaseVisitor;

public class JavaAstVisitorTest {

    /**
     * If a visit method is not overridden, we should explain why we do not 'visit' the
     * parse tree at this node and construct an AST. Reasons could include that we have
     * no terminal symbols (tokens) in the corresponding production rule, or that
     * we handle the construction of this particular AST in it's parent node. If we
     * have a production rule where we have terminal symbols (tokens), but we do not build
     * an AST from tokens in the rule context, the rule is extraneous.
     */
    private static final List<String> VISIT_METHODS_NOT_OVERRIDDEN = Arrays.asList(
            // no tokens in production rule, so no AST to build
            "visitClassOrInterfaceOrPrimitiveType",
            "visitNonWildcardTypeArgs",
            "visitStat",
            "visitAnnotationConstantRest",
            "visitSwitchLabeledRule",
            "visitLocalVariableDeclaration",
            "visitTypes",
            "visitSwitchStat",
            "visitSwitchPrimary",
            "visitClassDef",
            "visitInterfaceMemberDeclaration",
            "visitMemberDeclaration",
            "visitLiteralPrimary",
            "visitPatternDefinition",
            "visitLocalType",
            "visitLocalTypeDeclaration",
            "visitRecordBodyDeclaration",
            "visitResource",
            "visitVariableInitializer",
            "visitLambdaBody",

            // AST built in parent rule
            "visitCreatedNameExtended",
            "visitSuperSuffixSimple",
            "visitFieldAccessNoIdent",
            "visitClassType",
            "visitClassOrInterfaceTypeExtended",
            "visitQualifiedNameExtended"
    );

    @Test
    public void testAllVisitMethodsAreOverridden() {
        final Method[] baseVisitMethods = JavaLanguageParserBaseVisitor
                .class.getDeclaredMethods();
        final Method[] visitMethods = JavaAstVisitor.class.getDeclaredMethods();

        final Set<String> filteredBaseVisitMethodNames = new HashSet<String>();
        for (Method method : baseVisitMethods) {
            if (!VISIT_METHODS_NOT_OVERRIDDEN.contains(method.getName()) && method.getName().contains("visit") && method.getModifiers() == Modifier.PUBLIC) {
                filteredBaseVisitMethodNames.add(method.getName());
            }
        }

        final Set<String> filteredVisitMethodNames = new HashSet<String>();
        for (Method method : visitMethods) {
            if (method.getName().contains("visit") && method.getModifiers() == Modifier.PUBLIC) {
                filteredVisitMethodNames.add(method.getName());
            }
        }

        // remove overridden 'visit' method from ParseTreeVisitor interface in JavaAstVisitor
        filteredVisitMethodNames.remove("visit");

        final String message = "Visit methods in 'JavaLanguageParserBaseVisitor' generated from "
                + "production rules and labeled alternatives in 'JavaLanguageParser.g4' should "
                + "be overridden in 'JavaAstVisitor' or be added to 'VISIT_METHODS_NOT_OVERRIDDEN' "
                + "with comment explaining why.";

        assertWithMessage(message)
                .that(filteredVisitMethodNames)
                .containsExactlyElementsIn(filteredBaseVisitMethodNames);
    }

    @Test
    public void testOrderOfVisitMethodsAndProductionRules() throws Exception {
        // Order of BaseVisitor's generated 'visit' methods match the order of
        // production rules in 'JavaLanguageParser.g4'.
        final String baseVisitorFilename = "target/generated-sources/antlr/com/puppycrawl"
                + "/tools/checkstyle/grammar/java/JavaLanguageParserBaseVisitor.java";
        final DetailAST baseVisitorAst = JavaParser.parseFile(new File(baseVisitorFilename),
                            JavaParser.Options.WITHOUT_COMMENTS);

        final String visitorFilename = "src/main/java/com/puppycrawl/tools/checkstyle"
                + "/JavaAstVisitor.java";
        final DetailAST visitorAst = JavaParser.parseFile(new File(visitorFilename),
                            JavaParser.Options.WITHOUT_COMMENTS);

        final List<String> orderedBaseVisitorMethodNames =
                getOrderedVisitMethodNames(baseVisitorAst);
        final List<String> orderedVisitorMethodNames =
                getOrderedVisitMethodNames(visitorAst);

        orderedBaseVisitorMethodNames.removeAll(VISIT_METHODS_NOT_OVERRIDDEN);

        // remove overridden 'visit' method from ParseTreeVisitor interface in JavaAstVisitor
        orderedVisitorMethodNames.remove("visit");

        assertWithMessage("Visit methods in 'JavaAstVisitor' should appear in same order as "
                + "production rules and labeled alternatives in 'JavaLanguageParser.g4'.")
                .that(orderedVisitorMethodNames)
                .containsExactlyElementsIn(orderedBaseVisitorMethodNames)
                .inOrder();
    }

    /**
     * Finds all {@code visit...} methods in a source file, and collects
     * the method names into a list. This method counts on the simple structure
     * of 'JavaAstVisitor' and 'JavaLanguageParserBaseVisitor'.
     *
     * @param root the root of the AST to extract method names from
     * @return list of all {@code visit...} method names
     */
    private static List<String> getOrderedVisitMethodNames(DetailAST root) {
        final List<String> orderedVisitMethodNames = new ArrayList<String>();

        DetailAST classDef = root.getFirstChild();
        while (classDef.getType() != TokenTypes.CLASS_DEF) {
            classDef = classDef.getNextSibling();
        }

        final DetailAST objBlock = classDef.findFirstToken(TokenTypes.OBJBLOCK);
        DetailAST objBlockChild = objBlock.findFirstToken(TokenTypes.METHOD_DEF);
        while (objBlockChild != null) {
            if (isVisitMethod(objBlockChild)) {
                orderedVisitMethodNames.add(objBlockChild
                        .findFirstToken(TokenTypes.IDENT)
                        .getText());
            }
            objBlockChild = objBlockChild.getNextSibling();
        }
        return orderedVisitMethodNames;
    }

    /**
     * Checks if given AST is a visit method.
     *
     * @param objBlockChild AST to check
     * @return true if AST is a visit method
     */
    private static boolean isVisitMethod(DetailAST objBlockChild) {
        return objBlockChild.getType() == TokenTypes.METHOD_DEF
                && objBlockChild.findFirstToken(TokenTypes.IDENT).getText().contains("visit");
    }

    @Test
    public void testNullSelfInAddLastSibling() throws Exception {
        final Method addLastSibling = JavaAstVisitor.class
                .getDeclaredMethod("addLastSibling", DetailAstImpl.class, DetailAstImpl.class);
        addLastSibling.setAccessible(true);
        assertWithMessage("Method should not throw exception.")
                .that(addLastSibling.invoke(JavaAstVisitor.class, null, null))
                .isNull();
    }
}
