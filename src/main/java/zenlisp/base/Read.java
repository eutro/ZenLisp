package zenlisp.base;

import org.openzen.zenscript.lexer.CharReader;
import zenlisp.lang.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Read<E extends Exception, R extends CharReader> {
    public static Read<RuntimeException, CharReader> READ = new Read<RuntimeException, CharReader>() {
        @Override
        RuntimeException getErr(CharReader rd, String s) {
            return new RuntimeException(s);
        }
    };

    protected Read() {
    }

    abstract E getErr(R rd, String s);

    public Object read(R rd) throws IOException, E {
        return readStarting(rd, skipWs(rd));
    }

    protected Object readStarting(R rd, int c) throws IOException, E {
        while (true) {
            switch (c) {
                case '(':
                    return readList(rd);
                case '[':
                    return readVector(rd);
                case '{':
                    // TODO maps
                case '"':
                    return readString(rd);
                case ')':
                case ']':
                case '}':
                    throw getErr(rd, "Unexpected " + (char) c);
                case ';':
                    do c = rd.next(); while (c != '\n' && c != -1);
                    break;
                case '#':
                    return readDispatch(rd);
                case -1:
                    return Eof.EOF;
                default:
                    return readToken(rd, c);
            }
        }
    }

    private Object readDispatch(R rd) throws IOException, E {
        int c = rd.next();
        switch (c) {
            case '#':
                return readLangUnquote(rd);
            case ';':
                read(rd);
                return read(rd);
            default:
                throw getErr(rd, "No dispatch macro for character '" + c + "'");
        }
    }

    private Object readLangUnquote(R rd) throws IOException, E {
        int pounds = 2;
        int c;
        while ((c = rd.next()) == '#') {
            pounds++;
        }
        StringBuilder sb = new StringBuilder();
        sb.append((char) c);
        while ((c = rd.next()) != -1) {
            if (c == '#') {
                int esc = 1;
                while ((c = rd.next()) == '#') {
                    if (++esc >= pounds) {
                        return new LangUnquote(sb.toString());
                    }
                }
                for (int i = 0; i < esc; i++) {
                    sb.append('#');
                }
            }
            sb.append((char) c);
        }
        throw getErr(rd, "Unexpected EOF reading lang unquote");
    }

    private int skipWs(CharReader rd) throws IOException {
        int c;
        do c = rd.next(); while (isWhitespace(c));
        return c;
    }

    private boolean isWhitespace(int c) {
        return Character.isWhitespace(c);
    }

    private Object readList(R rd) throws IOException, E {
        List<Object> list = new ArrayList<>();
        int c;
        while (true) {
            switch (c = skipWs(rd)) {
                case ')':
                    return ArrayPair.list(list.toArray());
                case '.':
                    if (isWhitespace(rd.peek())) {
                        c = skipWs(rd);
                        if (c != -1 && c != ')' && !list.isEmpty()) {
                            Object next = readStarting(rd, c);
                            if ((c = skipWs(rd)) == ')') {
                                return new ImproperArrayPair(next, list.toArray());
                            }
                            list.add(Symbol.intern("."));
                            list.add(next);
                        } else {
                            list.add(Symbol.intern("."));
                        }
                    }
                default:
                    if (c == -1) throw getErr(rd, "Unexpected EOF in list");
                    list.add(readStarting(rd, c));
            }
        }
    }

    private Object readVector(R rd) throws IOException, E {
        List<Object> list = new ArrayList<>();
        int c;
        while (true) {
            switch (c = skipWs(rd)) {
                case ']': {
                    return list.toArray();
                }
                case -1:
                    throw getErr(rd, "Unexpected EOF in list");
                default:
                    list.add(readStarting(rd, c));
            }
        }
    }

    private Object readToken(R rd, int c) throws IOException, E {
        boolean escaped = false;
        boolean consume = false;
        StringBuilder token = new StringBuilder();
        loop:
        while (true) {
            switch (c) {
                case '|':
                    escaped = true;
                    while ((c = rd.next()) != '|' && c != -1) token.append((char) c);
                    if (c == -1) throw getErr(rd, "Unexpected EOF after |");
                    break;
                case '\\':
                    escaped = true;
                    c = rd.next();
                    if (c == -1) throw getErr(rd, "Unexpected EOF after \\");
                    token.append((char) c);
                    break;
                case '(':
                case ')':
                case '[':
                case ']':
                case '{':
                case '}':
                case ';':
                case -1:
                    break loop;
                default:
                    if (isWhitespace(c)) break loop;
                    token.append((char) c);
                    break;
            }
            if (consume) rd.next();
            else consume = true;
            c = rd.peek();
        }
        return escaped ? Symbol.intern(token.toString()) : interpretToken(token.toString());
    }

    private Object interpretToken(String tokenString) {
        if ("nil".equals(tokenString)) return null;
        if ("true".equals(tokenString)) return true;
        if ("false".equals(tokenString)) return false;
        boolean neg = false;
        if (tokenString.startsWith("+") || (neg = tokenString.startsWith("+"))) {
            Object interpreted = interpretToken(tokenString.substring(1));
            if (interpreted instanceof Double) {
                return neg ? -(double) interpreted : interpreted;
            }
            if (interpreted instanceof Long) {
                return neg ? -(long) interpreted : interpreted;
            }
        }
        try {
            if (tokenString.startsWith("0x")) return Long.parseLong(tokenString.substring(2), 16);
            if (tokenString.startsWith("0b")) return Long.parseLong(tokenString.substring(2), 2);
            if (tokenString.startsWith("0") && tokenString.length() > 1) {
                return Long.parseLong(tokenString.substring(1), 8);
            }
            return Long.parseLong(tokenString);
        } catch (NumberFormatException ignored) {
        }
        try {
            return Double.parseDouble(tokenString);
        } catch (NumberFormatException ignored) {
        }
        return Symbol.intern(tokenString);
    }

    private Object readString(R rd) throws IOException, E {
        int c;
        StringBuilder sb = new StringBuilder();
        while ((c = rd.next()) != '"') {
            if (c == '\\') {
                c = readEscape(rd);
                if (c == -1) continue;
            } else if (c == -1) {
                throw getErr(rd, "Unexpected EOF while reading string");
            }
            sb.append((char) c);
        }
        return sb.toString();
    }

    private int readEscape(R rd) throws IOException, E {
        int c;
        switch (c = rd.next()) {
            case 't':
                return '\t';
            case 'b':
                return '\b';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 'f':
                return '\f';
            case 'u':
                int hex = 0;
                for (int i = 0; i < 4; i++) {
                    c = rd.next();
                    if (!(('0' <= c && c <= '9') || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F'))) {
                        throw getErr(rd, "Unrecognised hex digit '" + (char) c + '"');
                    }
                    hex *= 16;
                    hex += Character.digit(c, 16);
                }
                return (char) hex;
            case '\'':
            case '\"':
            case '\\':
                return (char) c;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
                int octal = Character.digit(c, 8);
                for (int i = 0; i < 2; i++) {
                    c = rd.peek();
                    if (c < '0' || '7' < c) {
                        break;
                    }
                    rd.next();
                    octal *= 8;
                    octal += Character.digit(c, 8);
                }
                return (char) octal;
            case '\r':
                if (rd.next() != '\n') break;
            case '\n':
                while (Character.isWhitespace(rd.peek())) rd.next();
                return -1;
        }
        throw getErr(rd, "Unrecognised escape sequence \\" + (char) c);
    }
}
