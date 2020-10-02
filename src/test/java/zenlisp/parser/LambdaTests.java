package zenlisp.parser;

import org.junit.jupiter.api.Test;
import zenlisp.ZenLispTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LambdaTests extends ZenLispTest {
    @Test
    void lambda_out_true() {
        addScript("<lisp:((lambda [] (out true)))>;");
        executeEngine();
        assertEquals(true, out.get(0));
    }
}
