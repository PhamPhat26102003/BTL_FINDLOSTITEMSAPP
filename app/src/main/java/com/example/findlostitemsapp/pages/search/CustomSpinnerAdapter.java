package com.example.findlostitemsapp.pages.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.findlostitemsapp.R;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> items;
    private final Spinner spinner;

    public CustomSpinnerAdapter(Context context, List<String> items, Spinner spinner) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        this.spinner = spinner;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Nếu là dòng đầu tiên (mặc định), không hiển thị nút X
        if (position == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.spinner_item_simple, parent, false);
            TextView textItem = view.findViewById(R.id.textItem);
            textItem.setText(items.get(position));
            return view;
        } else {
            // Nếu đã chọn, hiển thị với nút X
            View view = LayoutInflater.from(context).inflate(R.layout.spinner_item_selected, parent, false);
            TextView textItem = view.findViewById(R.id.textItem);
            ImageButton btnClear = view.findViewById(R.id.btnClear);
            textItem.setText(items.get(position));
            btnClear.setOnClickListener(v -> spinner.setSelection(0));
            return view;
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_item_simple, parent, false);
        TextView textItem = view.findViewById(R.id.textItem);
        textItem.setText(items.get(position));
        return view;
    }
}

