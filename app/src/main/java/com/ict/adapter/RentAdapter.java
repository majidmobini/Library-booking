package com.ict.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ict.classes.RentClass;
import com.ict.classes.Set_font;
import com.ict.librarybooking.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RentAdapter extends  RecyclerView.Adapter<RentAdapter.ViewHolder> {

    ArrayList<RentClass> rents = new ArrayList<>();
    public static int lastPosition = -1;
    private Context mContext;

    private OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(int row,View view);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
        //listener.onItemClick(itemView, position);
    }
    public RentAdapter(ArrayList<RentClass> rents, Context ctx) {
        this.rents = rents;
        mContext = ctx;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView bookName;
        public TextView memberName;
        public TextView date;
        public ImageView more;
        public View row;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            bookName = itemView.findViewById(R.id.tvRentBookName);
            memberName = itemView.findViewById(R.id.tvRentMemberName);
            date = itemView.findViewById(R.id.tvRentDate);
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
        View contactView = inflater.inflate(R.layout.rent_row, parent, false);
        Set_font.setKodakFont(mContext,contactView);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RentClass rnt = rents.get(position);
        holder.memberName.setText(rnt.memberClass.name);
        holder.bookName.setText(rnt.bookClass.name);

        if (rnt.isReturned == 1)
        {
            holder.date.setText(rnt.dateText + "-" + rnt.dateTextReturn);
            holder.more.setVisibility(View.GONE);
        }
        else
        {
            holder.more.setVisibility(View.VISIBLE);
            holder.date.setText(rnt.dateText);
            holder.more.setTag(position);
            holder.more.setOnClickListener(view -> {
                        listener.onItemClick(position,view);
                    }
            );
        }

        setAnimation(holder.row,position);

    }

    @Override
    public int getItemCount() {
        return rents.size();
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