package com.example.spring.boot.composite.application.composite;

import com.example.spring.boot.composite.domain.composite.CompositeOutputVO;
import com.example.spring.boot.composite.domain.vehicle.VehicleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
@AllArgsConstructor
public class CompositeService {

    private final VehicleRepository vehicleRepository;

    public Set<CompositeOutputVO> getAllComposites(final String brandName) {
        return vehicleRepository.findAllComposites(brandName).stream().collect(toSet());
    }

}
