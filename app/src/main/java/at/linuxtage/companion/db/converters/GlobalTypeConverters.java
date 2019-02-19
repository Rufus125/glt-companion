package be.digitalia.companion.db.converters;

import androidx.room.TypeConverter;
import be.digitalia.companion.model.Day;
import be.digitalia.companion.model.Event;
import be.digitalia.companion.model.Person;
import be.digitalia.companion.model.Track;

public class GlobalTypeConverters {
	@TypeConverter
	public static Track.Type toTrackType(String value) {
		return Track.Type.valueOf(value);
	}

	@TypeConverter
	public static String fromTrackType(Track.Type value) {
		return value.name();
	}

	@TypeConverter
	public static long fromDay(Day day) {
		return day.getIndex();
	}

	@TypeConverter
	public static long fromTrack(Track track) {
		return track.getId();
	}

	@TypeConverter
	public static long fromPerson(Person person) {
		return person.getId();
	}

	@TypeConverter
	public static long fromEvent(Event event) {
		return event.getId();
	}
}
