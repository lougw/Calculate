
package com.lougw.calculate.utils.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;


public abstract class BaseRecyclerViewHolder<T> extends RecyclerView.ViewHolder {
    protected T mData;
    protected OnItemClickListener mItemClickListener;

    public BaseRecyclerViewHolder(View view) {
        super(view);
    }

    public void bindData(T data, int position) {
        mData = data;
        onDataBinding(position);
    }

    public T getData() {
        return mData;
    }

    public abstract void onDataBinding(int position);

    public abstract void recycle();

    public void recycleImageView(ImageView view) {
        if (null != view) {
            view.setImageDrawable(null);
        }
    }

    public void setItemListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public int hashCode() {
        if (null != mData) {
            return mData.hashCode();
        }
        return super.hashCode();
    }
}
