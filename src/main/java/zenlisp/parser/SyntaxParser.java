package zenlisp.parser;

import com.google.common.collect.ImmutableMap;
import org.openzen.zencode.shared.CodePosition;
import org.openzen.zencode.shared.LiteralSourceFile;
import org.openzen.zenscript.codemodel.CompareType;
import org.openzen.zenscript.lexer.ParseException;
import org.openzen.zenscript.lexer.ZSTokenParser;
import org.openzen.zenscript.parser.BracketExpressionParser;
import org.openzen.zenscript.parser.expression.*;
import org.openzen.zenscript.parser.statements.ParsedLambdaFunctionBody;
import org.openzen.zenscript.parser.type.IParsedType;
import zenlisp.functions.Func;
import zenlisp.lang.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.openzen.zenscript.codemodel.OperatorType.*;
import static zenlisp.functions.Func.*;
import static zenlisp.lang.Symbol.intern;

public final class SyntaxParser {
    private SyntaxParser() {
    }

    // dynamic bindings:
    //
    // BracketExpressonParser oldBep = BEP.get();
    // BEP.set(newBep);
    // try {
    //     // ...
    // } finally {
    //     BEP.set(oldBep);
    // }
    public static final ThreadLocal<BracketExpressionParser> BEP = ThreadLocal.withInitial(() -> null);

    public static final F3X1<Object, CodePosition, Pair, ParsedExpression, ParseException> APPLICATION = variadicOnePlus((pos, receiver, args) ->
            new ParsedExpressionCall(pos, receiver, new ParsedCallArguments(/* TODO type arguments */Collections.emptyList(), args)));

    private static Map<Symbol, F3X1<Object, CodePosition, Pair, ParsedExpression, ParseException>> SPECIAL_EXPRS = ImmutableMap
            .<Symbol, F3X1<Object, CodePosition, Pair, ParsedExpression, ParseException>>builder()
            .put(intern("if"), ternary(ParsedExpressionConditional::new))
            .put(intern("lambda"), binary(cmp3c(cmp3b(ParsedExpressionFunction::new, ParsedExpression::toLambdaHeader), ParsedLambdaFunctionBody::new)))

            .put(intern("set!"), binary(ParsedExpressionAssign::new))

            .put(intern("set+!"), binary(prt4d(ParsedExpressionOpAssign::new, ADDASSIGN)))
            .put(intern("set-!"), binary(prt4d(ParsedExpressionOpAssign::new, SUBASSIGN)))
            .put(intern("set*!"), binary(prt4d(ParsedExpressionOpAssign::new, MULASSIGN)))
            .put(intern("set/!"), binary(prt4d(ParsedExpressionOpAssign::new, DIVASSIGN)))
            .put(intern("set-mod!"), binary(prt4d(ParsedExpressionOpAssign::new, MODASSIGN)))
            .put(intern("set~!"), binary(prt4d(ParsedExpressionOpAssign::new, CATASSIGN)))
            .put(intern("set-or!"), binary(prt4d(ParsedExpressionOpAssign::new, ORASSIGN)))
            .put(intern("set-and!"), binary(prt4d(ParsedExpressionOpAssign::new, ANDASSIGN)))
            .put(intern("set-xor!"), binary(prt4d(ParsedExpressionOpAssign::new, XORASSIGN)))
            .put(intern("set<<!"), binary(prt4d(ParsedExpressionOpAssign::new, SHLASSIGN)))
            .put(intern("set>>!"), binary(prt4d(ParsedExpressionOpAssign::new, SHRASSIGN)))
            .put(intern("set>>>!"), binary(prt4d(ParsedExpressionOpAssign::new, USHRASSIGN)))

            .put(intern("in?"), binary(prt4d(ParsedExpressionBinary::new, CONTAINS)))
            .put(intern("<<"), binary(prt4d(ParsedExpressionBinary::new, SHL)))
            .put(intern(">>"), binary(prt4d(ParsedExpressionBinary::new, SHR)))
            .put(intern(">>>"), binary(prt4d(ParsedExpressionBinary::new, USHR)))
            .put(intern("mod"), binary(prt4d(ParsedExpressionBinary::new, MOD)))
            .put(intern("range"), binary(ParsedExpressionRange::new))
            .put(intern("ref"), variadicOnePlus(ParsedExpressionIndex::new))

