package org.teiath.data.domain.crp;

import javax.persistence.*;

@Entity
@Table(name = "crp_route_segment_waypoints")
public class RouteSegmentWaypoint {
	@Id
	@Column(name = "route_wayp_id")
	@SequenceGenerator(name = "route_segments_waypoints_seq", sequenceName = "crp_route_wayp_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "route_segments_waypoints_seq")
	private Integer id;

	@Column(name = "lat", length = 100, nullable = false)
	private String lat;
	@Column(name = "lng", length = 100, nullable = false)
	private String lng;
	@Column(name = "point_index", nullable = false)
	private Integer index;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "route_seg_id", nullable = false)
	private RouteSegment routeSegment;

	public RouteSegmentWaypoint() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public RouteSegment getRouteSegment() {
		return routeSegment;
	}

	public void setRouteSegment(RouteSegment routeSegment) {
		this.routeSegment = routeSegment;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
}
