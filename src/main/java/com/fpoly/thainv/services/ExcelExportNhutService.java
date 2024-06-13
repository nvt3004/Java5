package com.fpoly.thainv.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.fpoly.thainv.entities.Suppliers;

@Service
public class ExcelExportNhutService {

	public byte[] exportSuppliersToExcelAsByteArray(List<Suppliers> suppliers) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Suppliers");

			// Create header row
			Row headerRow = sheet.createRow(0);
			headerRow.createCell(0).setCellValue("Supplier ID");
			headerRow.createCell(1).setCellValue("Supplier Name");
			headerRow.createCell(2).setCellValue("Contact Name");
			headerRow.createCell(3).setCellValue("Email");
			headerRow.createCell(4).setCellValue("Phone");
			headerRow.createCell(5).setCellValue("Address Line 1");
			headerRow.createCell(6).setCellValue("Address Line 2");
			headerRow.createCell(7).setCellValue("City");
			headerRow.createCell(8).setCellValue("State");
			headerRow.createCell(9).setCellValue("Country");
			headerRow.createCell(10).setCellValue("Postal Code");
			headerRow.createCell(11).setCellValue("Is Deleted");

			// Populate data rows
			int rowIdx = 1;
			for (Suppliers supplier : suppliers) {
				Row row = sheet.createRow(rowIdx++);

				row.createCell(0).setCellValue(supplier.getSupplierId());
				row.createCell(1).setCellValue(supplier.getSupplierName());
				row.createCell(2).setCellValue(supplier.getContactName());
				row.createCell(3).setCellValue(supplier.getEmail());
				row.createCell(4).setCellValue(supplier.getPhone());

				if (supplier.getAddresses() != null) {
					row.createCell(5).setCellValue(supplier.getAddresses().getAddressLine1());
					row.createCell(6).setCellValue(supplier.getAddresses().getAddressLine2());
					row.createCell(7).setCellValue(supplier.getAddresses().getCity());
					row.createCell(8).setCellValue(supplier.getAddresses().getState());
					row.createCell(9).setCellValue(supplier.getAddresses().getCountry());
					row.createCell(10).setCellValue(supplier.getAddresses().getPostalCode());
				}

				row.createCell(11).setCellValue(supplier.getIsDeleted() ? "Deleted" : "Not Deleted");
			}

			workbook.write(out);
			return out.toByteArray();
		}
	}

	public byte[] generateSampleExcelTemplate() throws IOException {
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Supplier_Template");

			// Create header row with field names
			Row headerRow = sheet.createRow(0);
			headerRow.createCell(0).setCellValue("Supplier ID");
			headerRow.createCell(1).setCellValue("Supplier Name");
			headerRow.createCell(2).setCellValue("Contact Name");
			headerRow.createCell(3).setCellValue("Email");
			headerRow.createCell(4).setCellValue("Phone");
			headerRow.createCell(5).setCellValue("Address Line 1");
			headerRow.createCell(6).setCellValue("Address Line 2");
			headerRow.createCell(7).setCellValue("City");
			headerRow.createCell(8).setCellValue("State");
			headerRow.createCell(9).setCellValue("Country");
			headerRow.createCell(10).setCellValue("Postal Code");
			headerRow.createCell(11).setCellValue("Is Deleted");

			// Descriptions start from cell N7 (column 13, row 6)
			int descriptionColumn = 13; // Column N (0-indexed)
			int descriptionRowStart = 6; // Row 7 (0-indexed)
			int rowIdx = descriptionRowStart;

			String[][] descriptions = {
					{ "Supplier ID", "The unique identifier for the supplier (please leave this field blank)" },
					{ "Supplier Name", "The name of the supplier" },
					{ "Contact Name", "The name of the contact person for the supplier" },
					{ "Email", "The email address of the supplier" }, { "Phone", "The phone number of the supplier" },
					{ "Address Line 1", "The first line of the supplier's address" },
					{ "Address Line 2",
							"The second line of the supplier's address " + "(this field can be left blank)" },
					{ "City", "The city of the supplier's address" },
					{ "State", "The state or region of the supplier's address" },
					{ "Country", "The country of the supplier's address" },
					{ "Postal Code", "The postal code of the supplier's address" },
					{ "Is Deleted", "Indicator whether the supplier is marked as deleted (please enter Not Deleted)" } };

			for (String[] description : descriptions) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(descriptionColumn).setCellValue(description[0]);
				row.createCell(descriptionColumn + 1).setCellValue(description[1]);
			}

			workbook.write(out);
			return out.toByteArray();
		}
	}
}