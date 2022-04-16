package com.example.grobmadass.dataModels

data class PrivateCarData(
    var privateCarId: String = "",
    var privateCarWaitGeoN: Double = 0.0,
    var privateCarWaitGeoE: Double = 0.0,
    var privateCarDesGeoN: Double = 0.0,
    var privateCarDesGeoE: Double = 0.0,
    var privateCarTotalPax: Int = 0,
    var privateCarTotalTime: Int = 0,
    var privateCarTotalDistance: Double = 0.0,
    var privateCarTotalPrice: Double = 0.0,
    var privateCarStatus: Int = 0,
    var customerId: String = "",
)