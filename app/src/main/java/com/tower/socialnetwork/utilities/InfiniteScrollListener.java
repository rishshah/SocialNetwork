package com.tower.socialnetwork.utilities;


import android.util.Log;
import android.widget.AbsListView;

public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold = 2;
    private int currentPage = 0;
    private boolean loading = false;
    private boolean finished = false;

    public InfiniteScrollListener() {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (!finished && !loading && visibleThreshold >= firstVisibleItem ) {
            Log.e("ON SCROLL firstVisible",String.valueOf(firstVisibleItem));
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
