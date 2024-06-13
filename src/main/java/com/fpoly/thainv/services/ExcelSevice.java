package com.fpoly.thainv.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fpoly.thainv.entities.AttributeProduct;
import com.fpoly.thainv.entities.Products;
import com.fpoly.thainv.jpa.AttributeProductJPA;
import com.fpoly.thainv.jpa.ProductJPA;
import com.fpoly.thainv.models.AttributeProductModel;

@Service
public class ExcelSevice {

	@Autowired
	public ProductJPA productJPA;

	@Autowired
	AttributeProductJPA attributeProductJPA;

	public void saveProductsFromExcelFile(MultipartFile file) throws IOException {
		List<Products> products = new ArrayList<>();

		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();

		while (rows.hasNext()) {
			Row currentRow = rows.next();
			if (currentRow.getRowNum() == 0) { // Skip header row
				continue;
			}

			Products product = new Products();
			// product.setName(currentRow.getCell(0).getStringCellValue());
			// product.setPrice(currentRow.getCell(1).getNumericCellValue());

			products.add(product);
		}

		workbook.close();

		// productRepository.saveAll(products);
	}


	public byte[] exportProductsToExcel(List<Products> products) throws IOException {
	    Workbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Products");

	    Row headerRow = sheet.createRow(0);
	    headerRow.createCell(0).setCellValue("ID");
	    headerRow.createCell(1).setCellValue("Name");
	    headerRow.createCell(2).setCellValue("Manufacturer");
	    headerRow.createCell(3).setCellValue("Category");
	    headerRow.createCell(4).setCellValue("Retail Price");
	    headerRow.createCell(5).setCellValue("Wholesale Price");
	    headerRow.createCell(6).setCellValue("Import price");
	    headerRow.createCell(7).setCellValue("Description");
	    headerRow.createCell(8).setCellValue("Quantity");

	    int rowNum = 1;
	    for (Products product : products) {
	        Row row = sheet.createRow(rowNum++);
	        row.createCell(0).setCellValue(product.getProductId());
	        row.createCell(1).setCellValue(product.getProductName());
	        row.createCell(2).setCellValue(product.getManufacturers().getManufacturerName());
	        row.createCell(3).setCellValue(product.getCategories().getCategoryName());
	        row.createCell(4).setCellValue(Double.parseDouble(product.getRetailPrice() + ""));
	        row.createCell(5).setCellValue(Double.parseDouble(product.getWholesalePrice() + ""));
	        row.createCell(6).setCellValue(Double.parseDouble(product.getImportPrice() + ""));
	        row.createCell(7).setCellValue(product.getDescription());
	        row.createCell(8).setCellValue(product.getStockQuantity());
	    }

	    rowNum += 3;
	    Row headerRowVersion = sheet.createRow(rowNum);
	    headerRowVersion.createCell(0).setCellValue("Product Id");
	    headerRowVersion.createCell(1).setCellValue("Version");
	    headerRowVersion.createCell(2).setCellValue("Quantity");

	    rowNum++; 

	    for (Products product : products) {
	        for (AttributeProductModel version : getVersionProduct(product.getProductId() + "")) {
	            Row rowVersion = sheet.createRow(rowNum++);
	            rowVersion.createCell(0).setCellValue(product.getProductId());
	            rowVersion.createCell(1).setCellValue(version.getName());
	            rowVersion.createCell(2).setCellValue(version.getQuantity());
	        }
	    }

	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    workbook.write(outputStream);
	    workbook.close();

	    return outputStream.toByteArray();
	}

	public List<AttributeProductModel> getVersionProduct(String productId) {
		if (productId == null || productId.isEmpty()) {
			return new ArrayList<>();
		}

		Optional<Products> pdOptional = productJPA.findById(productId);
		List<AttributeProductModel> versions = new ArrayList<>();

		if (pdOptional.isPresent()) {
			Products pdEntity = pdOptional.get();
			Sort sort = Sort.by("attrPrdId");

			List<AttributeProduct> attributeProducts = attributeProductJPA
					.findAttributeByProductId(pdEntity.getProductId(), sort);

			for (int i = 2; i <= attributeProducts.size(); i += 2) {
				AttributeProduct attributeSize = attributeProducts.get(i - 2);
				AttributeProduct attributeColor = attributeProducts.get(i - 1);

				String name = String.format("%s - %s - %s", pdEntity.getProductName(),
						attributeColor.getAttributes().getValue(), attributeSize.getAttributes().getValue());

				int attributeIdSize = attributeSize.getAttrPrdId();
				int attributeIdColor = attributeColor.getAttrPrdId();
				int idSize = attributeSize.getAttributes().getAttributeId();
				int idColor = attributeColor.getAttributes().getAttributeId();
				int quantity = attributeSize.getQuantity();

				versions.add(
						new AttributeProductModel(attributeIdSize, attributeIdColor, name, idSize, idColor, quantity));
			}
		}

		return versions;
	}
}
