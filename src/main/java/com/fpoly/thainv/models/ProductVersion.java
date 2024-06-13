package com.fpoly.thainv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVersion {
	private int idSize;
	private int idColor;
	private boolean old;
}
