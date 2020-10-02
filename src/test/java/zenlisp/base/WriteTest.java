package zenlisp.base;

import org.junit.jupiter.api.Test;
import zenlisp.lang.ArrayPair;
import zenlisp.lang.Cons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static zenlisp.lang.Symbol.intern;

public class WriteTest {
    @Test
    void display_cons() {
        assertEquals("(a . b)", Display.DISPLAY.write(new Cons(intern("a"), intern("b"))));
        assertEquals("(a . b)", Display.DISPLAY.write(new Cons("a", "b")));
        assertEquals("(a b . c)", Display.DISPLAY.write(new Cons("a", new Cons("b", "c"))));
        assertEquals("([a b c] [d [e f] g])",
                Display.DISPLAY.write(ArrayPair.list(
                        new Object[]{intern("a"), "b", intern("c")},
                        new Object[]{intern("d"), new Object[]{intern("e"), intern("f")}, intern("g")})));
    }

    @Test
    void write_cons() {
        assertEquals("(a . b)", Write.WRITE.write(new Cons(intern("a"), intern("b"))));
        assertEquals("(\"a\" . \"b\")", Write.WRITE.write(new Cons("a", "b")));
        assertEquals("(\"a\" \"b\" . \"c\")", Write.WRITE.write(new Cons("a", new Cons("b", "c"))));
        assertEquals("([a \"b\" c] [d [e f] g])",
                Write.WRITE.write(ArrayPair.list(
                        new Object[]{intern("a"), "b", intern("c")},
                        new Object[]{intern("d"), new Object[]{intern("e"), intern("f")}, intern("g")})));
    }
}
