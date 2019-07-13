package com.ryeslim.currencyexchangeviewmodel

import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyViewModel : ViewModel() {

    var balance = arrayOf(BigDecimal(1000), BigDecimal(0), BigDecimal(0))
    var commission = arrayOf(BigDecimal(0), BigDecimal(0), BigDecimal(0))
    val currency = arrayOf("EUR", "USD", "JPY")
    var amountFrom = -1.toBigDecimal()
    var amountResult = 0.toBigDecimal()
    var indexFrom = -1
    var indexTo = -1
    var url = ""
    var thisCommission = 0.toBigDecimal()
    var numberOfOperations = 0
    var infoMessage = ""

    fun makeUrl() {
        var url: String =
            "http://api.evp.lt/currency/commercial/exchange/$amountFrom-${currency[indexFrom]}/${currency[indexTo]}/latest"
    }

    fun getResultFromNetwork() {
        //go to url via retrofit/volley
        //and get result in JSON

        //some fake number
        amountResult = 50.toBigDecimal()
    }

    fun calculateValues() {
        balance[indexFrom] =
            balance[indexFrom] - amountFrom - thisCommission
        balance[indexTo] = balance[indexTo] + amountResult
        commission[indexFrom] += thisCommission
    }

    fun calculateCommission() {
        if (numberOfOperations > 5) {
            thisCommission = zeroSevenPercent(amountFrom, 0.7.toBigDecimal())
        } else {
            thisCommission = 0.toBigDecimal()
        }
    }

    fun zeroSevenPercent(value: BigDecimal, percent: BigDecimal) = (value * percent / 100.toBigDecimal()).setScale(
        2, RoundingMode.HALF_EVEN
    )

    fun makeInfoMessage() {
        infoMessage = String.format(
            "You converted %.2f %s to %.2f %s. Commissions paid: %.2f %s",
            amountFrom,
            currency[indexFrom],
            amountResult,
            currency[indexTo],
            thisCommission,
            currency[indexFrom]
        )
    }
}