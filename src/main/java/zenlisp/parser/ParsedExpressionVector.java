package zenlisp.parser;

import org.openzen.zencode.shared.CodePosition;
import org.openzen.zenscript.lexer.ParseException;
import org.openzen.zenscript.parser.definitions.ParsedFunctionHeader;
import org.openzen.zenscript.parser.expression.ParsedExpression;
import org.openzen.zenscript.parser.expression.ParsedExpressionArray;
import org.openzen.zenscript.parser.expression.ParsedExpressionBracket;

import java.util.List;

public class ParsedExpressionVector extends ParsedExpressionArray {
    public ParsedExpressionVector(CodePosition position, List<ParsedExpression> expressions) {
        super(position, expressions);
    }

    @Override
    public ParsedFunctionHeader toLambdaHeader() throws ParseException {
        return new ParsedExpressionBracket(position, contents).toLambdaHeader();
    }
}
