package zenlisp.functions;

/** @formatter:off */
public interface Func {
    interface F0X1<R, T extends Throwable> { R $() throws T; }
    interface F1X1<A, R, T extends Throwable> { R $(A a) throws T; }
    interface F2X1<A, B, R, T extends Throwable> { R $(A a, B b) throws T; }
    interface F3X1<A, B, C, R, T extends Throwable> { R $(A a, B b, C c) throws T; }
    interface F4X1<A, B, C, D, R, T extends Throwable> { R $(A a, B b, C c, D d) throws T; }

    static <Z, R, T extends Throwable> F1X1<Z, R, T> drp1a(F0X1<R, T> f) { return z -> f.$(); }
    static <Z, B, R, T extends Throwable> F2X1<Z, B, R, T> drp2a(F1X1<B, R, T> f) { return (z, b) -> f.$(b); }
    static <A, Z, R, T extends Throwable> F2X1<A, Z, R, T> drp2b(F1X1<A, R, T> f) { return (a, z) -> f.$(a); }
    static <Z, B, C, R, T extends Throwable> F3X1<Z, B, C, R, T> drp3a(F2X1<B, C, R, T> f) { return (z, b, c) -> f.$(b, c); }
    static <A, Z, C, R, T extends Throwable> F3X1<A, Z, C, R, T> drp3b(F2X1<A, C, R, T> f) { return (a, z, c) -> f.$(a, c); }
    static <A, B, Z, R, T extends Throwable> F3X1<A, B, Z, R, T> drp3c(F2X1<A, B, R, T> f) { return (a, b, z) -> f.$(a, b); }
    static <Z, B, C, D, R, T extends Throwable> F4X1<Z, B, C, D, R, T> drp4a(F3X1<B, C, D, R, T> f) { return (z, b, c, d) -> f.$(b, c, d); }
    static <A, Z, C, D, R, T extends Throwable> F4X1<A, Z, C, D, R, T> drp4b(F3X1<A, C, D, R, T> f) { return (a, z, c, d) -> f.$(a, c, d); }
    static <A, B, Z, D, R, T extends Throwable> F4X1<A, B, Z, D, R, T> drp4c(F3X1<A, B, D, R, T> f) { return (a, b, z, d) -> f.$(a, b, d); }
    static <A, B, C, Z, R, T extends Throwable> F4X1<A, B, C, Z, R, T> drp4f(F3X1<A, B, C, R, T> f) { return (a, b, c, z) -> f.$(a, b, c); }

    static <A, R, T extends Throwable> F0X1<R, T> prt1a(F1X1<A, R, T> f, A a) { return () -> f.$(a); }
    static <A, B, R, T extends Throwable> F1X1<B, R, T> prt2a(F2X1<A, B, R, T> f, A a) { return b -> f.$(a, b); }
    static <A, B, R, T extends Throwable> F1X1<A, R, T> prt2b(F2X1<A, B, R, T> f, B b) { return a -> f.$(a, b); }
    static <A, B, C, R, T extends Throwable> F2X1<B, C, R, T> prt3a(F3X1<A, B, C, R, T> f, A a) { return (b, c) -> f.$(a, b, c); }
    static <A, B, C, R, T extends Throwable> F2X1<A, C, R, T> prt3b(F3X1<A, B, C, R, T> f, B b) { return (a, c) -> f.$(a, b, c); }
    static <A, B, C, R, T extends Throwable> F2X1<A, B, R, T> prt3c(F3X1<A, B, C, R, T> f, C c) { return (a, b) -> f.$(a, b, c); }
    static <A, B, C, D, R, T extends Throwable> F3X1<B, C, D, R, T> prt4a(F4X1<A, B, C, D, R, T> f, A a) { return (b, c, d) -> f.$(a, b, c, d); }
    static <A, B, C, D, R, T extends Throwable> F3X1<A, C, D, R, T> prt4b(F4X1<A, B, C, D, R, T> f, B b) { return (a, c, d) -> f.$(a, b, c, d); }
    static <A, B, C, D, R, T extends Throwable> F3X1<A, B, D, R, T> prt4c(F4X1<A, B, C, D, R, T> f, C c) { return (a, b, d) -> f.$(a, b, c, d); }
    static <A, B, C, D, R, T extends Throwable> F3X1<A, B, C, R, T> prt4d(F4X1<A, B, C, D, R, T> f, D d) { return (a, b, c) -> f.$(a, b, c, d); }

    static <A, T extends Throwable> F1X1<A, A, T> id() { return a -> a; }

