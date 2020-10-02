package zenlisp.lang;

import java.util.Map;

public class TypeMap<T> {
    private final Map<Class<?>, T> map;

    public TypeMap(Map<Class<?>, T> map) {
        this.map = map;
    }

    public T get(Class<?> type) {
        T v = get0(type);
        return v != null ? v : map.get(Object.class);
    }

    private T get0(Class<?> type) {
        while (type != null) {
            if (map.containsKey(type)) {
                return map.get(type);
            }
            for (Class<?> iType : type.getInterfaces()) {
                T candidate = get0(iType);
                if (candidate != null) return candidate;
            }
            type = type.getSuperclass();
        }
        return null;
    }
}
