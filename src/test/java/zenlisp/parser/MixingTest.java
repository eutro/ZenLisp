package zenlisp.parser;

import org.junit.jupiter.api.Test;
import zenlisp.ZenLispTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MixingTest extends ZenLispTest {
    @Test
    void factors() {
        addScript("" +
                  "var n = 100;\n" +
                  "for f in <lisp:(range 1 101)> {\n" +
                  "  <lisp:(if (= 0 (mod n f))\n" +
                  "            (out f)\n" +
                  "            (void))>;\n" +
                  "}\n");
        executeEngine();
        int n = 100;
        List<Integer> factors = new ArrayList<>();
        for (int f = 1; f < 101; f++) {
            if (n % f == 0) {
                factors.add(f);
            }
        }
        assertEquals(factors, out);
    }

    @Test
    void triple_nested() {
        addScript("" +
                  "out(<lisp:!!\n" +
                  "            (+ 1 ####2 + <lisp:!\n" +
                  "                          (+ 3 ###4 * <lisp:\n" +
                  "                                       (* 5 ##6 / 2##)\n" +
                  "                                       >###)\n" +
                  "                          !>####)\n" +
                  "            !!>);\n" +
                  "");
        executeEngine();
        assertEquals(1 + 2 + 3 + (4 * 5 * (6 / 2)), ((Number) out.get(0)).intValue());
    }
}
