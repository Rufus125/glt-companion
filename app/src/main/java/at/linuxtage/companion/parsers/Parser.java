package at.linuxtage.companion.parsers;

import okio.BufferedSource;

public interface Parser<T> {
    T parse(BufferedSource source) throws Exception;
}
