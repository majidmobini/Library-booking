package com.ict.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ict.classes.BookClass;
import com.ict.classes.Set_font;
import com.ict.librarybooking.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookAdapter extends  RecyclerView.Adapter<BookAdapter.ViewHolder> {

    ArrayList<BookClass> books = new ArrayList<>();
    public static int lastPosition = -1;
    private Context mContext;

    private OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
        //listener.onItemClick(itemView, position);
    }
    public BookAdapter( ArrayList<BookClass> books,Context ctx) {
        this.books = books;
        mContext = ctx;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView name;
        public TextView year;
        public TextView writer;
        public TextView publisher;
        public TextView count;
        public View row;
        public ImageView more;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            year = itemView.findViewById(R.id.tvYear);
            writer = itemView.findViewById(R.id.tvWiter);
            publisher = itemView.findViewById(R.id.tvPublisher);
            count = itemView.findViewById(R.id.tvCount);
            more = itemView.findViewById(R.id.imMore);
            this.row = itemView;
            // nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            //   messageButton = (Button) itemView.findViewById(R.id.message_button);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.book_row, parent, false);
        Set_font.setKodakFont(mContext,contactView);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookClass bk = books.get(position);
        holder.name.setText(bk.name);
        holder.year.setText(bk.year+"");
        holder.writer.setText(bk.writer);
        holder.publisher.setText(bk.publisher);
        holder.count.setText(bk.count+"");
        holder.more.setTag(position);
        holder.more.setOnClickListener(view -> {
            if(listener != null)
                listener.onItemClick(view,(int)view.getTag());
        });
        setAnimation(holder.row,position);

    }

    @Override
    public int getItemCount() {
        return books.size();
    }


    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}