package se.newbie.seconddisplaytest;

import se.newbie.seconddisplaytest.presentation.OpenGLDemoPresentation;
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

	private final SparseArray<Presentation> mActivePresentations = new SparseArray<Presentation>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_display);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_second_display, menu);
		return true;
	}

	@Override
	public void onDisplayChanged(Display aDisplay, boolean aIsChecked) {
		if (aIsChecked) {
			showPresentation(aDisplay);
		} else {
			hidePresentation(aDisplay);
		}
	}

	private void showPresentation(Display display) {
		final int displayId = display.getDisplayId();
		Log.d(TAG, "Showing presentation on display #" + displayId + ".");
		// DemoPresentation presentation = new DemoPresentation(this, display);
		OpenGLDemoPresentation presentation = new OpenGLDemoPresentation(this, display);
		presentation.show();
		presentation.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface aDialog) {
				Presentation presentation = (Presentation) aDialog;
				int displayId = presentation.getDisplay().getDisplayId();
				Log.d(TAG, "Presentation on display #" + displayId + " was dismissed.");
				mActivePresentations.delete(displayId);
			}
		});
		mActivePresentations.put(displayId, presentation);
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

	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, "Activity is being paused.");
		mActivePresentations.clear();
	}
}
