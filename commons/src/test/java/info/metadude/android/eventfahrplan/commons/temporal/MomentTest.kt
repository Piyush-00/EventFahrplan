package info.metadude.android.eventfahrplan.commons.temporal

import info.metadude.android.eventfahrplan.commons.temporal.Moment.Companion.MILLISECONDS_OF_ONE_HOUR
import info.metadude.android.eventfahrplan.commons.temporal.Moment.Companion.MILLISECONDS_OF_ONE_MINUTE
import info.metadude.android.eventfahrplan.commons.temporal.Moment.Companion.MILLISECONDS_OF_ONE_SECOND
import info.metadude.android.eventfahrplan.commons.temporal.Moment.Companion.MINUTES_OF_ONE_DAY
import info.metadude.android.eventfahrplan.commons.temporal.Moment.Companion.toMoment
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import java.util.TimeZone

class MomentTest {

    private companion object {

        /**
         * Milliseconds representation of 2019-12-30T22:47:57.615Z.
         */
        const val DEC_30_22_47_2019 = 1577746077615

    }

    @Test
    fun `constants return correct values`() {
        assertThat(MILLISECONDS_OF_ONE_SECOND).isEqualTo(1_000)
        assertThat(MILLISECONDS_OF_ONE_MINUTE).isEqualTo(60_000)
        assertThat(MILLISECONDS_OF_ONE_HOUR).isEqualTo(3_600_000L)
        assertThat(MINUTES_OF_ONE_DAY).isEqualTo(1_440)
    }

    @Test
    fun dateTimeFieldsAreCorrectlyMapped() {
        val moment = Moment.ofEpochMilli(DEC_30_22_47_2019)

        assertThat(moment.year).isEqualTo(2019)
        assertThat(moment.month).isEqualTo(12)
        assertThat(moment.monthDay).isEqualTo(30)
        assertThat(moment.hour).isEqualTo(22)
        assertThat(moment.minute).isEqualTo(47)
    }

    @Test
    fun startOfDay() {
        val moment = Moment.ofEpochMilli(DEC_30_22_47_2019)
        val startOfDay = moment.startOfDay()

        assertThat(startOfDay.year).isEqualTo(2019)
        assertThat(startOfDay.month).isEqualTo(12)
        assertThat(startOfDay.monthDay).isEqualTo(30)
        assertThat(startOfDay.hour).isEqualTo(0)
        assertThat(startOfDay.minute).isEqualTo(0)
    }

    @Test
    fun endOfDay() {
        val moment = Moment.ofEpochMilli(DEC_30_22_47_2019)
        val endOfDayUTC = moment.endOfDay().toUtcDateTime()

        assertThat(endOfDayUTC.year).isEqualTo(2019)
        assertThat(endOfDayUTC.monthValue).isEqualTo(12)
        assertThat(endOfDayUTC.dayOfMonth).isEqualTo(30)
        assertThat(endOfDayUTC.hour).isEqualTo(23)
        assertThat(endOfDayUTC.minute).isEqualTo(59)
    }

    @Test
    fun getMinuteOfDayZonedInput() {
        val startsAtDate = ZonedDateTime.of(2019, 8, 27, 6, 30, 0, 0, ZoneOffset.ofHours(4))
        assertThat(startsAtDate.toMoment().minuteOfDay).isEqualTo((6 - 4) * 60 + 30)
    }

    @Test
    fun getDayOfMonthWithLeapYearDay() {
        // Thursday, February 28, 2019 11:59:59 PM UTC
        assertThat(Moment.ofEpochMilli(1551312000000).monthDay).isEqualTo(28)
    }

    @Test
    fun getDayOfMonthWithDayAfterLeapYear() {
        // Friday, March 1, 2019 12:00:00 AM UTC
        assertThat(Moment.ofEpochMilli(1551398400000).monthDay).isEqualTo(1)
    }

    @Test
    fun startOfDayVsLocalDate() {
        val localDateString = "2019-12-30"
        val localDate = LocalDate.parse(localDateString)

        val startOfDay = Moment.ofEpochMilli(DEC_30_22_47_2019).startOfDay().toUtcDateTime().toLocalDate()

        assertThat(startOfDay).isEqualTo(localDate)
    }

