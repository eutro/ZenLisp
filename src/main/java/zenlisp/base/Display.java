package zenlisp.base;

import zenlisp.lang.Pair;
import zenlisp.lang.Symbol;
import zenlisp.lang.Syntax;
import zenlisp.lang.TypeMap;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Display {
    public static final Display DISPLAY = new Display();

    protected final IdentityHashMap<Class<?>, Function<Object, String>> map = new IdentityHashMap<>();
    private final TypeMap<Function<Object, String>> typeMap = new TypeMap<>(map);

    protected Display() {
        map.put(Object.class, this::writeDefault);

        map.put(CharSequence.class, Object::toString);
        map.put(Syntax.class, Object::toString);
        map.put(Number.class, Object::toString);
        map.put(Boolean.class, Object::toString);
        map.put(Character.class, Object::toString);
        map.put(Symbol.class, Object::toString);

        map.put(Object[].class, a ->
                "[" + Arrays.stream((Object[]) a).map(this::write).collect(Collectors.joining(" ")) + "]"
        );
        map.put(Pair.class, o -> {
            Pair p = (Pair) o;
            Object rhs = p.rhs();
            if (!(rhs instanceof Pair) && rhs != null) return "(" + write(p.lhs()) + " . " + write(rhs) + ")";
            StringBuilder sb = new StringBuilder("(").append(write(p.lhs()));
            while (rhs instanceof Pair) {
                sb.append(" ").append(write(((Pair) rhs).lhs()));
                rhs = ((Pair) rhs).rhs();
            }
            if (rhs != null) sb.append(" . ").append(write(rhs));
            return sb.append(")").toString();
        });
    }

    public String write(Object o) {
        return o == null ? writeNull() : typeMap.get(o.getClass()).apply(o);
    }

    protected String writeNull() {
        return "nil";
    }

    private String writeDefault(Object o) {
        return "#" + o.getClass().getName() + "[" + write(o.toString()) + ']';
    }
}
