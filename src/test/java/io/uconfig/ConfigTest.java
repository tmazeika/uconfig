package io.uconfig;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.junit.Assert.*;

public class ConfigTest
{
    private Config jsonConfig;
    private Config xmlConfig;
    private Config yamlConfig;

    @Before
    public void setUp() throws URISyntaxException
    {
        jsonConfig = Config.create(
                new File(getClass().getResource("/config.json").toURI()), true);
        xmlConfig = Config.create(
                new File(getClass().getResource("/config.xml").toURI()), true);
        yamlConfig = Config.create(
                new File(getClass().getResource("/config.yaml").toURI()), true);
    }

    @Test
    public void testCreateWithFilePathNoLazyLoad()
    {
        assertNotNull(Config.create("src/test/resources/config.json", false));
    }

    @Test
    public void testCreateWithFilePath()
    {
        assertNotNull(Config.create("src/test/resources/config.json"));
    }

    @Test
    public void testCreateWithFile() throws URISyntaxException
    {
        assertNotNull(Config.create(new File(getClass()
                .getResource("/config.json").toURI())));
    }

    @Test
    public void testGetOrDefaultForJSONRegularPath()
    {
        assertEquals("hello", jsonConfig.getOrDefault("path.to.value", ""));
    }

    @Test
    public void testGetOrDefaultForJSONEscapedDot()
    {
        assertEquals("dotescaping", jsonConfig.getOrDefault("dot\\.path", ""));
    }

    @Test
    public void testGetOrDefaultForJSONEscapedNumbers()
    {
        assertEquals("numbers", jsonConfig.getOrDefault("\\1.\\3", ""));
    }

    @Test
    public void testGetOrDefaultWithIndicesForJSONEscapedAll()
    {
        assertEquals("item1", jsonConfig.getOrDefaultWithIndices(
                "\\23.\\#.dot\\.path.#", "", 1));
    }

    @Test
    public void testGetOrDefaultForJSONDouble()
    {
        assertEquals(3.14, jsonConfig.getOrDefault("path.to.double", 0d), 0.01);
    }

    @Test
    public void testGetOrDefaultForJSONNull()
    {
        assertEquals("x", jsonConfig.getOrDefault("path.to.null", "x"));
    }

    @Test
    public void testGetOrDefaultForJSONArray()
    {
        assertEquals("item0", jsonConfig.getOrDefault("array.0", ""));
    }

    @Test
    public void testGetOrDefaultForJSONArrayAndMap()
    {
        assertEquals("world", jsonConfig.getOrDefault("array.1.key", ""));
    }

    @Test
    public void testGetOrDefaultForJSONNonExistent()
    {
        assertEquals("nonexistent", jsonConfig.getOrDefault("array.1.x",
                "nonexistent"));
    }

    @Test
    public void testGetOrDefaultForXMLRegularPath()
    {
        assertEquals("hello", xmlConfig.getOrDefault("root.path.to.value", ""));
    }

    @Test
    public void testGetOrDefaultForXMLEmptyWithAttribute()
    {
        assertEquals("", xmlConfig.getOrDefault("root.path.item.0", ""));
    }

    @Test
    public void testGetOrDefaultForXMLEmpty()
    {
        assertEquals("", xmlConfig.getOrDefault("root.path.anotherBlank", ""));
    }

    @Test
    public void testGetOrDefaultForXMLAttribute()
    {
        assertEquals("world", xmlConfig.getOrDefault("root.path.item.0.attr",
                ""));
    }

    @Test
    public void testGetOrDefaultForXMLNonExistent()
    {
        assertEquals("nonexistent", xmlConfig.getOrDefault("root.path.x",
                "nonexistent"));
    }

    @Test
    public void testGetOrDefaultForYAMLRegularPath()
    {
        assertEquals("hello", jsonConfig.getOrDefault("path.to.value", ""));
    }

    @Test
    public void testGetOrDefaultForYAMLDouble()
    {
        assertEquals(3.14, yamlConfig.getOrDefault("path.to.double", 0d), 0.01);
    }

    @Test
    public void testGetOrDefaultForYAMLNull()
    {
        assertEquals("x", yamlConfig.getOrDefault("path.to.null", "x"));
    }

    @Test
    public void testGetOrDefaultForYAMLEmpty()
    {
        assertEquals("x", yamlConfig.getOrDefault("path.to.empty", "x"));
    }

    @Test
    public void testGetOrDefaultForYAMLArray()
    {
        assertEquals("item0", yamlConfig.getOrDefault("array.0", ""));
    }

    @Test
    public void testGetOrDefaultForYAMLArrayAndMap()
    {
        assertEquals("world", yamlConfig.getOrDefault("array.1.key", ""));
    }

    @Test
    public void testGetOrDefaultForYAMLNonExistent()
    {
        assertEquals("nonexistent", yamlConfig.getOrDefault("array.1.x",
                "nonexistent"));
    }

    @Test
    public void testGetOrDefaultWithIndicesForJSON()
    {
        assertEquals("world", jsonConfig.getOrDefaultWithIndices("array.#.key",
                "", 1));
    }

    @Test
    public void testGetOrDefaultWithIndicesForJSONHashtag()
    {
        assertEquals("hashtag", jsonConfig.getOrDefaultWithIndices(
                "\\#.value.key", "", 1));
    }

    @Test
    public void testGetForJSON()
    {
        assertEquals("hello", jsonConfig.<String>get("path.to.value")
                .orElse(""));
    }

    @Test
    public void testGetForJSONNonExistent()
    {
        assertEquals(Optional.empty(), jsonConfig.<String>get("path.to.x"));
    }

    @Test
    public void testGetWithIndicesForJSON()
    {
        assertEquals("world", jsonConfig.<String>getWithIndices("array.#.key",
                1).orElse(""));
    }

    @Test
    public void testGetForJSONInteger()
    {
        assertEquals(2, jsonConfig.<Integer>get("path.to.int").orElse(0) / 2);
    }

    @Test
    public void testGetForJSONNull()
    {
        assertEquals(1, jsonConfig.<Integer>get("path.to.null").orElse(3) / 2);
    }
}
