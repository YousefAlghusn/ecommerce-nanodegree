package com.example.demo.controller;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);

        User user = createUserWithItemEgg();
        when(userRepository.findByUsername("test")).thenReturn(user);
    }

    private static User createUserWithItemEgg() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Egg");
        item.setPrice(BigDecimal.TEN);
        item.setDescription("this is my Egg description");
        List<Item> items = new ArrayList<>();
        items.add(item);

        User user = new User();
        Cart cart = new Cart();
        user.setUsername("test");
        user.setPassword("testPassword");
        cart.setUser(user);
        cart.setItems(items);
        cart.setTotal(BigDecimal.TEN);
        user.setCart(cart);
        return user;
    }


    @Test
    public void testSubmitOrderSuccess() {
        // Submit an order for a valid user
        ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
    }

    @Test
    public void testSubmitOrderErrorUserNotFound() {
        // Submit an order for a non-existing user
        ResponseEntity<UserOrder> response = orderController.submit("incorrect_username");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetOrdersForUserSuccess() {
        // Retrieve orders for a valid user
        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("test");

        assertNotNull(ordersForUser);
        assertEquals(200, ordersForUser.getStatusCodeValue());

        List<UserOrder> orders = ordersForUser.getBody();
        assertNotNull(orders);
    }

    @Test
    public void testGetOrdersForUserErrorNotFound() {
        // Retrieve orders for a non-existing user
        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("incorrect_username");

        assertNotNull(ordersForUser);
        assertEquals(404, ordersForUser.getStatusCodeValue());
    }
}
