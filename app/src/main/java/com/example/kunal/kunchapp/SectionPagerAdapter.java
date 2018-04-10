package com.example.kunal.kunchapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by kunal on 07/01/18.
 */

class SectionPagerAdapter extends FragmentPagerAdapter
{

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                requestfragment requestfragments = new requestfragment();
                return requestfragments;
            case 1:
                ChatsFragment chatssfragment = new ChatsFragment();
                return chatssfragment;
            case 2:
                FriendFragment friendsfragments = new FriendFragment();
                return friendsfragments;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public  CharSequence getPageTitle (int position)
    {
        switch (position)
        {
            case 0:
                return "REQUESTS";
            case 1:
                return "CHATS";
            case 2:
                return "FRIENDS";
            default:
                return null;
        }

    }
}
