package com.fpoly.thainv.controllers;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpoly.thainv.entities.Advertisements;
import com.fpoly.thainv.entities.AttributeProduct;
import com.fpoly.thainv.entities.Images;
import com.fpoly.thainv.entities.Products;
import com.fpoly.thainv.entities.ShoppingCarts;
import com.fpoly.thainv.entities.Users;
import com.fpoly.thainv.jpa.AdvJpa;
import com.fpoly.thainv.jpa.ProductJPA;
import com.fpoly.thainv.jpa.UserJpa;
import com.fpoly.thainv.models.Dashbord;
import com.fpoly.thainv.models.DashbordList;
import com.fpoly.thainv.models.MonthlyTotal;
import com.fpoly.thainv.models.PasswordChangeRequest;
import com.fpoly.thainv.models.User;
import com.fpoly.thainv.services.DashboardService;
import com.fpoly.thainv.services.OrderService;
import com.fpoly.thainv.tholh.JPA.CartJPA;
import com.fpoly.thainv.tholh.service.CartService;
import com.fpoly.thainv.untils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    HttpSession session;

    @Autowired
    UserJpa userJpa;

    @Autowired
    AdvJpa advJpa;

    @Autowired
    ProductJPA pJPA;

    @Autowired
    CartJPA cartJPA;

    @Autowired
    CartService cartService;

    @Autowired
    HttpServletRequest req;

    @Autowired
    HttpServletResponse resp;

    @Autowired
    OrderService orderService;

    @Autowired
    DashboardService dashboardService;

    public ShoppingCarts getShoppingCarts() {
        String email = CookieUtil.get(req, "email");
        if (email ==null || email.equals("")) {
            return null;
        }
        else{
            ShoppingCarts shoppingCart = cartJPA.findByUserEmail(email);
            return shoppingCart;
        }
    }

    @RequestMapping("/home")
    public String home(Model model, @RequestParam(name = "size", required = false, defaultValue = "8") int size) {

        String email = CookieUtil.get(req, "email");
        if (email == null || email.equals("")) {
            model.addAttribute("isLogin", false);
            session.setAttribute("total", 0);
        } else{
            model.addAttribute("isLogin", true  );
        }

        Advertisements advertisementsStartingToday = advJpa.findByStartDate(LocalDate.now());
        if (advertisementsStartingToday != null) {
            // Tách chuỗi ảnh thành mảng
            String[] imgs = advertisementsStartingToday.getImg().split(",");

            System.out.println("Gia tri cua mang = " + imgs.length);
            // Truyền mảng ảnh vào model
            model.addAttribute("imgbn", imgs);
        } else {
            model.addAttribute("imgbn", new String[0]);
        }

        Pageable pageable = PageRequest.of(0, size);
        Page<Products> pList = pJPA.findAll(pageable);
        for (Products p : pList) {
            System.out.println("This is product name list: ");
            System.out.printf("\nProduct name: %s\n", p.getProductName());
        }
        model.addAttribute("pList", pList);

        Set<AttributeProduct> attrbuteProds = new HashSet<>();

        List<AttributeProduct> attributeProductList = new ArrayList<>(attrbuteProds);
        model.addAttribute("attrbuteProdList", attributeProductList);

        model.addAttribute("loadMore",
                ((size + 8 > pList.getTotalElements()) ? size = (int) pList.getTotalElements() : size + 4));

        ShoppingCarts shoppingCart = getShoppingCarts();
        if (shoppingCart != null) {
            model.addAttribute("cart", shoppingCart);
            session.setAttribute("total", cartService.getTotal(shoppingCart.getCartId()));
        }

        return "Admin/Client/index";
    }

    public Boolean distinctValues(Set<AttributeProduct> set, AttributeProduct attrProd) {
        boolean isDistinct = false;

        for (AttributeProduct attributeProduct : set) {
            if (attributeProduct.getAttributes().getValue().equals(attrProd.getAttributes().getValue())) {
                isDistinct = true;
                return isDistinct;
            }
        }

        isDistinct = false;

        return isDistinct;
    }

    @RequestMapping("/show-detail")
    public String showDetail(RedirectAttributes RA, @RequestParam("pID") String pID) {

        RA.addFlashAttribute("check", true);

        Optional<Products> pDetail = pJPA.findById(pID);
        if (pDetail.isPresent()) {
            RA.addFlashAttribute("pDetail", pDetail.get());
        }

        return "redirect:/home";
    }

    @RequestMapping("/about")
    public String about() {
        return "Admin/Client/about";
    }

    @RequestMapping("/blog")
    public String blog() {
        return "Admin/Client/blog";
    }

    @RequestMapping("/blog-detail")
    public String blogDetail() {
        return "Admin/Client/blog-detail";
    }

    @RequestMapping("/contact")
    public String contact() {
        return "Admin/Client/contact";
    }

    @RequestMapping("/favourite-products")
    public String favouriteProducts() {
        return "Admin/Client/favourite-products";
    }

    @RequestMapping("/product")
    public String product(Model model, @RequestParam(name = "size", required = false, defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(0, size);
        Page<Products> pList = pJPA.findAll(pageable);
        for (Products p : pList) {
            System.out.println("This is product name list: ");
            System.out.printf("\nProduct name: %s\n", p.getProductName());
        }
        model.addAttribute("pList", pList);

        for (Products p : pList) {
            for (Images i : p.getImageses()) {
                System.out.printf("\nimage: %s\n", i.getImgUrl());
            }
        }
        model.addAttribute("loadMore",
                ((size + 8 > pList.getTotalElements()) ? size += pList.getTotalPages() : size + 12));

        return "Admin/Client/product";
    }

    @RequestMapping("/product-detail")
    public String productDetail(Model model, @RequestParam("prodDetailID") String prodDetailID) {

        Optional<Products> prod = pJPA.findById(prodDetailID);
        if (prod.isPresent()) {
            model.addAttribute("productDetail", prod.get());
        }

        return "Admin/Client/product-detail";
    }

    // @RequestMapping("/shoping-cart")
    // public String shopingCart() {
    // return "Admin/Client/shoping-cart";
    // }
    @RequestMapping("/app-ecommerce-dashboard")
    public String dashboard(Model model) {
        List<Dashbord> orders = orderService.getTop10RecentOrders();
        model.addAttribute("orders", orders);

        Map<YearMonth, Integer> monthlyTotals = dashboardService.getTotalAmountPerMonth();
        model.addAttribute("jsonChartData", monthlyTotals);

        // In ra console
        for (Map.Entry<YearMonth, Integer> entry : monthlyTotals.entrySet()) {
            System.out.println("Year-Month: " + entry.getKey());
            System.out.println("Total Amount: " + entry.getValue());
        }

        return "Admin/html/app-ecommerce-dashboard";
    }

    @RequestMapping("/admin/review")
    public String manageReviews() {
        return "Admin/html/app-ecommerce-manage-reviews";
    }

    @RequestMapping("/admin/list")
    public String productList(Model model) throws JsonProcessingException {

        List<DashbordList> dashboardData = dashboardService.getDashboardDataByMonth();

        // Chuyển đổi danh sách thành JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String dashboardDataJson = objectMapper.writeValueAsString(dashboardData);

        // Thêm dữ liệu vào model
        model.addAttribute("dashboardDataJson", dashboardDataJson);
        // Log information to console
        System.out.println("Dashboard data retrieved successfully. Size: {}" + dashboardData.size());

        // Log each DashbordList object
        for (DashbordList item : dashboardData) {
        System.out.println("DashbordList item: {}"+ item.toString());
        }

        // List<DashbordList> dashboardData = dashboardService.getDashboardDataByMonth();

        // // Chuyển đổi danh sách thành JSON
        // ObjectMapper objectMapper = new ObjectMapper();
        // String dashboardDataJson = objectMapper.writeValueAsString(dashboardData);

        // // Thêm dữ liệu vào model
        // model.addAttribute("dashboardDataJson", dashboardDataJson);

        // // Log information to console
        // System.out.println("Dashboard data retrieved successfully. Size: " + dashboardData.size());

        // // Log each DashbordList object
        // for (DashbordList item : dashboardData) {
        //     System.out.println("DashbordList item: " + item.toString());
        // }

        return "Admin/html/app-ecommerce-dashboard-list";
    }
    //
    // @RequestMapping("/auth-forgot-password-basic")
    // public String forgoPassword() {
    // return "Admin/html/auth-forgot-password-basic";
    // }
    //
    // @RequestMapping("/auth-login-basic")
    // public String authLogin() {
    // return "Admin/html/auth-login-basic";
    // }
    //
    // @RequestMapping("/auth-register-basic")
    // public String authRegister() {
    // return "Admin/html/auth-register-basic";
    // }

    @RequestMapping("/custommer-management")
    public String customer() {
        return "Admin/html/custommer-management";
    }

    @RequestMapping("/management-product")
    public String managementProduct() {
        return "Admin/html/management-product";
    }

    @RequestMapping("/management-product-detail")
    public String managementProductDetail() {
        return "Admin/html/management-product-detail";
    }

    @RequestMapping("/pages-account-settings-account")
    public String settingsAccount() {
        return "Admin/html/pages-account-settings-account";
    }

    @RequestMapping("/pages-account-settings-connections")
    public String settingsConnections() {
        return "Admin/html/pages-account-settings-connections";
    }

    @RequestMapping("/pages-account-settings-notifications")
    public String settingsNotifications() {
        return "Admin/html/pages-account-settings-notifications";
    }

    @RequestMapping("/pages-misc-error")
    public String miscError() {
        return "Admin/html/pages-misc-error";
    }

    @RequestMapping("/pages-misc-under-maintenance")
    public String miscUnderMaintenance() {
        return "Admin/html/pages-misc-under-maintenance";
    }

    @RequestMapping("/supplier-management")
    public String supplier() {
        return "Admin/html/supplier-management";
    }

    // @GetMapping("/auth-login-basic")
    // public String login(@ModelAttribute("user") User user) {
    // return "Admin/html/auth-login-basic";
    // }
    //
    @PostMapping("/pages-account-settings-account")
    public String account(@Valid User user, BindingResult errors, Model model, HttpServletRequest request,
            @RequestParam("address1") String addrees1) {
        System.out.println("Đã đi vào " + user.getUserId());
        // // Kiểm tra lỗi xác thực
        // if (errors.hasErrors()) {
        // // Nếu có lỗi, thêm lỗi và người dùng vào mô hình và trả về trang cài đặt tài
        // khoản
        // model.addAttribute("errors", errors);
        // return "Admin/html/pages-account-settings-account";
        // }

        // Tìm người dùng trong cơ sở dữ liệu
        Optional<Users> userOptional = userJpa.findById(user.getUserId());
        if (userOptional.isPresent()) {
            System.out.println("Tồn tại");
            // userOptional.get().getAddress().setAddressLine1(address);
            System.out.println("Update dia chi " + userOptional.get().getAddress().getAddressLine1());
            // Nếu người dùng tồn tại, cập nhật và lưu vào cơ sở dữ liệu
            Users userEntity = updateProductEntityFromModel(userOptional.get(), user);
            userEntity.getAddress().setAddressLine1(addrees1);
            Users savedUser = userJpa.save(userEntity);
            model.addAttribute("user", savedUser);
        } else {
            System.out.println("Không tồn tại");
        }

        // Trả về trang cài đặt tài khoản
        return "Admin/html/pages-account-settings-account";
    }

    private Users updateProductEntityFromModel(Users users, User user) {
        // Addresses addresses = users.getAddress();
        //
        // addresses.setAddressLine1(address);
        // Addresses AddressesSave = addressJpa.save(addresses);

        users.setFirstName(user.getFirstName());
        users.setLastName(user.getLastName());
        users.setEmail(user.getEmail());
        users.setPhone(user.getPhone());
        // users.setPassword(user.getPassword());
        // users.setAddress(AddressesSave);

        return users;
    }

    @GetMapping("/pages-account-settings-account")
    public String account(HttpServletRequest request, Model model) {
        String email = CookieUtil.get(request, "email");
        System.out.println("Email = " + email);
        if (email != null) {
            Optional<Users> userOptional = userJpa.findByEmail(email);
            System.out.println("Dia chi " + userOptional.get().getAddress().getAddressLine1());
            System.out.println("Dia chi " + userOptional.get().getAddress().getAddressId());
            if (userOptional.isPresent()) {
                Users userEntity = userOptional.get();
                model.addAttribute("user", userEntity);
                System.out.println("address = " + userEntity.getAddress().getAddressLine1());
                System.out.println("id = " + userEntity.getUserId());
                System.out.println("Tai khoan " + userEntity.getEmail());
                System.out.println("Mat khau " + userEntity.getPassword());
            } else {
                System.out.println("User not found!");
            }
        } else {
            System.out.println("No user email found in cookies!");
        }
        return "Admin/html/pages-account-settings-account";
    }

    @PostMapping("/change-pass")
    public String changepass(HttpServletRequest request, @Valid PasswordChangeRequest change, BindingResult errorss,
            Model model, Users users, User user, @RequestParam("newPassword") String newPass,
            @RequestParam("confirmPassword") String conPass, RedirectAttributes redirect, HttpServletResponse resp) {
        if (errorss.hasErrors()) {
            redirect.addFlashAttribute("errorss", errorss);
            redirect.addFlashAttribute("change", change);
        } else {
            String email = CookieUtil.get(request, "email");
            Optional<Users> userOptional = userJpa.findByEmail(email);
            if (userOptional.isPresent()) {
                System.out.println("Vào change pass " + userOptional.get().getUserId());
                if (!newPass.equals(conPass)) {
                    redirect.addFlashAttribute("errorpass", "Password incorrect");
                    return "redirect:/pages-account-settings-account";
                } else {
                    Users userEntity = userOptional.get();
                    userEntity.setPassword(conPass);
                    Users savedUser = userJpa.save(userEntity);
                    CookieUtil.clear(resp, "email");
                    CookieUtil.clear(resp, "password");
                    return "redirect:/login";
                }
            } else {
                System.out.println("Không tồn tại");
                return "redirect:/pages-account-settings-account";
            }

        }
        return "redirect:/pages-account-settings-account";
    }

    @PostMapping("/account/delete")
    public String account(HttpServletRequest request, Model model,
            @RequestParam(value = "accountActivation", required = false) String accountActivation,
            HttpServletResponse resp) {
        if (accountActivation != null && accountActivation.equals("true")) {
            System.out.println("Checkbox được chọn");
            String email = CookieUtil.get(request, "email");
            Optional<Users> userOptional = userJpa.findByEmail(email);
            Users userEntity = userOptional.get();
            userEntity.setIsDeleted(true);
            Users savedUser = userJpa.save(userEntity);
            CookieUtil.clear(resp, "email");
            CookieUtil.clear(resp, "password");
            return "redirect:/login";
        } else {
            System.out.println("Checkbox không được chọn");
        }
        // Xử lý tiếp tục
        return "Admin/html/pages-account-settings-account";
    }

    @GetMapping("/orders")
    public String getOrder() {
        return "Admin/html/pages-misc-error";
    }

    @RequestMapping("/logout")
    public String logout(Model model, RedirectAttributes RE) {

        CookieUtil cookieUtil = new CookieUtil();
        cookieUtil.clear(resp, "email");
        cookieUtil.clear(resp, "password");

        return "redirect:/home";
    }

}
