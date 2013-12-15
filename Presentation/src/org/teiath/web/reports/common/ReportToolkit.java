package org.teiath.web.reports.common;

import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;

import java.util.Collection;
import java.util.Date;

public class ReportToolkit {

	public static Report requestCreatedRoutesReport(Date dateFrom, Date dateTo, int reportType) {
		Report report = new Report();
		report.setDateFrom(dateFrom);
		report.setDateTo(dateTo);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("createdRoutes" + findFileExtension(reportType));
		report.setReportLocation("/reports/routes/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("createdRoutesReport.jasper");

		return report;
	}

	public static Report requestCompletedRoutesReport(Date dateFrom, Date dateTo, int reportType) {
		Report report = new Report();
		report.setDateFrom(dateFrom);
		report.setDateTo(dateTo);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("completedRoutes" + findFileExtension(reportType));
		report.setReportLocation("/reports/routes/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("completedRoutesReport.jasper");

		return report;
	}

	public static Report requestCreatedListingsReport(Date dateFrom, Date dateTo, int reportType) {
		Report report = new Report();
		report.setDateFrom(dateFrom);
		report.setDateTo(dateTo);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("createdListings" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("createdListingsReport.jasper");

		return report;
	}

	public static Report requestListingsTransactionTypeReport(int transactionTypeId, int reportType) {
		Report report = new Report();
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("ListingsTransactionTypeReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("ListingsTransactionTypeReport.jasper");
		report.getParameters().put("TRANSACTION_TYPE_ID", transactionTypeId);

		return report;
	}

	public static Report requestListingsProductCategoryReport(int productCategoryId, int reportType) {
		Report report = new Report();
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("ListingsProductCategoryReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("ListingsProductCategoryReport.jasper");
		report.getParameters().put("PRODUCT_CATEGORY_ID", productCategoryId);

		return report;
	}

	public static Report requestOnGoinfActionsReport(Date dateFrom, Date dateTo, int reportType) {
		Report report = new Report();
		report.setDateFrom(dateFrom);
		report.setDateTo(dateTo);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("onGoingActionsReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/actions/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("onGoingActionsReport.jasper");

		return report;
	}

	public static Report requestActionsByCategoryReport(int eventCategoryId, int reportType) {
		Report report = new Report();
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("actionsByCategoryReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/actions/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("actionsByCategoryReport.jasper");
		report.getParameters().put("EVENT_CATEGORY_ID", eventCategoryId);

		return report;
	}

	public static Report requestTransactionsReport(Date dateFrom, Date dateTo, int reportType) {
		Report report = new Report();
		report.setDateFrom(dateFrom);
		report.setDateTo(dateTo);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("TransactionsReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("TransactionsReport.jasper");

		return report;
	}

	public static Report requestIncomingRouteInterestsReport(Collection<RouteInterest> routeInterests, int reportType) {
		Report report = new Report();
		report.setRouteInterests(routeInterests);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("routeInterests" + findFileExtension(reportType));
		report.setReportLocation("/reports/routes/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("incomingRouteInterestsReport.jasper");

		return report;
	}

	public static Report requestSentRouteInterestsReport(Collection<RouteInterest> routeInterests, int reportType) {
		Report report = new Report();
		report.setRouteInterests(routeInterests);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("routeInterests" + findFileExtension(reportType));
		report.setReportLocation("/reports/routes/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("sentInterestsReport.jasper");

		return report;
	}

	public static Report requestRouteHistoryPassengerReport(Collection<RouteInterest> routeInterests, int reportType) {
		Report report = new Report();
		report.setRouteInterests(routeInterests);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("routeHistory" + findFileExtension(reportType));
		report.setReportLocation("/reports/routes/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("routeHistoryPassengerReport.jasper");

		return report;
	}

	public static Report requestRouteHistoryDriverReport(Collection<Route> routes, int reportType) {
		Report report = new Report();
		report.setRoutes(routes);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("routeHistory" + findFileExtension(reportType));
		report.setReportLocation("/reports/routes/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("routeHistoryDriverReport.jasper");

		return report;
	}

	public static Report requestRoutesReport(Collection<Route> routes, int reportType) {
		Report report = new Report();
		report.setRoutes(routes);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("routes" + findFileExtension(reportType));
		report.setReportLocation("/reports/routes/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("myRoutesReport.jasper");

		return report;
	}

	public static Report requestRouteInterestsReport(Collection<RouteInterest> routeInterests, int reportType) {
		Report report = new Report();
		report.setRouteInterests(routeInterests);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("reservations" + findFileExtension(reportType));
		report.setReportLocation("/reports/routes/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("reservationsReport.jasper");

		return report;
	}

	private static String findFileExtension(int reportType) {
		switch (reportType) {
			case ReportType.PDF:
				return ".pdf";
			case ReportType.EXCEL:
				return ".xls";
		}

		return null;
	}
}
