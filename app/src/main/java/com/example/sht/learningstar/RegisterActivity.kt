package com.example.sht.learningstar

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener

/**
 * 注册界面
 *
 * @author Jerry Huang
 * @date 2021/1/9
 */
class RegisterActivity : AppCompatActivity() {

    // region view

    /**
     * 用户名输入框
     */
    private var etUsername: EditText? = null

    /**
     * 密码输入框
     */
    private var etPassword: EditText? = null

    /**
     * 确认密码输入框
     */
    private var etConfirmationPassword: EditText? = null

    /**
     * 注册按钮
     */
    private var btnRegister: Button? = null

    /**
     * 取消按钮
     */
    private var btnCancel: Button? = null

    // endregion

    // region override

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // SDK 版本号大于等于 21 时，布局进入状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }

        // 初始化云存储
        Bmob.initialize(this, getString(R.string.app_key))

        // 绑定 view
        etUsername = findViewById(R.id.et_register_username)
        etPassword = findViewById(R.id.et_register_password)
        etConfirmationPassword = findViewById(R.id.et_register_confirmation_password)
        btnRegister = findViewById(R.id.btn_register)
        btnCancel = findViewById(R.id.btn_cancel)

        // 设置点击监听器
        btnRegister?.setOnClickListener(onClickListener)
        btnCancel?.setOnClickListener(onClickListener)
    }

    // endregion

    // region lambda

    /**
     * 点击监听器
     */
    private val onClickListener: (View?) -> Unit = onClick@ {
        if (it?.id == R.id.btn_register) {
            // 用户名、密码和确认密码判空
            val username = etUsername?.text
            if (username.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.username_can_not_be_empty), Toast.LENGTH_SHORT).show()
                return@onClick
            }
            val password = etPassword?.text
            if (password.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.password_can_not_be_empty), Toast.LENGTH_SHORT).show()
                return@onClick
            }
            val confirmationPassword = etConfirmationPassword?.text
            if (confirmationPassword.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.confirmation_password_can_not_be_empty), Toast.LENGTH_SHORT).show()
                return@onClick
            }

            // 检查密码和确认密码是否一致
            if (password != confirmationPassword) {
                Toast.makeText(this, getString(R.string.password_and_confirmation_password_are_not_the_same), Toast.LENGTH_SHORT).show()
                return@onClick
            }

            // 使用 BmobSDK 提供的注册功能
            val bmobUser = BmobUser()
            bmobUser.username = username.toString()
            bmobUser.setPassword(password.toString())
            bmobUser.signUp(object : SaveListener<BmobUser>() {
                override fun done(bmobUser: BmobUser, bmobException: BmobException?) {
                    Toast.makeText(this@RegisterActivity, if (bmobException == null) getString(R.string.register_successfully) else getString(R.string.register_failure), Toast.LENGTH_SHORT).show()
                }
            })
        }
        finish()
    }

    // endregion
}