            .put(intern("inc!"), unary(prt3c(ParsedExpressionUnary::new, INCREMENT)))
            .put(intern("dec!"), unary(prt3c(ParsedExpressionUnary::new, DECREMENT)))
            .put(intern("not"), unary(prt3c(ParsedExpressionUnary::new, NOT)))

            .put(intern("try?"), unary(ParsedTryConvertExpression::new))
            .put(intern("try!"), unary(ParsedTryRethrowExpression::new))

            .put(intern("+"), variadicCollect(prt4d(ParsedExpressionBinary::new, ADD)))
            .put(intern("-"), variadicCollect(prt4d(ParsedExpressionBinary::new, SUB), prt3c(ParsedExpressionUnary::new, SUB)))
            .put(intern("*"), variadicCollect(prt4d(ParsedExpressionBinary::new, MUL)))
            .put(intern("/"), variadicCollect(prt4d(ParsedExpressionBinary::new, DIV)))
            .put(intern("~"), variadicCollect(prt4d(ParsedExpressionBinary::new, CAT), prt3c(ParsedExpressionUnary::new, CAT)))
            .put(intern("bit-or"), variadicCollect(prt4d(ParsedExpressionBinary::new, OR)))
            .put(intern("bit-and"), variadicCollect(prt4d(ParsedExpressionBinary::new, AND)))
            .put(intern("xor"), variadicCollect(prt4d(ParsedExpressionBinary::new, XOR)))

            .put(intern("or"), variadicCollect(ParsedExpressionOrOr::new))
            .put(intern("and"), variadicCollect(ParsedExpressionAndAnd::new))

            .put(intern("="), binary(prt4d(ParsedExpressionCompare::new, CompareType.EQ)))
            .put(intern("!="), binary(prt4d(ParsedExpressionCompare::new, CompareType.NE)))
            .put(intern("<"), binary(prt4d(ParsedExpressionCompare::new, CompareType.LT)))
            .put(intern(">"), binary(prt4d(ParsedExpressionCompare::new, CompareType.GT)))
            .put(intern("<="), binary(prt4d(ParsedExpressionCompare::new, CompareType.LE)))
            .put(intern(">="), binary(prt4d(ParsedExpressionCompare::new, CompareType.GE)))

            .put(intern("."), SyntaxParser::parseMember)
            .put(intern("as"), SyntaxParser.parseCast(false))
            .put(intern("as?"), SyntaxParser.parseCast(true))

            .build();

    private static TypeMap<F3X1<Object, CodePosition, Object, ParsedExpression, ParseException>> HANDLERS = new TypeMap<>(new ImmutableMap.Builder<Class<?>, F3X1<Object, CodePosition, Object, ParsedExpression, ParseException>>() {
        private <T> void handle(Class<T> clazz, F2X1<CodePosition, T, ParsedExpression, ParseException> handler) {
            put(clazz, (blame, pos, expr) -> handler.$(pos, clazz.cast(expr)));
        }

        {
            handle(Pair.class, (pos, expr) -> {
                Object trans = expr.lhs();
                while (trans instanceof Syntax) trans = ((Syntax) trans).datum;
                //noinspection SuspiciousMethodCalls
                return SPECIAL_EXPRS.getOrDefault(trans, APPLICATION).$(trans, pos, expr);
            });

            handle(Object[].class, (pos, expr) -> {
                List<ParsedExpression> list = new ArrayList<>();
                for (Object el : expr) list.add(parseExpr(el));
                return new ParsedExpressionVector(pos, list);
            });

            handle(Symbol.class, cmp2b(prt3c(ParsedExpressionVariable::new, Collections.emptyList()), Object::toString));

            handle(Object.class, (pos, expr) -> {
                throw new ParseException(pos, "Cannot parse " + expr + " of class " + expr.getClass());
            });

            handle(ParsedExpression.class, drp2a(id()));
            handle(LangUnquote.class, cmp2b(prt3c(Func./* inference? */<F1X1<ZSTokenParser, ParsedExpression, ParseException>, CodePosition, String, String, ParsedExpression, ParseException>
                    prt4a(SyntaxParser::withTokenStream, ParsedExpression::parse), "single expression"), (LangUnquote expr) -> expr.value));

            handle(String.class, cmp2b(prt3c(ParsedExpressionString::new, false), Object::toString));
            handle(Long.class, cmp2b(ParsedExpressionInt::new, Object::toString));
            handle(Double.class, cmp2b(ParsedExpressionFloat::new, Object::toString));
            handle(Boolean.class, ParsedExpressionBool::new);
        }
    }.build());

