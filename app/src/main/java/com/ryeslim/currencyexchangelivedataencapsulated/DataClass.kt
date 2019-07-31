package com.ryeslim.currencyexchangelivedataencapsulated

import java.math.BigDecimal

data class Currency(var balanceValue: BigDecimal, val balanceCurrency: String, var commissionsValue: BigDecimal, val commissionsCurrency: String)