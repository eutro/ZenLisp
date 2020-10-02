package zenlisp.parser;

import org.openzen.zencode.shared.CodePosition;
import org.openzen.zenscript.lexer.ParseException;
import org.openzen.zenscript.lexer.StringCharReader;
import org.openzen.zenscript.lexer.ZSTokenParser;
import org.openzen.zenscript.lexer.ZSTokenType;
import org.openzen.zenscript.parser.BracketExpressionParser;
import org.openzen.zenscript.parser.expression.ParsedExpression;
import zenlisp.base.ReadSyntax;
import zenlisp.lang.Eof;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZenLispBracketParser implements BracketExpressionParser {
    @Override
    public ParsedExpression parse(CodePosition position, ZSTokenParser tokens) throws ParseException {
        int bangs = 0;
        while (tokens.optional(ZSTokenType.T_NOT) != null) bangs++;
        StringBuilder sb = new StringBuilder();
        if (bangs == 0) {
            while (tokens.optional(ZSTokenType.T_GREATER) == null) {
                sb.append(tokens.next().getContent());
                sb.append(tokens.getLastWhitespace());
            }
        } else {
            int outBangs;
            while (true) {
                while (tokens.optional(ZSTokenType.T_NOT) == null) {
                    sb.append(tokens.next().getContent());
                    sb.append(tokens.getLastWhitespace());
                }
                StringBuilder tmp = new StringBuilder();
                tmp.append("!").append(tokens.getLastWhitespace());
                outBangs = 1;
                while (tokens.optional(ZSTokenType.T_NOT) != null) {
                    outBangs++;
                    tmp.append("!").append(tokens.getLastWhitespace());
                }
                if (outBangs == bangs && tokens.optional(ZSTokenType.T_GREATER) != null) break;
                sb.append(tmp.toString());
            }
        }

        List<Object> exprs = new ArrayList<>();
        OffsetCountingCharReader rd = new OffsetCountingCharReader(new StringCharReader(sb.toString()), position);
        try {
            while (true) {
                Object expr = ReadSyntax.READ_SYNTAX.read(rd);
                if (expr == Eof.EOF) break;
                exprs.add(expr);
            }
        } catch (RuntimeException e) {
            throw new ParseException(position, "Eror reading", e);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        if (exprs.size() != 1) throw new UnsupportedOperationException();

        BracketExpressionParser old = SyntaxParser.BEP.get();
        SyntaxParser.BEP.set(tokens.bracketParser);
        try {
            return SyntaxParser.parseExpr(exprs.get(0));
        } finally {
            SyntaxParser.BEP.set(old);
        }
    }
}
