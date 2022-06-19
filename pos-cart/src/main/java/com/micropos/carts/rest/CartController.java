package com.micropos.carts.rest;

import com.micropos.api.CartApi;
import com.micropos.dto.ItemDto;
import com.micropos.mapper.CartMapper;
import com.micropos.model.Item;
import com.micropos.carts.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController implements CartApi {
    private final CartMapper cartMapper;

    private final CartService cartService;

    public CartController(CartMapper cartMapper, CartService cartService) {
        this.cartMapper = cartMapper;
        this.cartService = cartService;
    }

    @Override
    public ResponseEntity<List<ItemDto>> getCart() {
        var items = cartService.getCart().getItems();
        var cartDto = new ArrayList<>(cartMapper.toCartDto(items));
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ItemDto> getCartItem(String productId) {
        var item = cartService.getItem(productId);
        if (item == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cartMapper.toItemDto(item), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ItemDto>> removeProductFromCart(String productId) {
        cartService.removeItem(productId);
        return getCart();
    }

    @Override
    public ResponseEntity<List<ItemDto>> updateProductQuantityInCart(String productId, Integer quantity) {
        cartService.modifyItem(new Item(productId, quantity));
        return getCart();
    }

    @Override
    public ResponseEntity<List<ItemDto>> addProductToCart(String productId) {
        cartService.addToCart(new Item(productId, 1));
        return getCart();
    }
}
