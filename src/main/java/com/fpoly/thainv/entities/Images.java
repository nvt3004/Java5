package com.fpoly.thainv.entities;
// Generated Jun 6, 2024, 12:44:43 AM by Hibernate Tools 4.3.6.Final

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Images generated by hbm2java
 */
@Entity
@Table(name = "images", schema = "dbo", catalog = "TMDT")
public class Images implements java.io.Serializable {

	private Integer imgId;
	private Feedbacks feedbacks;
	private Products products;
	private String imgUrl;

	public Images() {
	}

	public Images(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Images(Feedbacks feedbacks, Products products, String imgUrl) {
		this.feedbacks = feedbacks;
		this.products = products;
		this.imgUrl = imgUrl;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "img_id", unique = true, nullable = false)
	public Integer getImgId() {
		return this.imgId;
	}

	public void setImgId(Integer imgId) {
		this.imgId = imgId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feedback_id")
	public Feedbacks getFeedbacks() {
		return this.feedbacks;
	}

	public void setFeedbacks(Feedbacks feedbacks) {
		this.feedbacks = feedbacks;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	public Products getProducts() {
		return this.products;
	}

	public void setProducts(Products products) {
		this.products = products;
	}

	@Column(name = "img_url", nullable = false)
	public String getImgUrl() {
		return this.imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

}