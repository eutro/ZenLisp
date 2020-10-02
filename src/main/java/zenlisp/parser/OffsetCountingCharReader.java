package zenlisp.parser;

import org.openzen.zencode.shared.CodePosition;
import org.openzen.zencode.shared.SourceFile;
import org.openzen.zenscript.lexer.CharReader;

import java.io.IOException;

public class OffsetCountingCharReader implements CharReader {
    private final CharReader reader;
    private final SourceFile file;

    private int line;
    private int lineOffset;

    public OffsetCountingCharReader(CharReader reader, CodePosition position) {
        this.reader = reader;
        this.file = position.getFile();

        line = position.getFromLine();
        lineOffset = position.getFromLineOffset();
    }

    public CodePosition getPosition() {
        return new CodePosition(file, line, lineOffset, line, lineOffset);
    }

    @Override
    public int peek() throws IOException {
        return reader.peek();
    }

    @Override
    public int next() throws IOException {
        int ch = reader.next();
        if (ch == -1)
            return ch;

        if (ch == '\n') {
            line++;
            lineOffset = 0;
        } else {
            lineOffset++;
        }
        return ch;
    }
}
