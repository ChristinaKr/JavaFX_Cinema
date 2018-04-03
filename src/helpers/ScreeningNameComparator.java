package helpers;

import models.Screening;

import java.util.Comparator;

/**
 * This class compares two Screening objects based on their date and time
 *
 * Earlier dates and times precede later ones.
 *
 */
public class ScreeningNameComparator implements Comparator<Screening> {

	/**
	 * Compare two Screening objects based on their movie title.
	 * If both have the same name, they are compared based on their screening date instead.
	 *
	 * @param screening1 The first screening to be compared
	 * @param screening2 The second screening to be compared
	 * @return an integer indicating which screening comes alphabetically first. If both screenings have the same name, they are compared temporally. 1 if screening1 comes first, -1 if screening2 comes last and 0 if both.
	 */
	@Override
	public int compare(Screening screening1, Screening screening2) {
		String name1 = screening1.getMovie().getName();
		String name2 = screening2.getMovie().getName();
		if (name1.compareTo(name2) == 0) {
			return new ScreeningDateComparator().compare(screening1, screening2);
		} else {
			return name1.compareTo(name2);
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
