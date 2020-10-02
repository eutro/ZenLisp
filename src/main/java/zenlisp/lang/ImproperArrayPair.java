package zenlisp.lang;

public class ImproperArrayPair extends ArrayPair {
    private final Object end;

    public ImproperArrayPair(Object end, Object... els) {
        super(els);
        this.end = end;
    }

    @Override
    public Object rhs() {
        Object rhs = super.rhs();
        return rhs == null ? end : rhs;
    }
}
