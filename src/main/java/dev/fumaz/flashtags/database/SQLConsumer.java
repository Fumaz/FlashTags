package dev.fumaz.flashtags.database;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLConsumer<T> {

    void accept(T value) throws SQLException;

}
