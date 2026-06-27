package com.bfhl.api.service;

import com.bfhl.api.dto.BfhlRequest;
import com.bfhl.api.dto.BfhlResponse;

/**
 * Service interface for BFHL processing logic
 */
public interface BfhlService {

    /**
     * Processes the input data array and returns categorized response
     *
     * @param request the incoming request containing data array
     * @return BfhlResponse with categorized data and computed fields
     */
    BfhlResponse processData(BfhlRequest request);
}
