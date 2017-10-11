package com.tower.socialnetwork.utilities;


import android.util.Log;
import android.widget.AbsListView;

/**
 * Created by bharat on 10/10/17.
 */

public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold = 2;
    private int currentPage = 0;
//    private int previousTotal = 0;
    private boolean loading = false;
    private boolean finished = false;

    public InfiniteScrollListener() {
    }
    public InfiniteScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (!finished && !loading && visibleThreshold >= firstVisibleItem ) {
            // I load the next page of gigs using a background task,
            // but you can call any function here.
            Log.e("ONSCROLL firstVisibl",String.valueOf(firstVisibleItem));
            getMorePosts(currentPage + 1);
            loading = true;
        }
    }

    public abstract void getMorePosts(int i);

    public void completed(){
        loading = false;
        currentPage++;
    }

    public void allPostsDone(){
        finished = true;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

}
