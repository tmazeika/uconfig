package me.mazeika.uconfig;

import java.io.File;
import java.util.Optional;

public abstract class Config
{
    /**
     * Creates a config for the given {@code file}. The file <em>must</em> have
     * the file extension appropriate to its contents. Supported file types
     * include:
     * <ul>
     *     <li><a href="http://www.json.org/">JSON</a></li>
     *     <li><a href="http://yaml.org/">YAML</a></li>
     *     <li><a href="https://en.wikipedia.org/wiki/XML">XML</a></li>
     * </ul>
     * Valid file extensions include:
     * <ul>
     *     <li>.json</li>
     *     <li>.yaml</li>
     *     <li>.yml</li>
     *     <li>.xml</li>
     * </ul>
     *
     * @param file the file to load and parse
     * @param lazyLoad whether the file should be lazily loaded
     *
     * @throws UnsupportedOperationException if the given file is not of a
     *                                       supported type
     *
     * @return a new config
     */
    public static Config create(File file, boolean lazyLoad)
    {
        return new UConfig(file, lazyLoad);
    }

    /**
     * Calls {@link #create(File, boolean)}, passing in {@code true} for lazy
     * loading.
     *
     * @param file the file to load and parse
     *
     * @return a new config
     */
    public static Config create(File file)
    {
        return create(file, true);
    }

    /**
     * Creates a config for the file at the given {@code filePath}. See
     * {@link #create(File, boolean)} for requirements.
     *
     * @param filePath the path to the file to load and parse
     * @param lazyLoad whether the file should be lazily loaded
     *
     * @return a new config
     *
     * @throws UnsupportedOperationException if the file at the given path is
     *                                       not of a supported type
     *
     * @see #create(File, boolean)
     */
    public static Config create(String filePath, boolean lazyLoad)
    {
        return create(new File(filePath), lazyLoad);
    }

    /**
     * Calls {@link #create(String, boolean)}, passing in {@code true} for lazy
     * loading.
     *
     * @param filePath the path to the file to load and parse
     *
     * @return a new config
     *
     * @see #create(String, boolean)
     */
    public static Config create(String filePath)
    {
        return create(filePath, true);
    }

    /**
     * Gets a uconfig value at the specified key. The key is in a special dot
     * notation, as explained below. {@code defaultValue} is returned if no
     * value was found at the specified key or if the value is {@code null}. If
     * arrays are to be frequently accessed, then instead of using string
     * concatenation or {@link String#format(String, Object...)} to replace
     * indices in {@code key} (like {@code "path.to." + index1 + "." + index2}),
     * consider using {@link #getOrDefaultWithIndices(String, Object, int...)}.
     * In addition, if {@link Optional}s are desired, try using
     * {@link #get(String)} or {@link #getWithIndices(String, int...)}.
     *
     * <h1>JSON</h1>
     *
     * For the following JSON, the table below it shows the return values for
     * each of the given keys.
     *
     * <pre><code>
     * {
     *   "path": {
     *     "to": {
     *       "value": "hello"
     *     }
     *   },
     *   "array": [
     *     "item0",
     *     { "key": "world" }
     *   ]
     * }
     * </code></pre>
     *
     * <table>
     *     <tr>
     *         <th>{@code key}</th>
     *         <th>Return Value</th>
     *     </tr>
     *     <tr>
     *         <td>"path.to.value"</td>
     *         <td>"hello"</td>
     *     </tr>
     *     <tr>
     *         <td>"array.0"</td>
     *         <td>"item0"</td>
     *     </tr>
     *     <tr>
     *         <td>"array.1.key"</td>
     *         <td>"world"</td>
     *     </tr>
     * </table>
     *
     * <h1>YAML</h1>
     *
     * For the following YAML, the table below it shows the return values for
     * each of the given keys.
     *
     * <pre><code>
     * path:
     *   to:
     *     value: hello
     *   1:
     *     value: world
     * array:
     *   - item0
     *   - { key: "!!!" }
     * </code></pre>
     *
     * <table>
     *     <tr>
     *         <th>{@code key}</th>
     *         <th>Return Value</th>
     *     </tr>
     *     <tr>
     *         <td>"path.to.value"</td>
     *         <td>"hello"</td>
     *     </tr>
     *     <tr>
     *         <td>"array.0"</td>
     *         <td>"item0"</td>
     *     </tr>
     *     <tr>
     *         <td>"path.\1.value"</td>
     *         <td>"world"</td>
     *     </tr>
     *     <tr>
     *         <td>"array.1.key"</td>
     *         <td>"!!!"</td>
     *     </tr>
     * </table>
     *
     * <strong>Note:</strong> If a key is a number all by itself (e.g. in
     * {@code "path.1.value"}), the number should be preceded
     * with a {@code '\'}, otherwise it is treated as an array index.
     *
     * <h1>XML</h1>
     *
     * For the following XML, the table below it shows the return values for
     * each of the given keys.
     *
     * <pre><code>
     * &lt;root&gt;
     *     &lt;path&gt;
     *         &lt;to&gt;
     *             &lt;value&gt;hello&lt;/value&gt;
     *         &lt;/to&gt;
     *         &lt;item attr="world"/&gt;
     *         &lt;item&gt;&lt;/item&gt;
     *     &lt;/path&gt;
     * &lt;/root&gt;
     * </code></pre>
     *
     * <table>
     *     <tr>
     *         <th>{@code key}</th>
     *         <th>Return Value</th>
     *     </tr>
     *     <tr>
     *         <td>"root.path.to.value"</td>
     *         <td>"hello"</td>
     *     </tr>
     *     <tr>
     *         <td>"root.path.item.1"</td>
     *         <td>{@code defaultValue}</td>
     *     </tr>
     *     <tr>
     *         <td>"root.path.item.0.attr"</td>
     *         <td>"world"</td>
     *     </tr>
     * </table>
     *
     * As visible for {@code "root.path.item.1"}, the trailing index is
     * necessary when elements of the same name are within the same parent.
     * <p>
     * <strong>Note:</strong> Due to the internal workings of the XML parser,
     * avoid naming elements or attributes {@code "content"}.
     *
     * @param key the key in special dot notation
     * @param defaultValue the default value
     * @param <T> the desired type of the value
     *
     * @return the value at the given key
     */
    public abstract <T> T getOrDefault(String key, T defaultValue);

