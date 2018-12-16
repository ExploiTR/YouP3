package downloads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.downloader.PRDownloader;
import com.downloader.Status;

import java.util.List;

import app.exploitr.nsg.youp3.R;

/*
 * Created by exploitr on 28-09-2017.
 */

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;

    public DownloadsAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    public void notifyDataSetChangedCustom(List<String> newdata) {
        mData.clear();
        mData.addAll(newdata);
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
        final String[] x = mData.get(position).split(",");
        holder.idTextView.setText(x[0]);
        holder.nameTextView.setText(x[1]);
        holder.pathTextView.setText(x[2]);
        holder.sizeTextView.setText(x[3]);
        holder.urlTextView.setText(x[6]);

        holder.itemView.post(new Runnable() {
            @Override
            public void run() {
                holder.progressBar.setMax(100);
                holder.progressBar.setProgress(0);
                holder.progressBar.setProgress(Integer.parseInt(x[4]));
            }
        });

        if (new RealmController().getVideoInfo(Integer.parseInt(holder.idTextView.getText().toString())) != null) {
            if (new RealmController().getVideoInfo(Integer.parseInt(holder.idTextView.getText().toString())).isCompleted()) {
                holder.openButton.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.GONE);
            } else {
                holder.openButton.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
            }
        }

        if (PRDownloader.getStatus(Integer.parseInt(x[0])) == Status.PAUSED) {
            holder.start_pause.setImageResource(R.drawable.ic_start);
        } else if (PRDownloader.getStatus(Integer.parseInt(x[0])) == Status.RUNNING) {
            holder.start_pause.setImageResource(R.drawable.ic_pause);
        } else {
            holder.start_pause.setVisibility(View.GONE);
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

        void onStartPauseClick(View view, int id, String name);

        void onCancelClick(int id, int position, String name, String path);
    }

    public interface ItemLongClickListener {
        void onOpenClick();

        void onStartPauseClick();

        void onReDownloadClick();

        void onCancelClick();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView idTextView, nameTextView, pathTextView, sizeTextView, urlTextView;
        ImageButton cancelButton, openButton, start_pause, reDownload;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.file_id);
            nameTextView = itemView.findViewById(R.id.file_downloads_name);
            pathTextView = itemView.findViewById(R.id.file_downloads_path);
            sizeTextView = itemView.findViewById(R.id.file_size);
            urlTextView = itemView.findViewById(R.id.file_downloads_url);

            cancelButton = itemView.findViewById(R.id.cancel_download);
            openButton = itemView.findViewById(R.id.open_file);
            start_pause = itemView.findViewById(R.id.start_pause);
            reDownload = itemView.findViewById(R.id.re_download);

            progressBar = itemView.findViewById(R.id.progress);

            openButton.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
            start_pause.setOnClickListener(this);
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
                            Integer.parseInt(idTextView.getText().toString()),
                            this.getLayoutPosition(),
                            nameTextView.getText().toString(),
                            pathTextView.getText().toString()
                    );
                } else if (view.getId() == openButton.getId()) {
                    mClickListener.onOpenClick(
                            Integer.parseInt(idTextView.getText().toString()),
                            this.getLayoutPosition(),
                            pathTextView.getText().toString()
                    );
                } else if (view.getId() == start_pause.getId()) {
                    mClickListener.onStartPauseClick(view,
                            Integer.parseInt(idTextView.getText().toString()),
                            nameTextView.getText().toString());
                } else if (view.getId() == reDownload.getId()) {
                    mClickListener.onReDownloadClick(
                            Integer.parseInt(idTextView.getText().toString()),
                            urlTextView.getText().toString());
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
                } else if (view.getId() == start_pause.getId()) {
                    mLongClickListener.onStartPauseClick();
                } else if (view.getId() == reDownload.getId()) {
                    mLongClickListener.onReDownloadClick();
                }
            }
            return true; //done activate
        }
    }

}
