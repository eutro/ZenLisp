package zenlisp.lang;

public class SneakyThrow {
    // Thanks, Java
    public static void sneakyThrow(Throwable t) {
        sneakyThrow0(t);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }
}
