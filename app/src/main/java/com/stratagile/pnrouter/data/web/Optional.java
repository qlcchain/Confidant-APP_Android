package com.stratagile.pnrouter.data.web;

import java.io.Serializable;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.stratagile.pnrouter.data.web.PreconditionsLocal.checkNotNull;

public abstract class Optional<T> implements Serializable {
    /**
     * Returns an {@code Optional} instance with no contained reference.
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> absent() {
        return (Optional<T>) Absent.INSTANCE;
    }

    /**
     * Returns an {@code Optional} instance containing the given non-null reference.
     */
    public static <T> Optional<T> of(T reference) {
        return new Present<T>(checkNotNull(reference));
    }

    /**
     * If {@code nullableReference} is non-null, returns an {@code Optional} instance containing that
     * reference; otherwise returns {@link Optional#absent}.
     */
    public static <T> Optional<T> fromNullable(T nullableReference) {
        return (nullableReference == null)
                ? Optional.<T>absent()
                : new Present<T>(nullableReference);
    }

    Optional() {}

    /**
     * Returns {@code true} if this holder contains a (non-null) instance.
     */
    public abstract boolean isPresent();

    /**
     * Returns the contained instance, which must be present. If the instance might be
     * absent, use {@link #or(Object)} or {@link #orNull} instead.
     *
     * @throws IllegalStateException if the instance is absent ({@link #isPresent} returns
     *     {@code false})
     */
    public abstract T get();

    /**
     * Returns the contained instance if it is present; {@code defaultValue} otherwise. If
     * no default value should be required because the instance is known to be present, use
     * {@link #get()} instead. For a default value of {@code null}, use {@link #orNull}.
     *
     * <p>Note about generics: The signature {@code public T or(T defaultValue)} is overly
     * restrictive. However, the ideal signature, {@code public <S super T> S or(S)}, is not legal
     * Java. As a result, some sensible operations involving subtypes are compile errors:
     * <pre>   {@code
     *
     *   Optional<Integer> optionalInt = getSomeOptionalInt();
     *   Number value = optionalInt.or(0.5); // error
     *
     *   FluentIterable<? extends Number> numbers = getSomeNumbers();
     *   Optional<? extends Number> first = numbers.first();
     *   Number value = first.or(0.5); // error}</pre>
     *
     * As a workaround, it is always safe to cast an {@code Optional<? extends T>} to {@code
     * Optional<T>}. Casting either of the above example {@code Optional} instances to {@code
     * Optional<Number>} (where {@code Number} is the desired output type) solves the problem:
     * <pre>   {@code
     *
     *   Optional<Number> optionalInt = (Optional) getSomeOptionalInt();
     *   Number value = optionalInt.or(0.5); // fine
     *
     *   FluentIterable<? extends Number> numbers = getSomeNumbers();
     *   Optional<Number> first = (Optional) numbers.first();
     *   Number value = first.or(0.5); // fine}</pre>
     */
    public abstract T or(T defaultValue);

    /**
     * Returns this {@code Optional} if it has a value present; {@code secondChoice}
     * otherwise.
     */
    public abstract Optional<T> or(Optional<? extends T> secondChoice);

    /**
     * Returns the contained instance if it is present; {@code supplier.get()} otherwise. If the
     * supplier returns {@code null}, a {@link NullPointerException} is thrown.
     *
     * @throws NullPointerException if the supplier returns {@code null}
     */
    public abstract T or(Supplier<? extends T> supplier);

    /**
     * Returns the contained instance if it is present; {@code null} otherwise. If the
     * instance is known to be present, use {@link #get()} instead.
     */
    public abstract T orNull();

    /**
     * Returns an immutable singleton {@link Set} whose only element is the contained instance
     * if it is present; an empty immutable {@link Set} otherwise.
     *
     * @since 11.0
     */
    public abstract Set<T> asSet();

    /**
     * If the instance is present, it is transformed with the given {@link Function}; otherwise,
     * {@link Optional#absent} is returned. If the function returns {@code null}, a
     * {@link NullPointerException} is thrown.
     *
     * @throws NullPointerException if the function returns {@code null}
     *
     * @since 12.0
     */

    public abstract <V> Optional<V> transform(Function<? super T, V> function);

    /**
     * Returns {@code true} if {@code object} is an {@code Optional} instance, and either
     * the contained references are {@linkplain Object#equals equal} to each other or both
     * are absent. Note that {@code Optional} instances of differing parameterized types can
     * be equal.
     */
    @Override public abstract boolean equals(Object object);

    /**
     * Returns a hash code for this instance.
     */
    @Override public abstract int hashCode();

    /**
     * Returns a string representation for this instance. The form of this string
     * representation is unspecified.
     */
    @Override public abstract String toString();

    /**
     * Returns the value of each present instance from the supplied {@code optionals}, in order,
     * skipping over occurrences of {@link Optional#absent}. Iterators are unmodifiable and are
     * evaluated lazily.
     *
     * @since 11.0 (generics widened in 13.0)
     */

//  public static <T> Iterable<T> presentInstances(
//      final Iterable<? extends Optional<? extends T>> optionals) {
//    checkNotNull(optionals);
//    return new Iterable<T>() {
//      @Override public Iterator<T> iterator() {
//        return new AbstractIterator<T>() {
//          private final Iterator<? extends Optional<? extends T>> iterator =
//              checkNotNull(optionals.iterator());
//
//          @Override protected T computeNext() {
//            while (iterator.hasNext()) {
//              Optional<? extends T> optional = iterator.next();
//              if (optional.isPresent()) {
//                return optional.get();
//              }
//            }
//            return endOfData();
//          }
//        };
//      };
//    };
//  }

    private static final long serialVersionUID = 0;
}
