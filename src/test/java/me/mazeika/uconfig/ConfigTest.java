package me.mazeika.uconfig;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ConfigTest
{
    private Config jsonConfig;
    private Config xmlConfig;
    private Config yamlConfig;
    private Config propertiesConfig;

    @Before
    public void setUp() throws URISyntaxException
    {
        jsonConfig = Config.create(
                new File(getClass().getResource("/config.json").toURI()), true);
        xmlConfig = Config.create(
                new File(getClass().getResource("/config.xml").toURI()), true);
        yamlConfig = Config.create(
                new File(getClass().getResource("/config.yaml").toURI()), true);
        propertiesConfig = Config.create(new File(getClass().getResource(
                "/config.properties").toURI()), true);
    }

    @Test
    public void testCreateWithFilePathNoLazyLoad()
    {
        assertThat(Config.create("src/test/resources/config.json", false),
                notNullValue());
    }

    @Test
    public void testCreateWithFilePath()
    {
        assertThat(Config.create("src/test/resources/config.json"),
                notNullValue());
    }

    @Test
    public void testCreateWithFile() throws URISyntaxException
    {
        assertThat(Config.create(new File(getClass()
                .getResource("/config.json").toURI())), notNullValue());
    }

    @Test
    public void testGetOrDefaultForJSONRegularPath()
    {
        assertThat(jsonConfig.getOrDefault("path.to.value", ""), is("hello"));
    }

    @Test
    public void testGetOrDefaultForJSONEscapedDot()
    {
        assertThat(jsonConfig.getOrDefault("dot\\.path", ""),
                is("dotescaping"));
    }

    @Test
    public void testGetOrDefaultForJSONEscapedNumbers()
    {
        assertThat(jsonConfig.getOrDefault("\\1.\\3", ""), is("numbers"));
    }

    @Test
    public void testGetOrDefaultWithIndicesForJSONEscapedAll()
    {
        assertThat(jsonConfig.getOrDefaultWithIndices(
                "\\23.\\#.dot\\.path.#", "", 1), is("item1"));
    }

    @Test
    public void testGetOrDefaultForJSONDouble()
    {
        assertThat(jsonConfig.getOrDefault("path.to.double", 0d), is(3.14));
    }

    @Test
    public void testGetOrDefaultForJSONNull()
    {
        assertThat(jsonConfig.getOrDefault("path.to.null", "x"), is("x"));
    }

    @Test
    public void testGetOrDefaultForJSONArray()
    {
        assertThat(jsonConfig.getOrDefault("array.0", ""), is("item0"));
    }

    @Test
    public void testGetOrDefaultForJSONArrayAndMap()
    {
        assertThat(jsonConfig.getOrDefault("array.1.key", ""), is("world"));
    }

    @Test
    public void testGetOrDefaultForJSONNonExistent()
    {
        assertThat(jsonConfig.getOrDefault("array.1.x", "nonexistent"),
                is("nonexistent"));
    }

    @Test
    public void testGetOrDefaultForPropertiesRegularPath()
    {
        assertThat(propertiesConfig.getOrDefault("path\\.to\\.value", ""),
                is("hello"));
    }

    @Test
    public void testGetOrDefaultForPropertiesEscapedAll()
    {
        assertThat(propertiesConfig.getOrDefault(
                "2\\.#\\.\\.\\.", 0) / 2, is(4));
    }

    @Test
    public void testGetOrDefaultForPropertiesDouble()
    {
        assertThat(propertiesConfig.getOrDefault("double", 0d), is(3.14));
    }

    @Test
    public void testGetOrDefaultForPropertiesRegular()
    {
        assertThat(propertiesConfig.getOrDefault("value", ""), is("hi"));
    }

    @Test
    public void testGetOrDefaultForPropertiesNonExistent()
    {
        assertThat(propertiesConfig.getOrDefault("value.nonexistent", "x"),
                is("x"));
    }

    @Test
    public void testGetOrDefaultForXMLRegularPath()
    {
        assertThat(xmlConfig.getOrDefault("root.path.to.value", ""),
                is("hello"));
    }

    @Test
    public void testGetOrDefaultForXMLEmptyWithAttribute()
    {
        assertThat(xmlConfig.getOrDefault("root.path.item.0", "x"), is("x"));
    }

    @Test
    public void testGetOrDefaultForXMLEmpty()
    {
        assertThat(xmlConfig.getOrDefault("root.path.anotherBlank", "x"),
                is(""));
    }

    @Test
    public void testGetOrDefaultForXMLAttribute()
    {
        assertThat(xmlConfig.getOrDefault("root.path.item.0.attr",
                ""), is("world"));
    }

    @Test
    public void testGetOrDefaultForXMLNonExistent()
    {
        assertThat(xmlConfig.getOrDefault("root.path.x", "nonexistent"),
                is("nonexistent"));
    }

    @Test
    public void testGetOrDefaultForYAMLRegularPath()
    {
        assertThat(jsonConfig.getOrDefault("path.to.value", ""), is("hello"));
    }

    @Test
    public void testGetOrDefaultForYAMLDouble()
    {
        assertThat(yamlConfig.getOrDefault("path.to.double", 0d), is(3.14));
    }

    @Test
    public void testGetOrDefaultForYAMLNull()
    {
        assertThat(yamlConfig.getOrDefault("path.to.null", "x"), is("x"));
    }

    @Test
    public void testGetOrDefaultForYAMLEmpty()
    {
        assertThat(yamlConfig.getOrDefault("path.to.empty", "x"), is("x"));
    }

    @Test
    public void testGetOrDefaultForYAMLArray()
    {
        assertThat(yamlConfig.getOrDefault("array.0", ""), is("item0"));
    }

    @Test
    public void testGetOrDefaultForYAMLArrayAndMap()
    {
        assertThat(yamlConfig.getOrDefault("array.1.key", ""), is("world"));
    }

    @Test
    public void testGetOrDefaultForYAMLNonExistent()
    {
        assertThat(yamlConfig.getOrDefault("array.1.x", "nonexistent"),
                is("nonexistent"));
    }

    @Test
    public void testGetOrDefaultWithIndicesForJSON()
    {
        assertThat(jsonConfig.getOrDefaultWithIndices("array.#.key", "", 1),
                is("world"));
    }

    @Test
    public void testGetOrDefaultWithIndicesForJSONHashtag()
    {
        assertThat(jsonConfig.getOrDefaultWithIndices("\\#.value", "", 1),
                is("hashtag"));
    }

    @Test
    public void testGetForJSON()
    {
        assertThat(jsonConfig.<String>get("path.to.value")
                .orElse(""), is("hello"));
    }

    @Test
    public void testGetForJSONNonExistent()
    {
        assertThat(jsonConfig.<String>get("path.to.x"), is(Optional.empty()));
    }

    @Test
    public void testGetWithIndicesForJSON()
    {
        assertThat(jsonConfig.<String>getWithIndices("array.#.key", 1).orElse(""),
                is("world"));
    }

    @Test
    public void testGetForJSONInteger()
    {
        assertThat(jsonConfig.<Integer>get("path.to.int").orElse(0) / 2, is(2));
    }

    @Test
    public void testGetForJSONNull()
    {
        assertThat(jsonConfig.<Integer>get("path.to.null").orElse(3) / 2, is(1));
    }
}
