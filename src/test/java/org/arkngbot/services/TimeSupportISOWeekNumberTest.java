package org.arkngbot.services;

import org.arkngbot.services.impl.TimeSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TimeSupportISOWeekNumberTest {

    TimeSupport timeSupport;

    @BeforeEach
    public void setUp() {
        timeSupport = new TimeSupport();
    }

    @Test
    public void shouldReturnWeekZero() {
        LocalDate sunday3rdJan2021 = LocalDate.of(2021, 1, 3);

        int weekNumber = timeSupport.calculateWeekNumber(sunday3rdJan2021);

        assertThat(weekNumber, is(0));
    }

    @Test
    public void shouldReturnWeekOne() {
        LocalDate monday4thJan2021 = LocalDate.of(2021, 1, 4);

        int weekNumber = timeSupport.calculateWeekNumber(monday4thJan2021);

        assertThat(weekNumber, is(1));
    }

    @Test
    public void shouldReturnWeekThirty() {
        LocalDate saturday31stJul2021 = LocalDate.of(2021, 7, 31);

        int weekNumber = timeSupport.calculateWeekNumber(saturday31stJul2021);

        assertThat(weekNumber, is(30));
    }

    @Test
    public void shouldReturnWeekOneNoZeroth() {
        LocalDate wednesday1stJan2020 = LocalDate.of(2020, 1, 1);

        int weekNumber = timeSupport.calculateWeekNumber(wednesday1stJan2020);

        assertThat(weekNumber, is(1));
    }

    @Test
    public void shouldReturnWeekFiftyTwo() {
        LocalDate friday31stDec2021 = LocalDate.of(2021, 12, 31);

        int weekNumber = timeSupport.calculateWeekNumber(friday31stDec2021);

        assertThat(weekNumber, is(52));
    }

    @Test
    public void shouldReturnWeekFiftyThree() {
        LocalDate thursday31stDec2020 = LocalDate.of(2020, 12, 31);

        int weekNumber = timeSupport.calculateWeekNumber(thursday31stDec2020);

        assertThat(weekNumber, is(53));
    }

}
