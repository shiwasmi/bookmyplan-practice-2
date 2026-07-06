package com.example.bookmyplan_practice_2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
public class hotelBooking {
    @GetMapping("/hotelbooking")
    public String getName() {
        return "Book your hotel as soon as possible";
    }
    public String getName1() {
        return "Book your hotel as soon as possible";
    }

}
