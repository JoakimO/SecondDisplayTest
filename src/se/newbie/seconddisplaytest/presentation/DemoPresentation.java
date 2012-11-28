package se.newbie.seconddisplaytest.presentation;

import se.newbie.seconddisplaytest.R;
import se.newbie.seconddisplaytest.presentation.PresentationState.hasPresentationState;
import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

/**
 * 
 * @author Joakim Olausson (player360@gmail.com)
 */
public class DemoPresentation extends Presentation implements hasPresentationState {
	private final static String TAG = DemoPresentation.class.getCanonicalName();

	PresentationState mState;

	public DemoPresentation(Context aContext, Display aDisplay, PresentationState aState) {
		super(aContext, aDisplay);
	}

	@Override
	public PresentationState getPresentationState() {
		return mState;
	}

	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
		Log.v(TAG, "onCreate");
		setContentView(R.layout.demo_presentation);
	}
}
