package com.example.schoolnear;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;

	public MainListAdapter(Context context, String[] values) {
		super(context, R.layout.timebased, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.timebased, parent, false);
		TextView mainList = (TextView) rowView.findViewById(R.id.label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		mainList.setText(values[position]);
		// Change the icon for Windows and iPhone
		String s = values[position];
		if (s.startsWith("Send SMS")) {
			imageView.setImageResource(R.drawable.sms);

		} else if (s.startsWith("Call Someone")) {
			imageView.setImageResource(R.drawable.call);
		} else if (s.startsWith("Set Alarm")) {
			imageView.setImageResource(R.drawable.alarm);
		} else if (s.startsWith("Send Email")) {
			imageView.setImageResource(R.drawable.email);
		} else if (s.startsWith("grocery_or_supermarket")) {
			imageView.setImageResource(R.drawable.grocery);
		} else if (s.startsWith("gas_station")) {
			imageView.setImageResource(R.drawable.gas);
		} else if (s.startsWith("bank")) {
			imageView.setImageResource(R.drawable.bank);
		} else if (s.startsWith("atm")) {
			imageView.setImageResource(R.drawable.atm);
		} else if (s.startsWith("pharmacy")) {
			imageView.setImageResource(R.drawable.pharmacy);
		}
		return rowView;
	}
}
