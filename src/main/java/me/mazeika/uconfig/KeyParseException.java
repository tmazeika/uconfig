package me.mazeika.uconfig;

public class KeyParseException extends RuntimeException
{
    public KeyParseException()
    {
        super();
    }

    public KeyParseException(String message)
    {
        super(message);
    }

    public KeyParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public KeyParseException(Throwable cause)
    {
        super(cause);
    }
}
