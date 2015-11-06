package me.mazeika.uconfig.parsing;

import me.mazeika.uconfig.ParseException;

public abstract class Parser
{
    /**
     * Creates a parser appropriate for the given {@code fileName}.
     *
     * @param fileName the file name
     *
     * @return a new parser appropriate for the given {@code fileName}
     *
     * @throws UnsupportedOperationException if the given file name does not
     *                                       have a supported extension
     */
    public static Parser create(String fileName)
    {
        final String ext = fileName.substring(fileName.lastIndexOf('.') + 1);

        switch (ext) {
            case "json":
                return new JSONParser();
            case "yaml":
            case "yml":
                return new YAMLParser();
            case "xml":
                return new XMLParser();
            case "properties":
                return new PropertiesParser();
            default:
                throw new UnsupportedOperationException(
                        ext + " file type not supported");
        }
    }

    /**
     * Parses the given input from its correct type, and converts it into a
     * tree.
     *
     * @param input the input to parse
     *
     * @return an object that acts as a tree to hold the parsed input data
     *
     * @throws ParseException if the given input cannot be parsed
     */
    public abstract Object parse(String input);

    public abstract ParserType getType();
}
