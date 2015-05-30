package com.haard.armaniroms.home;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.haard.armaniroms.MainActivity;
import com.haard.armaniroms.R;


public class HomeFragment extends Fragment {
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView=(RecyclerView)rootView.findViewById(R.id.rec);
        RecyclerView.LayoutManager llm=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        final HomeAdapter mAdapter=new HomeAdapter(getActivity(),MainActivity.mTitle,MainActivity.mLink);
        recyclerView.setAdapter(mAdapter);
        SwipeableRecyclerViewTouchListener swipeableRecyclerViewTouchListener=new SwipeableRecyclerViewTouchListener(recyclerView,new SwipeableRecyclerViewTouchListener.SwipeListener(){
            @Override
            public boolean canSwipe(int position){
                return true;
            }
            @Override
            public void onDismissedBySwipeLeft(RecyclerView recyclerView1,int[] reverseSortedPositions){
                for (int position:reverseSortedPositions){
                    MainActivity.mTitle.remove(MainActivity.mTitle.size()-position-1);
                    MainActivity.mLink.remove(MainActivity.mLink.size()-position-1);
                    mAdapter.notifyItemRemoved(MainActivity.mLink.size()-position-1);
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onDismissedBySwipeRight(RecyclerView recyclerView1,int[] reverseSortedPositions){
                for (int position:reverseSortedPositions){
                    MainActivity.mTitle.remove(MainActivity.mTitle.size()-position-1);
                    MainActivity.mLink.remove(MainActivity.mLink.size()-position-1);
                    mAdapter.notifyItemRemoved(MainActivity.mLink.size()-position-1);
                }
                mAdapter.notifyDataSetChanged();
            }

        });
        recyclerView.addOnItemTouchListener(swipeableRecyclerViewTouchListener);
        recyclerView.setHasFixedSize(false);

        return rootView;
    }

}
