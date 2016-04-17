package com.jasmartdev.drawingpaintandfill;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

public class DrawPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;

    public DrawPagerAdapter(FragmentManager fm, Context ct) {
        super(fm);
        context = ct;
    }

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    @Override
    public Fragment getItem(int position) {
        SlideFragment f = new SlideFragment();
        Bundle args = new Bundle();
        int id = getChildImages(MainActivity.s_cur_group).getResourceId(position, -1);
        args.putInt(SlideFragment.ARG_OBJECT, id);
        f.setArguments(args);
        return f;
    }

    @Override
    public int getCount() {
        return getChildImages(MainActivity.s_cur_group).length();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    public TypedArray getChildImages(int pos) {
        switch (pos) {
            case 0:
                return context.getResources().obtainTypedArray(R.array.num_array);
            case 1:
                return context.getResources().obtainTypedArray(R.array.char_array);
            default:
                return null;
        }
    }
}

