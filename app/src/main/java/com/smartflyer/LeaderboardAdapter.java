package com.smartflyer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.myViewHolder> {
    //private List<String> list;
    private ArrayList<user> user;
    private Context mContext;

    public LeaderboardAdapter(Context context, ArrayList<user> userData){
        //this.list = list;
        this.user = userData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        TextView textView = (TextView) LayoutInflater.from(mContext).
                inflate(R.layout.leaderboard_list, parent, false);
        myViewHolder mvh = new myViewHolder(textView);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.myViewHolder viewHolder, int position) {
        //viewHolder.user .setText(list.get(i));
        user currentUser = user.get(position);
        viewHolder.bindTo(currentUser);

    }


    @Override
    public int getItemCount() {
        return user.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView user;
        TextView count;
        //private TextView name;

        public myViewHolder(TextView itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.leaderboard_data);
            //count = itemView.findViewById(R.id.leaderboard_count);
        }

        void bindTo(user currentUser){
            // Populate the textviews with data.
            //Log.w("Adapter", " Response: " +currentUser.getName());
            //Log.w("Adapter", " Response: " +currentUser.get_id());
            user.setText(currentUser.get_id()+ " gave waittimes " + currentUser.getLeaderboard_count()+ " times!! ");
            //count.setText(currentUser.getLeaderboard_count().toString());
        }
    }
}
