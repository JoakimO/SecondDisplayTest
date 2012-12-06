package se.newbie.seconddisplaytest;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author Joakim Olausson (player360@gmail.com)
 */
public class DisplayListFragment extends Fragment {
	private final static String TAG = DisplayListFragment.class.getCanonicalName();

	private static final String SAVED_LIST_KEY = "selected_displays";

	private View mView;
	private ListView mListView;
	private OnDisplayChangedListener mCallback;
	private DisplayManager mDisplayManager;
	private DisplayAdapter mDisplayAdapter;
	private ArrayList<Integer> mSelectedDisplays;

	public interface OnDisplayChangedListener {
		public void onDisplayChanged(Display aDisplay, boolean aIsChecked);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");

		Context context = this.getActivity().getApplicationContext();
		mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);

		mView = inflater.inflate(R.layout.display_list, container, false);
		mListView = (ListView) mView.findViewById(R.id.display_list);

		if (savedInstanceState != null) {
			mSelectedDisplays = savedInstanceState.getIntegerArrayList(SAVED_LIST_KEY);
		} else {
			mSelectedDisplays = new ArrayList<Integer>();
		}

		mDisplayAdapter = new DisplayAdapter(context, this);
		mListView.setAdapter(mDisplayAdapter);

		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v(TAG, "onResume");
		mDisplayAdapter.updateDisplays();
		mDisplayManager.registerDisplayListener(mDisplayAdapter, null);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");
		mDisplayManager.unregisterDisplayListener(mDisplayAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle aOutState) {
		super.onSaveInstanceState(aOutState);

		aOutState.putIntegerArrayList(SAVED_LIST_KEY, mSelectedDisplays);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnDisplayChangedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnDisplaySelectedListener");
		}
	}

	public void onChange(Display aDisplay, boolean aIsChecked) {
		mCallback.onDisplayChanged(aDisplay, aIsChecked);
	}

	public DisplayAdapter getDisplayAdapter() {
		return mDisplayAdapter;
	}

	protected List<Integer> getSelectedDisplays() {
		return mSelectedDisplays;
	}

	class DisplayAdapter extends BaseAdapter implements DisplayListener {

		private Context mContext;
		private List<Display> mDisplays = new ArrayList<Display>();
		private DisplayListFragment mDisplayListFragment;

		public DisplayAdapter(Context context, DisplayListFragment displayListFragment) {
			mDisplayListFragment = displayListFragment;
			mContext = context;
		}

		public int getCount() {
			return mDisplays.size();
		}

		public Object getItem(int position) {
			return mDisplays.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public void clear() {
			mDisplays.clear();
		}

		public void addAll(Display[] displays) {
			for (Display display : displays) {
				mDisplays.add(display);
			}
			this.notifyDataSetChanged();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View view = inflater.inflate(R.layout.display_list_item, null);
			final Display display = mDisplays.get(position);
			TextView nameTextView = (TextView) view.findViewById(R.id.display_list_item_name_text_view);
			TextView descriptionTextView = (TextView) view.findViewById(R.id.display_list_item_description_text_view);

			final CheckBox checkBox = (CheckBox) view.findViewById(R.id.display_list_item_checkbox);

			if (mDisplayListFragment.getSelectedDisplays().contains(display.getDisplayId())) {
				checkBox.setChecked(true);
			}

			nameTextView.setText(display.getName());

			String description = "";

			DisplayMetrics outMetrics = new DisplayMetrics();
			display.getRealMetrics(outMetrics);

			description += outMetrics.widthPixels + "x" + outMetrics.heightPixels;
			description += ", " + outMetrics.densityDpi + " dpi";

			int flags = display.getFlags();
			if ((flags & Display.FLAG_SECURE) == Display.FLAG_SECURE) {
				description += ", FLAG_SECURE";
			}
			if ((flags & Display.FLAG_SUPPORTS_PROTECTED_BUFFERS) == Display.FLAG_SUPPORTS_PROTECTED_BUFFERS) {
				description += ", FLAG_SUPPORTS_PROTECTED_BUFFERS";
			}

			descriptionTextView.setText(description);

			checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View aV) {
					if (checkBox.isChecked()) {
						if (!mDisplayListFragment.getSelectedDisplays().contains(display.getDisplayId()))
							mDisplayListFragment.getSelectedDisplays().add(display.getDisplayId());
					} else {
						mDisplayListFragment.getSelectedDisplays().remove(display.getDisplayId());
					}
					mDisplayListFragment.onChange(display, checkBox.isChecked());
				}
			});

			return view;
		}

		public void updateDisplays() {
			DisplayManager displayManager = (DisplayManager) mContext.getSystemService(Context.DISPLAY_SERVICE);
			Display[] displays = displayManager.getDisplays(); // DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
			this.clear();
			addAll(displays);

			Log.d(TAG, "There are currently " + displays.length + " displays connected.");
			for (Display display : displays) {
				Log.v(TAG, display.toString());
			}
		}

		@Override
		public void onDisplayAdded(int index) {
			Log.v(TAG, "Display added #" + index);
			updateDisplays();
		}

		@Override
		public void onDisplayChanged(int index) {
			Log.v(TAG, "Display changed #" + index);
			updateDisplays();
		}

		@Override
		public void onDisplayRemoved(int index) {
			Log.v(TAG, "Display remove #" + index);
			updateDisplays();
		}
	}
}
