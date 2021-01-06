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

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

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
     * 保存按钮
     */
    private var btnSave: Button? = null

    /**
     * 取消按钮
     */
    private var btnCancel: Button? = null

    // endregion

    // region override

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 布局进入状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }

        // 初始化云存储
        Bmob.initialize(this, getString(R.string.app_key))

        // 绑定 view
        etUsername = findViewById(R.id.et_regist_user)
        etPassword = findViewById(R.id.et_regist_password)
        btnSave = findViewById(R.id.bt_regist_save)
        btnCancel = findViewById(R.id.bt_regist_cancel)

        // 设置监听器
        btnSave?.setOnClickListener(this)
        btnCancel?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_regist_save -> {
                val user_num = etUsername!!.text.toString()
                val user_password = etPassword!!.text.toString().trim { it <= ' ' }
                // 非空验证
                if (user_num.isEmpty() || user_password.isEmpty()) {
                    Toast.makeText(this@RegisterActivity, "账号或密码不能为空", Toast.LENGTH_SHORT).show()
                    return
                }
                // 使用BmobSDK提供的注册功能
                val myUser = BmobUser()
                myUser.username = user_num
                myUser.setPassword(user_password)
                myUser.signUp(object : SaveListener<BmobUser>() {
                    override fun done(s: BmobUser, e: BmobException) {
                        if (e == null) {
                            Toast.makeText(this@RegisterActivity, "注册成功", Toast.LENGTH_SHORT).show()
                        } else {
                            //loge(e);
                            Toast.makeText(this@RegisterActivity, "注册失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
                //注意：不能用save方法进行注册
                finish()
            }
            R.id.bt_regist_cancel -> finish()
            else -> {
            }
        }
    }

    // endregion
}