////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
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

package com.puppycrawl.tools.checkstyle.checks.blocks;

import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

/**
 * <p>
 * Checks for braces around code blocks.
 * </p>
 * <ul>
 * <li>
 * Property {@code allowSingleLineStatement} - allow single-line statements without braces.
 * Default value is {@code false}.
 * </li>
 * <li>
 * Property {@code allowEmptyLoopBody} - allow loops with empty bodies.
 * Default value is {@code false}.
 * </li>
 * <li>
 * Property {@code tokens} - tokens to check
 * Default value is:
 * <a href="https://checkstyle.org/apidocs/com/puppycrawl/tools/checkstyle/api/TokenTypes.html#LITERAL_DO">
 * LITERAL_DO</a>,
 * <a href="https://checkstyle.org/apidocs/com/puppycrawl/tools/checkstyle/api/TokenTypes.html#LITERAL_ELSE">
 * LITERAL_ELSE</a>,
 * <a href="https://checkstyle.org/apidocs/com/puppycrawl/tools/checkstyle/api/TokenTypes.html#LITERAL_FOR">
 * LITERAL_FOR</a>,
 * <a href="https://checkstyle.org/apidocs/com/puppycrawl/tools/checkstyle/api/TokenTypes.html#LITERAL_IF">
 * LITERAL_IF</a>,
 * <a href="https://checkstyle.org/apidocs/com/puppycrawl/tools/checkstyle/api/TokenTypes.html#LITERAL_WHILE">
 * LITERAL_WHILE</a>.
 * </li>
 * </ul>
 * <p>
 * To configure the check:
 * </p>
 * <pre>
 * &lt;module name="NeedBraces"/&gt;
 * </pre>
 * <p>
 * To configure the check for {@code if} and {@code else} blocks:
 * </p>
 * <pre>
 * &lt;module name=&quot;NeedBraces&quot;&gt;
 *   &lt;property name=&quot;tokens&quot; value=&quot;LITERAL_IF, LITERAL_ELSE&quot;/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * To configure the check to allow single-line statements
 * ({@code if, while, do-while, for}) without braces:
 * </p>
 * <pre>
 * &lt;module name=&quot;NeedBraces&quot;&gt;
 *   &lt;property name=&quot;allowSingleLineStatement&quot; value=&quot;true&quot;/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * Next statements won't be violated by check:
 * </p>
 * <pre>
 * if (obj.isValid()) return true; // OK
 * while (obj.isValid()) return true; // OK
 * do this.notify(); while (o != null); // OK
 * for (int i = 0; ; ) this.notify(); // OK
 * </pre>
 * <p>
 * To configure the check to allow {@code case, default} single-line statements without braces:
 * </p>
 * <pre>
 * &lt;module name=&quot;NeedBraces&quot;&gt;
 *   &lt;property name=&quot;tokens&quot; value=&quot;LITERAL_CASE, LITERAL_DEFAULT&quot;/&gt;
 *   &lt;property name=&quot;allowSingleLineStatement&quot; value=&quot;true&quot;/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * Next statements won't be violated by check:
 * </p>
 * <pre>
 * switch (num) {
 *   case 1: counter++; break; // OK
 *   case 6: counter += 10; break; // OK
 *   default: counter = 100; break; // OK
 * }
 * </pre>
 * <p>
 * To configure the check to allow loops ({@code while, for}) with empty bodies:
 * </p>
 * <pre>
 * &lt;module name=&quot;NeedBraces&quot;&gt;
 *   &lt;property name=&quot;allowEmptyLoopBody&quot; value=&quot;true&quot;/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * Next statements won't be violated by check:
 * </p>
 * <pre>
 * while (value.incrementValue() &lt; 5); // OK
 * for(int i = 0; i &lt; 10; value.incrementValue()); // OK
 * </pre>
 * <p>
 * To configure the check to lambdas:
 * </p>
 * <pre>
 * &lt;module name=&quot;NeedBraces&quot;&gt;
 *   &lt;property name=&quot;tokens&quot; value=&quot;LAMBDA&quot;/&gt;
 *   &lt;property name=&quot;allowSingleLineStatement&quot; value=&quot;true&quot;/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * Results in following:
 * </p>
 * <pre>
 * allowedFuture.addCallback(result -&gt; assertEquals("Invalid response",
 *   EnumSet.of(HttpMethod.GET, HttpMethod.OPTIONS), result), // violation, lambda spans 2 lines
 *   ex -&gt; fail(ex.getMessage())); // OK
 *
 * allowedFuture.addCallback(result -&gt; {
 *   return assertEquals("Invalid response",
 *     EnumSet.of(HttpMethod.GET, HttpMethod.OPTIONS), result);
 *   }, // OK
 *   ex -&gt; fail(ex.getMessage()));
 * </pre>
 *
 * @since 3.0
 */
