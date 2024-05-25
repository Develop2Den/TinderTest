package web.dao;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T>{
    T getById(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    void save(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(int id) throws SQLException;
}
