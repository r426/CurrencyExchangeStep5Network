package com.ryeslim.currencyexchangeviewmodel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private val mViewModel: CurrencyViewModel by lazy {
        ViewModelProviders.of(this).get(CurrencyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViews()
        convert.setOnClickListener { manageConversion() }
    }

    fun amountFrom() {
        if (amount_from.text.toString().trim().length > 0)
            mViewModel.amountFrom = amount_from.text.toString().toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
        else mViewModel.amountFrom = -1.toBigDecimal()
    }
    fun currencyFrom() {
        mViewModel.indexFrom = when (radio_group_from.getCheckedRadioButtonId()) {
            from_eur.getId() -> 0
            from_usd.getId() -> 1
            from_jpy.getId() -> 2
            else -> -1
        }
    }
    fun currencyTo() {
        mViewModel.indexTo = when (radio_group_to.getCheckedRadioButtonId()) {
            to_eur.getId() -> 0
            to_usd.getId() -> 1
            to_jpy.getId() -> 2
            else -> -1
        }
    }
    fun manageConversion() {
        var errorMessage: String? = null

        mViewModel.numberOfOperations++

        amountFrom()
        currencyFrom()
        currencyTo()

        //Calculated here to make sure, in the next step,
        //that the funds are sufficient
        mViewModel.calculateCommission()

        //Error check
        if (mViewModel.amountFrom < 0.toBigDecimal()) {
            errorMessage = getString(R.string.enter_the_amount)
        } else if (radio_group_from.getCheckedRadioButtonId() == -1
            || radio_group_to.getCheckedRadioButtonId() == -1
            || mViewModel.indexFrom == mViewModel.indexTo
        ) {
            errorMessage = getString(R.string.radio_button_error)
        } else if (mViewModel.amountFrom + mViewModel.thisCommission > mViewModel.balance[mViewModel.indexFrom]) {
            errorMessage = getString(R.string.insufficient_funds)
        }

        //Error message
        if (errorMessage != null) {
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG).show()
            mViewModel.numberOfOperations--
        } else {
            //No errors
            mViewModel.makeUrl()
            mViewModel.getResultFromNetwork()
            mViewModel.calculateValues()
            mViewModel.makeInfoMessage()
            setViews()
        }
        return
    }

    fun setViews() {
        radio_group_from.clearCheck()
        radio_group_to.clearCheck()
        amount_from.text.clear()
        eur_balance_value.text = String.format("%.2f", mViewModel.balance[0])
        usd_balance_value.text = String.format("%.2f", mViewModel.balance[1])
        jpy_balance_value.text = String.format("%.2f", mViewModel.balance[2])
        eur_commissions_value.text = String.format("%.2f", mViewModel.commission[0])
        usd_commissions_value.text = String.format("%.2f", mViewModel.commission[1])
        jpy_commissions_value.text = String.format("%.2f", mViewModel.commission[2])
        info_message.text = mViewModel.infoMessage
    }
}