    static <A, Z, R, T extends Throwable> F1X1<A, R, T> cmp1a(F1X1<Z, R, T> f, F1X1<A, Z, T> g) { return a -> f.$(g.$(a)); }
    static <A, B, Z, R, T extends Throwable> F2X1<A, B, R, T> cmp2a(F2X1<Z, B, R, T> f, F1X1<A, Z, T> g) { return (a, b) -> f.$(g.$(a), b); }
    static <A, B, Z, R, T extends Throwable> F2X1<A, B, R, T> cmp2b(F2X1<A, Z, R, T> f, F1X1<B, Z, T> g) { return (a, b) -> f.$(a, g.$(b)); }
    static <A, B, C, Z, R, T extends Throwable> F3X1<A, B, C, R, T> cmp3a(F3X1<Z, B, C, R, T> f, F1X1<A, Z, T> g) { return (a, b, c) -> f.$(g.$(a), b, c); }
    static <A, B, C, Z, R, T extends Throwable> F3X1<A, B, C, R, T> cmp3b(F3X1<A, Z, C, R, T> f, F1X1<B, Z, T> g) { return (a, b, c) -> f.$(a, g.$(b), c); }
    static <A, B, C, Z, R, T extends Throwable> F3X1<A, B, C, R, T> cmp3c(F3X1<A, B, Z, R, T> f, F1X1<C, Z, T> g) { return (a, b, c) -> f.$(a, b, g.$(c)); }
    static <A, B, C, D, Z, R, T extends Throwable> F4X1<A, B, C, D, R, T> cmp4a(F4X1<Z, B, C, D, R, T> f, F1X1<A, Z, T> g) { return (a, b, c, d) -> f.$(g.$(a), b, c, d); }
    static <A, B, C, D, Z, R, T extends Throwable> F4X1<A, B, C, D, R, T> cmp4b(F4X1<A, Z, C, D, R, T> f, F1X1<B, Z, T> g) { return (a, b, c, d) -> f.$(a, g.$(b), c, d); }
    static <A, B, C, D, Z, R, T extends Throwable> F4X1<A, B, C, D, R, T> cmp4c(F4X1<A, B, Z, D, R, T> f, F1X1<C, Z, T> g) { return (a, b, c, d) -> f.$(a, b, g.$(c), d); }
    static <A, B, C, D, Z, R, T extends Throwable> F4X1<A, B, C, D, R, T> cmp4d(F4X1<A, B, C, Z, R, T> f, F1X1<D, Z, T> g) { return (a, b, c, d) -> f.$(a, b, c, g.$(d)); }

    static <A, R, T extends Throwable> F1X1<A[], R, T> sprd1a(F1X1<A, R, T> f) { return as -> f.$(as[0]); }
    static <A, R, T extends Throwable> F1X1<A[], R, T> sprd2a(F2X1<A, A, R, T> f) { return as -> f.$(as[0], as[1]); }
    static <A, R, T extends Throwable> F1X1<A[], R, T> sprd3a(F3X1<A, A, A, R, T> f) { return as -> f.$(as[0], as[1], as[2]); }
    static <A, R, T extends Throwable> F1X1<A[], R, T> sprd4a(F4X1<A, A, A, A, R, T> f) { return as -> f.$(as[0], as[1], as[2], as[3]); }
    static <A, B, R, T extends Throwable> F2X1<A, B[], R, T> sprd2b(F2X1<A, B, R, T> f) { return (a, bs) -> f.$(a, bs[0]); }
    static <A, B, R, T extends Throwable> F2X1<A, B[], R, T> sprd3b(F3X1<A, B, B, R, T> f) { return (a, bs) -> f.$(a, bs[0], bs[1]); }
    static <A, B, R, T extends Throwable> F2X1<A, B[], R, T> sprd4b(F4X1<A, B, B, B, R, T> f) { return (a, bs) -> f.$(a, bs[0], bs[1], bs[2]); }
    static <A, B, C, R, T extends Throwable> F3X1<A, B, C[], R, T> sprd3c(F3X1<A, B, C, R, T> f) { return (a, b, cs) -> f.$(a, b, cs[0]); }
    static <A, B, C, R, T extends Throwable> F3X1<A, B, C[], R, T> sprd4c(F4X1<A, B, C, C, R, T> f) { return (a, b, cs) -> f.$(a, b, cs[0], cs[1]); }
    static <A, B, C, D, R, T extends Throwable> F4X1<A, B, C, D[], R, T> sprd4d(F4X1<A, B, C, D, R, T> f) { return (a, b, c, ds) -> f.$(a, b, c, ds[0]); }
}
