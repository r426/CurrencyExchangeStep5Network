package com.ryeslim.currencyexchangelivedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ryeslim.currencyexchangelivedata.databinding.ActivityMainBinding
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private val mViewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Setting up LiveData observation relationship
        mViewModel.euro.observe(this, Observer { newEuro -> 
            binding.eurBalanceValue.text = String.format("%.2f", newEuro.balanceValue)
            binding.eurCommissionsValue.text = String.format("%.2f", newEuro.commissionsValue)
        })
        mViewModel.dollar.observe(this, Observer { newDollar ->
            binding.usdBalanceValue.text = String.format("%.2f", newDollar.balanceValue)
            binding.usdCommissionsValue.text = String.format("%.2f", newDollar.commissionsValue)
        })
        mViewModel.yen.observe(this, Observer { newYen ->
            binding.jpyBalanceValue.text = String.format("%.2f", newYen.balanceValue)
            binding.jpyCommissionsValue.text = String.format("%.2f", newYen.commissionsValue)
        })
        mViewModel.infoMessage.observe(this, Observer { newInfoMessage ->
            binding.infoMessage.text = newInfoMessage
        })
        binding.convert.setOnClickListener { manageConversion() }
    }

    private fun amountFrom() {
        if (binding.amountFrom.text.toString().trim().isNotEmpty())
            mViewModel.amountFrom = binding.amountFrom.text.toString().toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
        else mViewModel.amountFrom = (-1).toBigDecimal()
    }
    private fun currencyFrom() {
        mViewModel.indexFrom = when (binding.radioGroupFrom.checkedRadioButtonId) {
            binding.fromEur.id -> 0
            binding.fromUsd.id -> 1
            binding.fromJpy.id -> 2
            else -> -1
        }
    }
    private fun currencyTo() {
        mViewModel.indexTo = when (binding.radioGroupTo.checkedRadioButtonId) {
            binding.toEur.id -> 0
            binding.toUsd.id -> 1
            binding.toJpy.id -> 2
            else -> -1
        }
    }
    private fun manageConversion() {
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
        } else if (binding.radioGroupFrom.checkedRadioButtonId == -1
            || binding.radioGroupTo.checkedRadioButtonId == -1
            || mViewModel.indexFrom == mViewModel.indexTo
        ) {
            errorMessage = getString(R.string.radio_button_error)
        } else if (mViewModel.amountFrom + mViewModel.thisCommission > mViewModel.currencies[mViewModel.indexFrom].value?.balanceValue) {
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
            clearButtons()
        }
        return
    }

    private fun clearButtons() {
        binding.radioGroupFrom.clearCheck()
        binding.radioGroupTo.clearCheck()
        binding.amountFrom.text.clear()
    }
}
