package com.tower.socialnetwork.fragments;


public interface SearchResults{
    void displayViewPostFragment(String action, boolean add, String data);
    void followUser(String user, boolean follow);
    void showProgress(boolean isVisible);
    void closeSearchView();
}
