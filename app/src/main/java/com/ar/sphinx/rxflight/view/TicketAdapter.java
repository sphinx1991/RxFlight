package com.ar.sphinx.rxflight.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ar.sphinx.rxflight.R;
import com.ar.sphinx.rxflight.network.model.Ticket;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.MyViewHolder> {

	private Context context;
	private TicketAdapterListener listener;
	private List<Ticket> ticketList;

	public TicketAdapter(Context context,List<Ticket> list,TicketAdapterListener listener) {
		this.context = context;
		this.ticketList = list;
		this.listener = listener;
	}

	class MyViewHolder extends RecyclerView.ViewHolder{

		@BindView(R.id.tv_airline_name)
		TextView airlineName;

		@BindView(R.id.logo)
		ImageView logo;

		@BindView(R.id.tv_num_stops)
		TextView stops;

		@BindView(R.id.tv_seats)
		TextView seats;

		@BindView(R.id.tv_departure)
		TextView deaprture;

		@BindView(R.id.tv_arrival)
		TextView arrival;

		@BindView(R.id.tv_time)
		TextView duration;

		@BindView(R.id.price)
		TextView price;

		@BindView(R.id.loader)
		SpinKitView loader;

		public MyViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this,itemView);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					listener.onTickSelection(ticketList.get(getAdapterPosition()));
				}
			});

		}
	}

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.layout_ticket,parent,false);
		return new MyViewHolder(view);
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		final Ticket ticket = ticketList.get(position);

		Glide.with(context)
				.load(ticket.getAirline().getLogo())
				.apply(RequestOptions.circleCropTransform())
				.into(holder.logo);

		holder.airlineName.setText(ticket.getAirline().getName());

		holder.deaprture.setText(String.format("%s Dep", ticket.getDeparture()));
		holder.arrival.setText(String.format("%s Dest", ticket.getArrival()));

		holder.duration.setText(ticket.getFlightNumber());
		holder.duration.append(", " + ticket.getDuration());
		holder.stops.setText(String.format("%d Stops", ticket.getNumberOfStops()));

		if (!TextUtils.isEmpty(ticket.getInstructions())) {
			holder.duration.append(", " + ticket.getInstructions());
		}

		if (ticket.getPrice() != null) {
			holder.price.setText(String.format("₹%s", String.format("%.0f", ticket.getPrice().getPrice())));
			holder.seats.setText(String.format("%s Seats", ticket.getPrice().getSeats()));
			holder.loader.setVisibility(View.INVISIBLE);
		} else {
			holder.loader.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public int getItemCount() {
		return ticketList.size();
	}

	public interface TicketAdapterListener {
		void onTickSelection(Ticket ticket);
	}
}
