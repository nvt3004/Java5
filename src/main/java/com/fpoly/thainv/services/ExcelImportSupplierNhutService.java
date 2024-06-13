package com.fpoly.thainv.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fpoly.thainv.entities.Addresses;
import com.fpoly.thainv.entities.Suppliers;
import com.fpoly.thainv.jpa.AddressJpa;
import com.fpoly.thainv.jpa.SupplierJPA;

@Service
public class ExcelImportSupplierNhutService {

	@Autowired
	SupplierJPA supplierJPA;
	@Autowired
	AddressJpa addressJPA;

	public void importSuppliersFromExcel(MultipartFile file) throws IOException {
	    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
	        Sheet sheet = workbook.getSheetAt(0);
	        List<Suppliers> suppliers = new ArrayList<>();
	        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bỏ qua hàng tiêu đề
	            Row row = sheet.getRow(i);
	            if (row != null) { // Kiểm tra null trước khi truy cập ô
	                Suppliers supplier = new Suppliers();
	                supplier.setSupplierName(getStringCellValue(row.getCell(1)));
	                supplier.setContactName(getStringCellValue(row.getCell(2)));
	                supplier.setEmail(getStringCellValue(row.getCell(3)));
	                supplier.setPhone(getStringCellValue(row.getCell(4)));
	                Addresses address = new Addresses();
	                address.setAddressLine1(getStringCellValue(row.getCell(5)));
	                address.setAddressLine2(getStringCellValue(row.getCell(6)));
	                address.setCity(getStringCellValue(row.getCell(7)));
	                address.setState(getStringCellValue(row.getCell(8)));
	                address.setCountry(getStringCellValue(row.getCell(9)));
	                address.setPostalCode(getStringCellValue(row.getCell(10)));
	                String isDeletedValue = getStringCellValue(row.getCell(11));
	                supplier.setIsDeleted("Deleted".equals(isDeletedValue));
	                Addresses savedAddress = addressJPA.save(address);
	                supplier.setAddresses(savedAddress);
	                suppliers.add(supplier);
	            }
	        }
	        supplierJPA.saveAll(suppliers);
	    }
	}

	private String getStringCellValue(Cell cell) {
	    if (cell != null) {
	        if (cell.getCellType() == CellType.STRING) {
	            return cell.getStringCellValue();
	        } else if (cell.getCellType() == CellType.NUMERIC) {
	            return String.valueOf((long) cell.getNumericCellValue());
	        } else {
	            return ""; // Xử lý các kiểu dữ liệu khác nếu cần
	        }
	    } else {
return ""; // Xử lý trường hợp ô là null
	    }
	}


}