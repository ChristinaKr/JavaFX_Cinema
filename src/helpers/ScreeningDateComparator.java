package helpers;

import models.Screening;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * This class compares two Screening objects based on their date and time
 *
 * Earlier dates and times precede later ones.
 *
 */
public class ScreeningDateComparator implements Comparator<Screening> {

	/**
	 * Compare two Screening objects based on their date and time
	 *
	 * @param screening1 The first screening to be compared
	 * @param screening2 The second screening to be compared
	 * @return an integer indicating which screening comes temporally first. 1 if screening1 comes first, -1 if screening2 comes last and 0 if both are at the same time.
	 */
	@Override
	public int compare(Screening screening1, Screening screening2) {
		// Parses the date from the String fields of the screening objects
		LocalDate date1 = LocalDate.parse(screening1.getDate());
		LocalDate date2 = LocalDate.parse(screening2.getDate());
		// Retrieves the time values from the screening objects
		int time1 = screening1.getTime();
		int time2 = screening2.getTime();
		// If the date of screening1 is before screening2, return -1
		if (date1.isBefore(date2)) {
			return -1;
		// If the date of screening1 is after screening2, return 1
		} else if (date1.isAfter(date2)) {
			return 1;
		// If both are on the same day, compare them by time
		} else {
			return time1 - time2;
		}
	}

	/**
	 * Unused equals method required by the interface.
	 *
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		return false;
	}
}
