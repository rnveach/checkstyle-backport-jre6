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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;

import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.FileText;

/**
 *<p>
 * Checks whether files end with a line separator.
 * </p>
 * <p>
 * Rationale: Any source files and text files in general should end with a line
 * separator to let other easily add new content at the end of file and "diff"
 * command does not show previous lines as changed.
 * </p>
 * <p>
 * Example (line 36 should not be in diff):
 * </p>
 * <pre>
 * &#64;&#64; -32,4 +32,5 &#64;&#64; ForbidWildcardAsReturnTypeCheck.returnTypeClassNamesIgnoreRegex
 * PublicReferenceToPrivateTypeCheck.name = Public Reference To Private Type
 *
 * StaticMethodCandidateCheck.name = Static Method Candidate
 * -StaticMethodCandidateCheck.desc = Checks whether private methods should be declared as static.
 * \ No newline at end of file
 * +StaticMethodCandidateCheck.desc = Checks whether private methods should be declared as static.
 * +StaticMethodCandidateCheck.skippedMethods = Method names to skip during the check.
 * </pre>
 * <p>
 * It can also trick the VCS to report the wrong owner for such lines.
 * An engineer who has added nothing but a newline character becomes the last
 * known author for the entire line. As a result, a mate can ask him a question
 * to which he will not give the correct answer.
 * </p>
 * <p>
 * Old Rationale: CVS source control management systems will even print
 * a warning when it encounters a file that doesn't end with a line separator.
 * </p>
 * <p>
 * Attention: property fileExtensions works with files that are passed by similar
 * property for at <a href="https://checkstyle.org/config.html#Checker">Checker</a>.
 * Please make sure required file extensions are mentioned at Checker's fileExtensions property.
 * </p>
 * <p>
 * This will check against the platform-specific default line separator.
 * </p>
 * <p>
 * It is also possible to enforce the use of a specific line-separator across
 * platforms, with the {@code lineSeparator} property.
 * </p>
 * <ul>
 * <li>
 * Property {@code lineSeparator} - Specify the type of line separator.
 * Default value is {@code lf_cr_crlf}.
 * </li>
 * <li>
 * Property {@code fileExtensions} - Specify the file type extension of the files to check.
 * Default value is {@code all files}.
 * </li>
 * </ul>
 * <p>
 * To configure the check:
 * </p>
 * <pre>
 * &lt;module name=&quot;NewlineAtEndOfFile&quot;/&gt;
 * </pre>
 * <p>
 * To configure the check to always use Unix-style line separators:
 * </p>
 * <pre>
 * &lt;module name=&quot;NewlineAtEndOfFile&quot;&gt;
 *   &lt;property name=&quot;lineSeparator&quot; value=&quot;lf&quot;/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * To configure the check to work only on Java, XML and Python files:
 * </p>
 * <pre>
 * &lt;module name=&quot;NewlineAtEndOfFile&quot;&gt;
 *   &lt;property name=&quot;fileExtensions&quot; value=&quot;java, xml, py&quot;/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * @since 3.1
 */
@StatelessCheck
public class NewlineAtEndOfFileCheck
    extends AbstractFileSetCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_UNABLE_OPEN = "unable.open";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_NO_NEWLINE_EOF = "noNewlineAtEOF";

    /** Specify the type of line separator. */
    private LineSeparatorOption lineSeparator = LineSeparatorOption.LF_CR_CRLF;

    @Override
    protected void processFiltered(File file, FileText fileText) {
        try {
            readAndCheckFile(file);
        }
        catch (final IOException ignored) {
            log(1, MSG_KEY_UNABLE_OPEN, file.getPath());
        }
    }

    /**
     * Setter to specify the type of line separator.
     *
     * @param lineSeparatorParam The line separator to set
     * @throws IllegalArgumentException If the specified line separator is not
     *         one of 'crlf', 'lf', 'cr', 'lf_cr_crlf' or 'system'
     */
    public void setLineSeparator(String lineSeparatorParam) {
        lineSeparator =
            Enum.valueOf(LineSeparatorOption.class, lineSeparatorParam.trim()
                .toUpperCase(Locale.ENGLISH));
    }

    /**
     * Reads the file provided and checks line separators.
     * @param file the file to be processed
     * @throws IOException When an IO error occurred while reading from the
     *         file provided
     */
    private void readAndCheckFile(File file) throws IOException {
        // Cannot use lines as the line separators have been removed!
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        try {
            if (!endsWithNewline(randomAccessFile)) {
                log(1, MSG_KEY_NO_NEWLINE_EOF, file.getPath());
            }
        }
        finally {
            randomAccessFile.close();
        }
    }

    /**
     * Checks whether the content provided by the Reader ends with the platform
     * specific line separator.
     * @param randomAccessFile The reader for the content to check
     * @return boolean Whether the content ends with a line separator
     * @throws IOException When an IO error occurred while reading from the
     *         provided reader
     */
    private boolean endsWithNewline(RandomAccessFile randomAccessFile)
            throws IOException {
        final boolean result;
        final int len = lineSeparator.length();
        if (randomAccessFile.length() < len) {
            result = false;
        }
        else {
            randomAccessFile.seek(randomAccessFile.length() - len);
            final byte[] lastBytes = new byte[len];
            final int readBytes = randomAccessFile.read(lastBytes);
            if (readBytes != len) {
                throw new IOException("Unable to read " + len + " bytes, got "
                        + readBytes);
            }
            result = lineSeparator.matches(lastBytes);
        }
        return result;
    }

}
