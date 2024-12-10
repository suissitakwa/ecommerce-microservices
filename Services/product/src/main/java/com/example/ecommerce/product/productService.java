package com.example.ecommerce.product;

import com.example.ecommerce.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class productService {

private final ProductRepository repository;
private final ProductMapper mapper;

    public Integer createProduct(ProductRequest request) {
        var product =mapper.toProduct(request);
        return repository.save(product).getId();
    }

    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request) {
        var productIds=request
                        .stream()
                        .map(ProductPurchaseRequest::productId)
                        .toList();
        var storedProducts=repository.findAllByIdInOrderById(productIds);
        if(productIds.size()!=storedProducts.size()){
            throw  new ProductPurchaseException("One org more products does not exist");
        }
        var storedRequest=request
                        .stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();
        var productPurchased=new ArrayList<ProductPurchaseResponse>();
        for(int i=0;i< storedProducts.size();i++){
            var product=storedProducts.get(i);
            var productRequest=storedRequest.get(i);
            if(product.getAvailableQuantity()<productRequest.quantity()){
                throw new ProductPurchaseException("Insufficient stock quantity for product with ID::"+productRequest.productId());

            }
            var newAvailableQuantity=product.getAvailableQuantity()-productRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity);
            repository.save(product);
            productPurchased.add(mapper.toProductPurchasedResponse(product,productRequest.quantity()));

        }
        return productPurchased;
    }

    public ProductResponse findById(Integer productId) {
        return repository.findById(productId).map(mapper::toProductResponse)
                .orElseThrow(()->new EntityNotFoundException("Product Not found with the ID::"+productId));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
