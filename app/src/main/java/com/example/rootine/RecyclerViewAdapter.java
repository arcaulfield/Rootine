package com.example.rootine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<Integer> mImages = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> imageNames, ArrayList<Integer> images, Context context){
        mImageNames = imageNames;
        mImages = images;
        mContext = context;
    }

    //responsible for inflating the view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == R.layout.layout_listitem){
            Log.d(TAG, "LISTITEMVIEW-CALLED");
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progress, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        if(position == mImageNames.size()){
            //initialize slider and stuff
        }
        else{
            holder.imageName.setText(mImageNames.get(position));
            holder.image.setImageResource(mImages.get(position));
            holder.parentLayout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mImageNames.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "GET-ITEM-VIEW-CALLED");
        return (position == mImageNames.size()) ? R.layout.layout_progress : R.layout.layout_listitem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView imageName;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.imageName);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            //Add slider and stuff to the viewholder

        }
    }
}
