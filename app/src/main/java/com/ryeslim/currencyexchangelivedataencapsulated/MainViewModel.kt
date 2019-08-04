package com.ryeslim.currencyexchangelivedataencapsulated

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode

class MainViewModel : ViewModel() {

    private val _euro = MutableLiveData<Currency>()
    val euro: LiveData<Currency>
        get() = _euro

    private val _dollar = MutableLiveData<Currency>()
    val dollar: LiveData<Currency>
        get() = _dollar

    private val _yen = MutableLiveData<Currency>()
    val yen: LiveData<Currency>
        get() = _yen

    private val _infoMessage = MutableLiveData<String>()
    val infoMessage: LiveData<String>
        get() = _infoMessage

    init {
        _euro.value = Currency(1000.toBigDecimal(), 0.toBigDecimal(), "EUR")
        _dollar.value = Currency(0.toBigDecimal(), 0.toBigDecimal(), "USD")
        _yen.value = Currency(0.toBigDecimal(), 0.toBigDecimal(), "JPY")
        _infoMessage.value = ""
    }

    val currencies = arrayOf(_euro, _dollar, _yen)

    var amountFrom = (-1).toBigDecimal()
    private var amountResult = 0.toBigDecimal()
    var indexFrom = -1
    var indexTo = -1
    var url = ""
    var thisCommission = 0.toBigDecimal()
    var numberOfOperations = 0


    fun makeUrl() {
        var url: String =
            "http://api.evp.lt/currency/commercial/exchange/$amountFrom-${(currencies[indexFrom].value)?.currencyCode}/${(currencies[indexTo].value)?.currencyCode}/latest"
    }

    fun getResultFromNetwork() {
        //go to url via retrofit/volley
        //and get result in JSON

        //some fake number
        amountResult = 50.toBigDecimal()
    }

    fun calculateValues() {
        currencies[indexFrom].value?.balanceValue =

            currencies[indexFrom].value?.balanceValue?.minus(amountFrom)?.minus(thisCommission)!!

        currencies[indexTo].value?.balanceValue = currencies[indexTo].value?.balanceValue?.plus(amountResult)!!

        currencies[indexFrom].value?.commissionsValue =
            currencies[indexFrom].value?.commissionsValue?.plus(thisCommission)!!

        // force postValue to notify Observers
        currencies[indexFrom].value = currencies[indexFrom].value
        currencies[indexTo].value = currencies[indexTo].value
    }

    fun calculateCommission() {
        thisCommission = if (numberOfOperations > 5) {
            zeroSevenPercent(amountFrom, 0.7.toBigDecimal())
        } else {
            0.toBigDecimal()
        }
    }

    private fun zeroSevenPercent(value: BigDecimal, percent: BigDecimal): BigDecimal =
        (value * percent / 100.toBigDecimal()).setScale(
            2, RoundingMode.HALF_EVEN
        )

    fun makeInfoMessage() {
        _infoMessage.value = String.format(
            "You converted %.2f %s to %.2f %s. Commissions paid: %.2f %s",
            amountFrom,
            currencies[indexFrom].value?.currencyCode,
            amountResult,
            currencies[indexTo].value?.currencyCode,
            thisCommission,
            currencies[indexFrom].value?.currencyCode
        )
    }
}