package se.newbie.seconddisplaytest.presentation;

import se.newbie.seconddisplaytest.R;
import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

/**
 * 
 * @author Joakim Olausson (player360@gmail.com)
 */
public class DemoPresentation extends Presentation {
	private final static String TAG = DemoPresentation.class.getCanonicalName();

	public DemoPresentation(Context context, Display display) {
		super(context, display);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		setContentView(R.layout.demo_presentation);
	}
}
