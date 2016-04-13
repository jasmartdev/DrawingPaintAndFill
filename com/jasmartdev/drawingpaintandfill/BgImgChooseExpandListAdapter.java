package com.jasmartdev.drawingpaintandfill;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BgImgChooseExpandListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Group> groups;
    private Dialog dialog;
    private DrawingView drawview;

    public BgImgChooseExpandListAdapter(Context context, ArrayList<Group> groups, Dialog dialog, DrawingView drawview) {
        this.context = context;
        this.groups = groups;
        this.dialog = dialog;
        this.drawview = drawview;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<Child> chList = groups.get(groupPosition).getItems();
        return chList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Child child = (Child) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.bgimg_chooser_child, null);
        }
        ImageView iv = (ImageView) convertView.findViewById(R.id.bgimg);
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 4;
        option.inDither = false;
        option.inPurgeable = true;
        iv.setImageBitmap(BitmapFactory.decodeResource(
                context.getResources(), child.getImage(), option));
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        final int img = child.getImage();
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawview.startNew();
                drawview.drawImg(img);
                dialog.dismiss();
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<Child> chList = groups.get(groupPosition).getItems();
        return chList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Group group = (Group) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.bgimg_chooser_group, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.group_bgimg);
        tv.setText(group.getName());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

class Group {

    private String Name;
    private ArrayList<Child> Items;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public ArrayList<Child> getItems() {
        return Items;
    }

    public void setItems(ArrayList<Child> Items) {
        this.Items = Items;
    }

}

class Child {

    private int Image;

    public int getImage() {
        return Image;
    }

    public void setImage(int Image) {
        Log.d("Hoang", "setImage Image:" + Image);
        this.Image = Image;
    }
}