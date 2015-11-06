package me.mazeika.uconfig.parsing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

class PropertiesParser extends Parser
{
    @Override
    public Object parse(String input)
    {
        Properties properties = new Properties();

        try {
            properties.load(new ByteArrayInputStream(input.getBytes()));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }

    @Override
    public ParserType getType()
    {
        return ParserType.PROPERTIES;
    }
}
