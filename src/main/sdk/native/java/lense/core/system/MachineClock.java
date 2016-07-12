package lense.core.system;

import lense.core.time.Clock;
import lense.core.time.Instant;

class MachineClock implements Clock {

	static MachineClock instance = new MachineClock();
	
	@Override
	public Instant instant() {
		return new Instant();
	}

}
