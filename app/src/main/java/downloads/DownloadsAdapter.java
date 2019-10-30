package downloads;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.text.DecimalFormat;
import java.util.List;

import app.exploitr.nsg.youp3.R;

/*
 * Created by exploitr on 28-09-2017.
 */

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {

    private List<VideoInfo> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;
    private static long SIZE_TOTAL = 1L;

    public DownloadsAdapter(Context context, List<VideoInfo> data) {
        this.mInflater = LayoutInflater.from(context);
        SIZE_TOTAL = Environment.getExternalStorageDirectory().getTotalSpace() / (1024 * 1024);
        this.mData = data;
    }

    public void notifyDataSetChangedCustom(List<VideoInfo> newData) {
        mData.clear();
        mData.addAll(newData);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_downloads_list_item, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final VideoInfo vInfo = mData.get(position);
        holder.nameTextView.setText(vInfo.getName().substring(0, 40) + ".....");
        holder.pathTextView.setText(vInfo.getPath());
        holder.sizeView.setProgress(Float.parseFloat(new DecimalFormat("##.##")
                .format((vInfo.getSize() / SIZE_TOTAL) * 100)));

        if (new RealmController().getVideoInfo(vInfo.getId()) != null) {
            if (new RealmController().getVideoInfo(vInfo.getId()).isCompleted()) {
                holder.openButton.setVisibility(View.VISIBLE);
            } else {
                holder.openButton.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void setLongClickListener(ItemLongClickListener itemClickListener) {
        this.mLongClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onOpenClick(int id, int position, String path);

        void onReDownloadClick(int id, String ytUrl);

        void onCancelClick(int id, int position, String name, String path);
    }

    public interface ItemLongClickListener {
        void onOpenClick();

        void onReDownloadClick();

        void onCancelClick();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView nameTextView, pathTextView;
        DonutProgress sizeView;
        ImageButton cancelButton, openButton, reDownload;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.file_downloads_name);
            pathTextView = itemView.findViewById(R.id.file_downloads_path);
            sizeView = itemView.findViewById(R.id.file_size);

            cancelButton = itemView.findViewById(R.id.cancel_download);
            openButton = itemView.findViewById(R.id.open_file);
            reDownload = itemView.findViewById(R.id.re_download);

            openButton.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
            reDownload.setOnClickListener(this);

            openButton.setOnLongClickListener(this);
            cancelButton.setOnLongClickListener(this);
            reDownload.setOnLongClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                if (view.getId() == cancelButton.getId()) {
                    mClickListener.onCancelClick(
                            mData.get(this.getLayoutPosition()).getId(),
                            this.getLayoutPosition(),
                            mData.get(this.getLayoutPosition()).getName(),
                            mData.get(this.getLayoutPosition()).getPath()
                    );
                } else if (view.getId() == openButton.getId()) {
                    mClickListener.onOpenClick(
                            mData.get(this.getLayoutPosition()).getId(),
                            this.getLayoutPosition(),
                            mData.get(this.getLayoutPosition()).getPath()
                    );
                } else if (view.getId() == reDownload.getId()) {
                    mClickListener.onReDownloadClick(
                            mData.get(this.getLayoutPosition()).getId(),
                            mData.get(this.getLayoutPosition()).getYtUrl());
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mLongClickListener != null) {
                if (view.getId() == cancelButton.getId()) {
                    mLongClickListener.onCancelClick();
                } else if (view.getId() == openButton.getId()) {
                    mLongClickListener.onOpenClick();
                } else if (view.getId() == reDownload.getId()) {
                    mLongClickListener.onReDownloadClick();
                }
            }
            return true; //done activate
        }
    }

}
