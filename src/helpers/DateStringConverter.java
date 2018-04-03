package helpers;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This extension to the StringConverter class formats the content of a DatePicker to "dd/MM/yyyy"
 *
 * Source: https://stackoverflow.com/questions/20383773/set-date-picker-time-format
 */
public class DateStringConverter extends StringConverter<LocalDate>{

	private DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Override
	public String toString(LocalDate localDate)	{
		if(localDate==null)
			return "";
		return dateTimeFormatter.format(localDate);
	}

	@Override
	public LocalDate fromString(String dateString) {
		if (dateString == null || dateString.trim().isEmpty()) {
			return null;
		}
		return LocalDate.parse(dateString, dateTimeFormatter);
	}
}
