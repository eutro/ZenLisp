package zenlisp.lang;

import zenlisp.base.Write;

public class ArrayPair implements Pair {
    final int offset;
    final Object[] els;

    public static ArrayPair list(Object... els) {
        return els.length == 0 ? null : new ArrayPair(els);
    }

    protected ArrayPair(Object... els) {
        this(0, els);
    }

    private ArrayPair(int offset, Object[] els) {
        this.offset = offset;
        this.els = els;
    }

    @Override
    public Object lhs() {
        return els[offset];
    }

    @Override
    public Object rhs() {
        return offset == els.length - 1 ? null : new ArrayPair(offset + 1, els);
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
