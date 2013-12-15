package org.teiath.webservices.controllers;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.log4j.Logger;
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RoutePath;
import org.teiath.data.domain.crp.RouteSegment;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteSearchCriteria;
import org.teiath.service.crp.*;
import org.teiath.service.exceptions.AuthenticationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.UserLoginService;
import org.teiath.service.vehicle.EditVehicleService;
import org.teiath.webservices.authentication.Utils;
import org.teiath.webservices.common.Location;
import org.teiath.webservices.common.PolylineDecoder;
import org.teiath.webservices.model.ResponseMessages;
import org.teiath.webservices.model.ServiceRoute;
import org.teiath.webservices.model.ServiceRouteList;
import org.teiath.webservices.model.ServiceStopList;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class RoutesController {

	@Autowired
	private SearchRoutesService searchRoutesService;
	@Autowired
	private ViewRouteService viewRouteService;
	@Autowired
	private CreateRouteService createRouteService;
	@Autowired
	private EditRouteService editRouteService;
	@Autowired
	private ListRoutesService listRoutesService;
	@Autowired
	private EditVehicleService editVehicleService;
	@Autowired
	private UserLoginService userLoginService;

	private static final Logger logger_c = Logger.getLogger(RoutesController.class);

	@RequestMapping(value = "/services/routes", method = RequestMethod.GET, headers="Accept=application/xml, application/json, application/rss+xml")
	public ServiceRouteList searchRoutes(String rFrom, Integer rFromRadius, String rTo, Integer rToRadius, String dFrom, String dTo, String tFrom, String tTo, Boolean avail,
	                                     Integer seats, Boolean obj, Double contr, Boolean smoke, String tags) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat time = new SimpleDateFormat("HH:mm");

		ServiceRouteList serviceRouteList = new ServiceRouteList();
		serviceRouteList.setServiceRoutes(new ArrayList<ServiceRoute>());
		ServiceRoute serviceRoute = null;

		RouteSearchCriteria criteria = new RouteSearchCriteria();
		try {
			if (rFrom != null) {
				criteria.setSearchLocationFrom(rFrom != null ? "POINT(" + rFrom + ")" : null);
			}
			if (rFromRadius != null) {
				criteria.setSearchRadiusFrom(rFromRadius != null ? rFromRadius : null);
			}
			if (rTo != null) {
				criteria.setSearchLocationTo(rTo != null ? "POINT(" + rTo + ")" : null);
			}
			if (rToRadius != null) {
				criteria.setSearchRadiusTo(rToRadius != null ? rToRadius : null);
			}
			criteria.setDateFrom(dFrom != null ? sdf.parse(dFrom) : null);
			criteria.setDateTo(dTo != null ? sdf.parse(dTo) : null);
			criteria.setTimeFrom(tFrom != null ? time.parse(tFrom) : null);
			criteria.setTimeTo(tTo != null ? time.parse(tTo) : null);
			criteria.setAvailability(avail != null ? avail ? 1 : 0 : null);
			criteria.setPeopleNumber(seats);
			criteria.setObjectTransportAllowed(obj != null ? obj ? 1 : 0 : null);
			criteria.setMaxAmount(contr != null ? new BigDecimal(contr) : null);
			criteria.setSmokingAllowed(smoke != null ? smoke ? 1 : 0 : null);
			criteria.setTags(tags);

			criteria.setPageNumber(0);
			criteria.setPageSize(Integer.MAX_VALUE);
			criteria.setSortOrder(1);

			SearchResults<Route> results = searchRoutesService.searchRoutes(criteria, false);

			for (Route route : results.getData()) {
				serviceRoute = new ServiceRoute();
				serviceRoute.setStartingPoint(route.getStartingPoint());
				serviceRoute.setDestinationPoint(route.getDestinationPoint());
				serviceRoute.setRouteDate(route.getRouteDate());
				serviceRoute.setRouteTime(route.getRouteTime());
				serviceRoute.setFlexible(route.isFlexible());
				serviceRoute.setTotalSeats(route.getTotalSeats());
				serviceRoute.setObjectTransportAllowed(route.isObjectTransportAllowed());
				serviceRoute.setSmokingAllowed(route.isSmokingAllowed());
				serviceRoute.setContributionAmount(route.getContributionAmount());
				serviceRoute.setTags(route.getTags());
				serviceRoute.setComment(route.getComment());
				serviceRoute.setRecurring(route.isRecurring());
				serviceRoute.setDays(route.getDays());
				serviceRoute.setRouteCreationDate(route.getRouteCreationDate());
				serviceRoute.setRouteEndDate(route.getRouteEndDate());
				serviceRoute.setVehicleName(route.getVehicle().getFullName());
				serviceRoute.setCode(route.getCode());
				if (route.getRouteSegments().size() > 1) {
					serviceRoute.setStops(new ServiceStopList());
					serviceRoute.getStops().setAddress(new ArrayList<String>());
					int index = 0;
					for (RouteSegment routeSegment: route.getRouteSegments()) {
						if (index++ > 0) {
							serviceRoute.getStops().getAddress().add(routeSegment.getStartAddress());
						}
					}
				}

				serviceRouteList.getServiceRoutes().add(serviceRoute);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		logger_c.debug("Returing Routes: " + serviceRoute);
		return serviceRouteList;
	}

	@RequestMapping(value = "/services/route", method = RequestMethod.GET)
	public ServiceRoute searchRouteByCode(String code) {

		ServiceRoute serviceRoute = new ServiceRoute();
		try {

			Route route = viewRouteService.getRouteByCode(code);

			if (route != null) {
				serviceRoute.setStartingPoint(route.getStartingPoint());
				serviceRoute.setDestinationPoint(route.getDestinationPoint());
				serviceRoute.setRouteDate(route.getRouteDate());
				serviceRoute.setRouteTime(route.getRouteTime());
				serviceRoute.setFlexible(route.isFlexible());
				serviceRoute.setTotalSeats(route.getTotalSeats());
				serviceRoute.setObjectTransportAllowed(route.isObjectTransportAllowed());
				serviceRoute.setSmokingAllowed(route.isSmokingAllowed());
				serviceRoute.setContributionAmount(route.getContributionAmount());
				serviceRoute.setTags(route.getTags());
				serviceRoute.setComment(route.getComment());
				serviceRoute.setRecurring(route.isRecurring());
				serviceRoute.setDays(route.getDays());
				serviceRoute.setRouteCreationDate(route.getRouteCreationDate());
				serviceRoute.setRouteEndDate(route.getRouteEndDate());
				serviceRoute.setVehicleName(route.getVehicle().getFullName());
				serviceRoute.setCode(route.getCode());
				if (route.getRouteSegments().size() > 1) {
					serviceRoute.setStops(new ServiceStopList());
					serviceRoute.getStops().setAddress(new ArrayList<String>());
					int index = 0;
					for (RouteSegment routeSegment: route.getRouteSegments()) {
						if (index++ > 0) {
							serviceRoute.getStops().getAddress().add(routeSegment.getStartAddress());
						}
					}
				}
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		logger_c.debug("Returing Route: " + serviceRoute);

		return serviceRoute;
	}

	@RequestMapping(value = "/services/route", method = RequestMethod.POST, headers="Accept=application/xml, application/json")
	public @ResponseBody ResponseMessages saveRoute(@RequestBody ServiceRoute route, HttpServletRequest request) {
		ResponseMessages responseMessages = new ResponseMessages();

		String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			responseMessages.addMessage("This method needs authorization");
			return responseMessages;
		} else {
			try {
				String[] credentials = Utils.decodeHeader(authorization);
				User user = userLoginService.wsLogin(credentials[0], credentials[1]);

				if (user == null) {
					throw new AuthenticationException("Invalid Username/Password");
				}

				Vehicle vehicle = null;

				if (route.getStartingPoint() == null) {
					responseMessages.addMessage("startingPoint is mandatory");
				}
				if (route.getDestinationPoint() == null) {
					responseMessages.addMessage("destination is mandatory");
				}
				if (route.getRouteDate() == null) {
					responseMessages.addMessage("routeDate is mandatory");
				}
				if (route.getRouteTime() == null) {
					responseMessages.addMessage("routeTime is mandatory");
				}
				if (route.getTotalSeats() == null) {
					responseMessages.addMessage("totalSeats is mandatory");
				}
				if (route.isRecurring() != null) {
					if (route.isRecurring()) {
						if (route.getDays() == null) {
							responseMessages.addMessage("days is mandatory");
						}
					}
				}
				if (route.getVehiclePlate() != null) {
					try {
						vehicle = editVehicleService.getVehicleByPlate(route.getVehiclePlate());
						if (vehicle.getUser().getId().intValue() != user.getId().intValue()) {
							responseMessages.addMessage("Permission denied. You don't own the vehicle with plate " + route.getVehiclePlate());
						}
					} catch (ServiceException e) {
						responseMessages.addMessage("vehiclePlate not found");
					}
				} else {
					responseMessages.addMessage("vehiclePlate is mandatory");
				}
				if (route.getStops() != null) {
					if (route.getStops().getAddress() == null) {
						responseMessages.addMessage("address is mandatory");
					}
				}

				if (responseMessages.getMessage() != null) {
					return responseMessages;
				} else {
					HttpHeaders headers = new HttpHeaders();
					headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
					HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
					RestTemplate template = new RestTemplate();

					ObjectMapper mapper = new ObjectMapper();
					JsonFactory factory = mapper.getJsonFactory();
					JsonParser jsonParser;
					ResponseEntity<String> entity;
					int index = 1, count = 0;
					List<RouteSegment> segments = new ArrayList<>();

					// Departure segment
					RouteSegment depSegment = new RouteSegment();
					depSegment.setStartAddress(route.getStartingPoint());

					entity = template.exchange("https://maps.googleapis.com/maps/api/geocode/json?address=" + route.getStartingPoint() + "&sensor=false", HttpMethod.GET,  requestEntity, String.class);
					jsonParser = factory.createJsonParser(entity.getBody());
					this.geocode(jsonParser, depSegment, RouteSegment.DEPARTURE);

					// Arrival segment
					RouteSegment arrSegment = new RouteSegment();
					arrSegment.setEndAddress(route.getDestinationPoint());

					entity = template.exchange("https://maps.googleapis.com/maps/api/geocode/json?address=" + route.getDestinationPoint() + "&sensor=false", HttpMethod.GET,  requestEntity, String.class);
					jsonParser = factory.createJsonParser(entity.getBody());
					this.geocode(jsonParser, arrSegment, RouteSegment.ARRIVAL);

					// Stops
					if (route.getStops() != null) {
						RouteSegment stopSegment;
						for (String address: route.getStops().getAddress()) {
							if (count == 0) {
								depSegment.setEndAddress(address);
								depSegment.setIndex(index++);

								entity = template.exchange("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&sensor=false", HttpMethod.GET,  requestEntity, String.class);
								jsonParser = factory.createJsonParser(entity.getBody());
								this.geocode(jsonParser, depSegment, RouteSegment.ARRIVAL);
								segments.add(depSegment);
							}
							if (count > 0 && count < route.getStops().getAddress().size()) {
								stopSegment =  new RouteSegment();
								stopSegment.setIndex(index++);
								stopSegment.setStartAddress(route.getStops().getAddress().get(count - 1));

								entity = template.exchange("https://maps.googleapis.com/maps/api/geocode/json?address=" + stopSegment.getStartAddress() + "&sensor=false", HttpMethod.GET,  requestEntity, String.class);
								jsonParser = factory.createJsonParser(entity.getBody());
								this.geocode(jsonParser, stopSegment, RouteSegment.DEPARTURE);

								stopSegment.setEndAddress(address);

								entity = template.exchange("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&sensor=false", HttpMethod.GET,  requestEntity, String.class);
								jsonParser = factory.createJsonParser(entity.getBody());
								this.geocode(jsonParser, stopSegment, RouteSegment.ARRIVAL);

								segments.add(stopSegment);
							}
							if (count == route.getStops().getAddress().size() - 1) {
								arrSegment.setStartAddress(address);
								arrSegment.setIndex(index);

								entity = template.exchange("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&sensor=false", HttpMethod.GET,  requestEntity, String.class);
								jsonParser = factory.createJsonParser(entity.getBody());
								this.geocode(jsonParser, arrSegment, RouteSegment.DEPARTURE);
								segments.add(arrSegment);
							}
							count++;
						}

					}

					// Path
					String restUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=" + route.getStartingPoint() + "&destination=" + route.getDestinationPoint();
					if (segments.size() > 0) {
						restUrl += "&waypoints=";
						for (int i=0, j=segments.size() - 1; i<j; i++) {
							restUrl += segments.get(i).getEndAddress() + "|";
						}
						restUrl = restUrl.substring(0, restUrl.length() - 1);
					}
					restUrl += "&sensor=false";

					entity = template.exchange(restUrl, HttpMethod.GET,  requestEntity, String.class);
					String test = entity.getBody().substring(entity.getBody().indexOf("overview_polyline"), entity.getBody().length()-1);
					test = test.substring(test.indexOf("points"), test.length()-1);
					test = test.substring(11, test.length()-1);
					test = test.substring(0, test.indexOf("},")-1).trim();
					test = test.substring(0, test.length()-1).trim();
					test = test.replace("\\\\", "\\");
//					System.out.println(test);

					ArrayList<Location> pathCoords = PolylineDecoder.decodePoly(test);

					WKTReader fromText = new WKTReader();
					String lineString = "LINESTRING(";
					for (Location location: pathCoords) {
						lineString += location.getLatitude() + " " + location.getLongitude() + ",";
					}
					lineString = lineString.substring(0, lineString.length() - 1);
					lineString += ")";

					Geometry path = fromText.read(lineString);
					path.setSRID(4326);


					// Route
					Route dataRoute = new Route();
					dataRoute.setStartingPoint(route.getStartingPoint());
					dataRoute.setDestinationPoint(route.getDestinationPoint());
					dataRoute.setRouteCreationDate(new Date());
					dataRoute.setRouteDate(route.getRouteDate());
					dataRoute.setRouteEndDate(route.getRouteDate());
					dataRoute.setRouteTime(route.getRouteTime());
					dataRoute.setTotalSeats(route.getTotalSeats());
					dataRoute.setVehicle(vehicle);
					if (route.isEnabled() != null) {
						dataRoute.setEnabled(route.isEnabled());
					} else {
						dataRoute.setEnabled(true);
					}
					if (route.isObjectTransportAllowed() != null) {
						dataRoute.setObjectTransportAllowed(route.isObjectTransportAllowed());
					} else {
						dataRoute.setObjectTransportAllowed(true);
					}
					if (route.isSmokingAllowed() != null) {
						dataRoute.setSmokingAllowed(route.isSmokingAllowed());
					} else {
						dataRoute.setSmokingAllowed(false);
					}
					if (route.isAmeaAccessible() != null) {
						dataRoute.setAmeaAccessible(route.isAmeaAccessible());
					} else {
						dataRoute.setAmeaAccessible(true);
					}
					if (route.getTags() != null) {
						dataRoute.setTags(route.getTags());
					}
					if (route.getComment() != null) {
						dataRoute.setComment(route.getComment());
					}
					if (route.getContributionAmount() != null) {
						dataRoute.setContributionAmount(route.getContributionAmount());
					} else {
						dataRoute.setContributionAmount(new BigDecimal(0.0));
					}
					if (route.isRecurring() != null) {
						dataRoute.setRecurring(route.isRecurring());
						if (route.isRecurring()) {
							if (route.getDays() != null) {
								dataRoute.setDays(route.getDays());
							}
							if (route.getRouteEndDate() != null) {
								dataRoute.setRouteEndDate(route.getRouteEndDate());
							}
						}
					} else {
						dataRoute.setRecurring(false);
					}
					dataRoute.setUser(user);

					// Segments
					dataRoute.setRouteSegments(new HashSet<RouteSegment>());
					for (RouteSegment segment : segments) {
						segment.setRoute(dataRoute);
						dataRoute.getRouteSegments().add(segment);
					}

					// Route Path
					dataRoute.setRoutePath(new RoutePath((LineString) path, dataRoute));

					createRouteService.saveRoute(dataRoute);

					responseMessages.addMessage("Route is successfully created");
				}
			} catch (AuthenticationException e) {
				responseMessages.addMessage(e.getMessage());
			} catch (IOException e) {
				responseMessages.addMessage("Route persist has failed");
				responseMessages.addMessage(e.getMessage());
				e.printStackTrace();
			} catch (com.vividsolutions.jts.io.ParseException e) {
				responseMessages.addMessage("Route persist has failed");
				responseMessages.addMessage("Cannot geocode the addresses");
				responseMessages.addMessage(e.getMessage());
				e.printStackTrace();
			} catch (ServiceException e) {
				responseMessages.addMessage("Route persist has failed");
				responseMessages.addMessage(e.getMessage());
				e.printStackTrace();
			}
		}

		return responseMessages;
	}

	@RequestMapping(value = "/services/route", method = RequestMethod.PUT, headers="Accept=application/xml, application/json")
	public @ResponseBody ResponseMessages updateRoute(String code, @RequestBody ServiceRoute route, HttpServletRequest request) {
		ResponseMessages responseMessages = new ResponseMessages();

		String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			responseMessages.addMessage("This method needs authorization");
			return responseMessages;
		} else {
			try {
				String[] credentials = Utils.decodeHeader(authorization);
				User user = userLoginService.wsLogin(credentials[0], credentials[1]);

				if (user == null) {
					throw new AuthenticationException("Invalid Username/Password");
				}


				Vehicle vehicle = null;

				if (code == null) {
					responseMessages.addMessage("Parameter 'code' is missing");
					return responseMessages;
				}

				if (route.getVehiclePlate() != null) {
					try {
						vehicle = editVehicleService.getVehicleByPlate(route.getVehiclePlate());
						if (vehicle.getUser().getId().intValue() != user.getId().intValue()) {
							responseMessages.addMessage("Permission denied. You don't own the vehicle with plate " + route.getVehiclePlate());
						}
					} catch (ServiceException e) {
						responseMessages.addMessage("vehiclePlate not found");
					}
				}

				if (route.getStartingPoint() == null && route.getDestinationPoint() != null) {
					responseMessages.addMessage("startingPoint is mandatory");
				}
				if (route.getDestinationPoint() == null && route.getStartingPoint() != null) {
					responseMessages.addMessage("destinationPoint is mandatory");
				}

				if (responseMessages.getMessage() != null) {
					return responseMessages;
				} else {
					Route dataRoute = editRouteService.getRouteByCode(code);

					if (dataRoute.getUser().getId().intValue() != user.getId().intValue()) {
						responseMessages.addMessage("Permission Denied. You don't own the selected Route");
						return responseMessages;
					}

					HttpHeaders headers = new HttpHeaders();
					headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
					HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
					RestTemplate template = new RestTemplate();

					ObjectMapper mapper = new ObjectMapper();
					JsonFactory factory = mapper.getJsonFactory();
					JsonParser jsonParser;
					ResponseEntity<String> entity;
					int index = 1, count = 0;
					List<RouteSegment> segments = new ArrayList<>();

					if (route.getStartingPoint() != null && route.getDestinationPoint() != null) {
						// Departure segment
						RouteSegment depSegment = new RouteSegment();
						depSegment.setStartAddress(route.getStartingPoint());

						entity = template.exchange("https://maps.googleapis.com/maps/api/geocode/json?address=" + route.getStartingPoint() + "&sensor=false", HttpMethod.GET,  requestEntity, String.class);
						jsonParser = factory.createJsonParser(entity.getBody());
						this.geocode(jsonParser, depSegment, RouteSegment.DEPARTURE);

						// Arrival segment
						RouteSegment arrSegment = new RouteSegment();
						arrSegment.setEndAddress(route.getDestinationPoint());

						entity = template.exchange("https://maps.googleapis.com/maps/api/geocode/json?address=" + route.getDestinationPoint() + "&sensor=false", HttpMethod.GET,  requestEntity, String.class);
						jsonParser = factory.createJsonParser(entity.getBody());
						this.geocode(jsonParser, arrSegment, RouteSegment.ARRIVAL);

						// Stops
						if (route.getStops() != null) {
							RouteSegment stopSegment;
							for (String address: route.getStops().getAddress()) {
								if (count == 0) {
									depSegment.setEndAddress(address);
									depSegment.setIndex(index++);

									entity = template.exchange("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&sensor=false", HttpMethod.GET,  requestEntity, String.class);
									jsonParser = factory.createJsonParser(entity.getBody());
									this.geocode(jsonParser, depSegment, RouteSegment.ARRIVAL);
									segments.add(depSegment);
								}
								if (count > 0 && count < route.getStops().getAddress().size()) {
									stopSegment =  new RouteSegment();
									stopSegment.setIndex(index++);
									stopSegment.setStartAddress(route.getStops().getAddress().get(count - 1));

									entity = template.exchange("https://maps.googleapis.com/maps/api/geocode/json?address=" + stopSegment.getStartAddress() + "&sensor=false", HttpMethod.GET,  requestEntity, String.class);
									jsonParser = factory.createJsonParser(entity.getBody());
									this.geocode(jsonParser, stopSegment, RouteSegment.DEPARTURE);

									stopSegment.setEndAddress(address);

									entity = template.exchange("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&sensor=false", HttpMethod.GET,  requestEntity, String.class);
									jsonParser = factory.createJsonParser(entity.getBody());
									this.geocode(jsonParser, stopSegment, RouteSegment.ARRIVAL);

									segments.add(stopSegment);
								}
								if (count == route.getStops().getAddress().size() - 1) {
									arrSegment.setStartAddress(address);
									arrSegment.setIndex(index);

									entity = template.exchange("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&sensor=false", HttpMethod.GET,  requestEntity, String.class);
									jsonParser = factory.createJsonParser(entity.getBody());
									this.geocode(jsonParser, arrSegment, RouteSegment.DEPARTURE);
									segments.add(arrSegment);
								}
								count++;
							}
						}

						// Path
						String restUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=" + route.getStartingPoint() + "&destination=" + route.getDestinationPoint();
						if (segments.size() > 0) {
							restUrl += "&waypoints=";
							for (int i=0, j=segments.size() - 1; i<j; i++) {
								restUrl += segments.get(i).getEndAddress() + "|";
							}
							restUrl = restUrl.substring(0, restUrl.length() - 1);
						}
						restUrl += "&sensor=false";

						entity = template.exchange(restUrl, HttpMethod.GET,  requestEntity, String.class);
						String test = entity.getBody().substring(entity.getBody().indexOf("overview_polyline"), entity.getBody().length()-1);
						test = test.substring(test.indexOf("points"), test.length()-1);
						test = test.substring(11, test.length()-1);
						test = test.substring(0, test.indexOf("},")-1).trim();
						test = test.substring(0, test.length()-1).trim();
						test = test.replace("\\\\", "\\");
						//					System.out.println(test);

						ArrayList<Location> pathCoords = PolylineDecoder.decodePoly(test);

						WKTReader fromText = new WKTReader();
						String lineString = "LINESTRING(";
						for (Location location: pathCoords) {
							lineString += location.getLatitude() + " " + location.getLongitude() + ",";
						}
						lineString = lineString.substring(0, lineString.length() - 1);
						lineString += ")";

						Geometry path = fromText.read(lineString);
						path.setSRID(4326);

						if (route.getStartingPoint() != null) {
							dataRoute.setStartingPoint(route.getStartingPoint());
						}
						if (route.getDestinationPoint() != null) {
							dataRoute.setDestinationPoint(route.getDestinationPoint());
						}
						if (route.getRouteDate() != null) {
							dataRoute.setRouteDate(route.getRouteDate());
						}
						if (route.getRouteTime() != null) {
							dataRoute.setRouteTime(route.getRouteTime());
						}
						if (route.getTotalSeats() != null) {
							dataRoute.setTotalSeats(route.getTotalSeats());
						}
						if (route.isEnabled() != null) {
							dataRoute.setEnabled(route.isEnabled());
						}
						if (vehicle != null) {
							dataRoute.setVehicle(vehicle);
						}
						if (route.isObjectTransportAllowed() != null) {
							dataRoute.setObjectTransportAllowed(route.isObjectTransportAllowed());
						}
						if (route.isSmokingAllowed() != null) {
							dataRoute.setSmokingAllowed(route.isSmokingAllowed());
						}
						if (route.isAmeaAccessible() != null) {
							dataRoute.setAmeaAccessible(route.isAmeaAccessible());
						}
						if (route.getTags() != null) {
							dataRoute.setTags(route.getTags());
						}
						if (route.getComment() != null) {
							dataRoute.setComment(route.getComment());
						}
						if (route.getContributionAmount() != null) {
							dataRoute.setContributionAmount(route.getContributionAmount());
						}
						if (route.isRecurring() != null) {
							dataRoute.setRecurring(route.isRecurring());
							if (route.isRecurring()) {
								if (route.getDays() != null) {
									dataRoute.setDays(route.getDays());
								}
								if (route.getRouteEndDate() != null) {
									dataRoute.setRouteEndDate(route.getRouteEndDate());
								}
							}
						}

						// Segments
//						for (RouteSegment segment: dataRoute.getRouteSegments()) {
//							segment.setRoute(null);
//						}
						dataRoute.getRouteSegments().clear();
						for (RouteSegment segment : segments) {
							segment.setRoute(dataRoute);
							dataRoute.getRouteSegments().add(segment);
						}

						// Route Path
						dataRoute.getRoutePath().setPath((LineString) path);

						editRouteService.saveRoute(dataRoute);

						responseMessages.addMessage("Route is successfully updated");
					}
				}
			} catch (AuthenticationException e) {
				responseMessages.addMessage(e.getMessage());
			} catch (ServiceException e) {
				responseMessages.addMessage("Route update has failed");
				responseMessages.addMessage(e.getMessage());
				e.printStackTrace();
			} catch (com.vividsolutions.jts.io.ParseException e) {
				responseMessages.addMessage("Route update has failed");
				responseMessages.addMessage("Cannot geocode the addresses");
				responseMessages.addMessage(e.getMessage());
				e.printStackTrace();
			} catch (JsonParseException e) {
				responseMessages.addMessage("Route update has failed");
				responseMessages.addMessage("Cannot geocode the addresses");
				responseMessages.addMessage(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				responseMessages.addMessage("Route update has failed");
				responseMessages.addMessage(e.getMessage());
				e.printStackTrace();
			}
		}

		return responseMessages;
	}

	@RequestMapping(value = "/services/route", method = RequestMethod.DELETE)
	public @ResponseBody ResponseMessages deleteRoute(String code, HttpServletRequest request) {
		ResponseMessages responseMessages = new ResponseMessages();

		String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			responseMessages.addMessage("This method needs authorization");
			return responseMessages;
		} else {
			try {
				String[] credentials = Utils.decodeHeader(authorization);
				User user = userLoginService.wsLogin(credentials[0], credentials[1]);

				if (user == null) {
					throw new AuthenticationException("Invalid Username/Password");
				}

				if (code == null) {
					responseMessages.addMessage("Parameter 'code' is missing");
					return responseMessages;
				}

				Route dataRoute = editRouteService.getRouteByCode(code);

				if (dataRoute.getUser().getId().intValue() != user.getId().intValue()) {
					responseMessages.addMessage("Permission Denied. You don't own the selected Route");
					return responseMessages;
				}

				listRoutesService.deleteRoute(dataRoute);

				responseMessages.addMessage("Route is successfully deleted");
			} catch (AuthenticationException e) {
				responseMessages.addMessage(e.getMessage());
			} catch (ServiceException e) {
				responseMessages.addMessage("Route delete has failed");
				e.printStackTrace();
			}
		}

		return responseMessages;
	}

	private void geocode(JsonParser jp, RouteSegment segment, int type) throws IOException, com.vividsolutions.jts.io.ParseException {
		WKTReader fromText = new WKTReader();
		JsonToken current = jp.nextToken();

		if (current != JsonToken.START_OBJECT) {
			System.out.println("Error: root should be object: quiting.");
		} else {
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				String fieldName = jp.getCurrentName();
//				System.out.println(fieldName);
				current = jp.nextToken();
				if (fieldName.equals("results")) {
					if (current == JsonToken.START_ARRAY) {
						while (jp.nextToken() != JsonToken.END_ARRAY) {
							JsonNode node = jp.readValueAsTree();

							switch (type) {
								case RouteSegment.DEPARTURE:
									segment.setStartLat(node.get("geometry").get("location").get("lat").asText());
									segment.setStartLng(node.get("geometry").get("location").get("lng").asText());
//									System.out.println("LAT: " + segment.getStartLat());
//									System.out.println("LNG: " + segment.getStartLng());
									break;
								case RouteSegment.ARRIVAL:
									segment.setEndLat(node.get("geometry").get("location").get("lat").asText());
									segment.setEndLng(node.get("geometry").get("location").get("lng").asText());
//									System.out.println("LAT: " + segment.getEndLat());
//									System.out.println("LNG: " + segment.getEndLng());
									break;
							}
						}
					} else {
						jp.skipChildren();
					}
				} else {
					jp.skipChildren();
				}
			}
		}

	}

}
