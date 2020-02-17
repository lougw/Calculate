package com.lougw.calculate.utils.adapter;

import java.util.List;

@SuppressWarnings("unused")
public interface IData<T> {
    /**
     * 根据位置查找数据
     *
     * @param position
     * @return
     */
    T getItem(int position);

    /**
     * 获取当前数据列表
     *
     * @return
     */
    List<T> getData();

    /**
     * 添加一条数据
     *
     * @param item
     */
    void add(T item);

    /**
     * 添加一条数据
     *
     * @param item
     */
    void add(T item, int index);

    /**
     * 添加多条数据
     *
     * @param list
     */
    void addAll(List<T> list);

    /**
     * 添加多条数据
     *
     * @param list
     */
    void addAll(List<T> list, int index);

    /**
     * 更新一条数据
     *
     * @param oldItem
     * @param newItem
     */
    void set(T oldItem, T newItem);

    /**
     * 根据下标更新一条数据
     *
     * @param index
     * @param item
     */
    void set(int index, T item);

    /**
     * 删除一条数据
     *
     * @param item
     */
    void remove(T item);

    /**
     * 根据下标删除一条数据
     *
     * @param index
     */
    void remove(int index);

    /**
     * 根据下标删除一条数据
     *
     * @param list
     */
    void removeAll(List<T> list);

    /**
     * 替换所有数据
     *
     * @param list
     */
    void setData(List<T> list);

    /**
     * 添加多条数据
     *
     * @param
     */
    void replaceAll(List<T> oldList, List<T> newList, int index);

    /**
     * 是否存在某个对象
     *
     * @param item
     * @return
     */
    boolean contains(T item);

    /**
     * 清空数据
     */
    void clear();
}
