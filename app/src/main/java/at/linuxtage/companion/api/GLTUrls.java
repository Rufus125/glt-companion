package at.linuxtage.companion.api;

import java.util.Locale;

/**
 * This class contains all GLT Urls
 * 
 * @author Christophe Beyls
 * 
 */
public class GLTUrls {
    // https://fosdem.org/schedule/xml
    //private static final String SCHEDULE_URL = "http://flo.cx/tmp/fosdem_d1.xml";

    private static final String SCHEDULE_URL = "https://glt%1$d-programm.linuxtage.at/schedule.xml";
	//private static final String SCHEDULE_URL = "https://fosdem.org/schedule/xml";
	private static final String ROOMS_URL = "https://api.fosdem.org/roomstatus/v1/listrooms";
	//private static final String ROOMS_URL = "https://api.fosdem.org/roomstatus/v1/listrooms";
    private static final String EVENT_URL_FORMAT = "https://glt%1$d-programm.linuxtage.at/events/%2$s.html";
	//private static final String EVENT_URL_FORMAT = "https://fosdem.org/%1$d/schedule/event/%2$s/";
    private static final String PERSON_URL_FORMAT = "https://glt%1$d-programm.linuxtage.at/speakers/%2$s.html";
	//private static final String PERSON_URL_FORMAT = "https://fosdem.org/%1$d/schedule/speaker/%2$s/";


	private static final String LOCAL_NAVIGATION_URL = "https://nav.fosdem.org/";
	private static final String LOCAL_NAVIGATION_TO_ROOM_URL_FORMAT = "https://nav.fosdem.org/d/%1$s/";
	private static final String VOLUNTEER_URL = "https://linuxtage.at/helfer/";
	//private static final String VOLUNTEER_URL = "https://fosdem.org/volunteer/";


	public static String getSchedule(int year) {
		return String.format(Locale.US, SCHEDULE_URL, year-2000);
	}

	public static String getRooms() {
		return ROOMS_URL;
	}

	public static String getEvent(String slug, int year) {
		return String.format(Locale.US, EVENT_URL_FORMAT, year-2000, slug);
	}

	public static String getPerson(String slug, int year) {
		return String.format(Locale.US, PERSON_URL_FORMAT, year-2000, slug);
	}

	/*public static String getSchedule(int year) {
		return String.format(Locale.US, SCHEDULE_URL, year-2000);
	}
*/

	public static String getLocalNavigation() {
		return LOCAL_NAVIGATION_URL;
	}

	public static String getLocalNavigationToLocation(String locationSlug) {
		return String.format(Locale.US, LOCAL_NAVIGATION_TO_ROOM_URL_FORMAT, locationSlug);
	}

	public static String getVolunteer() {
		return VOLUNTEER_URL;
	}
}


