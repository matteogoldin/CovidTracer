package Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.TimePoint;

class TimePointTest {
	TimePoint t1,t2;

	@BeforeEach
	void setUp() throws Exception {
		t1=new TimePoint(LocalDate.now());
		t2=new TimePoint(LocalDate.now().minusDays(3));
	}

	@Test
	void getIntervallTest() {
		long interval=t1.getInterval(t2);
		assertEquals(interval,-3);
	}
	
}
