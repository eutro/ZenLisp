package zenlisp.base;

import org.openzen.zenscript.lexer.ParseException;
import zenlisp.lang.Syntax;
import zenlisp.parser.OffsetCountingCharReader;

import java.io.IOException;

public class ReadSyntax extends Read<ParseException, OffsetCountingCharReader> {
    public static final ReadSyntax READ_SYNTAX = new ReadSyntax();

    protected ReadSyntax() {
    }

    @Override
    protected ParseException getErr(OffsetCountingCharReader rd, String s) {
        return new ParseException(rd.getPosition(), s);
    }

    @Override
    public Object read(OffsetCountingCharReader rd) throws IOException, ParseException {
        return super.read(rd);
    }

    @Override
    protected Object readStarting(OffsetCountingCharReader rd, int c) throws IOException, ParseException {
        return Syntax.toSyntax(super.readStarting(rd, c), rd.getPosition());
    }
}