    @Test
    fun timeZoneHasNoEffectOnMilliseconds() {
        val nowUTC = Moment.now()
        val utcMillis = nowUTC.toMilliseconds()
        val utcSeconds = utcMillis / MILLISECONDS_OF_ONE_SECOND

        val nowTZ = nowUTC.toZonedDateTime(ZoneOffset.ofHours(2))
        val tzSeconds = nowTZ.toEpochSecond()

        assertThat(utcSeconds).isEqualTo(tzSeconds)
    }

    @Test
    fun toLocalDate() {
        val localDateString = "2019-12-31"
        val localDate = LocalDate.parse(localDateString)

        val momentLocalDate = Moment.parseDate(localDateString).toUtcDateTime().toLocalDate()

        assertThat(momentLocalDate).isEqualTo(localDate)
    }

    @Test
    fun isBefore() {
        val momentOne = Moment.now()
        val momentTwo = Moment.now().plusSeconds(1)

        assertThat(momentOne.isBefore(momentTwo)).isTrue()
        assertThat(momentTwo.isBefore(momentOne)).isFalse()
        assertThat(momentOne.isBefore(momentOne)).isFalse()
    }

    @Test
    fun plusSeconds() {
        val momentOne = Moment.ofEpochMilli(0).plusSeconds(1)
        assertThat(momentOne.toMilliseconds()).isEqualTo(MILLISECONDS_OF_ONE_SECOND.toLong())

        val momentTwo = Moment.ofEpochMilli(MILLISECONDS_OF_ONE_SECOND.toLong()).plusSeconds(-1)
        assertThat(momentTwo.toMilliseconds()).isEqualTo(0)
    }

    @Test
    fun plusMinutes() {
        val momentOne = Moment.ofEpochMilli(0).plusMinutes(1)
        assertThat(momentOne.toMilliseconds()).isEqualTo(MILLISECONDS_OF_ONE_MINUTE.toLong())

        val momentTwo = Moment.ofEpochMilli(MILLISECONDS_OF_ONE_MINUTE.toLong()).plusMinutes(-1)
        assertThat(momentTwo.toMilliseconds()).isEqualTo(0)
    }

    @Test
    fun minusHours() {
        val momentOne = Moment.ofEpochMilli(MILLISECONDS_OF_ONE_HOUR.toLong()).minusHours(1)
        assertThat(momentOne.toMilliseconds()).isEqualTo(0)

        val momentTwo = Moment.ofEpochMilli(0).minusHours(-1)
        assertThat(momentTwo.toMilliseconds()).isEqualTo(MILLISECONDS_OF_ONE_HOUR)
    }

    @Test
    fun minusMinutes() {
        val momentOne = Moment.ofEpochMilli(MILLISECONDS_OF_ONE_MINUTE.toLong()).minusMinutes(1)
        assertThat(momentOne.toMilliseconds()).isEqualTo(0)

        val momentTwo = Moment.ofEpochMilli(0).minusMinutes(-1)
        assertThat(momentTwo.toMilliseconds()).isEqualTo(MILLISECONDS_OF_ONE_MINUTE.toLong())
    }

    @Test
    fun getSystemOffsetMinutesWithGmtPlus1() = withTimeZone("GMT+1") {
        assertThat(Moment.getSystemOffsetMinutes()).isEqualTo(60)
    }

    @Test
    fun getSystemOffsetMinutesWithGmt() = withTimeZone("GMT") {
        assertThat(Moment.getSystemOffsetMinutes()).isEqualTo(0)
    }

    @Test
    fun getSystemOffsetMinutesWithGmtMinus1() = withTimeZone("GMT-1") {
        assertThat(Moment.getSystemOffsetMinutes()).isEqualTo(-60)
    }

    private fun withTimeZone(temporaryTimeZoneId: String, block: () -> Unit) {
        val systemTimezone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone(temporaryTimeZoneId))
        block()
        TimeZone.setDefault(systemTimezone)
    }

}
