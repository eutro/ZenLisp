package zenlisp.lang;

import org.openzen.zencode.shared.CodePosition;
import zenlisp.base.Write;

import java.util.Objects;

public final class Syntax {
    public final Object datum;
    public final CodePosition position;

    private Syntax(Object datum, CodePosition position) {
        this.datum = datum;
        this.position = position;
    }

    public static Object toSyntax(Object datum, CodePosition position) {
        if (datum == Eof.EOF) return datum;
        if (datum instanceof Syntax) datum = ((Syntax) datum).datum;
        return new Syntax(datum, position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Syntax syntax = (Syntax) o;
        return Objects.equals(datum, syntax.datum) && Objects.equals(position, syntax.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datum, position);
    }

    @Override
    public String toString() {
        return Write.WRITE.write(datum);
    }
}
