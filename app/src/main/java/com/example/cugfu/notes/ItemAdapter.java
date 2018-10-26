package com.example.cugfu.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.LinkedList;

import models.Item;

public class ItemAdapter extends ArrayAdapter {
    public ItemAdapter(Context context, LinkedList<Item> items)
    {
        super(context, R.layout.list_item, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = (Item) getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
        }
        ((TextView) convertView.findViewById(R.id.it)).setText(item.getName());
        ((TextView) convertView.findViewById(R.id.textView7)).setText(item.getGenre());
        return convertView;
    }
}
