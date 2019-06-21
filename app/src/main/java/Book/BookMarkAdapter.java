package Book;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import app.exploitr.nsg.youp3.R;



/*
 * Created by exploitr on 28-09-2017.
 */

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;

    public BookMarkAdapter(Context context, List<String> data) {
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
        View view = mInflater.inflate(R.layout.row_bookmarks_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] x = mData.get(position).split(",");
        holder.titleTextView.setText(x[1]);
        holder.urlTextView.setText(x[2]);
        holder.id = Integer.parseInt(x[0]);

        byte[] decodedString = Base64.decode(x[3], Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        holder.favicon.setImageBitmap(decodedByte);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void setLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mLongClickListener = itemLongClickListener;
    }

    public interface ItemClickListener {
        void onOpenClick(String url);
    }

    public interface ItemLongClickListener {
        void onDeleteClick(int id);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView urlTextView, titleTextView;
        CircularImageView favicon;
        LinearLayout clicker;
        int id;

        ViewHolder(View itemView) {
            super(itemView);
            id = 0;
            urlTextView = itemView.findViewById(R.id.urlView);
            titleTextView = itemView.findViewById(R.id.titleView);
            favicon = itemView.findViewById(R.id.favicon);
            clicker = itemView.findViewById(R.id.bookmarkClickable);

            clicker.setOnClickListener(this);
            clicker.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                if (view.getId() == clicker.getId()) {
                    mClickListener.onOpenClick(urlTextView.getText().toString());
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mLongClickListener != null) {
                if (view.getId() == clicker.getId()) {
                    mLongClickListener.onDeleteClick(id);
                }
            }
            return true;
        }
    }

}
