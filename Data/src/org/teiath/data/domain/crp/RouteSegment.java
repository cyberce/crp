package org.teiath.data.domain.crp;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "crp_route_segments")
public class RouteSegment {

	public final static int DEPARTURE = 1;
	public final static int ARRIVAL = 2;
	public final static int WAYPOINT = 3;

	@Id
	@Column(name = "route_seg_id")
	@SequenceGenerator(name = "route_segments_seq", sequenceName = "crp_route_seg_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "route_segments_seq")
	private Integer id;

	@Column(name = "path_index", nullable = false)
	private int index;
	@Column(name = "start_address", length = 1000, nullable = true)
	private String startAddress;
	@Column(name = "start_lat", length = 100, nullable = true)
	private String startLat;
	@Column(name = "start_lng", length = 100, nullable = true)
	private String startLng;
	@Column(name = "end_address", length = 1000, nullable = true)
	private String endAddress;
	@Column(name = "end_lat", length = 100, nullable = true)
	private String endLat;
	@Column(name = "end_lng", length = 100, nullable = true)
	private String endLng;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "routeSegment")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private Set<RouteSegmentWaypoint> routeSegmentWaypoints;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "route_id", nullable = false)
	private Route route;

	public RouteSegment() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public String getStartAddress() {
		return startAddress;
	}

	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}

	public String getStartLat() {
		return startLat;
	}

	public void setStartLat(String startLat) {
		this.startLat = startLat;
	}

	public String getStartLng() {
		return startLng;
	}

	public void setStartLng(String startLng) {
		this.startLng = startLng;
	}

	public String getEndAddress() {
		return endAddress;
	}

	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}

	public String getEndLat() {
		return endLat;
	}

	public void setEndLat(String endLat) {
		this.endLat = endLat;
	}

	public String getEndLng() {
		return endLng;
	}

	public void setEndLng(String endLng) {
		this.endLng = endLng;
	}

	public Set<RouteSegmentWaypoint> getRouteSegmentWaypoints() {
		return routeSegmentWaypoints;
	}

	public void setRouteSegmentWaypoints(Set<RouteSegmentWaypoint> routeSegmentWaypoints) {
		this.routeSegmentWaypoints = routeSegmentWaypoints;
	}
}
