package com.ray.basic.model;

public class HoldDoubleValue<A,B> {

	public final A a ;
	public final B b ;
	
	public HoldDoubleValue(A a , B b){
		this.a = a ;
		this.b = b ;
	}

	@Override
	public String toString() {
		return "HoldDoubleValue{" +
				"a=" + a +
				", b=" + b +
				'}';
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HoldDoubleValue<?, ?> that = (HoldDoubleValue<?, ?>) o;

		if (a != null ? !a.equals(that.a) : that.a != null) return false;
		return b != null ? b.equals(that.b) : that.b == null;

	}

	@Override
	public int hashCode() {
		int result = a != null ? a.hashCode() : 0;
		result = 31 * result + (b != null ? b.hashCode() : 0);
		return result;
	}
}
