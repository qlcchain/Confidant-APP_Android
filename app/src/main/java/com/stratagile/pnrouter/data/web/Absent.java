package com.stratagile.pnrouter.data.web;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.stratagile.pnrouter.data.web.PreconditionsLocal.checkNotNull;

final class Absent extends Optional<Object> {
    static final Absent INSTANCE = new Absent();

    @Override public boolean isPresent() {
        return false;
    }

    @Override public Object get() {
        throw new IllegalStateException("value is absent");
    }

    @Override public Object or(Object defaultValue) {
        return checkNotNull(defaultValue, "use orNull() instead of or(null)");
    }

    @SuppressWarnings("unchecked") // safe covariant cast
    @Override public Optional<Object> or(Optional<?> secondChoice) {
        return (Optional) checkNotNull(secondChoice);
    }

    @Override public Object or(Supplier<?> supplier) {
        return checkNotNull(supplier.get(),
                "use orNull() instead of a Supplier that returns null");
    }

    @Override public Object orNull() {
        return null;
    }

    @Override public Set<Object> asSet() {
        return Collections.emptySet();
    }

    @Override
    public <V> Optional<V> transform(Function<? super Object, V> function) {
        checkNotNull(function);
        return Optional.absent();
    }

    @Override public boolean equals(Object object) {
        return object == this;
    }

    @Override public int hashCode() {
        return 0x598df91c;
    }

    @Override public String toString() {
        return "Optional.absent()";
    }

    private Object readResolve() {
        return INSTANCE;
    }

    private static final long serialVersionUID = 0;
}

