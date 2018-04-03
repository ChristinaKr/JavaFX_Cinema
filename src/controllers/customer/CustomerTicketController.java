package controllers.customer;

import helpers.Helpers;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Booking;

public class CustomerTicketController {

    @FXML
    Label lblMovieName, lblDate, lblSeats, lblPrice, lblBookingID, lblTime;
    @FXML
    ImageView imageLogo, imageQRCode;

    //sets the chosen booking from booking history in Customer Profile View to the booking object
    public void setup(Booking booking) {
        lblMovieName.setText(booking.getScreening().getMovie().getName());
        lblDate.setText(Helpers.formatDateString(booking.getScreening().getDate()));
        lblTime.setText("" + booking.getScreening().getTime() + ":00");
        lblSeats.setText(booking.getFormattedSeatList());
        lblPrice.setText("£ " + booking.getSeatList().size() * 8);
        lblBookingID.setText("" + booking.getBookingID());
        imageLogo.setImage(new Image("file:" + System.getProperty("user.dir") + "/img/Logo.png"));
        imageQRCode.setImage(new Image("file:" + System.getProperty("user.dir") + "/img/qrcode.png"));
    }
}
