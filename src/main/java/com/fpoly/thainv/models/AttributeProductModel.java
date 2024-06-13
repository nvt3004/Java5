package com.fpoly.thainv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeProductModel {
	private int attributeIdSize;
	private int attributeIdColor;
	private String name;
	private int sizeId;
	private int colorId;
	private int quantity;
}
