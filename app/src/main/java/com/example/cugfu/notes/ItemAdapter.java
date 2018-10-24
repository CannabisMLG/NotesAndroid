package com.example.cugfu.notes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
        if(item.getType().equals("Фильм")){
            if(item.isCh().equals("1"))((TextView) convertView.findViewById(R.id.textView7)).setText("Моя оценка: " + item.getMyRate());
            else ((TextView) convertView.findViewById(R.id.textView7)).setText("Оценка на кинопоиске: " + item.getKpRate());
        }
        if(item.getType().equals("Книга")){
            if(item.isCh().equals("1"))((TextView) convertView.findViewById(R.id.textView7)).setText("Моя оценка: " + item.getMyRate());
            else ((TextView) convertView.findViewById(R.id.textView7)).setText("");
        }
        if(item.getType().equals("Игра")){
            if(item.isCh().equals("1"))((TextView) convertView.findViewById(R.id.textView7)).setText("Моя оценка: " + item.getMyRate());
            else ((TextView) convertView.findViewById(R.id.textView7)).setText("");
        }
        return convertView;
    }
}
