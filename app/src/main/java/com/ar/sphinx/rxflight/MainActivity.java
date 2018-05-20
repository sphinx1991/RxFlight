package com.ar.sphinx.rxflight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ar.sphinx.rxflight.network.ApiClient;
import com.ar.sphinx.rxflight.network.ApiService;
import com.ar.sphinx.rxflight.network.model.Price;
import com.ar.sphinx.rxflight.network.model.Ticket;
import com.ar.sphinx.rxflight.view.TicketAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements TicketAdapter.TicketAdapterListener{

	private static final String TAG = "MainActivity";
	private static final String from = "DEL";
	private static final String to = "HYD";
	private CompositeDisposable compositeDisposable;
	private Unbinder unbinder;

	private ApiService apiService;
	private List<Ticket> ticketList;
	private TicketAdapter ticketAdapter;

	@BindView(R.id.recycler_view)
	private RecyclerView recyclerView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		unbinder = ButterKnife.bind(this);
		apiService = ApiClient.getClient().create(ApiService.class);
		ticketAdapter = new TicketAdapter(this,ticketList,this);

		RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,1);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(ticketAdapter);

		ConnectableObservable<List<Ticket>> ticketObservable = getTickets(from,to).replay();

		compositeDisposable.add(
				ticketObservable
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeWith(new DisposableObserver<List<Ticket>>(){

							@Override
							public void onNext(List<Ticket> list) {
								//got results , add to list.
								ticketList.clear();
								ticketList.addAll(list);
								ticketAdapter.notifyDataSetChanged();
							}

							@Override
							public void onError(Throwable e) {

							}

							@Override
							public void onComplete() {

							}
						})
		);

		compositeDisposable.add(
				ticketObservable
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.flatMap(new Function<List<Ticket>, ObservableSource<Ticket>>() {
					@Override
					public ObservableSource<Ticket> apply(List<Ticket> list) throws Exception {
						return Observable.fromIterable(list);
					}
				})
				.flatMap(new Function<Ticket, ObservableSource<Ticket>>() {
					@Override
					public ObservableSource<Ticket> apply(Ticket ticket) throws Exception {
						return getPriceObservable(ticket);
					}
				})
				.subscribeWith(new DisposableObserver<Ticket>() {
					@Override
					public void onNext(Ticket ticket) {
						int pos = ticketList.indexOf(ticket);
						if(pos == -1){
							return;
						}
						ticketList.set(pos,ticket);
						ticketAdapter.notifyDataSetChanged();
					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onComplete() {

					}
				})
		);
		ticketObservable.connect();
	}

	private Observable<List<Ticket>> getTickets(String from,String to){
		return apiService.searchTickets(from,to)
				.toObservable()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	private Observable<Ticket> getPriceObservable(final Ticket ticket){
		return apiService.getPrice(ticket.getFlightNumber(),ticket.getFrom(),ticket.getTo())
				.toObservable()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.map(new Function<Price, Ticket>() {
					@Override
					public Ticket apply(Price price) throws Exception {
						ticket.setPrice(price);
						return ticket;
					}
				});
	}

	@Override
	public void onTickSelection(Ticket ticket) {
		//do some thing when it clicked.
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		compositeDisposable.dispose();
		unbinder.unbind();
	}
}
