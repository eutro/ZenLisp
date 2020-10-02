package zenlisp.parser;

import org.junit.jupiter.api.Test;
import zenlisp.ZenLispTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneralTests extends ZenLispTest {
    @Test
    void plain_out() {
        addScript("out(true);");
        executeEngine();
        assertEquals(true, out.get(0));
    }

    @Test
    void out_true() {
        addScript("<lisp:(out true)>;");
        executeEngine();
        assertEquals(true, out.get(0));
    }
}
