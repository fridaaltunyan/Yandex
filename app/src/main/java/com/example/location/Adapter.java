package com.example.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.geometry.Point;

import java.util.List;
import java.util.Objects;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private final Context context;
    private final List<GeoObjectCollection.Item> itemList;
    private ForPointListener mCallback;

    public void setOnForPointListener(ForPointListener mCallback) {
        this.mCallback = mCallback;
    }


    public Adapter(Context context, List<GeoObjectCollection.Item> mapObjects) {
        this.context = context;
        this.itemList = mapObjects;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
      //  Toast.makeText(context, String.valueOf(itemList.size()), Toast.LENGTH_SHORT).show();
        String item = Objects.requireNonNull(itemList.get(position).getObj()).getName();
        holder.textView.setText(item);
        holder.itemView.setOnClickListener(v -> {
            Point resultLocation = Objects.requireNonNull(itemList.get(position).getObj()).getGeometry().get(0).getPoint();
          //  Log.e("mi ban", resultLocation.toString());
            mCallback.getPoint(resultLocation);


        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.address);

        }
    }

    interface ForPointListener {
        void getPoint(Point point);
    }

}

