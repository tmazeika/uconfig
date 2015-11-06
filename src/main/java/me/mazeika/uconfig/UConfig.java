package me.mazeika.uconfig;

import me.mazeika.uconfig.parsing.Parser;
import me.mazeika.uconfig.parsing.ParserType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class UConfig extends Config
{
    private static final String KEY_INDICES_DELIMITER = "#";
    private static final String KEY_INDICES_ESCAPED_DELIMITER = "\\#";
    private static final String KEY_INDICES_ESCAPE = "\\";

    private final File file;
    private final Parser parser;
    private final ParserType parserType;

    private Object data;

    UConfig(File file, boolean lazyLoad)
    {
        this.file = file;
        parser = Parser.create(file.getName());
        parserType = parser.getType();

        if (! lazyLoad) {
            getData();
        }
    }

    @Override
    public <T> T getOrDefault(String key, T defaultValue)
    {
        return getOrDefaultWithIndices(key, defaultValue);
    }

    @Override
    public <T> T getOrDefaultWithIndices(String key, T defaultValue,
                                         int... indices)
    {
        final String[] parsedKey = parseKey(key, indices);

        Object data = getData();

        for (String keyPart : parsedKey) {
            if (isPositiveInteger(keyPart)) {
                if (data instanceof List) {
                    final int keyPartInt = Integer.parseUnsignedInt(keyPart);
                    final List list = (List) data;

                    if (keyPartInt >= list.size()) {
                        return defaultValue;
                    }

                    data = list.get(keyPartInt);
                    continue;
                }
                else {
                    return defaultValue;
                }
            }

            if (keyPart.startsWith(KEY_INDICES_ESCAPE)) {
                keyPart = keyPart.substring(1);
            }

            if (data instanceof Map) {
                // noinspection unchecked
                if ((data = ((Map<String, Object>) data).get(keyPart))
                        == null) {
                    return defaultValue;
                }

                if (parserType == ParserType.XML && data instanceof Map) {
                    final Object content;

                    // noinspection unchecked
                    if ((content = ((Map<String, Object>) data).get("content"))
                            != null) {
                        data = content;
                        break;
                    }
                }
            }
        }

        /*
        First, check that we're dealing with XML. Next, if we *shouldn't* return
        a map and the data *is* a map, return the default value. The reasoning
        is that an empty XML element is actually an empty map due to how it's
        converted to JSON. So, if we request the key `path.to.value` and the XML
        looks like `<path><to><value/></to></path>`, for example, the default
        value should be returned.
         */
        if (parserType == ParserType.XML && ! (defaultValue instanceof Map)
                && data instanceof Map) {
            return defaultValue;
        }

        /*
        Check if the data is a map or list, and if it's an empty map or list,
        return the default value.
         */
        if ((data instanceof Map && ((Map) data).isEmpty())
                || (data instanceof List && ((List) data).isEmpty())) {
            return defaultValue;
        }

        /*
        Check if the return type should be a string... if so, we'll want to
        convert whatever it is we're going to return into a string so that
        any other data types don't complain they can't be converted to a string.
         */
        if (defaultValue instanceof String) {
            // noinspection unchecked
            return (T) data.toString();
        }

        // noinspection unchecked
        return (T) data;
    }

    @Override
    public <T> Optional<T> get(String key)
    {
        return getWithIndices(key);
    }

    @Override
    public <T> Optional<T> getWithIndices(String key, int... indices)
    {
        // noinspection unchecked
        return Optional.ofNullable((T) getOrDefaultWithIndices(key, null,
                indices));
    }

    /**
     * Parses the given {@code key} into tokens. See special dot notation syntax
     * in {@link Config}.
     *
     * @param key the key to parse
     * @param indices the indices to use; can be left out if none are to be used
     *
     * @return the parsed key, separated into tokens
     *
     * @throws IllegalArgumentException if not enough indices were supplied to
     *                                  replace the key index delimiters
     */
    private String[] parseKey(String key, int... indices)
    {
        key = key.trim();

        //                                 (?<!\\)\.
        final String[] tokens = key.split("(?<!\\\\)\\.");
        int indicesIndex = 0;

        for (int i = 0; i < tokens.length; i++) {
            final String token = tokens[i];

            //                            (?<!\\)\\\.
            tokens[i] = token.replaceAll("(?<!\\\\)\\\\\\.", ".");

            if (indices.length > 0) {
                if (token.equals(KEY_INDICES_DELIMITER)) {
                    try {
                        tokens[i] = String.valueOf(indices[indicesIndex++]);
                    }
                    catch (IndexOutOfBoundsException e) {
                        throw new IllegalArgumentException(
                                "Insufficient indices supplied for key " + key
                                        + ", received " + indices.length + ": "
                                        + Arrays.toString(indices));
                    }
                }
                else if (token.equals(KEY_INDICES_ESCAPED_DELIMITER)) {
                    // '\#' -> '#'
                    tokens[i] = KEY_INDICES_DELIMITER;
                }
            }
        }

        return tokens;
    }

    /**
     * Gets the config data. Caches the data and returns the cached data if
     * available. Otherwise, performs IO and reads/parses the file.
     *
     * @return the map
     */
    private synchronized Object getData()
    {
        if (data == null) {
            final StringBuilder builder = new StringBuilder();

            try (BufferedReader in = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = in.readLine()) != null) {
                    builder.append(line).append('\n');
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            data = parser.parse(builder.toString());
        }

        return data;
    }

    /**
     * Gets if the given string is a positive integer.
     *
     * @param str the string to check
     *
     * @return {@code true} if the given string is a positive integer
     */
    private boolean isPositiveInteger(String str)
    {
        for (char c : str.toCharArray()) {
            if (c < '0' || c > '9') {
                return false;
            }
        }

        return true;
    }
}
