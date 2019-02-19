package at.linuxtage.companion.db.converters;

import androidx.room.TypeConverter;
import at.linuxtage.companion.model.Day;
import at.linuxtage.companion.model.Event;
import at.linuxtage.companion.model.Person;
import at.linuxtage.companion.model.Track;

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
