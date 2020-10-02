package zenlisp.base;

import org.junit.jupiter.api.Test;
import org.openzen.zenscript.lexer.StringCharReader;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ReadTest {
    Object readString(String s) throws IOException {
        return Read.READ.read(new StringCharReader(s));
    }

    @Test
    void read() throws IOException {
        readString("(+ 1 2 3)");
        assertNull(readString("nil"));
        assertEquals(true, readString("true"));
        assertEquals(false, readString("false"));
    }
}