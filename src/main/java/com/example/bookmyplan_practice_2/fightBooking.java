package com.example.bookmyplan_practice_2;

import org.springframework.web.bind.annotation.GetMapping;

public class fightBooking {
    @GetMapping("/fightBooking")
    public String getName() {
        return "Book your fight as soon as possible.";
    }
    public String getName1() {
        return "Book your fight as soon as possible.";
    }
}
