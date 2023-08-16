package com.example.calculator

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.calculator.databinding.ActivityMainBinding
import com.example.calculator.MainActivityViewModel

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private var inputNums = StringBuilder()
    private var lastOperand = 0
    private var lastOperator = ""
    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var viewModel: MainActivityViewModel

    override fun onPause() {
        super.onPause()
        val result = binding.tvResult.text.toString()
        editor.apply() {
            putString("sf_result", result)
            commit()
        }
    }

    override fun onResume() {
        super.onResume()
        val result = sf.getString("sf_result", null)
        if (result != null)
            binding.tvResult.text = result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.tvProgress.text = viewModel.progressValue

        sf = getSharedPreferences("my_sf", MODE_PRIVATE)
        editor = sf.edit()

        binding.tvOne.setOnClickListener(this)
        binding.tvTwo.setOnClickListener(this)
        binding.tvZero.setOnClickListener(this)
        binding.tvThree.setOnClickListener(this)
        binding.tvFour.setOnClickListener(this)
        binding.tvFive.setOnClickListener(this)
        binding.tvSix.setOnClickListener(this)
        binding.tvSeven.setOnClickListener(this)
        binding.tvEight.setOnClickListener(this)
        binding.tvNine.setOnClickListener(this)
        binding.tvPlus.setOnClickListener {
            clickOperator(it)
        }
        binding.tvEqual.setOnClickListener {
            clickOperator(it)
        }
        binding.back.setOnClickListener {
            clickUnaryOperator({ x: Int -> removeLastDigit(x) })
        }
        binding.tvSign.setOnClickListener {
            clickUnaryOperator({ x: Int -> -x })
        }
        binding.tvAC.setOnClickListener {
            clearCalculator()
        }
        binding.tvPercent.setOnClickListener {
            clickOperator(it)
        }

        binding.tvMinus.setOnClickListener {
            clickOperator(it)
        }

        binding.tvMultiple.setOnClickListener {
            clickOperator(it)
        }

        binding.tvDivide.setOnClickListener {
            clickOperator(it)
        }
    }

    private fun clearCalculator() {
        inputNums.clear()
        lastOperand = 0
        lastOperator = ""
        binding.tvResult.text = ""
        displayExpression()
    }

    private fun clickUnaryOperator(converter: (Int) -> Int) {
        if (lastOperator == "") {
            lastOperand = -lastOperand
            if (binding.tvResult.text.toString().isNotEmpty()) {
                var result = binding.tvResult.text.toString().toInt()
                result = converter(result)
                binding.tvResult.text = result.toString()
            }
        }
        if (inputNums.isNotEmpty() && inputNums.toString().toInt() != 0) {
            var value = inputNums.toString().toInt()
            value = converter(value)
            inputNums.clear()
            inputNums.append(value.toString())
        }
        displayExpression()
    }

    private fun clickPercentOperator() {
        val operand = inputNums.toString().toDouble() / 100
        inputNums.clear()
        inputNums.append(operand)
        binding.tvResult.text = inputNums.toString()
        displayExpression()
    }

    private fun clickOperator(view: View) {
        val textView = view as TextView
        if (inputNums.isEmpty()) {
            lastOperator = textView.text.toString()
            if (lastOperator == "=") {
                lastOperator = ""
                binding.tvResult.text = lastOperand.toString()
            }
            displayExpression()
            return
        }
        var result = lastOperand.toString()
        if (lastOperator != "") {
            val operand2 = inputNums.toString().toInt()
            result = calculate(lastOperand, operand2, lastOperator)
        }

        if (!result.isEmpty())
            lastOperand = result.toInt()
        lastOperator = textView.text.toString()
        if (lastOperator == "=")
            lastOperator = ""

        viewModel.updateResult(result)
        inputNums.clear()
        displayExpression()
    }

    private fun calculate(op1: Int, op2: Int, operator: String): String {
        var result = ""
        when (operator) {
            "+" -> result = (op1 + op2).toString()
            "-" -> result = (op1 - op2).toString()
            "*" -> result = (op1 * op2).toString()
            "/" -> {
                if (op2 != 0) {
                    result = (op1 / op2).toString()
                } else {
                    result = "Error"
                }
            }
        }
        return result
    }

    override fun onClick(v: View?) {
        val textView = v as TextView
        val buttonText = textView.text.toString()

        if (buttonText == "%") {
            clickPercentOperator()
        } else {
            clickNumber(textView)
        }
    }

    private fun clickNumber(v: TextView) {
        val number = v.text.toString()
        inputNums.clear()
        inputNums.append(number)
        binding.tvResult.text = inputNums.toString()
        displayExpression()
    }

    private fun displayExpression() {
        var str = StringBuilder()
        str.append(lastOperand)
        if (lastOperator != "") {
            str.append(" $lastOperator ")
            if (!inputNums.isEmpty())
                str.append(inputNums)
        }
        binding.tvProgress.text = str.toString()


    }

    private fun removeLastDigit(num: Int): Int {
        var s = num.toString().dropLast(1)
        var r = 0
        if(s.isNotEmpty())
            r = s.toInt()
        return r
    }

}
