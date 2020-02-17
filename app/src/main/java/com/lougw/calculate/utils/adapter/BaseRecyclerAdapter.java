
package com.lougw.calculate.utils.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


import com.lougw.calculate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseRecyclerAdapter
 *
 * @param <T>
 * @param <VH>
 */

public abstract class BaseRecyclerAdapter<T, VH extends BaseRecyclerViewHolder> extends RecyclerView.Adapter<VH> implements
        IData<T>, View.OnClickListener {
    protected ArrayList<T> mData;
    private boolean isNormalItem = true;
    protected OnItemClickListener mItemClickListener;

    public BaseRecyclerAdapter() {
        mData = new ArrayList<T>();
    }

    public BaseRecyclerAdapter(OnItemClickListener onItemListener) {
        mData = new ArrayList<T>();
        mItemClickListener = onItemListener;
    }

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
        if (!isNormalItem) {
            holder.setItemListener(null);
        }
        holder.recycle();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {
        T data = mData.get(position);
        if (isNormalItem) {
            holder.itemView.setOnClickListener(this);
        } else {
            holder.setItemListener(mItemClickListener);
        }
        holder.itemView.setTag(R.id.base_adapter_position, position);
        onBindInvokingChild(holder, position);
        holder.bindData(data, position);
    }

    public void onBindInvokingChild(VH holder, int position) {

    }


    @Override
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public List<T> getData() {
        return mData;
    }

    @Override
    public T getItem(int position) {
        if (position >= 0 && getItemCount() > 0 && position < getItemCount()) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public void add(T item) {
        mData.add(item);
        int index = mData.size() - 1 >= 0 ? mData.size() - 1 : 0;
        notifyItemInserted(index);
    }

    @Override
    public void add(T item, int index) {
        if (index < mData.size()) {
            mData.add(index, item);
            notifyItemInserted(index);
        } else {
            mData.add(item);
            index = mData.size() - 1 >= 0 ? mData.size() - 1 : 0;
            notifyItemInserted(index);
        }

    }

    @Override
    public void addAll(List<T> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        mData.addAll(list);
        notifyItemRangeInserted(mData.size() - list.size(), list.size());
    }

    @Override
    public void addAll(List<T> list, int index) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (index < mData.size()) {
            mData.addAll(index, list);
            notifyItemRangeInserted(index, mData.size());
        } else {
            mData.addAll(list);
            notifyItemRangeInserted(mData.size() - list.size(), list.size());
        }

    }


    @Override
    public void set(T oldItem, T newItem) {
        set(mData.indexOf(oldItem), newItem);
    }

    @Override
    public void set(int index, T item) {
        if (index < mData.size()) {
            mData.set(index, item);
            notifyItemChanged(index);
        } else {
            mData.add(item);
            index = mData.size() - 1 >= 0 ? mData.size() - 1 : 0;
            notifyItemChanged(index);
        }
    }

    @Override
    public void remove(T item) {
        final int index = mData.indexOf(item);
        remove(index);
    }

    @Override
    public void remove(int index) {
        if (index >= mData.size()) {
            return;
        }
        mData.remove(index);
        notifyItemRemoved(index);
    }


    @Override
    public void removeAll(List<T> list) {
        mData.removeAll(list);
        notifyItemRangeChanged(0, mData.size());
    }

    @Override
    public void setData(List<T> list) {
        int size = mData.size();
        if (size > 0) {
            mData.clear();
            notifyItemRangeRemoved(0, size);
        }
        mData.addAll(list);
        notifyItemRangeInserted(0, mData.size());
    }


    @Override
    public void replaceAll(List<T> oldList, List<T> newList, int index) {
        int size = mData.size();
        if (size > 0) {
            removeAll(oldList);
        }
        if (newList == null || newList.size() == 0) {
            return;
        }
        if (index < mData.size()) {
            mData.addAll(index, newList);
            notifyItemRangeInserted(index, mData.size());
        } else {
            mData.addAll(newList);
            notifyItemRangeInserted(mData.size() - newList.size(), newList.size());
        }
    }

    @Override
    public boolean contains(T item) {
        return mData.contains(item);
    }

    @Override
    public void clear() {
        mItemClickListener = null;
        int size = mData.size();
        mData.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void setNormalItem(boolean normalItem) {
        isNormalItem = normalItem;
    }

    @Override
    public void onClick(View view) {
        if (mItemClickListener != null) {
            Object tag = view.getTag(R.id.base_adapter_position);
            if (tag instanceof Integer) {
                int position = (int) tag;
                mItemClickListener.onItemClick(view, position);
            }
        }
    }

    public void setItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

}
