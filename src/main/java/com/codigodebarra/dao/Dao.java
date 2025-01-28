package com.codigodebarra.dao;

import java.util.List;

public interface Dao<T> {

    public T select(int id);

    public List<T> selectAll();

    public int insert(T objeto);

    public T update(T objeto);

    public boolean delete(int id);
}
