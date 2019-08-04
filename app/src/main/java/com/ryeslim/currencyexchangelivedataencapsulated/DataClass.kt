package com.ryeslim.currencyexchangelivedataencapsulated

import java.math.BigDecimal

data class Currency(var balanceValue: BigDecimal, var commissionsValue: BigDecimal, val currencyCode: String)