    public static ParsedExpression parseExpr(Object stx) throws ParseException {
        CodePosition position = CodePosition.UNKNOWN;
        if (stx instanceof Syntax) {
            position = ((Syntax) stx).position;
            stx = ((Syntax) stx).datum;
        }
        return stx == null ? new ParsedExpressionNull(position) : HANDLERS.get(stx.getClass()).$(stx, position, stx);
    }

    private static F3X1<Object, CodePosition, Pair, ParsedExpression, ParseException> withArity(int arity, F2X1<CodePosition, Object[], ParsedExpression, ParseException> parse) {
        return (blame, pos, expr) -> {
            Object[] args = new Object[arity];
            int i = 0;
            for (Object tail = expr.rhs(); tail != null; ) {
                if (!(tail instanceof Pair)) throw new ParseException(pos, "Expected list in " + blame + " expression");
                if (i >= arity) throw new ParseException(pos, "Expected " + arity + " arguments in " + blame);
                args[i++] = ((Pair) tail).lhs();
                tail = ((Pair) tail).rhs();
            }
            if (i < arity) throw new ParseException(pos, "Expected " + arity + " arguments in " + blame);
            return parse.$(pos, args);
        };
    }

    private static F3X1<Object, CodePosition, Pair, ParsedExpression, ParseException> unary(F2X1<CodePosition, ParsedExpression, ParsedExpression, ParseException> unary) {
        return withArity(1, sprd2b(cmp2b(unary, SyntaxParser::parseExpr)));
    }

    private static F3X1<Object, CodePosition, Pair, ParsedExpression, ParseException> binary(F3X1<CodePosition, ParsedExpression, ParsedExpression, ParsedExpression, ParseException> binary) {
        return withArity(2, sprd3b(cmp3c(cmp3b(binary, SyntaxParser::parseExpr), SyntaxParser::parseExpr)));
    }

    private static F3X1<Object, CodePosition, Pair, ParsedExpression, ParseException> ternary(F4X1<CodePosition, ParsedExpression, ParsedExpression, ParsedExpression, ParsedExpression, ParseException> ternary) {
        return withArity(3, sprd4b(cmp4d(cmp4c(cmp4b(ternary, SyntaxParser::parseExpr), SyntaxParser::parseExpr), SyntaxParser::parseExpr)));
    }

    private static F3X1<Object, CodePosition, Pair, ParsedExpression, ParseException> variadicOnePlus(F3X1<CodePosition, ParsedExpression, List<ParsedExpression>, ParsedExpression, ParseException> variadic) {
        return (blame, pos, expr) -> {
            List<ParsedExpression> arguments = new ArrayList<>();
            Object argExprs = expr.rhs();
            while (argExprs instanceof Pair) {
                arguments.add(parseExpr(((Pair) argExprs).lhs()));
                argExprs = ((Pair) argExprs).rhs();
            }
            if (argExprs != null) {
                throw new ParseException(pos, "Improper list shorthand not yet supported for " + blame);
            }
            return variadic.$(pos, parseExpr(expr.lhs()), arguments);
        };
    }

