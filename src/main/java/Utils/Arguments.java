package Utils;

public enum Arguments {

    EMBED ("embed"),
    IN ("in"),
    CARRIER ("p"),
    OUT ("out"),
    STEG ("steg"),
    EXTRACT ("extract");

    final String argumentName;

    Arguments(String argumentName) {
        this.argumentName = argumentName;
    }

    public String getArgumentName() {
        return argumentName;
    }


}

