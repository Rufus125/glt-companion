package at.linuxtage.companion.model;

import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;
import at.linuxtage.companion.db.converters.NullableDateTypeConverters;

import java.util.Date;

public class AlarmInfo {

	@ColumnInfo(name = "event_id")
	private long eventId;
	@ColumnInfo(name = "start_time")
	@TypeConverters({NullableDateTypeConverters.class})
	private Date startTime;

	public AlarmInfo(long eventId, Date startTime) {
		this.eventId = eventId;
		this.startTime = startTime;
	}

	public long getEventId() {
		return eventId;
	}

	public Date getStartTime() {
		return startTime;
	}
}
