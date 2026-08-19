package com.example.ecommerce.controller.v1;

import com.example.ecommerce.dto.customer.CustomerRequestDto;
import com.example.ecommerce.dto.customer.CustomerResponseDto;
import com.example.ecommerce.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(
        name = "Customers",
        description = "Customer Management APIs"
)
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }



    // post : http://localhost:8080/api/v1/customers
      /*
      {
  "name": "Mohan",
  "email": "mohan.reddy@example.com",
  "phone": "9876543212",
  "address": "gayee Tech Park, Bangalore, India"
}
       */
    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<CustomerResponseDto> create(
            @Valid @RequestBody CustomerRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.create(request));
    }



    // get :  http://localhost:8080/api/v1/customers

    @GetMapping
    @Operation(summary = "Get all customers")
    public ResponseEntity<List<CustomerResponseDto>> getAll() {

        return ResponseEntity.ok(
                customerService.getAll()
        );
    }


    //get : http://localhost:8080/api/v1/customers/1

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                customerService.getById(id)
        );
    }

    // put : http://localhost:8080/api/v1/customers/1
    /*
    {
  "name": "Mohan up",
  "email": "mohan1.reddy@example.com",
  "phone": "9876543212",
  "address": "gayee Tech Park, Bangalore, India"
}

     */
    @PutMapping("/{id}")
    @Operation(summary = "Update customer details")
    public ResponseEntity<CustomerResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto request) {

        return ResponseEntity.ok(
                customerService.update(id, request)
        );
    }


    //delete :  http://localhost:8080/api/v1/customers/1

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer by ID")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        customerService.delete(id);

        return ResponseEntity.noContent().build();
    }
}