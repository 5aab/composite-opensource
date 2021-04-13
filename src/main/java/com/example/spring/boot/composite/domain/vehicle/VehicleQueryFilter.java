package com.example.spring.boot.composite.domain.vehicle;

import com.querydsl.core.types.dsl.BooleanExpression;

public class VehicleQueryFilter {

    private static com.example.spring.boot.composite.domain.vehicle.QVehicle $= com.example.spring.boot.composite.domain.vehicle.QVehicle.vehicle;

   public static BooleanExpression byBrandName(String name){
         return $.brandName.equalsIgnoreCase(name);
   }

}
