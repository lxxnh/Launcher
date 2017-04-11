package com.android.launcher3;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserHandleCompat;
import com.android.launcher3.compat.UserManagerCompat;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhongwenguang on 2/10/17.
 */

public class PrivateFolderActivity extends Activity {

    private RecyclerView mPrivateList;
    private List<AppItem> mList;
    private BaseAppAdapter mAdapter;
    private AppItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_list);
        mPrivateList = (RecyclerView) findViewById(R.id.private_list);
        mPrivateList.setLayoutManager(new GridLayoutManager(this,4));
        mList = new ArrayList<AppItem>();
        initData();
        mAdapter = new BaseAppAdapter(mList,this);
        mPrivateList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v) {
                Object tag = v.getTag();
                String pkgName = ((AppItem)tag).getPkgName();
                String className = ((AppItem)tag).getClassName();
                Intent intent = new Intent();
                intent.setClassName(pkgName,className);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        mAdapter.setmOnItemLongClickListener(new OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(final View v, final Context context) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.delete_target_label);
                builder.setMessage(R.string.remove_from_private);
                builder.setPositiveButton(R.string.private_confirm_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Object tag = v.getTag();
                        String pkgName = ((AppItem)tag).getPkgName();
                        //TODO remove the app from current private folder
                        LauncherAppState.getInstance().getIconCache().removePrivateApp(pkgName);
                        mList.remove((AppItem) tag);
                        mAdapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
                        //Remove掉mBgAllAppsList.privated中的AppInfo
                        LauncherAppState.getInstance().getModel().mBgAllAppsList.removePrivatedPackage(((AppItem)tag).getPkgName(), UserHandleCompat.myUserHandle());
                        //AllAppList中add从private folder中remove的AppInfo
                        LauncherAppState.getInstance().getModel().getCallback().bindAppsAdd(LauncherAppState.getInstance().getModel().mBgAllAppsList.privatedRemoved);

                    }
                });
                builder.setNegativeButton(R.string.private_confirm_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }


    public void initData() {
        Cursor cr = LauncherAppState.getInstance().getIconCache().queryInfoFromDB();
        try {
            while(cr.moveToNext()) {
                String name = cr.getString(0);
                byte[] icon = cr.getBlob(1);
                Bitmap bitmap = BitmapFactory.decodeByteArray(icon,0,icon.length);
                String pkgName = cr.getString(2);
                String className = cr.getString(3);
                mItem = new AppItem(name,bitmap,pkgName,className);
                mList.add(mItem);
            }
        } finally {
            cr.close();
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View v);
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View v, Context context);
    }


    public class BaseAppAdapter extends RecyclerView.Adapter<BaseAppAdapter.AppViewHolder> {
        private List<AppItem> list;
        private OnRecyclerViewItemClickListener mOnItemClickListener;
        private OnRecyclerViewItemLongClickListener mOnItemLongClickListener;
        private Context mContext;

        public BaseAppAdapter(List<AppItem> list, Context context) {
            this.list = list;
            this.mContext = context;
        }

        public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
            this.mOnItemClickListener = listener;

        }

        public void setmOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
            this.mOnItemLongClickListener = listener;

        }

        @Override
        public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View appView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.private_app_item, parent, false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            appView.setLayoutParams(params);
            appView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(appView);
                    }
                }
            });
            appView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mOnItemLongClickListener != null) {
                        mOnItemLongClickListener.onItemLongClick(appView,mContext);
                    }
                    return true;
                }
            });
            return new AppViewHolder(appView);
        }

        @Override
        public void onBindViewHolder(AppViewHolder viewHolder, int position) {
            AppViewHolder holder = (AppViewHolder) viewHolder;
            AppItem item = list.get(position);
            holder.mAppName.setText(item.getName());
            holder.mAppIcon.setImageBitmap(item.getIcon());
            holder.mAppRoot.setTag(item);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class AppViewHolder extends RecyclerView.ViewHolder{
            public LinearLayout mAppRoot;
            public ImageView mAppIcon;
            public TextView mAppName;

            public AppViewHolder(View itemView) {
                super(itemView);
                mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
                mAppName = (TextView) itemView.findViewById(R.id.app_name);
                mAppRoot = (LinearLayout) itemView.findViewById(R.id.app_item);
            }
        }
    }

    class AppItem  extends ItemInfo{
        private String name;
        private Bitmap icon;
        private String pkgName;
        private String className;

        public AppItem(String name, Bitmap icon, String pkgName, String className) {
            this.name = name;
            this.icon = icon;
            this.pkgName = pkgName;
            this.className = className;
        }

        public String getName() {
            return name;
        }

        public Bitmap getIcon() {
            return icon;
        }

        public String getPkgName() {
            return pkgName;
        }

        public String getClassName() {
            return className;
        }
    }


}
