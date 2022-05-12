package itba.edu.ar.Utils;

public enum OptionalArguments {
    A ("a"),
    M ("m"),
    PASS ("pass");

    final String optArgumentName;

    OptionalArguments(String argumentName) {
        this.optArgumentName = argumentName;
    }

    public String getArgumentName() {
        return optArgumentName;
    }
}
