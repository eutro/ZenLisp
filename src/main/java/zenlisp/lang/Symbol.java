package zenlisp.lang;

import com.google.common.collect.MapMaker;

import java.util.*;

public class Symbol {
    private static Map<String, Symbol> interns = new MapMaker().weakValues().makeMap();
    private final String name;

    public Symbol(String name) {
        this.name = name;
    }

    public static Symbol intern(String name) {
        return interns.computeIfAbsent(name, t -> new Symbol(name));
    }

    @Override
    public String toString() {
        return name;
    }
}
