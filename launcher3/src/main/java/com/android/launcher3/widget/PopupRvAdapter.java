package com.android.launcher3.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.launcher3.AppInfo;
import com.android.launcher3.AudioAppInfo;
import com.android.launcher3.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by qizhi on 17-7-5.
 */

public class PopupRvAdapter extends RecyclerView.Adapter<PopupRvAdapter.ViewHolder> {

    private Context mContext;
    private List<AudioAppInfo> mLists;
    private MyItemClickListener mItemClickListener;

    public PopupRvAdapter(Context context, List<AudioAppInfo> lists) {
        this.mContext = context;
        this.mLists = lists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext,R.layout.popup_rv_item,null);
        ViewHolder viewHolder = new ViewHolder(view,mItemClickListener);

        viewHolder.iv = (ImageView) view.findViewById(R.id.rv_item_iv);
        viewHolder.tv = (TextView) view.findViewById(R.id.rv_item_tv);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.iv.setImageDrawable(mLists.get(position).getDrawable());
        holder.tv.setText(mLists.get(position).getAppName());
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private MyItemClickListener mListener;

        public ViewHolder(View itemView , MyItemClickListener listener) {
            super(itemView);
            this.mListener = listener;
            itemView.setOnClickListener(this);
        }
        ImageView iv;
        TextView tv;

        @Override
        public void onClick(View view) {
            if (mListener != null){
                mListener.onItemClick(view,getPosition());
            }
        }
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setItemClickListener(MyItemClickListener myItemClickListener) {
        this.mItemClickListener = myItemClickListener;
    }
}
