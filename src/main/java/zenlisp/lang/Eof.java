package zenlisp.lang;

public final class Eof {
    public static final Eof EOF = new Eof();
    private Eof() {}

    @Override
    public String toString() {
        return "EOF";
    }
}
