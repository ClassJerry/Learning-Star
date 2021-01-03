package com.example.sht.learningstar

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.example.sht.learningstar.MobileLoad.MobileLoad

class StartActivity : AppCompatActivity(), View.OnClickListener {
    // region view

    /**
     * 注册入口
     */
    private var tvRegister: TextView? = null

    /**
     * 用户名输入框
     */
    private var etUserName: EditText? = null

    /**
     * 密码输入框
     */
    private var etPassword: EditText? = null

    /**
     * 账号登录
     */
    private var btnLogin: Button? = null

    /**
     * 手机登录
     */
    private var btnMobileLogin: Button? = null

    // endregion

    // region override

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // 去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }

        // 注册云存储
        Bmob.initialize(this, "bd4814e57ed9c8f00aa0d119c5676cf9")

        // 绑定 view
        tvRegister = findViewById(R.id.tv_register)
        btnLogin = findViewById(R.id.login)
        btnMobileLogin = findViewById(R.id.monile_login)
        etUserName = findViewById(R.id.et_login_user)
        etPassword = findViewById(R.id.et_login_password)

        // 设置监听器
        tvRegister!!.setOnClickListener(this)
        btnLogin!!.setOnClickListener(this)
        btnMobileLogin!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_register -> {
                val registerIntent = Intent(this, RegistActivity::class.java)
                startActivity(registerIntent)
            }
            R.id.monile_login -> {
                val mobileLoginIntent = Intent(this, MobileLoad::class.java)
                startActivity(mobileLoginIntent)
            }
            R.id.login -> {
                val userName = etUserName!!.text.toString()
                if (userName.isBlank()) {
                    Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show()
                    return
                }
                val userPassword = etPassword!!.text.toString().trim()
                if (userPassword.isBlank()) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show()
                    return
                }
                val bu2 = User()
                bu2.username = userName
                bu2.setPassword(userPassword)
                // 使用BmobSDK提供的登录功能
                bu2.login(object : SaveListener<BmobUser>() {
                    override fun done(bmobUser: BmobUser, e: BmobException?) {
                        if (e == null) {
                            Toast.makeText(this@StartActivity, "登陆成功", Toast.LENGTH_SHORT).show()
                            val loginIntent = Intent(this@StartActivity, MainActivity::class.java)
                            startActivity(loginIntent)
                            //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                            //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                        } else {
                            Toast.makeText(this@StartActivity, "登陆失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
    }

    // endregion
}