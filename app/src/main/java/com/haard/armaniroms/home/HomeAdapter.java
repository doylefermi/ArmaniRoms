package com.haard.armaniroms.home;
/**
 * Created by harikishen on 5/30/15.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.haard.armaniroms.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harikishen on 5/29/15.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private static List<String> mTitles=new ArrayList<>();
    private static List<String> mLinks=new ArrayList<>();
    private static TextView title;
    private static TextView link;
    public static Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mLink;
        public ViewHolder(View v) {
            super(v);
            mTitle=title;
            mLink=link;
            v.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    openLink(mLinks.get(mLinks.size()-getPosition()-1));
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener(){
               @Override
               public boolean onLongClick(View view){
                   share(mLinks.get(mLinks.size()-getPosition()-1));
                   return true;
               }
            });
        }
    }

    public HomeAdapter(Context context,List<String> myTitles,List<String> myLink) {
        mTitles=myTitles;
        this.context=context;
        mLinks=myLink;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
        ViewHolder vh;
        title=(TextView)v.findViewById(R.id.cardtitle);
        link=(TextView)v.findViewById(R.id.link);
        vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTitle.setText(mTitles.get(mTitles.size()-position-1));
            holder.mLink.setText(mLinks.get(mLinks.size()-position-1));
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }
    public static void share(String link){
        Intent sendIntent=new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent,"Share link using..."));
    }
    public static void openLink(String url){
        if(!url.startsWith("http://") && !url.startsWith("https://"))
            url="http://"+url;
        Log.d("TESTBR",""+url);
       Intent browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }
}

