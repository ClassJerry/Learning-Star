package com.example.sht.learningstar

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import org.json.JSONObject
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class PhoneLoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MobileLoginActivity"

        /**
         * 手机号正常长度
         */
        private const val PHONE_NUMBER_NORMAL_LENGTH = 11

        /**
         * 手机号地区前缀
         */
        private const val PHONE_NUMBER_DISTRICT_PREFIX = "86"

        /**
         * 一分钟内的秒数
         */
        private const val SECONDS_IN_MINUTE = 60

        /**
         * 一秒内的毫秒数
         */
        private const val MILLISECONDS_IN_SECOND = 1000L

        /**
         * 验证码正常长度
         */
        private const val VERIFICATION_CODE_NORMAL_LENGTH = 4
    }

    // region view

    /**
     * 手机号输入框
     */
    private var etPhoneNumber: EditText? = null

    /**
     * 验证码输入框
     */
    private var etVerificationCode: EditText? = null

    /**
     * 点击获取验证码的文本
     */
    private var tvGetVerificationCode: TextView? = null

    /**
     * 登录按钮
     */
    private var btnLogin: Button? = null

    // endregion

    private var eventHandler: EventHandler? = null

    // region override

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)

        // 绑定 view
        etPhoneNumber = findViewById(R.id.et_phone_number)
        etVerificationCode = findViewById(R.id.et_verification_code)
        tvGetVerificationCode = findViewById(R.id.btn_get_verification_code)
        btnLogin = findViewById(R.id.btn_login)

        // 设置点击监听器
        tvGetVerificationCode?.setOnClickListener(onClickListener)
        btnLogin?.setOnClickListener(onClickListener)

        // 初始化短信服务
        SMSSDK.initSDK(this, getString(R.string.smssdk_app_key), getString(R.string.smssdk_app_secret))

        eventHandler = object : EventHandler() {
            override fun afterEvent(event: Int, result: Int, data: Any) {
                val message = myHandler.obtainMessage(0x00).apply {
                    arg1 = event
                    arg2 = result
                    obj = data
                }
                myHandler.sendMessage(message)
            }
        }
        SMSSDK.registerEventHandler(eventHandler)
    }

    override fun onDestroy() {
        super.onDestroy()
        SMSSDK.unregisterEventHandler(eventHandler)
    }

    // endregion

    // region lambda

    /**
     * 点击监听器
     */
    private val onClickListener: (View?) -> Unit = onClick@ {
        // 获取输入的手机号
        val phoneNumber = etPhoneNumber?.text?.toString()

        // 手机号为空
        if (phoneNumber.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.phone_number_can_not_be_empty), Toast.LENGTH_SHORT).show()
            return@onClick
        }

        // 手机号长度不够
        if (phoneNumber.length < PHONE_NUMBER_NORMAL_LENGTH) {
            Toast.makeText(this, getString(R.string.length_of_phone_number_is_not_enough), Toast.LENGTH_SHORT).show()
            return@onClick
        }

        when (it?.id) {
            R.id.btn_get_verification_code -> {
                // 防止重复申请验证码
                tvGetVerificationCode?.isClickable = false

                // 请求验证码
                SMSSDK.getVerificationCode(PHONE_NUMBER_DISTRICT_PREFIX, phoneNumber)

                // 一分钟后才能再次请求
                thread {
                    for (i in SECONDS_IN_MINUTE downTo 1) {
                        val message = myHandler.obtainMessage(0x01)
                        message.arg1 = i
                        myHandler.sendMessage(message)
                        try { sleep(MILLISECONDS_IN_SECOND) } catch (e: InterruptedException) { e.printStackTrace() }
                    }
                    myHandler.sendEmptyMessage(0x02)
                }
            }
            R.id.btn_login -> {
                // 获取输入的验证码
                val verificationCode = etVerificationCode?.text?.toString()

                // 验证码为空
                if (verificationCode.isNullOrBlank()) {
                    Toast.makeText(this, getString(R.string.verification_code_can_not_be_empty), Toast.LENGTH_SHORT).show()
                    return@onClick
                }

                // 验证码长度不够
                if (verificationCode.length < VERIFICATION_CODE_NORMAL_LENGTH) {
                    Toast.makeText(this, getString(R.string.length_of_verification_code_is_not_enough), Toast.LENGTH_SHORT).show()
                    return@onClick
                }

                // 提交手机号和验证码
                SMSSDK.submitVerificationCode(PHONE_NUMBER_DISTRICT_PREFIX, phoneNumber, verificationCode)
                Log.d(TAG, verificationCode)
            }
        }
    }

    // endregion

    var myHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0x00 -> {
                    val event = msg.arg1
                    val result = msg.arg2
                    val data = msg.obj
                    Log.e(TAG, "result : $result, event: $event, data : $data")
                    if (result == SMSSDK.RESULT_COMPLETE) { //回调  当返回的结果是complete
                        if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) { //获取验证码
                            Toast.makeText(this@PhoneLoginActivity, "发送验证码成功", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "get verification code successful.")
                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) { //提交验证码
                            Log.d(TAG, "submit code successful")
                            Toast.makeText(this@PhoneLoginActivity, "提交验证码成功", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@PhoneLoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.d(TAG, data.toString())
                        }
                    } else { //进行操作出错，通过下面的信息区分析错误原因
                        try {
                            val throwable = data as Throwable
                            throwable.printStackTrace()
                            val `object` = JSONObject(throwable.message)
                            val des = `object`.optString("detail") //错误描述
                            val status = `object`.optInt("status") //错误代码
                            //错误代码：  http://wiki.mob.com/android-api-%E9%94%99%E8%AF%AF%E7%A0%81%E5%8F%82%E8%80%83/
                            Log.e(TAG, "status: $status, detail: $des")
                            if (status > 0 && !TextUtils.isEmpty(des)) {
                                Toast.makeText(this@PhoneLoginActivity, des, Toast.LENGTH_SHORT).show()
                                return
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                0x01 -> tvGetVerificationCode!!.text = "重新发送" + msg.arg1 + ")"
                0x02 -> {
                    tvGetVerificationCode!!.text = "获取验证码"
                    tvGetVerificationCode!!.isClickable = true
                }
            }
        }
    }
}