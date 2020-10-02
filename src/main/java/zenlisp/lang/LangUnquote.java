package zenlisp.lang;

public class LangUnquote {
    public final String value;
    public LangUnquote(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
