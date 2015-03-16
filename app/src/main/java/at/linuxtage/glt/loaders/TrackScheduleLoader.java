package at.linuxtage.glt.loaders;

import android.content.Context;
import android.database.Cursor;
import at.linuxtage.glt.db.DatabaseManager;
import at.linuxtage.glt.model.Day;
import at.linuxtage.glt.model.Track;

public class TrackScheduleLoader extends SimpleCursorLoader {

	private final Day day;
	private final Track track;

	public TrackScheduleLoader(Context context, Day day, Track track) {
		super(context);
		this.day = day;
		this.track = track;
	}

	@Override
	protected Cursor getCursor() {
		return DatabaseManager.getInstance().getEvents(day, track);
	}
}
