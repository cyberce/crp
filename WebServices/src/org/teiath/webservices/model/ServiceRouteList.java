package org.teiath.webservices.model;

import org.codehaus.jackson.map.annotate.JsonRootName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "routes")
@JsonRootName(value = "routes")
public class ServiceRouteList {

	private List<ServiceRoute> routes;

	public ServiceRouteList() {

	}

	public ServiceRouteList(List<ServiceRoute> routes) {
		this.routes = routes;
	}

	@XmlElement(name = "route")
	public List<ServiceRoute> getServiceRoutes() {
		return this.routes;
	}

	public void setServiceRoutes(List<ServiceRoute> routes) {
		this.routes = routes;
	}
}
