package edu.cuhk.csci3310.basketball_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.cuhk.csci3310.basketball_app.models.SimpleCourtEvent;

public class CourtEventAdapter extends RecyclerView.Adapter<CourtEventAdapter.CourtEventViewHolder> {
    private List<SimpleCourtEvent> events;

    public CourtEventAdapter(List<SimpleCourtEvent> events) {
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
        holder.setSimpleCourtEvent(this.events.get(position));
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

        public CourtEventViewHolder(@NonNull View view) {
            super(view);

            this.titleView = view.findViewById(R.id.value_title);
            this.timeView = view.findViewById(R.id.value_time);
        }

        private void setSimpleCourtEvent(SimpleCourtEvent event) {
            this.titleView.setText(event.getTitle());
            this.timeView.setText(event.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
    }
}
