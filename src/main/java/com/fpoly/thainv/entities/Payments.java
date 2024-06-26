package com.fpoly.thainv.entities;
// Generated Jun 6, 2024, 12:44:43 AM by Hibernate Tools 4.3.6.Final

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Payments generated by hbm2java
 */
@Entity
@Table(name = "payments", schema = "dbo", catalog = "TMDT")
public class Payments implements java.io.Serializable {

	private Integer paymentId;
	private Orders orders;
	private PaymentMethods paymentMethods;
	private BigDecimal amount;
	private Date paymentDate;

	public Payments() {
	}

	public Payments(BigDecimal amount, Date paymentDate) {
		this.amount = amount;
		this.paymentDate = paymentDate;
	}

	public Payments(Orders orders, PaymentMethods paymentMethods, BigDecimal amount, Date paymentDate) {
		this.orders = orders;
		this.paymentMethods = paymentMethods;
		this.amount = amount;
		this.paymentDate = paymentDate;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "payment_id", unique = true, nullable = false)
	public Integer getPaymentId() {
		return this.paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	public Orders getOrders() {
		return this.orders;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_method_id")
	public PaymentMethods getPaymentMethods() {
		return this.paymentMethods;
	}

	public void setPaymentMethods(PaymentMethods paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	@Column(name = "amount", nullable = false, precision = 10)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "payment_date", nullable = false, length = 10)
	public Date getPaymentDate() {
		return this.paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

}
