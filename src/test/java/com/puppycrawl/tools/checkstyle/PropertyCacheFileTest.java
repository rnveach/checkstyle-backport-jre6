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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.jre6.file.Files7;
import com.puppycrawl.tools.checkstyle.jre6.file.Path;
import com.puppycrawl.tools.checkstyle.jre6.file.Paths;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class PropertyCacheFileTest extends AbstractPathTestSupport {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/propertycachefile";
    }

    @Test
    public void testCtor() {
        try {
            final Object test = new PropertyCacheFile(null, "");
            fail("exception expected but got " + test);
        }
        catch (IllegalArgumentException ex) {
            assertEquals("config can not be null", ex.getMessage(), "Invalid exception message");
        }
        final Configuration config = new DefaultConfiguration("myName");
        try {
            final Object test = new PropertyCacheFile(config, null);
            fail("exception expected but got " + test);
        }
        catch (IllegalArgumentException ex) {
            assertEquals("fileName can not be null", ex.getMessage(), "Invalid exception message");
        }
    }

    @Test
    public void testInCache() throws IOException {
        final Configuration config = new DefaultConfiguration("myName");
        final String filePath = File.createTempFile("junit", null, temporaryFolder.newFolder()).getPath();
        final PropertyCacheFile cache = new PropertyCacheFile(config, filePath);
        cache.put("myFile", 1);
        assertTrue(cache.isInCache("myFile", 1), "Should return true when file is in cache");
        assertFalse(cache.isInCache("myFile", 2), "Should return false when file is not in cache");
        assertFalse(cache.isInCache("myFile1", 1), "Should return false when file is not in cache");
    }

    @Test
    public void testResetIfFileDoesNotExist() throws IOException {
        final Configuration config = new DefaultConfiguration("myName");
        final PropertyCacheFile cache = new PropertyCacheFile(config, "fileDoesNotExist.txt");

        cache.load();

        assertNotNull(cache.get(PropertyCacheFile.CONFIG_HASH_KEY),
                "Config hash key should not be null");
    }

    @Test
    public void testPopulateDetails() throws IOException {
        final Configuration config = new DefaultConfiguration("myName");
        final PropertyCacheFile cache = new PropertyCacheFile(config,
                getPath("InputPropertyCacheFile"));
        cache.load();

        final String hash = cache.get(PropertyCacheFile.CONFIG_HASH_KEY);
        assertNotNull(hash, "Config hash key should not be null");
        assertNull(cache.get("key"), "Should return null if key is not in cache");

        cache.load();

        assertEquals(hash, cache.get(PropertyCacheFile.CONFIG_HASH_KEY), "Invalid config hash key");
        assertEquals("value", cache.get("key"), "Invalid cache value");
        assertNotNull(cache.get(PropertyCacheFile.CONFIG_HASH_KEY),
                "Config hash key should not be null");
    }

    @Test
    public void testConfigHashOnReset() throws IOException {
        final Configuration config = new DefaultConfiguration("myName");
        final String filePath = File.createTempFile("junit", null, temporaryFolder.newFolder()).getPath();
        final PropertyCacheFile cache = new PropertyCacheFile(config, filePath);

        cache.load();

        final String hash = cache.get(PropertyCacheFile.CONFIG_HASH_KEY);
        assertNotNull(hash, "Config hash key should not be null");

        cache.reset();

        assertEquals(hash, cache.get(PropertyCacheFile.CONFIG_HASH_KEY),
                "Invalid config hash key");
    }

    @Test
    public void testConfigHashRemainsOnResetExternalResources() throws IOException {
        final Configuration config = new DefaultConfiguration("myName");
        final String filePath = File.createTempFile("junit", null, temporaryFolder.newFolder()).getPath();
        final PropertyCacheFile cache = new PropertyCacheFile(config, filePath);

        // create cache with one file
        cache.load();
        cache.put("myFile", 1);

        final String hash = cache.get(PropertyCacheFile.CONFIG_HASH_KEY);
        assertNotNull(hash, "Config hash key should not be null");

        // apply new external resource to clear cache
        final Set<String> resources = new HashSet<String>();
        resources.add("dummy");
        cache.putExternalResources(resources);

        assertEquals(hash, cache.get(PropertyCacheFile.CONFIG_HASH_KEY),
                "Invalid config hash key");
        assertFalse(cache.isInCache("myFile", 1),
                "Should return false in file is not in cache");
    }

    @Test
    public void testCacheRemainsWhenExternalResourceTheSame() throws IOException {
        final Configuration config = new DefaultConfiguration("myName");
        final String externalResourcePath =
                File.createTempFile("junit", null, temporaryFolder.newFolder()).getPath();
        final String filePath = File.createTempFile("junit", null, temporaryFolder.newFolder()).getPath();
        final PropertyCacheFile cache = new PropertyCacheFile(config, filePath);

        // pre-populate with cache with resources

        cache.load();

        final Set<String> resources = new HashSet<String>();
        resources.add(externalResourcePath);
        cache.putExternalResources(resources);

        cache.persist();

        // test cache with same resources and new file

        cache.load();
        cache.put("myFile", 1);
        cache.putExternalResources(resources);

        assertTrue(cache.isInCache("myFile", 1),
                "Should return true in file is in cache");
    }

    @Test
    public void testExternalResourceIsSavedInCache() throws Exception {
        final Configuration config = new DefaultConfiguration("myName");
        final String filePath = File.createTempFile("junit", null, temporaryFolder.newFolder()).getPath();
        final PropertyCacheFile cache = new PropertyCacheFile(config, filePath);

        cache.load();

        final Set<String> resources = new HashSet<String>();
        final String pathToResource = getPath("InputPropertyCacheFileExternal.properties");
        resources.add(pathToResource);
        cache.putExternalResources(resources);

        final MessageDigest digest = MessageDigest.getInstance("SHA-1");
        final URI uri = CommonUtil.getUriByFilename(pathToResource);
        final byte[] input =
                ByteStreams.toByteArray(new BufferedInputStream(uri.toURL().openStream()));
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(input);
        digest.update(out.toByteArray());
        final String expected = BaseEncoding.base16().upperCase().encode(digest.digest());

        assertEquals(expected, cache.get("module-resource*?:" + pathToResource),
                "Hashes are not equal");
    }

    @Test
    public void testCacheDirectoryDoesNotExistAndShouldBeCreated() throws IOException {
        final Configuration config = new DefaultConfiguration("myName");
        final String filePath = String.format(Locale.getDefault(), "%s%2$stemp%2$scache.temp",
            temporaryFolder.newFolder(), File.separator);
        final PropertyCacheFile cache = new PropertyCacheFile(config, filePath);

        // no exception expected, cache directory should be created
        cache.persist();

        assertTrue(new File(filePath).exists(), "cache exists in directory");
    }

    @Test
    public void testPathToCacheContainsOnlyFileName() throws IOException {
        final Configuration config = new DefaultConfiguration("myName");
        final String filePath = "temp.cache";
        final PropertyCacheFile cache = new PropertyCacheFile(config, filePath);

        // no exception expected
        cache.persist();
        assertTrue(Files7.exists(Paths.get(filePath)), "Cache file does not exist");
        Files7.delete(Paths.get(filePath));
    }

    @Test
    public void testChangeInConfig() throws Exception {
        final DefaultConfiguration config = new DefaultConfiguration("myConfig");
        config.addProperty("attr", "value");

        final File cacheFile = File.createTempFile("junit", null, temporaryFolder.newFolder());
        final PropertyCacheFile cache = new PropertyCacheFile(config, cacheFile.getPath());
        cache.load();

        final String expectedInitialConfigHash = "D5BB1747FC11B2BB839C80A6C28E3E7684AF9940";
        final String actualInitialConfigHash = cache.get(PropertyCacheFile.CONFIG_HASH_KEY);
        assertEquals(expectedInitialConfigHash, actualInitialConfigHash, "Invalid config hash");

        cache.persist();

        final Properties details = new Properties();
        final BufferedReader reader = Files7.newBufferedReader(new Path(cacheFile));
        try {
            details.load(reader);
        }
        finally {
            reader.close();
        }
        assertEquals(1, details.size(), "Invalid details size");

        // change in config
        config.addProperty("newAttr", "newValue");

        final PropertyCacheFile cacheAfterChangeInConfig =
            new PropertyCacheFile(config, cacheFile.getPath());
        cacheAfterChangeInConfig.load();

        final String expectedConfigHashAfterChange = "714876AE38C069EC52BF86889F061B3776E526D3";
        final String actualConfigHashAfterChange =
            cacheAfterChangeInConfig.get(PropertyCacheFile.CONFIG_HASH_KEY);
        assertEquals(expectedConfigHashAfterChange, actualConfigHashAfterChange,
                "Invalid config hash");

        cacheAfterChangeInConfig.persist();

        final Properties detailsAfterChangeInConfig = new Properties();
        final BufferedReader reader2 = Files7.newBufferedReader(new Path(cacheFile));
        try {
            detailsAfterChangeInConfig.load(reader2);
        }
        finally {
            reader2.close();
        }
        assertEquals(1, detailsAfterChangeInConfig.size(), "Invalid cache size");
    }

}
