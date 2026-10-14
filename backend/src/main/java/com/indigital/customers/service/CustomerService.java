package com.indigital.customers.service;

import com.indigital.customers.dto.request.CustomerCreateRequest;
import com.indigital.customers.dto.response.CustomerIndicatorsResponse;
import com.indigital.customers.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerCreateRequest request);

    List<CustomerResponse> getCustomers(String dni, String email);

    CustomerResponse getCustomerById(Long id);

    CustomerIndicatorsResponse getIndicators();
}
