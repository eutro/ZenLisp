package zenlisp.base;

import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zenscript.lexer.StringCharReader;
import zenlisp.lang.ArrayPair;
import zenlisp.lang.Pair;

import java.io.IOException;

@ZenCodeType.Name("zenlisp.base.Globals")
public class Globals {
    @ZenCodeType.Method("read-string")
    public static Object readString(String s) {
        try {
            return Read.READ.read(new StringCharReader(s));
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @ZenCodeType.Method
    public static Object cons(Object... els) {
        return ArrayPair.list(els);
    }

    @ZenCodeType.Method
    public static Object list(Object... els) {
        return ArrayPair.list(els);
    }

    @ZenCodeType.Method
    public static Object lhs(Object pair) {
        return ((Pair) pair).lhs();
    }

    @ZenCodeType.Method
    public static Object rhs(Object pair) {
        return ((Pair) pair).rhs();
    }
}
