package org.teiath.data.domain.crp;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.hibernate.annotations.Type;
import org.teiath.data.domain.NotificationCriteria;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.StringTokenizer;

@Entity
@Table(name = "crp_route_notifications_criteria")
@PrimaryKeyJoinColumn(name = "ntf_criteria_id")
public class RouteNotificationCriteria
		extends NotificationCriteria {

	@Column(name = "route_notification_criteria_location", columnDefinition = "Geometry", nullable = true)
	@Type(type = "org.hibernate.spatial.GeometryType")
	private Geometry location;
	@Column(name = "route_notification_criteria_radius", nullable = false)
	private int radius;
	@Column(name = "route_notification_criteria_dateFrom", nullable = true)
	private Date dateFrom;
	@Column(name = "route_notification_criteria_dateTo", nullable = true)
	private Date dateTo;
	@Column(name = "route_notification_criteria_objectTransportAllowed", nullable = false)
	private boolean objectTransportAllowed;
	@Column(name = "route_notification_criteria_smokingAllowed", nullable = false)
	private boolean smokingAllowed;
	@Column(name = "route_notification_criteria_maxAmount", precision = 6, scale = 2, nullable = true)
	private BigDecimal maxAmount;
	@Column(name = "route_notification_criteria_tags", length = 2000, nullable = true)
	private String tags;
	@Column(name = "route_notification_criteria_from_address", length = 2000, nullable = true)
	private String fromAddress;
	@Column(name = "route_notification_criteria_from_coordinates", length = 2000, nullable = true)
	private String fromCoordinates;
	@Column(name = "route_notification_criteria_from_radius", nullable = true)
	private int fromRadius;
	@Column(name = "route_notification_criteria_to_address", length = 2000, nullable = true)
	private String toAddress;
	@Column(name = "route_notification_criteria_to_coordinates", length = 2000, nullable = true)
	private String toCoordinates;
	@Column(name = "route_notification_criteria_to_radius", nullable = true)
	private int toRadius;

	public RouteNotificationCriteria() {
	}

	public Geometry getLocation() {
		return location;
	}

	public void setLocation(Geometry location) {
		this.location = location;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public boolean isObjectTransportAllowed() {
		return objectTransportAllowed;
	}

	public void setObjectTransportAllowed(boolean objectTransportAllowed) {
		this.objectTransportAllowed = objectTransportAllowed;
	}

	public boolean isSmokingAllowed() {
		return smokingAllowed;
	}

	public void setSmokingAllowed(boolean smokingAllowed) {
		this.smokingAllowed = smokingAllowed;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void materializeGeometry(String googleCoords)
			throws ParseException {
		StringTokenizer stringTokenizer = new StringTokenizer(googleCoords, "|");

		String centerToken;
		String lat, lng;
		StringTokenizer st;

		WKTReader fromText = new WKTReader();
		if (stringTokenizer.hasMoreTokens()) {
			centerToken = stringTokenizer.nextToken();
			st = new StringTokenizer(centerToken, "),(");
			if (st.hasMoreTokens()) {
				lat = st.nextToken().trim();
				lng = st.nextToken().trim();

				System.out.println("Route Point: " + lat + " - " + lng);

				this.location = fromText.read("POINT(" + lat + " " + lng + ")");
				this.location.setSRID(4326);
			}
		}

		if (stringTokenizer.hasMoreTokens()) {
			this.radius = new BigDecimal(stringTokenizer.nextToken()).intValue();
		}
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getFromCoordinates() {
		return fromCoordinates;
	}

	public void setFromCoordinates(String fromCoordinates) {
		this.fromCoordinates = fromCoordinates;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getToCoordinates() {
		return toCoordinates;
	}

	public void setToCoordinates(String toCoordinates) {
		this.toCoordinates = toCoordinates;
	}

	public int getFromRadius() {
		return fromRadius;
	}

	public void setFromRadius(int fromRadius) {
		this.fromRadius = fromRadius;
	}

	public int getToRadius() {
		return toRadius;
	}

	public void setToRadius(int toRadius) {
		this.toRadius = toRadius;
	}
}