    /**
     * Same as {@link #getOrDefault(String, Object)}, however all {@code '#'}
     * characters will be replaced with its respective index in {@code indices}.
     * Use {@code '\#'} to escape it. For example, a {@code key} of
     * {@code "path.to.\#"} is equivalent to a call to
     * {@link #getOrDefault(String, Object)} with {@code "path.to.#"} being
     * passed as the {@code key}. In addition, a call to this method with a
     * {@code key} of {@code "path.to.#.#"} and {@code indices} of {@code 4, 5}
     * is equivalent to a call to {@link #getOrDefault(String, Object)} with
     * {@code "path.to.4.5"} being passed as the {@code key}.
     * <p>
     * If {@code indices} is empty, this call is equivalent to
     * {@link #getOrDefault(String, Object)}.
     *
     * @param key the key in special dot notation
     * @param defaultValue the default value
     * @param indices the indices to replace respective {@code '#'}s with
     * @param <T> the desired type of the value
     *
     * @return the value at the given key
     *
     * @throws IllegalArgumentException if not enough indices were supplied to
     *                                  replace the key index delimiters
     *
     * @see #getOrDefault(String, Object)
     */
    public abstract <T> T getOrDefaultWithIndices(String key, T defaultValue,
                                                  int... indices);

    /**
     * Same as {@link #getOrDefault(String, Object)}, however an
     * {@link Optional} is returned. The optional is empty if the value is
     * {@code null} or the key was not found. Otherwise, the optional will be
     * filled with the value.
     * <p>
     * One use of this method may be as follows:
     * {@code config.&lt;Integer&gt;getOrDefaultWithIndices("path.to.value").orElse(80);}
     *
     * @param key the key in special dot notation
     * @param <T> the desired type of the value
     *
     * @return an optional of the value at the given key; empty if {@code null}
     *         or not found
     *
     * @see #getOrDefault(String, Object)
     */
    public abstract <T> Optional<T> get(String key);

    /**
     * Same as {@link #getOrDefaultWithIndices(String, Object, int...)}, however
     * an {@link Optional} is returned, which functions the same as found in
     * {@link #get(String)}.
     * <p>
     * If {@code indices} is empty, this call is equivalent to
     * {@link #get(String)}.
     * <p>
     * One use of this method may be as follows:
     * {@code config.&lt;Integer&gt;getOrDefaultWithIndices("path.to.#", 0).orElse(80);}
     *
     * @param key the key in special dot notation
     * @param indices the indices to replace respective {@code '#'}s with
     * @param <T> the desired type of the value
     *
     * @return an optional of the value at the given key; empty if {@code null}
     *         or not found
     *
     * @throws IllegalArgumentException if not enough indices were supplied to
     *                                  replace the key index delimiters
     *
     * @see #getOrDefaultWithIndices(String, Object, int...)
     * @see #get(String)
     */
    public abstract <T> Optional<T> getWithIndices(String key, int... indices);
}
