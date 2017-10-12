package com.tower.socialnetwork.utilities;


import android.util.Log;
import android.widget.AbsListView;

public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
    private int limit = 10;
    private boolean start = false;
    private int visibleThreshold = 2;
    private int offset = 0;
    private boolean loading = false;
    private boolean finished = false;

    public InfiniteScrollListener() {
    }
    public InfiniteScrollListener(int limit, boolean start) {
        this.limit = limit;
        this.start = start;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (!finished && !loading && visibleThreshold >= firstVisibleItem ) {
            Log.e("ON SCROLL firstVisible",String.valueOf(firstVisibleItem));
            if(start){
                start = false;
                getMorePosts(-1);
                Log.e(" INIT OFFSET asked", "-1");
            } else {
                getMorePosts(offset);
                Log.e(" OFFSET TO ASK FOR", String.valueOf(offset));
            }
            loading = true;
        }
    }

    public abstract void getMorePosts(int i);

    public void completed(int offset){
        loading = false;
        this.offset = offset;
    }

    public void allPostsDone(){
        finished = true;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}
