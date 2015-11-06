# uconfig (Universal Config)

uconfig is a Java 8 config library. It's well documented, lightweight, completely thread-safe, unit tested, and easy to use. It can parse:

- JSON
- YAML
- XML

It's perfect for simple to complex configurations in your application. No need to deal  with casting objects to their correct data type, and you don't have to make your own parser or data model.

### Usage

Usage is ridiculously easy. First, if you're using Maven, just add the following to your pom.xml:

    <dependency>
    	<groupId>io.uconfig</groupId>
    	<artifactId>uconfig</artifactId>
    	<version>0.1</version>
    </dependency>

Next, create a JSON, XML, or YAML file in the root of your project. For testing, you can copy and paste the following into config.json:

    {
        "server": {
            "host": "127.0.0.1",
            "port": 8080
        }
    }

Now, you have to create the config object. Just use the static factory method to create one for you:

    public static void main(String[] args)
    {
        Config config = Config.create("config.json");
        String host = config.getOrDefault("server.host", "");

        System.out.println(host);
    }

Run it, and it should print `127.0.0.1`! That was easy.

### Other Methods

You may have noticed that `Config` has some other methods. The methods' Javadocs give a ton of information about them, but here's some example usages of them. For these examples, the following JSON file is assumed:

    {
        "path": {
            "to": {
                "value": "hello world!"
            }
        },
        "names": [
            "T.J.",
            {
                "phone": 1234567890
            }
        ]
    }

---

#### `T getOrDefault(String key, T defaultValue)`

Returns the value at the given key. If the value is not found or is `null`, the `defaultValue` is returned.

Examples:

    config.getOrDefault("path.to.value", "Not found"); // "hello world!"
    config.getOrDefault("server.nonexistent", "Not found"); // "Not found"

---

#### `T getOrDefaultWithIndices(String key, T defaultValue, int... indices)`

Same as above, however, all `#` characters are replaced with the respective index in `indices`.

Examples:

    config.getOrDefaultWithIndices("names.#", "Not found", 0); // "T.J."
    config.getOrDefaultWithIndices("names.#.phone", "Not found", 1); // 1234567890
    config.getOrDefaultWithIndices("names.#.phone", "Not found", 2); // "Not found"

---

#### `Optional<T> get(String key)`

Returns an [Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) of the value at the given key. If the value is not found or is `null`, an empty Optional is returned.

**Important:** When `get(...)` is called, there is obviously no `defaultValue`, so there's no way to tell what type you're expecting from such a value. Instead, you must specify the type before the method call. If none is specified, an Object is returned. This is demonstrated in the second example!

Examples:

    config.get("path.to.value").orElse("Not found"); // "hello, world!"
    config.<Integer>get("names.1.phone").orElse(0); // 1234567890

---

#### `Optional<T> getWithIndices(String key, int indices)`

Returns an [Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) of the value at the given key. If the value is not found or is `null`, an empty Optional is returned. The indices functionality works exactly the same as `getOrDefaultWithIndices`.

Examples:

    config.getWithIndices("names.#", 0).orElse("Not found"); // "T.J."
    config.<Integer>get("names.#.phone", 1).orElse(0); // 1234567890

---

### Escaping

As keys are specified in a special dot notation, there are some cases where you may need to access a value with a key that contains a `#`, `.`, or, for example, `"path.1.3"` (where `1` and `3` are keys, rather than indices to an array). The following are keys that will escape such characters:

- `#` - `"path.\#"` will get the value at, for example, the JSON entry `{ "#": "hello, world" }`
- `.` - `"path\.to\.value"` will get the value at, for example, the JSON entry `{ "path.to.value": "hello world" }`
- `"path.1.3"` *(example)* - `"path.\1.\3"` will get the value at, for example, the JSON entry `{ "path": { "1": { "3": "hello world" } } }`

### XML

In order to properly allow accessing XML elements using the special dot notation, it is converted to JSON. While JSON does not support attributes like in XML, they can still be accessed. The following config.xml file will be parsed and examples will demonstrate how to navigate it:

    <root>
        <path>
            <to>
                <value attr="my_value"/>
            </to>
        </path>
    </root>

Examples:

    config.getOrDefault("root.path.to.value.attr", "Not found") // "my_value"
    config.getOrDefault("root.path.to.value", "Not found") // "Not found"

In addition, there are a few things to be aware of in order to avoid unintended or odd functionality:

- Avoid naming elements or attributes `content`
- If `<to>` was modified to be `<to value="some_value">`, for example, it should be considered unknown if the attribute or the child element will be accessed
