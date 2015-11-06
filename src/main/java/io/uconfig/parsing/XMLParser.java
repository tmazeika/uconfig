package io.uconfig.parsing;

import org.json.XML;
import org.yaml.snakeyaml.Yaml;

class XMLParser extends Parser
{
    private static final Yaml yaml = new Yaml();

    @Override
    public Object parse(String input)
    {
        final String json = XML.toJSONObject(input).toString();

        return yaml.load(json);
    }

    @Override
    public ParserType getType()
    {
        return ParserType.XML;
    }
}
