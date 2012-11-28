package se.newbie.seconddisplaytest.presentation;

import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author Joakim Olausson (player360@gmail.com)
 */
public class PresentationState implements Parcelable {
	public interface hasPresentationState {
		public PresentationState getPresentationState();
	}

	private HashMap<String, String> mMap;

	public PresentationState() {
		mMap = new HashMap<String, String>();
	}

	public PresentationState(Parcel aIn) {
		mMap = new HashMap<String, String>();
		readFromParcel(aIn);
	}

	public static final Parcelable.Creator<PresentationState> CREATOR = new Parcelable.Creator<PresentationState>() {
		public PresentationState createFromParcel(Parcel aIn) {
			return new PresentationState(aIn);
		}

		public PresentationState[] newArray(int aSize) {
			return new PresentationState[aSize];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel aDest, int aFlags) {
		aDest.writeInt(mMap.size());
		for (String s : mMap.keySet()) {
			aDest.writeString(s);
			aDest.writeString(mMap.get(s));
		}
	}

	public void readFromParcel(Parcel aIn) {
		int count = aIn.readInt();
		for (int i = 0; i < count; i++) {
			mMap.put(aIn.readString(), aIn.readString());
		}
	}

	public String get(String aKey) {
		return mMap.get(aKey);
	}

	public void put(String aKey, String aValue) {
		mMap.put(aKey, aValue);
	}
}
