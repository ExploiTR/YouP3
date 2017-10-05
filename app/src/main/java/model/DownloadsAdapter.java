package model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import app.exploitr.nsg.youp3.R;

/*
 * Created by exploitr on 28-09-2017.
 */

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {

    private List<String> mData = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public DownloadsAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_downloads_list_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] x = mData.get(position).split(",");

        holder.idTextView.setText(x[0]);
        holder.nameTextView.setText(x[1]);
        holder.pathTextView.setText(x[2]);
        holder.sizeTextView.setText(x[3]);

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView idTextView, nameTextView, pathTextView, sizeTextView;
        ImageButton cancelButton, openButton;

        ViewHolder(View itemView) {
            super(itemView);

            idTextView = (TextView) itemView.findViewById(R.id.file_id);
            nameTextView = (TextView) itemView.findViewById(R.id.file_downloads_name);
            pathTextView = (TextView) itemView.findViewById(R.id.file_downloads_path);
            sizeTextView = (TextView) itemView.findViewById(R.id.file_size);

            cancelButton = (ImageButton) itemView.findViewById(R.id.cancel_download);
            openButton = (ImageButton) itemView.findViewById(R.id.open_file);

            cancelButton.setOnClickListener(this);
            openButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                if (view.getId() == cancelButton.getId()) {

                    mClickListener.onCancelClick(
                            view,
                            Integer.parseInt(idTextView.getText().toString()),
                            nameTextView.getText().toString(),
                            pathTextView.getText().toString()
                    );

                } else if (view.getId() == openButton.getId()) {

                    mClickListener.onOpenClick(
                            view,
                            Integer.parseInt(idTextView.getText().toString()),
                            pathTextView.getText().toString()
                    );
                }
            }
        }
    }


    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {

        void onCancelClick(View view, int id, String name, String path);

        void onOpenClick(View view, int id, String path);
    }
}
