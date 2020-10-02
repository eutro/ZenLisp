package zenlisp.lang;

import zenlisp.base.Write;

public class Cons implements Pair {
    private final Object lhs, rhs;

    public Cons(Object lhs, Object rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object lhs() {
        return lhs;
    }

    @Override
    public Object rhs() {
        return rhs;
    }

    @Override
    public String toString() {
        return Write.WRITE.write(this);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Pair && Pair.equals(this, (Pair) o);
    }

    @Override
    public int hashCode() {
        return Pair.hashCode(this);
    }
}
