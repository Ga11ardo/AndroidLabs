package com.labs.lab3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.VH> {
    private List<Request> items;
    public RequestAdapter(List<Request> items) { this.items = items; }

    @Override public VH onCreateViewHolder(ViewGroup p, int v) {
        View v2 = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_request, p, false);
        return new VH(v2);
    }
    @Override public void onBindViewHolder(VH h, int i) {
        Request r = items.get(i);
        h.text.setText(r.text);
        h.text.setTextColor(r.color);
        h.datetime.setText(r.datetime);
    }
    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView text, datetime;
        public VH(View v) {
            super(v);
            text = v.findViewById(R.id.reqText);
            datetime = v.findViewById(R.id.reqDatetime);
        }
    }
}