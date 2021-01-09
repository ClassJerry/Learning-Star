package com.example.sht.learningstar

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.example.sht.learningstar.MobileLoad.MobileLoginActivity

/**
 * 登录界面
 *
 * @author Jerry Huang
 * @date 2021/1/9
 */
class LoginActivity : AppCompatActivity() {

    // region view

    /**
     * 注册入口
     */
    private var tvRegister: TextView? = null

    /**
     * 用户名输入框
     */
    private var etUsername: EditText? = null

    /**
     * 密码输入框
     */
    private var etPassword: EditText? = null

    /**
     * 账号登录
     */
    private var btnAccountLogin: Button? = null

    /**
     * 手机登录
     */
    private var btnMobileLogin: Button? = null

    // endregion

    // region override

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // SDK 版本号大于等于 21 时，布局进入状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }

        // 初始化云存储
        Bmob.initialize(this, getString(R.string.app_key))

        // 绑定 view
        tvRegister = findViewById(R.id.tv_register)
        btnAccountLogin = findViewById(R.id.btn_account_login)
        btnMobileLogin = findViewById(R.id.btn_mobile_login)
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)

        // 设置点击监听器
        tvRegister?.setOnClickListener(onClickListener)
        btnAccountLogin?.setOnClickListener(onClickListener)
        btnMobileLogin?.setOnClickListener(onClickListener)
    }

    // endregion

    // region lambda

    /**
     * 点击监听器
     */
    private val onClickListener: (View?) -> Unit = onClick@ {
        when (it?.id) {
            R.id.tv_register -> {
                // 跳转注册界面
                val registerIntent = Intent(this, RegisterActivity::class.java)
                startActivity(registerIntent)
            }
            R.id.btn_mobile_login -> {
                // 跳转手机登录界面
                val mobileLoginIntent = Intent(this, MobileLoginActivity::class.java)
                startActivity(mobileLoginIntent)
            }
            R.id.btn_account_login -> {
                // 用户名和密码判空
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

                // 使用 BmobSDK 提供的登录功能
                val bmobUser = BmobUser()
                bmobUser.username = username.toString()
                bmobUser.setPassword(password.toString())
                bmobUser.login(object : SaveListener<BmobUser>() {
                    override fun done(bmobUser: BmobUser, bmobException: BmobException?) {
                        if (bmobException == null) {
                            Toast.makeText(this@LoginActivity, getString(R.string.login_successfully), Toast.LENGTH_SHORT).show()

                            // 跳转主界面
                            val loginIntent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(loginIntent)
                        } else {
                            Toast.makeText(this@LoginActivity, getString(R.string.login_failure), Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
    }

    // endregion
}
