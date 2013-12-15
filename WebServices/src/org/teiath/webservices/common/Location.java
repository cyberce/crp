package org.teiath.webservices.common;

import java.io.Serializable;

public class Location implements Serializable {

	private double latitude;

	private double longitude;

	public Location() {

	}

	public Location(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
}