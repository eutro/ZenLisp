package zenlisp.base;

import java.util.regex.Pattern;

public class Write extends Display {
    public static Write WRITE = new Write();

    protected Write() {
        Pattern escapeRegex = Pattern.compile("['\"\\\\]");
        map.put(String.class, o -> "\"" + escapeRegex.matcher((CharSequence) o).replaceAll("\\\\$1") + '"');
    }
}
