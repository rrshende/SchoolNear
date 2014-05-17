package com.example.schoolnear;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class MainListAdapterReminders extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;

	 public MainListAdapterReminders(Context context, String[] values) {
		super(context, R.layout.activity_main_list_adapter_reminders, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.activity_main_list_adapter_reminders, parent, false);
		TextView category = (TextView) rowView.findViewById(R.id.label3);
		TextView note= (TextView) rowView.findViewById(R.id.textView1);
		TextView time = (TextView) rowView.findViewById(R.id.textView2);
		String[] a = values[position].split("~");
		category.setText(a[2]);
		note.setText(a[1]);
		time.setText(a[0]);
		// Change the icon for Windows and iPhone
	

		return rowView;
	}
}

