package org.teiath.data.domain.crp;

import com.vividsolutions.jts.geom.LineString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name = "crp_route_paths")
public class RoutePath {
	@Id
	@Column(name="route_id", unique=true, nullable=false)
	@GeneratedValue(generator="gen")
	@GenericGenerator(name="gen", strategy="foreign", parameters=@Parameter(name="property", value="route"))
	private Integer id;

	@Column(name = "path", columnDefinition = "Geometry", nullable = true)
	@Type(type = "org.hibernate.spatial.GeometryType")
	private LineString path;

	@OneToOne
	@PrimaryKeyJoinColumn
	private Route route;

	public RoutePath() {
	}

	public RoutePath(LineString path, Route route) {
		this.path = path;
		this.route = route;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LineString getPath() {
		return path;
	}

	public void setPath(LineString path) {
		this.path = path;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
}