    private static F3X1<Object, CodePosition, Pair, ParsedExpression, ParseException> variadicCollect(F3X1<CodePosition, ParsedExpression, ParsedExpression, ParsedExpression, ParseException> binary) {
        return variadicCollect(binary, drp2a(id()));
    }

    private static F3X1<Object, CodePosition, Pair, ParsedExpression, ParseException> variadicCollect(F3X1<CodePosition, ParsedExpression, ParsedExpression, ParsedExpression, ParseException> binary,
                                                     F2X1<CodePosition, ParsedExpression, ParsedExpression, ParseException> unary) {
        F3X1<Object, CodePosition, Pair, ParsedExpression, ParseException> transform = variadicOnePlus((pos, receiver, args) -> {
            if (args.size() == 0) return unary.$(pos, receiver);
            for (ParsedExpression arg : args) {
                receiver = binary.$(pos, receiver, arg);
            }
            return receiver;
        });
        return (blame, pos, expr) -> {
            if (!(expr.rhs() instanceof Pair)) {
                throw new ParseException(pos, "Expected list of more than one element for " + blame);
            }
            return transform.$(blame, pos, ((Pair) expr.rhs()));
        };
    }

    private static ParsedExpression parseMember(Object blame, CodePosition pos, Pair expr) throws ParseException {
        ParsedExpression value = parseExpr(expr.lhs());
        for (Object tail = expr.rhs(); tail != null; ) {
            if (!(tail instanceof Pair)) throw new ParseException(pos, "Expected list in " + blame + " expression");
            value = nextMember(value, ((Pair) tail).lhs());
            tail = ((Pair) tail).rhs();
        }
        return value;
    }

    private static ParsedExpression nextMember(ParsedExpression value, Object member) throws ParseException {
        CodePosition position = CodePosition.UNKNOWN;
        if (member instanceof Syntax) {
            position = ((Syntax) member).position;
            member = ((Syntax) member).datum;
        }
        if (member instanceof Symbol || member instanceof String) {
            value = new ParsedExpressionMember(position, value, member.toString(), Collections.emptyList());
        } else if (member instanceof Pair) {
            return parseExpr(new Cons(nextMember(value, ((Pair) member).lhs()), ((Pair) member).rhs()));
        } else {
            throw new ParseException(position, "Cannot get member with expression " + member + (member == null ? "" : " of class " + member.getClass()));
        }
        return value;
    }

    private static F3X1<Object, CodePosition, Pair, ParsedExpression, ParseException> parseCast(boolean optional) {
        return withArity(2, (pos, exprs) -> new ParsedExpressionCast(pos, parseExpr(exprs[1]), parseType(exprs[0]), optional));
    }

    private static <T> T withTokenStream(F1X1<ZSTokenParser, T, ParseException> func, CodePosition at, String input, String onResidue) throws ParseException {
        ZSTokenParser tokens;
        try {
            tokens = ZSTokenParser.create(new LiteralSourceFile(at.getFilename(), input), BEP.get());
        } catch (IOException e) {
            throw new AssertionError();
        }
        try {
            T ret = func.$(tokens);
            if (tokens.hasNext()) throw new ParseException(at, "Expected " + onResidue + " got " + input);
            return ret;
        } catch (ParseException e) {
            ParseException exn = new ParseException(
                    new CodePosition(at.file,
                            at.fromLine + e.position.fromLine - 1,
                            at.fromLineOffset + e.position.fromLineOffset,
                            at.toLine + e.position.toLine - 1,
                            at.toLineOffset + e.position.toLineOffset),
                    e.message);
            exn.setStackTrace(e.getStackTrace());
            throw exn;
        }
    }

    private static IParsedType parseType(Object type) throws ParseException {
        CodePosition typePos;
        if (type instanceof Syntax) {
            typePos = ((Syntax) type).position;
            type = ((Syntax) type).datum;
        } else {
            typePos = CodePosition.UNKNOWN;
        }
        if (!(type instanceof Symbol)) throw new ParseException(typePos, "Expected symbol, got " + type);
        String input = type.toString();
        return withTokenStream(IParsedType::parse, typePos, input, "type");
    }
}
