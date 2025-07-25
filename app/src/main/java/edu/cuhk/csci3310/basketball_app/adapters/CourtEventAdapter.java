package edu.cuhk.csci3310.basketball_app.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.cuhk.csci3310.basketball_app.CourtEventActivity;
import edu.cuhk.csci3310.basketball_app.R;
import edu.cuhk.csci3310.basketball_app.models.server.SimpleCourtEvent;

public class CourtEventAdapter extends RecyclerView.Adapter<CourtEventAdapter.CourtEventViewHolder> {
    private final double lat, lon;
    private List<SimpleCourtEvent> events;

    public CourtEventAdapter(double lat, double lon, List<SimpleCourtEvent> events) {
        this.lat = lat;
        this.lon = lon;
        this.events = events;
    }

    @NonNull
    @Override
    public CourtEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_court_event, parent, false);
        return new CourtEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourtEventViewHolder holder, int position) {
        holder.setSimpleCourtEvent(this.lat, this.lon, this.events.get(position));
    }

    @Override
    public int getItemCount() {
        return this.events.size();
    }

    public void updateData(List<SimpleCourtEvent> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    public static class CourtEventViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView, timeView;
        private double lat, lon;
        private int eventId;
        private boolean hasData;

        public CourtEventViewHolder(@NonNull View view) {
            super(view);

            this.titleView = view.findViewById(R.id.value_title);
            this.timeView = view.findViewById(R.id.value_time);

            this.titleView.setOnClickListener(this::onClick);
            this.timeView.setOnClickListener(this::onClick);
            view.findViewById(R.id.button_view).setOnClickListener(this::onClick);
        }

        private void setSimpleCourtEvent(double lat, double lon, SimpleCourtEvent event) {
            this.titleView.setText(event.getTitle());
            this.timeView.setText(event.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            this.lat = lat;
            this.lon = lon;
            this.eventId = event.getId();
            this.hasData = true;
        }

        private void onClick(View view) {
            if (!this.hasData) return;
            Intent intent = new Intent(view.getContext(), CourtEventActivity.class);
            intent.putExtra("lat", this.lat);
            intent.putExtra("lon", this.lon);
            intent.putExtra("eventId", this.eventId);
            intent.putExtra("title", this.titleView.getText());
            intent.putExtra("time", this.timeView.getText());
            view.getContext().startActivity(intent);
        }
    }
}
