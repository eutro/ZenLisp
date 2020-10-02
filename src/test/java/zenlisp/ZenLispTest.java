package zenlisp;

import org.junit.jupiter.api.BeforeEach;
import org.openzen.zencode.java.ScriptingEngine;
import org.openzen.zencode.java.ZenCodeGlobals;
import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zencode.java.module.JavaNativeModule;
import org.openzen.zencode.shared.CompileException;
import org.openzen.zencode.shared.LiteralSourceFile;
import org.openzen.zencode.shared.SourceFile;
import org.openzen.zenscript.codemodel.FunctionParameter;
import org.openzen.zenscript.codemodel.SemanticModule;
import org.openzen.zenscript.lexer.ParseException;
import org.openzen.zenscript.parser.PrefixedBracketParser;
import zenlisp.parser.GeneralTests;
import zenlisp.parser.ZenLispBracketParser;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public abstract class ZenLispTest {

    private final List<SourceFile> sourceFiles;
    protected ScriptingEngine engine;
    protected JavaNativeModule testModule;
    protected static List<Object> out = new ArrayList<>();

    protected ZenLispTest() {
        sourceFiles = new ArrayList<>();
    }

    @BeforeEach
    public void beforeEach() throws CompileException {
        engine = new ScriptingEngine();
        engine.debug = true;
        testModule = engine.createNativeModule("test_module", ZenLispTest.class.getPackage().getName());
        testModule.addGlobals(GeneralTests.Globals.class);
        testModule.addClass(GeneralTests.Globals.class);
        engine.registerNativeProvided(testModule);
        out = new ArrayList<>();
    }

    public void executeEngine() {
        try {
            PrefixedBracketParser bep = new PrefixedBracketParser(null);
            bep.register("lisp", new ZenLispBracketParser());
            SemanticModule module = engine.createScriptedModule("script_tests",
                    sourceFiles.toArray(new SourceFile[0]),
                    bep,
                    FunctionParameter.NONE);
            assertTrue(module.isValid(), "Scripts are not valid!");
            engine.registerCompiled(module);
            engine.run();
        } catch (ParseException e) {
            e.printStackTrace();
            fail("Error in Engine execution", e);
        }
    }

    public void addScript(String content) {
        sourceFiles.add(new LiteralSourceFile("test" + sourceFiles.size() + ".zs", content));
    }

    @ZenCodeType.Name(".Globals")
    public static class Globals {
        @ZenCodeGlobals.Global
        public static void out(Object v) {
            out.add(v);
        }

        @ZenCodeGlobals.Global
        public static void out(byte v) {
            out((Object) v);
        }

        @ZenCodeGlobals.Global
        public static void out(char v) {
            out((Object) v);
        }

        @ZenCodeGlobals.Global
        public static void out(short v) {
            out((Object) v);
        }

        @ZenCodeGlobals.Global
        public static void out(int v) {
            out((Object) v);
        }

        @ZenCodeGlobals.Global
        public static void out(long v) {
            out((Object) v);
        }

        @ZenCodeGlobals.Global
        public static void out(float v) {
            out((Object) v);
        }

        @ZenCodeGlobals.Global
        public static void out(double v) {
            out((Object) v);
        }

        @ZenCodeGlobals.Global("void")
        public static void voidd() {
        }
    }
}
