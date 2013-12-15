package org.teiath.web.reports.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.service.crp.RoutesExcelService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.reports.common.ExcelToolkit;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class CreatedRoutesReport {

	public HSSFWorkbook exportList(RoutesExcelService routesExcelService, Date dateFrom, Date dateTo) {
		HSSFWorkbook wb = new HSSFWorkbook();
		int rowDataIndex = 1;
		int rowIndex = 1;

		HSSFSheet sheet = wb.createSheet("Λίστα Διαδρομών");

		HSSFRow row = sheet.createRow((short) 0);
		row.setHeightInPoints((float) 25);

		HSSFFont font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 9);

		ExcelToolkit excelToolkit = new ExcelToolkit();

		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 0, "A/A", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, false, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit
				.createCell(ExcelToolkit.GENERIC, wb, row, font, 1, "Ημερομηνία Διαδρομής", HSSFCellStyle.ALIGN_CENTER,
						HSSFCellStyle.VERTICAL_CENTER, false, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 2, "Ωρα", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 3, "Αφετηρία", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 4, "Προορισμός", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 5, "Ποσό συνεισοφοράς", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 6, "Οδηγός", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 7, "Συνεπιβάτες", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);

		sheet.setColumnWidth(0, 2000);
		sheet.setColumnWidth(1, 5000);
		sheet.setColumnWidth(2, 10000);
		sheet.setColumnWidth(3, 15000);
		sheet.setColumnWidth(4, 5000);
		sheet.setColumnWidth(5, 5000);
		sheet.setColumnWidth(6, 5000);
		sheet.setColumnWidth(7, 5000);

		font = wb.createFont();
		font.setFontHeightInPoints((short) 9);
		font.setBoldweight(Font.BOLDWEIGHT_NORMAL);

		try {
			Collection<Route> routes = routesExcelService.getCreatedRoutes(dateFrom, dateTo);

			for (Route route : routes) {
				row = sheet.createRow((short) rowDataIndex++);
				excelToolkit
						.createCell(ExcelToolkit.GENERIC, wb, row, font, 0, rowIndex++ + "", HSSFCellStyle.ALIGN_CENTER,
								HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 1,
						new SimpleDateFormat("dd/MM/yyyy").format(route.getRouteDate()), HSSFCellStyle.ALIGN_CENTER,
						HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 2,
						new SimpleDateFormat("H:mm").format(route.getRouteTime()), HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 3, route.getStartingPoint(),
						HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 4, route.getDestinationPoint(),
						HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit
						.createCell(ExcelToolkit.GENERIC, wb, row, font, 5, route.getContributionAmount().toString(),
								HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_CENTER, true, true,
								HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 6, route.getUser().getFullName(),
						HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				Collection<RouteInterest> routeInterests = routesExcelService.getPassengers(route);
				StringBuilder sb = new StringBuilder();
				for (RouteInterest routeInterest : routeInterests) {
					sb.append(routeInterest.getUser().getFullName() + "\n");
				}
				String multilineCellValue = sb.toString();
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 7, multilineCellValue,
						HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		return wb;
	}
}
