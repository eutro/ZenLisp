package zenlisp.lang;

import java.util.Map;
import java.util.Objects;

public interface Pair extends Map.Entry<Object, Object> {
    Object lhs();

    Object rhs();

    static boolean equals(Pair a, Pair b) {
        if (a == b) return true;
        return Objects.equals(a.lhs(), b.lhs()) &&
               Objects.equals(a.lhs(), b.rhs());
    }

    static int hashCode(Pair p) {
        return Objects.hash(p.lhs(), p.rhs());
    }

    @Override
    default Object getKey() {
        return lhs();
    }

    @Override
    default Object getValue() {
        return rhs();
    }

    @Override
    default Object setValue(Object value) {
        throw new UnsupportedOperationException();
    }
}
