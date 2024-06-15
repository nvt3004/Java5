package com.fpoly.thainv.controllers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.thainv.entities.AttributeProduct;
import com.fpoly.thainv.entities.CartProduct;
import com.fpoly.thainv.entities.Coupons;
import com.fpoly.thainv.entities.OrderDetails;
import com.fpoly.thainv.entities.OrderStatus;
import com.fpoly.thainv.entities.Orders;
import com.fpoly.thainv.entities.Products;
import com.fpoly.thainv.entities.ShoppingCarts;
import com.fpoly.thainv.entities.Users;
import com.fpoly.thainv.tholh.JPA.AttributeProductJPATho;
import com.fpoly.thainv.tholh.JPA.CartJPA;
import com.fpoly.thainv.tholh.JPA.CartProductJPA;
import com.fpoly.thainv.tholh.JPA.CouponJPA;
import com.fpoly.thainv.tholh.JPA.OrderDetailJPA;
import com.fpoly.thainv.tholh.JPA.OrderJPA;
import com.fpoly.thainv.tholh.JPA.OrderStatusJPA;
import com.fpoly.thainv.tholh.JPA.ProductJPATho;
import com.fpoly.thainv.tholh.JPA.UserJPA;
import com.fpoly.thainv.tholh.service.CartService;
import com.fpoly.thainv.untils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CartController {

    @Autowired
    HttpSession session;

    @Autowired
    ProductJPATho pJPA;

    @Autowired
    CartJPA cartJPA;

    @Autowired
    UserJPA userJPA;

    @Autowired
    CartProductJPA cartProductJPA;

    @Autowired
    OrderStatusJPA orderStatusJPA;

    @Autowired
    OrderJPA orderJPA;

    @Autowired
    OrderDetailJPA orderDetailJPA;

    @Autowired
    CouponJPA couponJPA;

    @Autowired
    AttributeProductJPATho attributeProductJPA;

    @Autowired
    CartService cartService;

    @Autowired
    HttpServletRequest req;

    @Autowired
    HttpServletResponse resp;

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    public ShoppingCarts getShoppingCarts() {
        String email = CookieUtil.get(req, "email");
        ShoppingCarts shoppingCart = cartJPA.findByUserEmail(email);
        Optional<Users> user = userJPA.findUserByEmail(email);
        if (user.isPresent() && shoppingCart == null) {
            shoppingCart = new ShoppingCarts();
            shoppingCart.setUsers(user.get());
            cartJPA.save(shoppingCart);
        }
        return shoppingCart;
    }

    @Transactional
    @RequestMapping("/shoping-cart")
    public String shopingCart(Model model, @RequestParam("cartID") Optional<String> cartID, @RequestParam("coupon") Optional<String> coupon) {

        ShoppingCarts shoppingCart = getShoppingCarts();
        String email = CookieUtil.get(req, "email");

        Optional<Users> user = userJPA.findUserByEmail(email);
        if (user.isPresent()) {
            model.addAttribute("address", user.get().getAddress());
        }

        if (shoppingCart != null) {
            session.setAttribute("cart", shoppingCart);

            model.addAttribute("cart", shoppingCart);
            model.addAttribute("cartService", cartService);
            model.addAttribute("total", cartService.getTotal(shoppingCart.getCartId()));

            model.addAttribute("loginError", false);

            return "Admin/Client/shoping-cart";
        } else {
            model.addAttribute("loginError", true);
            return "redirect:/home";
        }

    }

    @PostMapping("/add-to-cart")
    public String postAdd(@RequestParam("prodID") String prodID, @RequestParam("size") Optional<Integer> sizeID, @RequestParam("color") Optional<Integer> colorID,
            @RequestParam("num-product") Optional<String> quantity) {

        Optional<Products> prod = pJPA.findById(prodID);
        ShoppingCarts shoppingCarts = this.getShoppingCarts();
        if (shoppingCarts == null) {
            return "redirect:/home";
        }
        Set<CartProduct> cartProducts = shoppingCarts.getCartProducts();

        String email = CookieUtil.get(req, "email");
        Optional<Users> user = userJPA.findUserByEmail(email);
        if (!user.isPresent()) {
            shoppingCarts = new ShoppingCarts();
            shoppingCarts.setUsers(user.get());
            cartJPA.save(shoppingCarts);
        }

        Map<String, Object> response = new HashMap<>();
        if (prod.isPresent()) {
            boolean productExistsInCart = false;

            for (CartProduct cartProduct : cartProducts) {
                if (cartProduct.getProducts().getProductId().equals(prod.get().getProductId())) {
                    if (quantity.isPresent() && quantity.get() != null) {
                        if (Integer.valueOf(quantity.get()) >= 1) {
                            cartProduct.setQuantity(cartProduct.getQuantity() + Integer.valueOf(quantity.get()));
                        }
                    }
                    // cartProduct.setQuantity(cartProduct.getQuantity() + 1);
                    if (sizeID.isPresent() && sizeID.get() != null) {
                        Optional<AttributeProduct> size = attributeProductJPA.findById(String.valueOf(sizeID.get()));
                        if (size.isPresent()) {
                            cartProduct.setSize(size.get());
                        }
                    }
                    if (colorID.isPresent() && colorID.get() != null) {
                        Optional<AttributeProduct> color = attributeProductJPA.findById(String.valueOf(colorID.get()));
                        if (color.isPresent()) {
                            cartProduct.setColor(color.get());
                        }
                    }

                    cartProductJPA.save(cartProduct);
                    productExistsInCart = true;
                    break;
                }
            }

            if (!productExistsInCart) {
                CartProduct newCartProduct = new CartProduct();
                newCartProduct.setProducts(prod.get());
                newCartProduct.setShoppingCarts(shoppingCarts);
                // newCartProduct.setQuantity(1);
                if (quantity.isPresent() && quantity.get() != null) {
                    if (Integer.valueOf(quantity.get()) >= 1) {
                        newCartProduct.setQuantity(Integer.valueOf(quantity.get()));
                    }
                }
                if (sizeID.isPresent()) {
                    Optional<AttributeProduct> size = attributeProductJPA.findById(String.valueOf(sizeID.get()));
                    if (size.isPresent()) {
                        newCartProduct.setSize(size.get());
                    }
                }
                if (colorID.isPresent()) {
                    Optional<AttributeProduct> color = attributeProductJPA.findById(String.valueOf(colorID.get()));
                    if (color.isPresent()) {
                        newCartProduct.setColor(color.get());
                    }
                }

                cartProductJPA.save(newCartProduct);
            }
        }

        return "redirect:/home";
    }

    @RequestMapping("/remove")
    public String remove(@RequestParam("cartProdID") String cartProdID) {

        Optional<CartProduct> cartProduct = cartProductJPA.findById(cartProdID);
        if (cartProduct.isPresent()) {
            cartProductJPA.delete(cartProduct.get());
        }

        return "redirect:/shoping-cart";
    }

    @RequestMapping("/update-quantity")
    public String updateQuantity(@RequestParam("cartProdID") String cartID, @RequestParam(name = "quantity", defaultValue = "1") int quantity) {

        if (quantity >= 1) {
            cartService.update(Integer.valueOf(cartID), quantity);
        }

        return "redirect:/shoping-cart";
    }

    @RequestMapping("/check-out")
    @Transactional
    public String checkOut(@RequestParam("cartID") String cartID) {
        boolean checkOrder = false;
        boolean checkOrderDetail = false;

        Orders order = new Orders();
        String email = CookieUtil.get(req, "email");
        Optional<OrderStatus> orderStatus = orderStatusJPA.findById("1");
        Optional<Coupons> coupon = couponJPA.findById("1");

        Date date = new Date();

        Optional<Users> user = userJPA.findUserByEmail(email);
        if (user.isPresent()) {
            order.setUsers(user.get());
            order.setAddresses(user.get().getAddress());
        }

        order.setOrderDate(date);
        order.setOrderStatus(orderStatus.orElse(null));
        order.setCoupons(coupon.orElse(null));

        ShoppingCarts shoppingCart = this.getShoppingCarts();

        BigDecimal totalAmount = new BigDecimal(cartService.getTotal(shoppingCart.getCartId()) + 2);
        order.setTotalAmount(totalAmount);

        // Save order to db
        try {
            orderJPA.save(order);
            checkOrder = true;
        } catch (Exception e) {
            logger.error("Error saving order: ", e);
            throw e; // Re-throw to mark the transaction for rollback
        }

        // Add values to orderDetails
        for (CartProduct cartProduct : shoppingCart.getCartProducts()) {
            Products prod = cartProduct.getProducts();

            OrderDetails orderDetail = new OrderDetails();
            orderDetail.setOrders(order);
            orderDetail.setProducts(prod);
            orderDetail.setUnitPrice(prod.getRetailPrice());
            orderDetail.setQuantity(cartProduct.getQuantity());
            orderDetail.setSize(cartProduct.getSize().getAttributes());
            orderDetail.setColor(cartProduct.getColor().getAttributes());

            try {
                orderDetailJPA.save(orderDetail);
                checkOrderDetail = true;
            } catch (Exception e) {
                logger.error("Error saving order detail: ", e);
                throw e; // Re-throw to mark the transaction for rollback
            }

            if (checkOrderDetail) {
                Optional<AttributeProduct> attrProdOptional = attributeProductJPA.findById(String.valueOf(cartProduct.getSize().getAttrPrdId()));
                if (attrProdOptional.isPresent()) {
                    AttributeProduct attrProd = attrProdOptional.get();
                    attrProd.setQuantity(attrProd.getQuantity() - cartProduct.getQuantity());
                    try {
                        attributeProductJPA.save(attrProd);
                    } catch (Exception e) {
                        logger.error("Error updating attribute product: ", e);
                        throw e; // Re-throw to mark the transaction for rollback
                    }
                }
            }
        }

        if (checkOrderDetail) {
            try {
                cartProductJPA.deleteAll(shoppingCart.getCartProducts());
            } catch (Exception e) {
                logger.error("Error deleting cart products: ", e);
                throw e; // Re-throw to mark the transaction for rollback
            }
        }

        return "redirect:/shoping-cart";
    }

    @ModelAttribute("orderDetailList")
    public List<OrderDetails> getOrderDetailList() {

        List<OrderDetails> orderDetailList = orderDetailJPA.findAll();

        return orderDetailList;
    }

    @ModelAttribute("orderList")
    public List<Orders> getOrderList() {

        List<Orders> orderList = orderJPA.findAll();

        return orderList;
    }

    @RequestMapping("/order-details")
    public String getOrderDetail(@RequestParam("orderID") String orderID, Model model) {

        Optional<Orders> orderOptional = orderJPA.findById(orderID);
        if (orderOptional.isPresent()) {
            model.addAttribute("order", orderOptional.get());
        }

        return "Admin/Client/order-details";
    }

    @PostMapping("/cancel")
    public String cancel(@RequestParam("orderID") String orderID, @RequestParam("path") String path) {

        String uri = req.getRequestURI();
        System.out.println("URI: " + uri);

        Optional<OrderStatus> status = orderStatusJPA.findById("5");

        Optional<Orders> orderOptional = orderJPA.findById(orderID);
        if (orderOptional.isPresent()) {
            Orders order = orderOptional.get();
            order.setOrderStatus(status.get());

            orderJPA.save(order);
        }

        if ("order-details".equals(path)) {
            return "redirect:/order-details?orderID=" + orderID;
        } else {
            return "redirect:/shoping-cart";
        }

    }
}