@StatelessCheck
public class NeedBracesCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_NEED_BRACES = "needBraces";

    /**
     * Allow single-line statements without braces.
     */
    private boolean allowSingleLineStatement;

    /**
     * Allow loops with empty bodies.
     */
    private boolean allowEmptyLoopBody;

    /**
     * Setter to allow single-line statements without braces.
     * @param allowSingleLineStatement Check's option for skipping single-line statements
     */
    public void setAllowSingleLineStatement(boolean allowSingleLineStatement) {
        this.allowSingleLineStatement = allowSingleLineStatement;
    }

    /**
     * Setter to allow loops with empty bodies.
     * @param allowEmptyLoopBody Check's option for allowing loops with empty body.
     */
    public void setAllowEmptyLoopBody(boolean allowEmptyLoopBody) {
        this.allowEmptyLoopBody = allowEmptyLoopBody;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.LITERAL_DO,
            TokenTypes.LITERAL_ELSE,
            TokenTypes.LITERAL_FOR,
            TokenTypes.LITERAL_IF,
            TokenTypes.LITERAL_WHILE,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] {
            TokenTypes.LITERAL_DO,
            TokenTypes.LITERAL_ELSE,
            TokenTypes.LITERAL_FOR,
            TokenTypes.LITERAL_IF,
            TokenTypes.LITERAL_WHILE,
            TokenTypes.LITERAL_CASE,
            TokenTypes.LITERAL_DEFAULT,
            TokenTypes.LAMBDA,
        };
    }

    @Override
    public int[] getRequiredTokens() {
        return CommonUtil.EMPTY_INT_ARRAY;
    }

    @Override
    public void visitToken(DetailAST ast) {
        final boolean hasNoSlist = ast.findFirstToken(TokenTypes.SLIST) == null;
        if (hasNoSlist && !isSkipStatement(ast) && isBracesNeeded(ast)) {
            log(ast, MSG_KEY_NEED_BRACES, ast.getText());
        }
    }

    /**
     * Checks if token needs braces.
     * Some tokens have additional conditions:
     * <ul>
     *     <li>{@link TokenTypes#LITERAL_FOR}</li>
     *     <li>{@link TokenTypes#LITERAL_WHILE}</li>
     *     <li>{@link TokenTypes#LITERAL_CASE}</li>
     *     <li>{@link TokenTypes#LITERAL_DEFAULT}</li>
     *     <li>{@link TokenTypes#LITERAL_ELSE}</li>
     * </ul>
     * For all others default value {@code true} is returned.
     * @param ast token to check
     * @return result of additional checks for specific token types,
     * {@code true} if there is no additional checks for token
     */
    private boolean isBracesNeeded(DetailAST ast) {
        final boolean result;
        switch (ast.getType()) {
            case TokenTypes.LITERAL_FOR:
            case TokenTypes.LITERAL_WHILE:
                result = !isEmptyLoopBodyAllowed(ast);
                break;
            case TokenTypes.LITERAL_CASE:
            case TokenTypes.LITERAL_DEFAULT:
                result = hasUnbracedStatements(ast);
                break;
            case TokenTypes.LITERAL_ELSE:
                result = ast.findFirstToken(TokenTypes.LITERAL_IF) == null;
                break;
            default:
                result = true;
                break;
        }
        return result;
    }

    /**
     * Checks if current loop has empty body and can be skipped by this check.
     * @param ast for, while statements.
     * @return true if current loop can be skipped by check.
     */
    private boolean isEmptyLoopBodyAllowed(DetailAST ast) {
        return allowEmptyLoopBody && ast.findFirstToken(TokenTypes.EMPTY_STAT) != null;
    }

    /**
     * Checks if switch member (case, default statements) has statements without curly braces.
     * @param ast case, default statements.
     * @return true if switch member has unbraced statements, false otherwise.
     */
    private static boolean hasUnbracedStatements(DetailAST ast) {
        final DetailAST nextSibling = ast.getNextSibling();
        return nextSibling != null
            && nextSibling.getType() == TokenTypes.SLIST
            && nextSibling.getFirstChild().getType() != TokenTypes.SLIST;
    }

    /**
     * Checks if current statement can be skipped by "need braces" warning.
     * @param statement if, for, while, do-while, lambda, else, case, default statements.
     * @return true if current statement can be skipped by Check.
     */
    private boolean isSkipStatement(DetailAST statement) {
        return allowSingleLineStatement && isSingleLineStatement(statement);
    }

    /**
     * Checks if current statement is single-line statement, e.g.:
     * <p>
     * {@code
     * if (obj.isValid()) return true;
     * }
     * </p>
     * <p>
     * {@code
     * while (obj.isValid()) return true;
     * }
     * </p>
     * @param statement if, for, while, do-while, lambda, else, case, default statements.
     * @return true if current statement is single-line statement.
     */
    private static boolean isSingleLineStatement(DetailAST statement) {
        final boolean result;

        switch (statement.getType()) {
            case TokenTypes.LITERAL_IF:
                result = isSingleLineIf(statement);
                break;
            case TokenTypes.LITERAL_FOR:
                result = isSingleLineFor(statement);
                break;
            case TokenTypes.LITERAL_DO:
                result = isSingleLineDoWhile(statement);
                break;
            case TokenTypes.LITERAL_WHILE:
                result = isSingleLineWhile(statement);
                break;
            case TokenTypes.LAMBDA:
                result = isSingleLineLambda(statement);
                break;
            case TokenTypes.LITERAL_CASE:
            case TokenTypes.LITERAL_DEFAULT:
                result = isSingleLineSwitchMember(statement);
                break;
            default:
                result = isSingleLineElse(statement);
                break;
        }

        return result;
    }

    /**
     * Checks if current while statement is single-line statement, e.g.:
     * <p>
     * {@code
     * while (obj.isValid()) return true;
     * }
     * </p>
     * @param literalWhile {@link TokenTypes#LITERAL_WHILE while statement}.
     * @return true if current while statement is single-line statement.
     */
    private static boolean isSingleLineWhile(DetailAST literalWhile) {
        boolean result = false;
        if (literalWhile.getParent().getType() == TokenTypes.SLIST) {
            final DetailAST block = literalWhile.getLastChild().getPreviousSibling();
            result = TokenUtil.areOnSameLine(literalWhile, block);
        }
        return result;
    }

    /**
     * Checks if current do-while statement is single-line statement, e.g.:
     * <p>
     * {@code
     * do this.notify(); while (o != null);
     * }
     * </p>
     * @param literalDo {@link TokenTypes#LITERAL_DO do-while statement}.
     * @return true if current do-while statement is single-line statement.
     */
    private static boolean isSingleLineDoWhile(DetailAST literalDo) {
        boolean result = false;
        if (literalDo.getParent().getType() == TokenTypes.SLIST) {
            final DetailAST block = literalDo.getFirstChild();
            result = TokenUtil.areOnSameLine(block, literalDo);
        }
        return result;
    }

    /**
     * Checks if current for statement is single-line statement, e.g.:
     * <p>
     * {@code
     * for (int i = 0; ; ) this.notify();
     * }
     * </p>
     * @param literalFor {@link TokenTypes#LITERAL_FOR for statement}.
     * @return true if current for statement is single-line statement.
     */
    private static boolean isSingleLineFor(DetailAST literalFor) {
        boolean result = false;
        if (literalFor.getLastChild().getType() == TokenTypes.EMPTY_STAT) {
            result = true;
        }
        else if (literalFor.getParent().getType() == TokenTypes.SLIST) {
            result = TokenUtil.areOnSameLine(literalFor, literalFor.getLastChild());
        }
        return result;
    }

    /**
     * Checks if current if statement is single-line statement, e.g.:
     * <p>
     * {@code
     * if (obj.isValid()) return true;
     * }
     * </p>
     * @param literalIf {@link TokenTypes#LITERAL_IF if statement}.
     * @return true if current if statement is single-line statement.
     */
    private static boolean isSingleLineIf(DetailAST literalIf) {
        boolean result = false;
        if (literalIf.getParent().getType() == TokenTypes.SLIST) {
            final DetailAST literalIfLastChild = literalIf.getLastChild();
            final DetailAST block;
            if (literalIfLastChild.getType() == TokenTypes.LITERAL_ELSE) {
                block = literalIfLastChild.getPreviousSibling();
            }
            else {
                block = literalIfLastChild;
            }
            final DetailAST ifCondition = literalIf.findFirstToken(TokenTypes.EXPR);
            result = TokenUtil.areOnSameLine(ifCondition, block);
        }
        return result;
    }

    /**
     * Checks if current lambda statement is single-line statement, e.g.:
     * <p>
     * {@code
     * Runnable r = () -> System.out.println("Hello, world!");
     * }
     * </p>
     * @param lambda {@link TokenTypes#LAMBDA lambda statement}.
     * @return true if current lambda statement is single-line statement.
     */
    private static boolean isSingleLineLambda(DetailAST lambda) {
        final DetailAST lastLambdaToken = getLastLambdaToken(lambda);
        return TokenUtil.areOnSameLine(lambda, lastLambdaToken);
    }

    /**
     * Looks for the last token in lambda.
     *
     * @param lambda token to check.
     * @return last token in lambda
     */
    private static DetailAST getLastLambdaToken(DetailAST lambda) {
        DetailAST node = lambda;
        do {
            node = node.getLastChild();
        } while (node.getLastChild() != null);
        return node;
    }

    /**
     * Checks if switch member (case or default statement) is single-line statement, e.g.:
     * <p>
     * {@code
     * case 1: doSomeStuff(); break;
     * case 2: doSomeStuff(); break;
     * case 3: ;
     * default: doSomeStuff();break;
     * }
     * </p>
     * @param ast {@link TokenTypes#LITERAL_CASE case statement} or
     * {@link TokenTypes#LITERAL_DEFAULT default statement}.
     * @return true if current switch member is single-line statement.
     */
    private static boolean isSingleLineSwitchMember(DetailAST ast) {
        boolean result = false;
        DetailAST node = ast.getNextSibling();
        if (node != null) {
            node = node.getLastChild();
            if (node != null) {
                result = TokenUtil.areOnSameLine(ast, node);
            }
        }
        return result;
    }

    /**
     * Checks if current else statement is single-line statement, e.g.:
     * <p>
     * {@code
     * else doSomeStuff();
     * }
     * </p>
     * @param literalElse {@link TokenTypes#LITERAL_ELSE else statement}.
     * @return true if current else statement is single-line statement.
     */
    private static boolean isSingleLineElse(DetailAST literalElse) {
        final DetailAST block = literalElse.getFirstChild();
        return TokenUtil.areOnSameLine(literalElse, block);
    }

}
