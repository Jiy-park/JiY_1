package com.example.jiy_1

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.ContentInfoCompat.Flags
import com.example.jiy_1.databinding.ActivityMainBinding
import render.animations.Attention
import render.animations.Render

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val render = Render(this@MainActivity)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run{
            btnDone.setOnClickListener {
                if(check()) {
                    process()
                    ivCopy.visibility = View.VISIBLE
                }
            }
            weight.setOnEditorActionListener { v, actionId, event ->
                if(check()) {
                    process()
                    ivCopy.visibility = View.VISIBLE
                }
                false
            }
            ivCopy.setOnClickListener{
                val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("result", this.tvResult.text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@MainActivity, "복사하였습니다.", Toast.LENGTH_SHORT).show()
            }
            btnClear.setOnClickListener {
                ivCopy.visibility = View.GONE
                tvResult.text = resources.getString(R.string.TXT_RESULT)
                totalPay.text.clear()
                totalPeople.text.clear()
                weight.text.clear()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun process(){
        val TOTAL_PAY = binding.totalPay.text.toString().toInt() //지불 총액
        val TOTAL_PEOPLE = binding.totalPeople.text.toString().toInt() // 총 인원
        val PAIED_PEOPLE = binding.paidPeople.text.toString().toInt() // 회비 지불 인원
        val WEIGHT = binding.weight.text.toString().toInt() //가중치
        val default_pay = TOTAL_PAY / TOTAL_PEOPLE // 기본 더치페이

        var weight_money = 0F // 가중될 금액
        var weighted_pay = 0F // 가중된 최후 금액
        var non_weighted_pay = 0F // 감면된 최후 금액 (기본 더치페이 - 가중될 금액)

//        Toast.makeText(this@MainActivity, "$default_pay",Toast.LENGTH_SHORT).show()//check

        binding.run {
            if(weight.text.toString() != "0"){
                weight_money = if(checkBox.isChecked) (default_pay * WEIGHT/100).toFloat()
                                else WEIGHT.toFloat()

                non_weighted_pay = default_pay - weight_money
                val remaining = TOTAL_PAY - non_weighted_pay * PAIED_PEOPLE
                weighted_pay = remaining / (TOTAL_PEOPLE - PAIED_PEOPLE)
            }
            else{
                weighted_pay = default_pay.toFloat()
                non_weighted_pay = default_pay.toFloat()
            }

            tvResult.text =
                """"
                총 지불 금액 : $TOTAL_PAY 원
                지불 인원 : $TOTAL_PEOPLE 명
                인당 금액 : $default_pay 원
                
                회비 낸 인원 : $PAIED_PEOPLE 명
                회비 혜택금 : $weight_money 원
                
                회비 X 금액 : $weighted_pay 원
                회비 O 금액 : $non_weighted_pay 원
                """
        }


    }

    fun check():Boolean{
        binding.run {
            if(totalPay.text.toString() == "") {
                bounceView(totalPay)
                return false
            }
            if(totalPeople.text.toString() == ""){
                bounceView(totalPeople)
                return false
            }
            if(paidPeople.text.toString() == ""){
                bounceView(paidPeople)
                return false
            }
            if(weight.text.toString() == ""){
                bounceView(weight)
                return false
            }
        }
        return true
    }

    fun bounceView(view:View){
        render.setAnimation(Attention().Bounce(view))
        render.setDuration(500L)
        render.start()
    }
}