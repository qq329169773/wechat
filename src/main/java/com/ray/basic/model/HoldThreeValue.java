package com.ray.basic.model;

public class HoldThreeValue<A, B, C> extends HoldDoubleValue<A, B> {

	public final C c ;
	public HoldThreeValue(A a, B b, C c) {
		super(a, b);
		this.c = c ;
	}

	@Override
	public String toString() {
		return "HoldThreeValue{" +
				"a=" + a +
				", b=" + b +
				", c=" + c +
				"} " ;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof HoldThreeValue)) return false;
		if (!super.equals(o)) return false;

		HoldThreeValue<?, ?, ?> that = (HoldThreeValue<?, ?, ?>) o;

		return c != null ? c.equals(that.c) : that.c == null;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (c != null ? c.hashCode() : 0);
		return result;
	}
}
