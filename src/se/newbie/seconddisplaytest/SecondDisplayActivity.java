package se.newbie.seconddisplaytest;

import se.newbie.seconddisplaytest.DisplayListFragment.DisplayAdapter;
import se.newbie.seconddisplaytest.presentation.OpenGLDemoPresentation;
import se.newbie.seconddisplaytest.presentation.PresentationState;
import se.newbie.seconddisplaytest.presentation.PresentationState.hasPresentationState;
import android.app.Activity;
import android.app.Presentation;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Menu;

/**
 * @author Joakim Olausson (player360@gmail.com)
 */
public class SecondDisplayActivity extends Activity implements DisplayListFragment.OnDisplayChangedListener {

	private final static String TAG = SecondDisplayActivity.class.getCanonicalName();
	private static final String PRESENTATION_KEY = "presentation";

	private final SparseArray<Presentation> mActivePresentations = new SparseArray<Presentation>();
	private SparseArray<PresentationState> mPresentationStates;

	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
		Log.v(TAG, "onCreate.");
		if (aSavedInstanceState != null) {
			mPresentationStates = aSavedInstanceState.getSparseParcelableArray(PRESENTATION_KEY);
			Log.v(TAG, "Found " + mPresentationStates.size() + " stored presentation states.");
		} else {
			mPresentationStates = new SparseArray<PresentationState>();
		}
		setContentView(R.layout.activity_second_display);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "onResume.");

		DisplayListFragment fragment = (DisplayListFragment) getFragmentManager().findFragmentById(R.id.activity_display_list_fragment);
		DisplayAdapter adapter = fragment.getDisplayAdapter();
		adapter.updateDisplays();

		for (int i = 0; i < adapter.getCount(); i++) {
			final Display display = (Display) adapter.getItem(i);
			final PresentationState state = mPresentationStates.get(display.getDisplayId());
			if (state != null) {
				Log.v(TAG, "Resuming presentation on #" + display.getDisplayId());
				showPresentation(display, state);
			}
		}
		mPresentationStates.clear();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, "onPause.");

		for (int i = 0; i < mActivePresentations.size(); i++) {
			Presentation presentation = mActivePresentations.valueAt(i);
			int displayId = mActivePresentations.keyAt(i);
			Log.v(TAG, "Store state for #" + displayId);
			if (presentation instanceof hasPresentationState) {
				mPresentationStates.put(displayId, ((hasPresentationState) presentation).getPresentationState());
			}
			presentation.dismiss();
		}
		mActivePresentations.clear();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_second_display, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle aOutState) {
		super.onSaveInstanceState(aOutState);
		Log.v(TAG, "Saving instance states for " + mPresentationStates.size() + " presentations.");
		aOutState.putSparseParcelableArray(PRESENTATION_KEY, mPresentationStates);
	}

	@Override
	public void onDisplayChanged(Display aDisplay, boolean aIsChecked) {
		if (aIsChecked) {
			PresentationState state = new PresentationState();
			showPresentation(aDisplay, state);
		} else {
			hidePresentation(aDisplay);
		}
	}

	private void showPresentation(Display aDisplay, PresentationState aState) {
		final int displayId = aDisplay.getDisplayId();
		if (mActivePresentations.indexOfKey(displayId) < 0) {
			Log.d(TAG, "Showing presentation on display #" + displayId + ".");
			OpenGLDemoPresentation presentation = new OpenGLDemoPresentation(this, aDisplay, aState);
			presentation.show();
			presentation.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface aDialog) {
					Log.v(TAG, "Presentation on display #" + displayId + " was dismissed.");
					// mActivePresentations.delete(displayId);
				}
			});
			mActivePresentations.put(displayId, presentation);
		}
	}

	private void hidePresentation(Display display) {
		final int displayId = display.getDisplayId();
		Presentation presentation = mActivePresentations.get(displayId);
		if (presentation != null) {
			Log.d(TAG, "Dismissing presentation on display #" + displayId + ".");
			presentation.dismiss();
			mActivePresentations.delete(displayId);
		}
	}
}
