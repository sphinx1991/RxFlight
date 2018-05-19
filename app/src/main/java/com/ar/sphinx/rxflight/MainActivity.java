package com.ar.sphinx.rxflight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ar.sphinx.rxflight.network.model.Ticket;
import com.ar.sphinx.rxflight.view.TicketAdapter;

public class MainActivity extends AppCompatActivity implements TicketAdapter.TicketAdapterListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onTickSelection(Ticket ticket) {
		//do some thing when it clicked.
	}
}
