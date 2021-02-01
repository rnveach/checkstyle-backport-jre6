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

package com.puppycrawl.tools.checkstyle.meta;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.junit.jupiter.api.function.Executable;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.internal.utils.CheckUtil;
import com.puppycrawl.tools.checkstyle.jre6.file.Path;
import com.puppycrawl.tools.checkstyle.jre6.util.function.Function;

public final class MetadataGeneratorUtilTest {

    private final String invalidMetadataPackage = System.getProperty("user.dir")
        + "/src/test/resources/com/puppycrawl/tools/checkstyle"
        + "/meta/javadocmetadatascraper/invalid_metadata";

    private final List<String> modulesContainingNoMetadataFile = Arrays.asList(
            "Checker",
            "TreeWalker",
            "JavadocMetadataScraper"
    );

    @Test
    public void testMetadataFilesGenerationAllFiles() throws Exception {
        MetadataGeneratorUtil.generate(System.getProperty("user.dir")
                + "/src/main/java/com/puppycrawl/tools/checkstyle");
        final Set<String> metaFiles = new TreeSet<String>();

        walk(new File(System.getProperty("user.dir") + "/src/main/resources/com/puppycrawl"
                        + "/tools/checkstyle/meta"),
            new Function<File, Object>() {
                @Override
                public Object apply(File file) {
                    if (file.isFile()) {
                        metaFiles.add(getMetaFileName(new Path(file)));
                    }
                    return null;
                }
            });
        final Set<String> checkstyleModules =
                CheckUtil.getSimpleNames(CheckUtil.getCheckstyleModules());
        checkstyleModules.removeAll(modulesContainingNoMetadataFile);
        assertEquals("Number of generated metadata files dont match with number of checkstyle "
                        + "module", checkstyleModules, metaFiles);
    }

    @Test
    public void testMetadataFileGenerationDefaultValueMisplaced() {
        final CheckstyleException exc = assertThrows(CheckstyleException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                MetadataGeneratorUtil.generate(invalidMetadataPackage
                    + "/InputJavadocMetadataScraperPropertyMisplacedDefaultValueCheck.java");
            }
        });
        assertThat(exc.getCause()).isInstanceOf(MetadataGenerationException.class);
        assertThat(exc.getCause().getMessage()).isEqualTo(
            "Default value for property 'misplacedDefaultValue' is missing");
    }

    @Test
    public void testMetadataFileGenerationTypeMisplaced() {
        final CheckstyleException exc = assertThrows(CheckstyleException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                MetadataGeneratorUtil.generate(invalidMetadataPackage
                    + "/InputJavadocMetadataScraperPropertyMisplacedTypeCheck.java");
            }
        });
        assertThat(exc.getCause()).isInstanceOf(MetadataGenerationException.class);
        assertThat(exc.getCause().getMessage()).isEqualTo(
            "Type for property 'misplacedType' is missing");
    }

    @Test
    public void testMetadataFileGenerationTypeMissing() {
        final CheckstyleException exc = assertThrows(CheckstyleException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                MetadataGeneratorUtil.generate(invalidMetadataPackage
                    + "/InputJavadocMetadataScraperPropertyMissingTypeCheck.java");
            }
        });
        assertThat(exc.getCause()).isInstanceOf(MetadataGenerationException.class);
        assertThat(exc.getCause().getMessage()).isEqualTo(
            "Type for property 'missingType' is missing");
    }

    @Test
    public void testMetadataFileGenerationDefaultValueMissing() {
        final CheckstyleException exc = assertThrows(CheckstyleException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                MetadataGeneratorUtil.generate(invalidMetadataPackage
                    + "/InputJavadocMetadataScraperPropertyMissingDefaultValueCheck.java");
            }
        });
        assertThat(exc.getCause()).isInstanceOf(MetadataGenerationException.class);
        assertThat(exc.getCause().getMessage()).isEqualTo(
            "Default value for property 'missingDefaultValue' is missing");
    }

    /**
     * Get meta file name from full file name.
     *
     * @param file file to process
     * @return meta file name
     */
    private static String getMetaFileName(Path file) {
        final String fileName = file.getFileName().toString();
        final int lengthToOmit;
        if (fileName.contains("Check")) {
            lengthToOmit = "Check.xml".length();
        }
        else {
            lengthToOmit = ".xml".length();
        }
        return fileName.substring(0, fileName.length() - lengthToOmit);
    }

    private static void walk(File dir, Function<File, Object> func) {
        final File[] list = dir.listFiles();

        if (list != null) {
            for (File f : list) {
                func.apply(f);
                if (f.isDirectory()) {
                    walk(f, func);
                }
            }
        }
    }
}
